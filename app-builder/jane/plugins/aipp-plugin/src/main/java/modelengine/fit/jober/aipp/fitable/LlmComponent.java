/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jade.aipp.prompt.constant.Constant.PROMPT_METADATA_KEY;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.MessageType;
import modelengine.fel.core.model.http.SecureConfig;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fel.tool.mcp.client.McpClient;
import modelengine.fel.tool.mcp.client.McpClientFactory;
import modelengine.fel.tool.mcp.entity.Tool;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.jober.aipp.util.McpUtils;
import modelengine.fitframework.inspection.Validation;
import modelengine.jade.store.service.ToolService;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.ModelErrCode;
import modelengine.fit.jober.aipp.enums.StreamMsgType;
import modelengine.fit.jober.aipp.fel.AippLlmMeta;
import modelengine.fit.jober.aipp.fel.AippMemory;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippLogStreamService;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.vo.AippLogVO;
import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.parameterization.StringFormatException;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.UuidUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LLM 组件实现
 *
 * @author 黄夏露
 * @since 2024/4/15
 */
@Component
public class LlmComponent implements FlowableService {
    private static final Logger log = Logger.get(LlmComponent.class);
    private static final String SYSTEM_PROMPT = "{{0}}";
    private static final String PROMPT_TEMPLATE = "{{1}}";
    private static final String AGENT_NODE_ID = "agent";
    private static final String REGEX_MODEL = "statusCode=(\\d+)";
    private static final String WORKFLOW_CALLBACK_FITABLE_ID = "modelengine.fit.jober.aipp.fitable.LLMComponentCallback";
    private static final String WORKFLOW_EXCEPTION_FITABLE_ID =
            "modelengine.fit.jober.aipp.fitable.LLMComponentException";
    private static final String TOOL_UNIQUE_NAME = "toolUniqueName";
    private static final String TOOL_NAME = "name";

    // 暂时使用ConcurrentHashMap存储父节点的元数据
    private final ConcurrentHashMap<String, AippLlmMeta> llmCache = new ConcurrentHashMap<>();

    private final FlowInstanceService flowInstanceService;
    private final ToolService toolService;
    private final AiProcessFlow<Tip, ChatMessage> agentFlow;
    private final AippLogService aippLogService;
    private final AippLogStreamService aippLogStreamService;
    private final ObjectSerializer serializer;
    private final AippModelCenter aippModelCenter;
    private final PromptBuilderChain promptBuilderChain;
    private final AppTaskInstanceService appTaskInstanceService;
    private final McpClientFactory mcpClientFactory;

    /**
     * 大模型节点构造器，内部通过提供的 agent 和 tool 构建智能体工作流。
     *
     * @param flowInstanceService 表示流程实例服务的 {@link FlowInstanceService}。
     * @param toolService 表示工具提供者功能的 {@link ToolService}。
     * @param agent 表示提供智能体功能的 {@link AbstractAgent}。
     * @param aippLogService 表示提供日志服务的 {@link AippLogService}。
     * @param aippLogStreamService 表示提供日志流服务的 {@link AippLogStreamService}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @param aippModelCenter 表示模型中心的 {@link AippModelCenter}。
     * @param promptBuilderChain 表示提示器构造器职责链的 {@link PromptBuilderChain}。
     * @param appTaskInstanceService 表示任务实例服务的 {@link AppTaskInstanceService}。
     * @param mcpClientFactory 表示大模型上下文客户端工厂的 {@link McpClientFactory}。
     */
    public LlmComponent(FlowInstanceService flowInstanceService,
            @Fit ToolService toolService,
            @Fit(alias = AippConst.WATER_FLOW_AGENT_BEAN) AbstractAgent agent,
            AippLogService aippLogService,
            AippLogStreamService aippLogStreamService,
            @Fit(alias = "json") ObjectSerializer serializer,
            AippModelCenter aippModelCenter,
            PromptBuilderChain promptBuilderChain,
            AppTaskInstanceService appTaskInstanceService,
            McpClientFactory mcpClientFactory) {
        this.flowInstanceService = flowInstanceService;
        this.toolService = toolService;
        this.aippLogService = aippLogService;
        this.aippLogStreamService = aippLogStreamService;
        this.serializer = notNull(serializer, "The serializer cannot be nul.");
        this.aippModelCenter = aippModelCenter;

        // handleTask从入口开始处理，callback从agent node开始处理
        this.agentFlow = AiFlows.<Tip>create()
                .prompt(Prompts.sys(SYSTEM_PROMPT), Prompts.history(), Prompts.human(PROMPT_TEMPLATE))
                .id(AGENT_NODE_ID)
                .delegate(agent)
                .close();
        this.promptBuilderChain = promptBuilderChain;
        this.appTaskInstanceService = appTaskInstanceService;
        this.mcpClientFactory = notNull(mcpClientFactory, "The mcp client factory cannot be null.");
    }

