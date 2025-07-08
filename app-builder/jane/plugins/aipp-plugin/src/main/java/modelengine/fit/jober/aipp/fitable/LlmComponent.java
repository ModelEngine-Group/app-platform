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
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.ToolMessage;
import modelengine.fel.core.model.http.ModelExtraHttpBody;
import modelengine.fel.core.model.http.SecureConfig;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.core.tool.ToolProvider;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fit.jade.aipp.formatter.OutputFormatterChain;
import modelengine.fit.jade.aipp.formatter.support.ResponsibilityResult;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.prompt.PromptMessage;
import modelengine.fit.jade.aipp.prompt.UserAdvice;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippJsonDecodeException;
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
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.spi.FlowCallbackService;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.parameterization.StringFormatException;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.UuidUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * LLM 组件实现
 *
 * @author 黄夏露
 * @since 2024/4/15
 */
@Component
public class LlmComponent implements FlowableService, FlowCallbackService, FlowExceptionService {
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
    private final ToolProvider toolProvider;
    private final AiProcessFlow<Tip, Prompt> agentFlow;
    private final AippLogService aippLogService;
    private final AippLogStreamService aippLogStreamService;
    private final ObjectSerializer serializer;
    private final AippModelCenter aippModelCenter;
    private final PromptBuilderChain promptBuilderChain;
    private final AppTaskInstanceService appTaskInstanceService;
    private final OutputFormatterChain formatterChain;

    /**
     * 大模型节点构造器，内部通过提供的 agent 和 tool 构建智能体工作流。
     *
     * @param flowInstanceService 表示流程实例服务的 {@link FlowInstanceService}。
     * @param toolProvider 表示具提供者功能的 {@link ToolProvider}。
     * @param agent 表示提供智能体功能的 {@link AbstractAgent}{@code <}{@link ChatMessages}{@code ,
     * }{@link ChatMessages}{@code >}。
     * @param aippLogService 表示提供日志服务的 {@link AippLogService}。
     * @param aippLogStreamService 表示提供日志流服务的 {@link AippLogStreamService}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @param aippModelCenter 表示模型中心的 {@link AippModelCenter}。
     * @param promptBuilderChain 表示提示器构造器职责链的 {@link PromptBuilderChain}。
     * @param appTaskInstanceService 表示任务实例服务的 {@link AppTaskInstanceService}。
     */
    public LlmComponent(FlowInstanceService flowInstanceService,
            ToolProvider toolProvider,
            @Fit(alias = AippConst.WATER_FLOW_AGENT_BEAN) AbstractAgent<Prompt, Prompt> agent,
            AippLogService aippLogService,
            AippLogStreamService aippLogStreamService,
            @Fit(alias = "json") ObjectSerializer serializer,
            AippModelCenter aippModelCenter,
            PromptBuilderChain promptBuilderChain,
            AppTaskInstanceService appTaskInstanceService, OutputFormatterChain formatterChain) {
        this.flowInstanceService = flowInstanceService;
        this.toolProvider = toolProvider;
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
        this.formatterChain = formatterChain;
    }