    /**
     * 调用模型接口的实现
     *
     * @param flowData 流程执行上下文数据，包含模型参数、用户问题及技能列表
     * @return 流程执行上下文数据，包含模型执行结果
     */
    @Fitable("modelengine.fit.jober.aipp.fitable.LLMComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        String instId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        log.debug("LLMComponent business data {}", businessData);
        log.info("[perf] [{}] handleTask start, instId={}", System.currentTimeMillis(), instId);
        AippLlmMeta llmMeta = AippLlmMeta.parse(flowData);
        llmCache.put(llmMeta.getInstId(), llmMeta);
        String path = this.aippLogService.buildPath(instId, parentInstId);

        if (!this.checkModelAvailable(businessData)) {
            this.doOnAgentError(llmMeta, "statusCode=500");
            return flowData;
        }

        // 待add多模态，期望使用image的url，当前传入的历史记录里面没有image
        Map<String, Object> toolContext = MapBuilder.<String, Object>get()
                .put(AippConst.PARENT_INSTANCE_ID, llmMeta.getInstId())
                .put(AippConst.CONTEXT_USER_ID, ObjectUtils.cast(businessData.get(AippConst.CONTEXT_USER_ID)))
                .build();
        String systemPrompt = ObjectUtils.cast(businessData.get("systemPrompt"));
        String msgId = UuidUtils.randomUuidString();
        PromptMessage promptMessage = this.buildPromptMessage(systemPrompt, businessData);
        final boolean[] firstTokenFlag = {true};
        llmMeta.setPromptMetadata(promptMessage.getMetadata());
        StreamMsgSender streamMsgSender =
                new StreamMsgSender(this.aippLogStreamService, this.serializer, path, msgId, instId);
        streamMsgSender.sendKnowledge(promptMessage.getMetadata(), businessData);
        ChatOption chatOption = this.buildChatOptions(businessData);
        agentFlow.converse()
                .bind((acc, chunk) -> {
                    if (firstTokenFlag[0]) {
                        log.info("[perf] [{}] converse sendLog start, instId={}, chunk={}", System.currentTimeMillis(),
                                instId, chunk.text());
                        firstTokenFlag[0] = false;
                        streamMsgSender.sendMsg(chunk.text(), businessData);
                        log.info("[perf] [{}] converse sendLog end, instId={}", System.currentTimeMillis(),
                                instId);
                        return;
                    }
                    streamMsgSender.sendMsg(chunk.text(), businessData);
                })
                .bind(new AippMemory(this.getMemoriesByMaxRounds(businessData)))
                .bind(AippConst.TOOL_CONTEXT_KEY, toolContext)
                .doOnConsume(msg -> llmOutputConsumer(llmMeta, msg, promptMessage.getMetadata()))
                .doOnError(throwable -> doOnAgentError(llmMeta,
                        throwable.getCause() == null ? throwable.getMessage() : throwable.getCause().getMessage()))
                .bind(chatOption)
                .bind(AippConst.TOOLS_KEY, chatOption.tools())
                .offer(Tip.fromArray(promptMessage.getSystemMessage(), promptMessage.getHumanMessage()));
        log.info("[perf] [{}] handleTask end, instId={}", System.currentTimeMillis(), instId);
        return flowData;
    }