    /**
     * 工作流回调大模型节点的接口实现。
     *
     * @param childFlowData 工作流上下文信息，需要包含子流程的输出结果和主流程的instId。
     */
    @Fitable(WORKFLOW_CALLBACK_FITABLE_ID)
    @Override
    public void callback(List<Map<String, Object>> childFlowData) {
        Map<String, Object> childBusinessData = DataUtils.getBusiness(childFlowData);
        log.debug("LLMComponentCallback business data {}", childBusinessData);
        String toolOutput = ObjectUtils.cast(childBusinessData.get(AippConst.BS_AIPP_FINAL_OUTPUT));
        String parentInstanceId = ObjectUtils.cast(childBusinessData.get(AippConst.PARENT_INSTANCE_ID));
        AippLlmMeta llmMeta = llmCache.get(parentInstanceId);
        Map<String, Object> promptMetadata = ObjectUtils.nullIf(llmMeta.getPromptMetadata(), Collections.emptyMap());
        if (!ObjectUtils.<Boolean>cast(childBusinessData.get(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM))) {
            Map<String, Object> businessData = llmMeta.getBusinessData();
            Map<String, Object> output = new HashMap<>();
            //  当前如果子流程不需要模型加工，子流程和主流程会重复打印 toolOutput。
            //  为了避免这种情况，临时设置一个 key 来表明结果是否来自子流程。
            //  如果结果来自子流程，主流程的结束节点不打印；否则主流程的结束节点打印。
            output.put("llmOutput", toolOutput);
            output.put("reference", promptMetadata.getOrDefault(PROMPT_METADATA_KEY, Collections.emptyMap()));
            businessData.put("output", output);
            doOnAgentComplete(llmMeta);
            return;
        }
        // 暂时原地修改，之后再看是否需要创建新的；子流结束再经过模型的情况还未验证过
        ChatMessages chatMessages = ChatMessages.from(llmMeta.getTrace().messages());
        ChatMessage lastMessage = chatMessages.messages().remove(chatMessages.messages().size() - 1);
        chatMessages.add(new ToolMessage(lastMessage.id().orElse(null), toolOutput));
        llmMeta.setTrace(chatMessages);
        String msgId = UuidUtils.randomUuidString();
        String path = this.aippLogService.getParentPath(parentInstanceId);
        StreamMsgSender streamMsgSender =
                new StreamMsgSender(this.aippLogStreamService, this.serializer, path, msgId, parentInstanceId);
        agentFlow.converse()
                .bind((acc, chunk) -> streamMsgSender.sendMsg(chunk.text(), childBusinessData))
                .doOnSuccess(msg -> llmOutputConsumer(llmMeta, ObjectUtils.cast(msg), promptMetadata))
                .doOnError(throwable -> doOnAgentError(llmMeta,
                        throwable.getCause() == null ? throwable.getMessage() : throwable.getCause().getMessage()))
                .offer(AGENT_NODE_ID, Collections.singletonList(chatMessages));
    }

    @Fitable(WORKFLOW_EXCEPTION_FITABLE_ID)
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> contexts, FlowErrorInfo errorInfo) {
        // 工具流执行失败时，将大模型对应节点同步设置为失败
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        AippLlmMeta llmMeta = this.llmCache.get(parentInstanceId);
        if (llmMeta == null) {
            log.error("Can not find the llm meta, instanceId={}, parentInstanceId={}", instanceId, parentInstanceId);
            return;
        }
        String toolUniqueName = ObjectUtils.cast(businessData.get(TOOL_UNIQUE_NAME));
        List<ToolInfo> toolInfoList = this.toolProvider.getTool(Collections.singletonList(toolUniqueName));
        String toolName = toolInfoList.isEmpty()
                ? toolUniqueName
                : ObjectUtils.cast(toolInfoList.get(0).parameters().getOrDefault(TOOL_NAME, toolUniqueName));
        String parentFlowDataId = llmMeta.getFlowDataId();
        log.info("[LlmComponent] handle exception start. instanceId={}, parentInstanceId={}, parentFlowDataId={}",
                instanceId, parentInstanceId, parentFlowDataId);
        this.failLlmComponentNode(llmMeta,
                new JoberErrorInfo(StringUtils.format("[{0}] {1}", toolName, errorInfo.getErrorMessage()),
                        errorInfo.getErrorCode(), errorInfo.getArgs()));
        log.info("[LlmComponent] handle exception end. instanceId={}, parentInstanceId={}, parentFlowDataId={}",
                instanceId, parentInstanceId, parentFlowDataId);
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
                .doOnSuccess(msg -> llmOutputConsumer(llmMeta, msg, promptMessage.getMetadata()))
                .doOnError(throwable -> doOnAgentError(llmMeta,
                        throwable.getCause() == null ? throwable.getMessage() : throwable.getCause().getMessage()))
                .bind(buildChatOptions(businessData))
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
     * @param llmMeta 表示大模型元数据的{@link AippLlmMeta}。
     * @param trace 表示模型返回的响应的{@link Prompt}。
     * @param promptMetadata 表示提示词元数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    private void llmOutputConsumer(AippLlmMeta llmMeta, Prompt trace, Map<String, Object> promptMetadata) {
        ChatMessage answer = trace.messages().get(trace.messages().size() - 1);
        if (answer.type() == MessageType.AI) {
            List<ChatMessage> messages = trace.messages();
            int humanIndex = this.lastHumanIndex(messages);
            String text = trace.messages().stream()
                    .skip(humanIndex)
                    .filter(message -> message.type() == MessageType.AI)
                    .map(ChatMessage::text)
                    .collect(Collectors.joining());
            addAnswer(llmMeta, text, ObjectUtils.nullIf(promptMetadata, Collections.emptyMap()));
            return;
        }
        // 还没保存trace数据，子流程就跑完了怎么办？（目前走到这里一定有表单阻塞，所以暂时不会有这个问题）
        llmMeta.setTrace(trace);
        try {
            String childInstanceId = JsonUtils.parseObject(answer.text(), String.class);
            this.setChildInstanceId(llmMeta, childInstanceId);
        } catch (AippJsonDecodeException e) {
            this.doOnAgentError(llmMeta, e.getMessage());
        }
    }

    private int lastHumanIndex(List<ChatMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (MessageType.HUMAN == messages.get(i).type()) {
                return i;
            }
        }
        return 0;
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
            Map<String, Object> llmOutput = new HashMap<>();
            llmOutput.put("output", output);
            Optional<ResponsibilityResult> formatOutput = this.formatterChain.handle(llmOutput);
            String logMsg = formatOutput.map(ResponsibilityResult::text).orElse(answer);
            this.aippLogService.insertLogWithInterception(AippInstLogType.META_MSG.name(),
                    AippLogData.builder().msg(logMsg).build(),
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

    private void setChildInstanceId(AippLlmMeta llmMeta, String childInstanceId) {
        // 修改taskInstance.
        AppTaskInstance updateEntity = AppTaskInstance.asUpdate(llmMeta.getVersionId(), llmMeta.getInstId())
                .setChildInstanceId(childInstanceId)
                .build();
        this.appTaskInstanceService.update(updateEntity, llmMeta.getContext());
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
            throw new AippException(AippErrCode.MODEL_NODE_FAILED_TO_PARSE_THE_FILE, data.getClass().getName());
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
        List<String> skillNameList = new ArrayList<>(ObjectUtils.cast(businessData.get("tools")));
        if (businessData.containsKey("workflows")) {
            skillNameList.addAll(ObjectUtils.cast(businessData.get("workflows")));
        }
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
                .secureConfig(modelAccessInfo.isSystemModel() ? null : SecureConfig.custom().ignoreTrust(true).build())
                .apiKey(modelAccessInfo.getAccessKey())
                .temperature(ObjectUtils.cast(businessData.get("temperature")))
                .tools(this.toolProvider.getTool(skillNameList))
                .user(opContext.getOperator())
                .extras(Collections.singletonList(new ModelExtraHttpBody(new ModelExtraBody(chatId))))
                .build();
    }

    private static class ModelExtraBody {
        @Property(name = "session_id")
        private String sessionId;

        public ModelExtraBody(String sessionId) {
            if (!UuidUtils.isUuidString(sessionId, true)) {
                throw new IllegalArgumentException("Invalid session id. It should be 32 characters without hyphens.");
            }
            // 下层要求是一个标准的 uuid，理论上不应该有这个限制，后续应该可以放开，目前暂时做一次标准化
            this.sessionId =
                    sessionId.substring(0, 8) + "-" + sessionId.substring(8, 12) + "-" + sessionId.substring(12, 16)
                            + "-" + sessionId.substring(16, 20) + "-" + sessionId.substring(20);
        }
    }

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