    private boolean checkModelAvailable(Map<String, Object> businessData) {
        boolean isDebug = ObjectUtils.cast(businessData.getOrDefault("isDebug", false));
        if (!isDebug) {
            return true;
        }
        log.info("Business Data has debug tag.");
        String model = ObjectUtils.cast(businessData.get("model"));
        Map<String, String> accessInfo = ObjectUtils.nullIf(ObjectUtils.cast(businessData.get("accessInfo")),
                MapBuilder.<String, String>get().put("serviceName", model).put("tag", "INTERNAL").build());
        String serviceName = accessInfo.get("serviceName");
        String tag = accessInfo.get("tag");
        log.info("Need to check llm model available: model: {}, tag: {}", serviceName, tag);
        if (!StringUtils.equals(tag, "INTERNAL")) {
            log.info("check external service, default true");
            return true;
        }
        return this.aippModelCenter.fetchModelList(AippConst.CHAT_MODEL_TYPE, null, DataUtils.getOpContext(businessData))
                .getModels()
                .stream()
                .filter(modelAccessInfo -> StringUtils.equals(modelAccessInfo.getTag(), "INTERNAL"))
                .anyMatch(modelAccessInfo -> StringUtils.equals(serviceName, modelAccessInfo.getServiceName()));
    }

    private List<Map<String, String>> getMemoriesByMaxRounds(Map<String, Object> businessData) {
        List<Map<String, String>> memories = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_MEMORIES_KEY));
        Object obj = businessData.get(AippConst.BS_MAX_MEMORY_ROUNDS);
        if (!(obj instanceof Integer)) {
            return memories;
        }
        Integer maxRounds = ObjectUtils.cast(obj);
        int size = memories.size();
        return memories.subList(Math.max(size - maxRounds, 0), size);
    }

    /**
     * 处理agent响应的回调函数。
     * <ul>
     * <li>当模型返回最终结果时，保存数据，同时唤醒流程</li>
     * <li>当模型返回工作流实例id时，通知前端处理工作流，并保存大模型处理元数据</li>
     * </ul>
     *
     * @param llmMeta 表示大模型元数据的 {@link AippLlmMeta}。
     * @param answer 表示模型返回的响应的 {@link ChatMessage}。
     * @param promptMetadata 表示提示词元数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    private void llmOutputConsumer(AippLlmMeta llmMeta, ChatMessage answer, Map<String, Object> promptMetadata) {
        if (answer.type() == MessageType.AI) {
            addAnswer(llmMeta, answer.text(), ObjectUtils.nullIf(promptMetadata, Collections.emptyMap()));
        }
    }

    private void addAnswer(AippLlmMeta llmMeta, String answer, Map<String, Object> promptMetadata) {
        Map<String, Object> businessData = llmMeta.getBusinessData();
        Map<String, Object> output = new HashMap<>();
        output.put("llmOutput", answer);
        output.put("reference", promptMetadata.getOrDefault(PROMPT_METADATA_KEY, Collections.emptyMap()));
        businessData.put("output", output);

        // 如果节点配置为输出到聊天，模型回复内容需要持久化
        boolean enableLog = checkEnableLog(businessData);
        if (enableLog) {
            this.aippLogService.insertLog(AippInstLogType.MSG.name(),
                    AippLogData.builder().msg(answer).build(),
                    businessData);
        }

        // 修改taskInstance.
        AppTaskInstance updateEntity = AppTaskInstance.asUpdate(llmMeta.getVersionId(), llmMeta.getInstId())
                .setLlmOutput(answer)
                .build();
        this.appTaskInstanceService.update(updateEntity,
                JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                        OperationContext.class));
        doOnAgentComplete(llmMeta);
    }

    /**
     * llm节点处理结束回调函数。
     * <ul>
     * <li>当模型返回最终结果时触发</li>
     * <li>当工作流触发回调，但没有返回数据时触发</li>
     * </ul>
     *
     * @param llmMeta 表示大模型元数据的{@link AippLlmMeta}
     */
    private void doOnAgentComplete(AippLlmMeta llmMeta) {
        // 删除cache
        llmCache.remove(llmMeta.getInstId());
        // resumeFlow
        this.flowInstanceService.resumeAsyncJob(llmMeta.getFlowDataId(),
                llmMeta.getBusinessData(),
                llmMeta.getContext());
    }

    private void doOnAgentError(AippLlmMeta llmMeta, String errorMessage) {
        log.error("versionId {} errorMessage {}", llmMeta.getVersionId(), errorMessage);
        JoberErrorInfo joberErrorInfo = new JoberErrorInfo(errorMessage,
                AippErrCode.MODEL_SERVICE_INVOKE_ERROR.getErrorCode(), errorMessage);
        this.failLlmComponentNode(llmMeta, joberErrorInfo);
    }

    private void failLlmComponentNode(final AippLlmMeta llmMeta, final JoberErrorInfo joberErrorInfo) {
        // 修复失败场景未释放cache的问题
        llmCache.remove(llmMeta.getInstId());
        this.flowInstanceService.failAsyncJob(llmMeta.getFlowDataId(),
                joberErrorInfo,
                DataUtils.getOpContext(llmMeta.getBusinessData()));
    }

    private void setErrorCode(JoberErrorInfo joberErrorInfo, int statusCode) {
        switch (ModelErrCode.getErrorCodes(statusCode)) {
            case MODEL_ROUTER_ERROR:
            case MODEL_SERVICE_ERROR:
                joberErrorInfo.setCode(AippErrCode.MODEL_SERVICE_NOT_AVAILABLE.getErrorCode());
                break;
            case REQUEST_PARAM_ERROR:
            case INVALID_REQUEST_PARAM:
                joberErrorInfo.setCode(AippErrCode.MODEL_PARAMETER_ERROR.getErrorCode());
                break;
            default:
                joberErrorInfo.setCode(AippErrCode.MODEL_SERVICE_GENERIC_ERROR.getErrorCode());
                break;
        }
    }

    private PromptMessage buildPromptMessage(String background, Map<String, Object> businessData) {
        Map<String, Object> input = ObjectUtils.cast(businessData.get("prompt"));
        String template = ObjectUtils.cast(input.get("template"));
        Map<String, String> standardInput = ObjectUtils.<Map<String, String>>cast(input.get("variables"))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> this.getStandardValue(entry.getValue())));
        try {
            return this.promptBuilderChain.build(new UserAdvice(background, template, standardInput), businessData)
                    .orElseThrow(() -> new IllegalStateException("Cannot find valid prompt builder."));
        } catch (StringFormatException exception) {
            log.error("build prompt error.", exception);
            throw new AippException(DataUtils.getOpContext(businessData),
                    AippErrCode.LLM_COMPONENT_TEMPLATE_RENDER_FAILED);
        }
    }

    private String getStandardValue(Object value) {
        notNull(value, "The value cannot be null.");
        if (value instanceof String) {
            return ObjectUtils.cast(value);
        }
        if (value instanceof List && ((List<?>) value).stream().allMatch(item -> item instanceof Map)) {
            return ((List<?>) value).stream()
                    .map(obj -> Optional.ofNullable(ObjectUtils.<Map<String, String>>cast(obj).get("text"))
                            .orElseGet(() -> this.serializer.serialize(obj)))
                    .collect(Collectors.joining(AippConst.CONTENT_DELIMITER));
        }
        try {
            return this.serializer.serialize(value);
        } catch (SerializationException exception) {
            log.error("Serialize failed.");
            log.debug("Serialize failed, value: {}.", value.toString());
            return StringUtils.EMPTY;
        }
    }

    /**
     * 获取文件路径。
     *
     * @param businessData 表示工作流执行上下文数据的{@link Map}。
     * @return 返回文件路径。
     * @throws AippException 当数据类型不支持时抛出
     */
    private String getFilePath(Map<String, Object> businessData) {
        if (!businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            return StringUtils.EMPTY;
        }
        Object data = businessData.get(AippConst.BS_AIPP_FILE_DESC_KEY);
        if (!(data instanceof Map)) {
            log.error("The file desc type is not map. [type={}]", data.getClass().getName());
            throw new AippException(AippErrCode.MODEL_NODE_FAILED_TO_PARSE_THE_FILE);
        }
        Map<String, String> fileDesc = ObjectUtils.cast(data);
        if (MapUtils.isEmpty(fileDesc)) {
            return StringUtils.EMPTY;
        }
        return ("filePath: " + fileDesc.get("file_path") + "\n");
    }

    /**
     * 解析表示自定义参数的{@link ChatOption}, 当前支持模型、温度、工具。
     *
     * @param businessData 表示工作流执行上下文数据的{@link Map}。
     * @return 返回表示自定义参数。
     */
    private ChatOption buildChatOptions(Map<String, Object> businessData) {
        String model = ObjectUtils.cast(businessData.get("model"));
        Map<String, String> accessInfo = ObjectUtils.nullIf(ObjectUtils.cast(businessData.get("accessInfo")),
                MapBuilder.<String, String>get().put("serviceName", model).put("tag", "INTERNAL").build());

        RunContext runContext = new RunContext(businessData, new OperationContext());
        String chatId = runContext.getOriginChatId();
        OperationContext opContext = DataUtils.getOpContext(businessData);
        ModelAccessInfo modelAccessInfo = this.aippModelCenter.getModelAccessInfo(accessInfo.get("tag"),
                accessInfo.get("serviceName"), opContext);
        return ChatOption.custom()
                .model(accessInfo.get("serviceName"))
                .baseUrl(modelAccessInfo.getBaseUrl())
                .stream(true)
                .secureConfig(modelAccessInfo.isSystemModel() ? null : SecureConfig.custom().ignoreTrust(true).build())
                .apiKey(modelAccessInfo.getAccessKey())
                .temperature(ObjectUtils.cast(businessData.get("temperature")))
                .tools(this.buildToolInfos(businessData))
                .build();
    }

    private List<ToolInfo> buildToolInfos(Map<String, Object> businessData) {
        List<String> skillNameList = new ArrayList<>(ObjectUtils.cast(businessData.get("tools")));
        if (businessData.containsKey("workflows")) {
            skillNameList.addAll(ObjectUtils.cast(businessData.get("workflows")));
        }
        Map<String, Object> mcpServersConfig = ObjectUtils.cast(businessData.get(AippConst.MCP_SERVERS_KEY));

        return Stream.concat(this.buildToolInfos(skillNameList).stream(),
                this.buildMcpToolInfos(mcpServersConfig).stream()).collect(Collectors.toList());
    }

    private List<ToolInfo> buildMcpToolInfos(Map<String, Object> mcpServersConfig) {
        List<ToolInfo> result = new ArrayList<>();
        ObjectUtils.nullIf(mcpServersConfig, new HashMap<String, Object>()).forEach((serverName, value) -> {
            Map<String, Object> serverConfig = ObjectUtils.cast(value);
            String url = Validation.notBlank(ObjectUtils.cast(serverConfig.get(AippConst.MCP_SERVER_URL_KEY)),
                    "The mcp url should not be empty.");

            try (McpClient mcpClient = this.mcpClientFactory.create(McpUtils.getBaseUrl(url),
                    McpUtils.getSseEndpoint(url))) {
                mcpClient.initialize();
                List<Tool> tools = mcpClient.getTools();
                result.addAll(tools.stream().map(tool -> buildMcpToolInfo(serverName, tool, serverConfig)).toList());
            } catch (IOException exception) {
                throw new AippException(AippErrCode.CALL_MCP_SERVER_FAILED, exception.getMessage());
            }
        });
        return result;
    }

    private List<ToolInfo> buildToolInfos(List<String> skillNameList) {
        return skillNameList.stream()
                .map(this.toolService::getTool)
                .filter(Objects::nonNull)
                .map(this::buildToolInfo)
                .collect(Collectors.toList());
    }

    private ToolInfo buildToolInfo(ToolData toolData) {
        return ToolInfo.custom()
                .name(buildUniqueToolName(AippConst.STORE_SERVER_TYPE,
                        AippConst.STORE_SERVER_NAME,
                        toolData.getUniqueName()))
                .description(toolData.getDescription())
                .parameters(new HashMap<>(toolData.getSchema()))
                .extensions(MapBuilder.<String, Object>get()
                        .put(AippConst.TOOL_REAL_NAME, toolData.getUniqueName())
                        .build())
                .build();
    }

    private static ToolInfo buildMcpToolInfo(String serverName, Tool tool, Map<String, Object> serverConfig) {
        return ToolInfo.custom()
                .name(buildUniqueToolName(AippConst.MCP_SERVER_TYPE, serverName, tool.getName()))
                .description(tool.getDescription())
                .parameters(tool.getInputSchema())
                .extensions(MapBuilder.<String, Object>get()
                        .put(AippConst.MCP_SERVER_KEY, serverConfig)
                        .put(AippConst.TOOL_REAL_NAME, tool.getName())
                        .build())
                .build();
    }

    private static String buildUniqueToolName(String type, String serverName, String toolName) {
        return StringUtils.format("{0}_{1}_{2}", type, serverName, toolName);
    }

    /**
     * 判断是否启用日志。
     *
     * @param businessData 表示业务上下文数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示是否启用日志的 {@code boolean}。
     */
    public static boolean checkEnableLog(Map<String, Object> businessData) {
        Object value = businessData.get(AippConst.BS_LLM_ENABLE_LOG);
        if (value == null) {
            return true;
        }
        return Boolean.parseBoolean(value.toString());
    }

    static class StreamMsgSender {
        private final AippLogStreamService aippLogStreamService;
        private final ObjectSerializer serializer;
        private final String path;
        private final String msgId;
        private final String instId;

        StreamMsgSender(AippLogStreamService aippLogStreamService, ObjectSerializer serializer,
                String path, String msgId, String instId) {
            this.aippLogStreamService = aippLogStreamService;
            this.serializer = serializer;
            this.path = path;
            this.msgId = msgId;
            this.instId = instId;
        }

        /**
         * 发送对话信息。
         *
         * @param msg 表示流式响应片段的 {@link String}。
         * @param businessData 表示流程上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        public void sendMsg(String msg, Map<String, Object> businessData) {
            boolean enableLog = checkEnableLog(businessData);
            if (!enableLog || StringUtils.isBlank(msg) || msg.contains("<tool_call>")) {
                return;
            }
            this.sendMsgHandle(msg, StreamMsgType.from(AippInstLogType.MSG), businessData);
        }

        /**
         * 发送溯源信息。
         *
         * @param promptMetadata 表示提示词元数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         * @param businessData 表示流程上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
         */
        public void sendKnowledge(Map<String, Object> promptMetadata, Map<String, Object> businessData) {
            if (!checkEnableLog(businessData) || !promptMetadata.containsKey(PROMPT_METADATA_KEY)) {
                return;
            }
            String knowledgeData = this.serializer.serialize(promptMetadata.get(PROMPT_METADATA_KEY));
            this.sendMsgHandle(knowledgeData, StreamMsgType.KNOWLEDGE, businessData);
        }

        private void sendMsgHandle(String msg, StreamMsgType logType, Map<String, Object> businessData) {
            RunContext runContext = new RunContext(businessData, new OperationContext());
            String chatId = runContext.getOriginChatId();
            String atChatId = runContext.getAtChatId();
            AippLogData logData = AippLogData.builder().msg(msg).build();
            AippLogVO logVO = AippLogVO.builder()
                    .logData(JsonUtils.toJsonString(logData))
                    .logType(logType.value())
                    .path(this.path)
                    .msgId(this.msgId)
                    .instanceId(this.instId)
                    .chatId(chatId)
                    .atChatId(atChatId)
                    .build();
            this.aippLogStreamService.send(logVO);
        }
    }
}