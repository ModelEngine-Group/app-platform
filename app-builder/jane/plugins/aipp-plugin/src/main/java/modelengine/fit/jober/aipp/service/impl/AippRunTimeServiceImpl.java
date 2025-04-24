/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippExceptionHandler.LOCALES;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_FLOW_DEF_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_INPUT_KEY;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.dynamicform.entity.DynamicFormDetailEntity;
import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jane.task.domain.type.DateTimeConverter;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.condition.AippInstanceQueryCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.AippInstanceCreateDto;
import modelengine.fit.jober.aipp.dto.AippInstanceDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppStartDto;
import modelengine.fit.jober.aipp.dto.AppInputParam;
import modelengine.fit.jober.aipp.dto.MemoryConfigDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.entity.StartChatParam;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.MetaInstSortKeyEnum;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.enums.RestartModeEnum;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.AippLogUtils;
import modelengine.fit.jober.aipp.util.AppUtils;
import modelengine.fit.jober.aipp.util.CacheUtils;
import modelengine.fit.jober.aipp.util.FlowUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.MetaInstanceUtils;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fit.jober.aipp.vo.MetaVo;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.entity.FlowInstanceResult;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fit.waterflow.entity.FlowStartInfo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.globalization.LocaleService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * aipp运行时服务层接口实现
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
@Component
public class AippRunTimeServiceImpl
        implements AippRunTimeService, modelengine.fit.jober.aipp.genericable.AippRunTimeService {
    private static final String DEFAULT_QUESTION = "请解析以下文件。";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private static final Logger log = Logger.get(AippRunTimeServiceImpl.class);

    private static final String DOWNLOAD_FILE_ORIGIN = "/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?";

    private static final String NAME_KEY = "name";

    private static final String VALUE_KEY = "value";

    private static final String TYPE_KEY = "type";

    private static final String PARENT_CALLBACK_ID = "modelengine.fit.jober.aipp.fitable.LLMComponentCallback";

    private static final String UI_WORD_KEY = "aipp.service.impl.AippRunTimeServiceImpl";

    private static final List<String> MEMORY_MSG_TYPE_WHITE_LIST =
            Arrays.asList(AippInstLogType.MSG.name(), AippInstLogType.FORM.name(), AippInstLogType.META_MSG.name());

    private final MetaService metaService;

    private final MetaInstanceService metaInstanceService;

    private final FlowInstanceService flowInstanceService;

    private final AippLogService aippLogService;

    private final BrokerClient client;

    private final FlowsService flowsService;

    private final String appEngineUrl;

    private final AopAippLogService aopAippLogService;

    private final AppChatSseService appChatSSEService;

    private final AippLogService logService;

    private final AppChatSessionService appChatSessionService;

    private final HttpClassicClientFactory httpClientFactory;

    private final AppBuilderAppFactory appFactory;

    private final LocaleService localeService;

    private final RuntimeInfoService runtimeInfoService;

    public AippRunTimeServiceImpl(@Fit MetaService metaService, @Fit MetaInstanceService metaInstanceService,
            @Fit FlowInstanceService flowInstanceService, @Fit AippLogService aippLogService, BrokerClient client,
            @Fit FlowsService flowsService, @Value("${app-engine.endpoint}") String appEngineUrl,
            @Fit AopAippLogService aopAippLogService, @Fit AppChatSseService appChatSSEService,
            @Fit AippLogService logService, AppChatSessionService appChatSessionService,
            @Fit HttpClassicClientFactory httpClientFactory, @Fit AppBuilderAppFactory appFactory,
            @Fit LocaleService localeService, @Fit RuntimeInfoService runtimeInfoService) {
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.flowInstanceService = flowInstanceService;
        this.aippLogService = aippLogService;
        this.client = client;
        this.flowsService = flowsService;
        this.appEngineUrl = appEngineUrl;
        this.aopAippLogService = aopAippLogService;
        this.appChatSessionService = appChatSessionService;
        this.httpClientFactory = httpClientFactory;
        this.appChatSSEService = appChatSSEService;
        this.logService = logService;
        this.appFactory = appFactory;
        this.runtimeInfoService = runtimeInfoService;
        this.localeService = localeService;
    }

    private static void setExtraBusinessData(OperationContext context, Map<String, Object> businessData, Meta meta,
            String instId) {
        businessData.put(AippConst.BS_AIPP_ID_KEY, meta.getId());
        businessData.put(AippConst.BS_AIPP_VERSION_KEY, meta.getVersion());
        businessData.put(AippConst.BS_META_VERSION_ID_KEY, meta.getVersionId());
        businessData.put(AippConst.ATTR_AIPP_TYPE_KEY,
                meta.getAttributes().getOrDefault(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.name()));
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, instId);
        businessData.put(AippConst.BS_HTTP_CONTEXT_KEY, JsonUtils.toJsonString(context));
    }

    private InstanceDeclarationInfo genMetaInstInitialInfo(Map<String, Object> businessData, String creator) {
        return InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_NAME_KEY, businessData.getOrDefault(AippConst.INST_NAME_KEY, "无标题"))
                .putInfo(AippConst.INST_CREATOR_KEY, creator)
                .putInfo(AippConst.INST_CREATE_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.RUNNING.name())
                .putInfo(AippConst.INST_PROGRESS_KEY, "0")
                .build();
    }

    /**
     * 启动一个Aipp
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @return 实例id
     */
    @Override
    @Fitable("default")
    public String createAippInstance(String aippId, String version, Map<String, Object> initContext,
            OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        return createInstanceHandle(initContext, context, meta);
    }

    /**
     * 根据 App 唯一标识启动一个最新的 Aipp 实例。
     *
     * @param appId App 唯一标识。
     * @param isDebug 是否调试启动。
     * @param initContext 流程初始化的businessData。
     * @param context 操作上下文。
     * @return Aipp 实例唯一标识。
     */
    @Override
    @Fitable("default")
    public String createLatestAippInstanceByAppId(String appId, boolean isDebug, Map<String, Object> initContext,
            OperationContext context) {
        Meta meta = CacheUtils.getMetaByAppId(metaService, appId, isDebug, context);
        String metaInstId = this.createAippInstance(meta.getId(), meta.getVersion(), initContext, context);
        return metaInstanceService.list(Collections.singletonList(metaInstId), 0, 1, context)
                .getResults()
                .get(0)
                .getInfo()
                .get("flow_trans_id");
    }

    @Override
    @Fitable("default")
    public Boolean isInstanceRunning(String instanceId, OperationContext context) {
        String versionId = this.metaInstanceService.getMetaVersionId(instanceId);

        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        Map<String, String> instInfo = instDetail.getInfo();
        if (!instInfo.containsKey(AippConst.INST_STATUS_KEY)) {
            return false;
        }
        return MetaInstStatusEnum.getMetaInstStatus(instInfo.get(AippConst.INST_STATUS_KEY))
                == MetaInstStatusEnum.RUNNING;
    }

    /**
     * 启动一个运行Aipp
     *
     * @param appId appId
     * @param question 会话问题
     * @param businessData 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @param isDebug 是否为debug会话
     * @return 实例id
     */
    @Override
    public Tuple createInstanceByApp(String appId, String question, Map<String, Object> businessData,
            OperationContext context, boolean isDebug) {
        Meta meta = CacheUtils.getMetaByAppId(metaService, appId, isDebug, context);
        return this.createInstanceHandle(question, businessData, meta, context, isDebug);
    }

    @Override
    public MetaVo queryLatestMetaVoByAppId(String appId, boolean isDebug, OperationContext context) {
        Meta meta = CacheUtils.getMetaByAppId(metaService, appId, isDebug, context);
        return MetaVo.builder().id(meta.getId()).version(meta.getVersion()).build();
    }

    @Override
    public Choir<Object> startFlowWithUserSelectMemory(String metaInstId, Map<String, Object> initContext,
            OperationContext context, boolean isDebug) {
        String versionId = this.metaInstanceService.getMetaVersionId(metaInstId);
        Meta meta = this.metaService.retrieve(versionId, context);
        Map<String, Object> businessData = ObjectUtils.cast(initContext.get(AippConst.BS_INIT_CONTEXT_KEY));
        businessData.put("startNodeInputParams", JSONObject.parse(JSONObject.toJSONString(businessData)));
        String flowDefinitionId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
        setExtraBusinessData(context, businessData, meta, metaInstId);
        String path = this.logService.getParentPath(metaInstId);
        String parentInstanceId =
                (StringUtils.isNotEmpty(path)) ? path.split(AippLogUtils.PATH_DELIMITER)[1] : metaInstId;
        businessData.putIfAbsent(AippConst.PARENT_INSTANCE_ID, parentInstanceId);
        businessData.putIfAbsent(AippConst.PARENT_CALLBACK_ID, PARENT_CALLBACK_ID);
        businessData.putIfAbsent(AippConst.CONTEXT_USER_ID, context.getOperator());

        Locale locale = getLocale();
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));

        return Choir.create(emitter -> {
            try {
                this.appChatSessionService.addSession(parentInstanceId,
                        new ChatSession<>(emitter, appId, isDebug, locale));
                this.startFlow(versionId, flowDefinitionId, metaInstId, businessData, context);
                this.sendReadyStatus(metaInstId, businessData);
            } catch (AippException e) {
                this.updateInstanceStatusError(versionId, metaInstId, context);
                // 更新日志类型为HIDDEN_FORM
                aippLogService.insertLog(AippInstLogType.ERROR.name(),
                        AippLogData.builder().msg(e.getMessage()).build(),
                        businessData);
            }
        });
    }

    /**
     * 启动一个最新版本的Aipp
     *
     * @param aippId aippId
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @return 实例响应
     */
    @Override
    public AippInstanceCreateDto createAippInstanceLatest(String aippId, Map<String, Object> initContext,
            OperationContext context) {
        Meta meta = MetaUtils.getLastPublishedMeta(metaService, aippId, context);
        String instId = createInstanceHandle(initContext, context, meta);
        return AippInstanceCreateDto.builder()
                .instanceId(instId)
                .version(meta.getVersion())
                .versionId(meta.getVersionId())
                .build();
    }

    private String createInstanceHandle(Map<String, Object> initContext, OperationContext context, Meta meta) {
        Map<String, Object> businessData = (Map<String, Object>) initContext.get(AippConst.BS_INIT_CONTEXT_KEY);
        businessData.put("startNodeInputParams",
                JSONObject.parse(JSONObject.toJSONString(initContext.get(AippConst.BS_INIT_CONTEXT_KEY))));
        String restartMode = ObjectUtils.cast(businessData.getOrDefault(AippConst.RESTART_MODE,
                RestartModeEnum.OVERWRITE.getMode()));
        businessData.put(AippConst.RESTART_MODE, restartMode);
        // 记录启动时间
        businessData.put(AippConst.INSTANCE_START_TIME, LocalDateTime.now());

        // 创建meta实例
        String metaVersionId = meta.getVersionId();
        Instance metaInst = this.metaInstanceService.createMetaInstance(metaVersionId,
                genMetaInstInitialInfo(businessData, context.getOperator()),
                context);
        String metaInstId = metaInst.getId();
        setExtraBusinessData(context, businessData, meta, metaInstId);

        log.info("[perf] [{}] createInstanceHandle persistAippLog start, metaInstId={}",
                System.currentTimeMillis(),
                metaInstId);
        // 持久化日志
        String flowDefinitionId = ObjectUtils.cast(meta.getAttributes().get(ATTR_FLOW_DEF_ID_KEY));
        this.persistAippLog(businessData, flowDefinitionId, context);
        log.info("[perf] [{}] createInstanceHandle persistAippLog end, metaInstId={}",
                System.currentTimeMillis(),
                metaInstId);

        // 持久化aipp实例表单记录
        this.persistAippFormLog(meta, businessData);
        log.info("[perf] [{}] createInstanceHandle persistAippFormLog end, metaInstId={}",
                System.currentTimeMillis(),
                metaInstId);

        // 记录上下文
        this.recordContext(context, meta, businessData, metaInst);
        log.info("[perf] [{}] createInstanceHandle recordContext end, metaInstId={}",
                System.currentTimeMillis(),
                metaInstId);

        // 添加memory
        this.startFlowWithMemoryOrNot(businessData, meta, context, metaInst);
        log.info("[perf] [{}] createInstanceHandle startFlowWithMemoryOrNot end, metaInstId={}",
                System.currentTimeMillis(),
                metaInstId);
        return metaInstId;
    }

    private Tuple createInstanceHandle(String question, Map<String, Object> businessData, Meta meta,
            OperationContext context, boolean isDebug) {
        businessData.put("startNodeInputParams", JSONObject.parse(JSONObject.toJSONString(businessData)));

        businessData.put(AippConst.RESTART_MODE,
                businessData.getOrDefault(AippConst.RESTART_MODE, RestartModeEnum.OVERWRITE.getMode()));
        // 记录启动时间
        businessData.put(AippConst.INSTANCE_START_TIME, LocalDateTime.now());

        // 创建meta实例
        Instance metaInst = this.metaInstanceService.createMetaInstance(meta.getVersionId(),
                genMetaInstInitialInfo(businessData, context.getOperator()),
                context);
        AippRunTimeServiceImpl.setExtraBusinessData(context, businessData, meta, metaInst.getId());

        // 持久化日志
        businessData.put(AippConst.BS_AIPP_QUESTION_KEY, question);

        Locale locale = getLocale();
        String flowDefinitionId = ObjectUtils.cast(meta.getAttributes().get(ATTR_FLOW_DEF_ID_KEY));
        List<Object> appChatInfo = AppUtils.getAndRemoveAppChatInfo();
        String appid = ObjectUtils.cast(appChatInfo.get(0));

        return Tuple.duet(metaInst.getId(), Choir.create(emitter -> {
            ChatSession<Object> chatEmitter = new ChatSession<>(emitter, appid, isDebug, locale);
            startChat(new StartChatParam(businessData, meta, context, metaInst, flowDefinitionId, chatEmitter));
        }));
    }

    private Locale getLocale() {
        Locale locale = Locale.getDefault();
        if (UserContextHolder.get() != null && StringUtils.isNotEmpty(UserContextHolder.get().getLanguage())) {
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(UserContextHolder.get().getLanguage());
            locale = CollectionUtils.isEmpty(list) ? Locale.getDefault() : Locale.lookup(list, LOCALES);
        }
        return locale;
    }

    private void startChat(StartChatParam param) {
        try {
            this.appChatSessionService.addSession(param.metaInst().getId(), param.chatSession());

            log.info("[perf] [{}] startChat persistAippLog start, metaInstId={}",
                    System.currentTimeMillis(),
                    param.metaInst().getId());
            this.persistAippLog(param.businessData(), param.flowDefinitionId(), param.context());

            log.info("[perf] [{}] startChat persistAippLog end, metaInstId={}",
                    System.currentTimeMillis(),
                    param.metaInst().getId());
            // 持久化aipp实例表单记录
            this.persistAippFormLog(param.meta(), param.businessData());
            log.info("[perf] [{}] startChat persistAippFormLog end, metaInstId={}",
                    System.currentTimeMillis(),
                    param.metaInst().getId());

            // 记录上下文
            this.recordContext(param.context(), param.meta(), param.businessData(), param.metaInst());
            log.info("[perf] [{}] startChat recordContext end, metaInstId={}",
                    System.currentTimeMillis(),
                    param.metaInst().getId());

            this.startFlowWithMemoryOrNot(param.businessData(), param.meta(), param.context(), param.metaInst());
            log.info("[perf] [{}] startChat startFlowWithMemoryOrNot end, metaInstId={}",
                    System.currentTimeMillis(),
                    param.metaInst().getId());
        } catch (Exception e) {
            log.error("Error occurs when starting a chat:", e);
            this.updateInstanceStatusError(param.meta().getVersionId(), param.metaInst().getId(), param.context());
            String msg = this.localeService.localize(UI_WORD_KEY);
            aippLogService.insertLog(AippInstLogType.ERROR.name(),
                    AippLogData.builder().msg(msg).build(),
                    param.businessData());
        }
    }

    private void startFlowWithMemoryOrNot(Map<String, Object> businessData, Meta meta, OperationContext context,
            Instance metaInst) {
        this.appChatSSEService.send(metaInst.getId(),
                AppChatRsp.builder()
                        .instanceId(metaInst.getId())
                        .status(FlowTraceStatus.READY.name())
                        .atChatId(ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID)))
                        .chatId(ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID)))
                        .build());
        String flowDefinitionId = cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));
        List<Map<String, Object>> memoryConfigs = this.getMemoryConfigs(meta, flowDefinitionId, context);
        boolean isMemorySwitch = this.getMemorySwitch(memoryConfigs, businessData);
        String memoryType = this.getMemoryType(memoryConfigs);
        if (!isMemorySwitch && !StringUtils.equalsIgnoreCase("UserSelect", memoryType)) {
            businessData.put(AippConst.BS_AIPP_MEMORIES_KEY, new ArrayList<>());
        }
        if (!isMemorySwitch) {
            this.startFlow(meta.getVersionId(), flowDefinitionId, metaInst.getId(), businessData, context);
            this.sendReadyStatus(metaInst.getId(), businessData);
            return;
        }
        if (!StringUtils.equalsIgnoreCase("UserSelect", memoryType)) {
            String memoryChatId = ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID));
            String aippType = ObjectUtils.cast(meta.getAttributes()
                    .getOrDefault(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.name()));
            businessData.put(AippConst.BS_AIPP_MEMORIES_KEY,
                    this.getMemories(meta.getId(),
                            memoryType,
                            memoryChatId,
                            memoryConfigs,
                            aippType,
                            businessData,
                            context));
            this.startFlow(meta.getVersionId(), flowDefinitionId, metaInst.getId(), businessData, context);
            this.sendReadyStatus(metaInst.getId(), businessData);
        } else {
            MemoryConfigDto dto = this.buildMemoryConfigDto(businessData, metaInst.getId(), "UserSelect");
            this.appChatSSEService.sendToAncestorLastData(metaInst.getId(), dto);
        }
    }

    private void sendReadyStatus(String metaInstId, Map<String, Object> businessData) {
        this.appChatSSEService.send(metaInstId,
                AppChatRsp.builder()
                        .instanceId(metaInstId)
                        .status(FlowTraceStatus.READY.name())
                        .atChatId(ObjectUtils.cast(businessData.get(AippConst.BS_AT_CHAT_ID)))
                        .chatId(ObjectUtils.cast(businessData.get(AippConst.BS_CHAT_ID)))
                        .build());
    }

    private void recordContext(OperationContext context, Meta meta, Map<String, Object> businessData,
            Instance metaInst) {
        businessData.put(AippConst.CONTEXT_APP_ID, meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        businessData.put(AippConst.CONTEXT_INSTANCE_ID, metaInst.getId());
        businessData.put(AippConst.CONTEXT_USER_ID, context.getOperator());
        if (businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            List<Map<String, String>> fileDescription =
                    ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_FILE_DESC_KEY));
            List<String> fileUrls =
                    fileDescription.stream().map(fileDesc -> fileDesc.get("file_url")).collect(Collectors.toList());
            businessData.put(AippConst.BS_AIPP_FILES_DOWNLOAD_KEY, fileUrls);
            businessData.put(AippConst.BS_AIPP_FILE_DOWNLOAD_KEY, fileUrls.get(0));
        }
    }

    private void persistAippFormLog(Meta meta, Map<String, Object> businessData) {
        String formId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_START_FORM_ID_KEY));
        String formVersion = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_START_FORM_VERSION_KEY));
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp app = this.appFactory.create(appId);
        if (StringUtils.isNotEmpty(formId) && StringUtils.isNotEmpty(formVersion)) {
            AippLogData logData =
                    FormUtils.buildLogDataWithFormData(app.getFormProperties(), formId, formVersion, businessData);
            aippLogService.insertLog(AippInstLogType.FORM.name(), logData, businessData);
        }
    }

    private boolean getMemorySwitch(List<Map<String, Object>> memoryConfig, Map<String, Object> businessData) {
        if (memoryConfig == null || memoryConfig.isEmpty()) {
            return false;
        }
        Map<String, Object> memorySwitchConfig = memoryConfig.stream()
                .filter(config -> StringUtils.equals(ObjectUtils.cast(config.get(NAME_KEY)),
                        AippConst.MEMORY_SWITCH_KEY))
                .findFirst()
                .orElse(null);
        if (memorySwitchConfig == null) {
            return false;
        }
        Boolean shouldUseMemory = ObjectUtils.cast(businessData.getOrDefault(AippConst.BS_AIPP_USE_MEMORY_KEY,
                memorySwitchConfig.get(VALUE_KEY)));
        businessData.put(AippConst.BS_AIPP_USE_MEMORY_KEY, shouldUseMemory);
        return shouldUseMemory;
    }

    private String getMemoryType(List<Map<String, Object>> memoryConfigs) {
        if (memoryConfigs == null || memoryConfigs.isEmpty()) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> typeConfig = memoryConfigs.stream()
                .filter(config -> StringUtils.equals(ObjectUtils.<String>cast(config.get(NAME_KEY)), TYPE_KEY))
                .findFirst()
                .orElseThrow(() -> new AippException(AippErrCode.PARSE_MEMORY_CONFIG_FAILED));
        return ObjectUtils.cast(typeConfig.get(VALUE_KEY));
    }

    private List<Map<String, Object>> getMemoryConfigs(Meta meta, String flowDefinitionId, OperationContext context) {
        FlowInfo flowInfo;
        try {
            flowInfo = MetaUtils.isPublished(meta) ? CacheUtils.getPublishedFlowWithCache(this.flowsService,
                    flowDefinitionId,
                    context) : this.flowsService.getFlows(flowDefinitionId, context);
        } catch (JobberException e) {
            log.error("get flow failed, flowDefinitionId {}", flowDefinitionId);
            throw new AippException(context, AippErrCode.OBTAIN_APP_ORCHESTRATION_INFO_FAILED);
        }
        return flowInfo.getInputParamsByName(AippConst.MEMORY_CONFIG_KEY);
    }

    private MemoryConfigDto buildMemoryConfigDto(Map<String, Object> initContext, String instanceId, String memory) {
        return MemoryConfigDto.builder().initContext(initContext).instanceId(instanceId).memory(memory).build();
    }

    private void startFlow(String metaVersionId, String flowDefinitionId, String metaInstId,
            Map<String, Object> businessData, OperationContext context) {
        FlowStartInfo parameter = new FlowStartInfo(context.getOperator(), null, businessData);

        FlowInstanceResult flowInst;
        try {
            flowInst = flowInstanceService.startFlow(flowDefinitionId, parameter, context);
        } catch (JobberException e) {
            log.error("start flow failed, flowDefinitionId: {}", flowDefinitionId);
            throw new AippException(context, AippErrCode.APP_CHAT_WAIT_RESPONSE_ERROR);
        }

        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_FLOW_INST_ID_KEY, flowInst.getId()).build();
        // 记录流程实例id到meta实例
        this.metaInstanceService.patchMetaInstance(metaVersionId, metaInstId, info, context);
    }

    private void persistAippLog(Map<String, Object> businessData, String flowDefinitionId, OperationContext context) {
        String question = ObjectUtils.<String>cast(businessData.get(AippConst.BS_AIPP_QUESTION_KEY));
        List<Map<String, String>> fileDescList = businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)
                ? ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_FILE_DESC_KEY))
                : Collections.emptyList();
        // 持久化日志
        if (CollectionUtils.isEmpty(fileDescList)) {
            if (this.isIncreamentMode(businessData)) {
                // 如果是处于增长式的重新对话中，插入 hidden_question
                aippLogService.insertLog(AippInstLogType.HIDDEN_QUESTION.name(),
                        AippLogData.builder().msg(question).build(),
                        businessData);
            } else {
                // 插入question日志
                Map<String, Object> infos = this.buildLogInfos(businessData, flowDefinitionId, context);
                aippLogService.insertLog(AippInstLogType.QUESTION.name(),
                        AippLogData.builder().msg(question).infos(infos).build(),
                        businessData);
            }
        } else {
            JSONObject msgJsonObj = new JSONObject();
            msgJsonObj.put("question", question);
            msgJsonObj.put("files", fileDescList);
            aippLogService.insertLog(AippInstLogType.QUESTION_WITH_FILE.name(),
                    AippLogData.builder().msg(msgJsonObj.toJSONString()).build(),
                    businessData);
            businessData.put(AippConst.BS_AIPP_QUESTION_KEY, question);
        }
    }

    private Map<String, Object> buildLogInfos(Map<String, Object> businessData, String flowDefinitionId,
            OperationContext context) {
        List<String> names = FlowUtils.getAppInputParams(this.flowsService, flowDefinitionId, context)
                .stream()
                .map(AppInputParam::getName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(names)) {
            return new HashMap<>();
        }
        Map<String, Object> inputParams = new HashMap<>();
        businessData.entrySet()
                .stream()
                .filter(data -> names.contains(data.getKey()))
                .forEach(data -> inputParams.put(data.getKey(), data.getValue()));
        Map<String, Object> infos = new HashMap<>();
        infos.put(BUSINESS_INPUT_KEY, inputParams);
        return infos;
    }

    private boolean isIncreamentMode(Map<String, Object> businessData) {
        return StringUtils.equals(RestartModeEnum.INCREMENT.getMode(),
                ObjectUtils.cast(businessData.get(AippConst.RESTART_MODE)));
    }

    private boolean isChildInstance(Map<String, Object> businessData) {
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String parentCallbackId = ObjectUtils.cast(businessData.get(AippConst.PARENT_CALLBACK_ID));
        return StringUtils.isNotEmpty(parentInstanceId) && StringUtils.isNotEmpty(parentCallbackId);
    }

    private boolean checkInstanceStatus(String aippId, Instance instDetail, Function<String, Boolean> handler) {
        Map<String, String> instInfo = instDetail.getInfo();
        if (!instInfo.containsKey(AippConst.INST_STATUS_KEY)) {
            log.error("aipp {} inst{} status key not found.", aippId, instDetail.getId());
            return false;
        }
        String status = instInfo.get(AippConst.INST_STATUS_KEY);
        return handler.apply(status);
    }

    private List<Map<String, Object>> getMemories(String aippId, String memoryType, String chatId,
            List<Map<String, Object>> memoryConfigs, String aippType, Map<String, Object> businessData,
            OperationContext context) {
        if (memoryConfigs == null || memoryConfigs.isEmpty()) {
            return this.getConversationTurns(aippId, aippType, 5, context, chatId);
        }
        Map<String, Object> valueConfig = memoryConfigs.stream()
                .filter(config -> StringUtils.equals(ObjectUtils.<String>cast(config.get(NAME_KEY)), VALUE_KEY))
                .findFirst()
                .orElseThrow(() -> new AippException(AippErrCode.PARSE_MEMORY_CONFIG_FAILED));
        switch (memoryType) {
            case "ByConversationTurn":
                Integer turnNum = Integer.parseInt(ObjectUtils.<String>cast(valueConfig.get(VALUE_KEY)));
                return getConversationTurns(aippId, aippType, turnNum, context, chatId);
            case "NotUseMemory":
                // 兼容旧应用
                businessData.put(AippConst.BS_AIPP_USE_MEMORY_KEY, false);
                return new ArrayList<>();
            case "Customizing":
                // 如何定义这个genericable接口，入参为一个map?
                String fitableId = ObjectUtils.<String>cast(valueConfig.get(VALUE_KEY));
                // 目前flow graph中并没有params的配置，暂时用一个空map
                Map<String, Object> params = new HashMap<>();
                return getCustomizedLogs(fitableId, params, aippId, aippType, context);
            default:
                return getConversationTurns(aippId, aippType, 5, context, chatId);
        }
    }

    private List<Map<String, Object>> getConversationTurns(String aippId, String aippType, Integer count,
            OperationContext context, String chatId) {
        List<AippInstLogDataDto> logs;
        if (chatId != null) {
            logs = aippLogService.queryChatRecentInstLog(aippId, aippType, count, context, chatId);
            return getLogMaps(logs);
        }
        return new ArrayList<>();
    }

    /**
     * 将日志数据转换为前端可展示的格式
     *
     * @param logs 日志数据列表
     * @return 转换后的前端展示数据列表
     */
    public static List<Map<String, Object>> getLogMaps(List<AippInstLogDataDto> logs) {
        List<Map<String, Object>> memories = new ArrayList<>();
        logs.forEach(log -> {
            Map<String, Object> logMap = new HashMap<>();
            AippInstLogDataDto.AippInstanceLogBody question = log.getQuestion();
            if (question == null) {
                return;
            }
            logMap.put("question", getLogData(question.getLogData(), question.getLogType()));
            List<AippInstLogDataDto.AippInstanceLogBody> answers = log.getInstanceLogBodies()
                    .stream()
                    .filter(item -> MEMORY_MSG_TYPE_WHITE_LIST.contains(StringUtils.toUpperCase(item.getLogType())))
                    .collect(Collectors.toList());
            List<AippInstLogDataDto.AippInstanceLogBody> files = log.getInstanceLogBodies()
                    .stream()
                    .filter(l -> l.getLogType().equals(AippInstLogType.FILE.name()))
                    .collect(Collectors.toList());
            if (!answers.isEmpty()) {
                AippInstLogDataDto.AippInstanceLogBody logBody = answers.get(answers.size() - 1);
                logMap.put("answer", getLogData(logBody.getLogData(), logBody.getLogType()));
            }
            if (!files.isEmpty()) {
                AippInstLogDataDto.AippInstanceLogBody fileBody = files.get(0);
                logMap.put("fileDescription", getLogData(fileBody.getLogData(), fileBody.getLogType()));
            }
            memories.add(logMap);
        });
        return memories.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static String getLogData(String logData, String logType) {
        Map<String, String> logInfo = ObjectUtils.<Map<String, String>>cast(JSON.parse(logData));
        if (!StringUtils.isEmpty(logInfo.get("form_args"))) {
            return logInfo.get("form_args");
        }
        String msg = logInfo.get("msg");
        if (Objects.equals(logType, AippInstLogType.META_MSG.name())) {
            Map<String, Object> referenceMsg = ObjectUtils.cast(JSON.parse(msg));
            return ObjectUtils.cast(referenceMsg.get("data"));
        }
        if (Objects.equals(logType, AippInstLogType.QUESTION_WITH_FILE.name())) {
            return JSONObject.parseObject(msg).getString("question");
        }
        return msg;
    }

    private List<Map<String, Object>> getCustomizedLogs(String fitableId, Map<String, Object> params, String aippId,
            String aippType, OperationContext context) {
        final String genericableId = "68dc66a6185cf64c801e55c97fc500e4";
        if (fitableId == null) {
            log.warn("no fitable id in customized log selection.");
            return Collections.emptyList();
        }
        List<Map<String, Object>> logs;
        try {
            logs = this.client.getRouter(genericableId)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(params, aippId, aippType, context);
        } catch (FitException t) {
            log.error("Error occurred when get history logs, error: {}", t.getMessage());
            throw new AippException(AippErrCode.GET_HISTORY_LOG_FAILED);
        }
        return logs;
    }

    /**
     * 删除应用实例
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     * @param context 操作上下文
     */
    @Override
    public void deleteAippInstance(String aippId, String version, String instanceId, OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        String versionId = meta.getVersionId();

        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        Function<String, Boolean> handler = status -> MetaInstStatusEnum.getMetaInstStatus(status).getValue()
                > MetaInstStatusEnum.RUNNING.getValue();
        if (!checkInstanceStatus(aippId, instDetail, handler)) {
            log.error("aipp {} version {} inst{}, not allow terminate.", aippId, version, instanceId);
            throw new AippForbiddenException(context, AippErrCode.DELETE_INSTANCE_FORBIDDEN);
        }
        metaInstanceService.deleteMetaInstance(versionId, instanceId, context);
    }

    private MetaInstanceFilter genInstFilter(AippInstanceQueryCondition cond) {
        MetaInstanceFilter filter = new MetaInstanceFilter();

        String sortEncode = String.format(Locale.ROOT,
                "%s(info.%s)",
                DirectionEnum.getDirection(nullIf(cond.getOrder(), DirectionEnum.DESCEND.name())).getValue(),
                MetaInstSortKeyEnum.getInstSortKey(nullIf(cond.getSort(), MetaInstSortKeyEnum.START_TIME.name()))
                        .getKey());
        List<String> orderBy = Collections.singletonList(sortEncode);

        filter.setOrderBy(orderBy);
        Map<String, List<String>> infos = new HashMap<>();
        if (StringUtils.isNotBlank(cond.getCreator())) {
            infos.put(AippConst.INST_CREATOR_KEY, Collections.singletonList(cond.getCreator()));
        }
        if (StringUtils.isNotBlank(cond.getAippInstanceName())) {
            infos.put(AippConst.INST_NAME_KEY, Collections.singletonList(cond.getAippInstanceName()));
        }
        filter.setInfos(infos);
        return filter;
    }

    private AippInstanceDto buildAippInstanceDtoFromEntityList(Instance instance,
            List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> entityMaps, OperationContext context) {
        String formId = instance.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY);
        String version = instance.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY);
        DynamicFormDetailEntity entity = null;
        if (this.checkParameter(formId, version) && !entityMaps.isEmpty()) {
            entity = entityMaps.stream().filter(Objects::nonNull).filter(map -> {
                FormMetaQueryParameter parameterOfMap = map.keySet().iterator().next();
                return Objects.equals(parameterOfMap.getFormId(), formId) && Objects.equals(parameterOfMap.getVersion(),
                        version);
            }).map(Map::values).flatMap(Collection::stream).findFirst().orElse(null);
        }
        Map<String, String> info = instance.getInfo();
        return AippInstanceDto.builder()
                .aippInstanceId(instance.getId())
                .tenantId(context.getTenantId())
                .aippInstanceName(info.get(AippConst.INST_NAME_KEY))
                .status(info.get(AippConst.INST_STATUS_KEY))
                .formMetadata(entity == null ? null : entity.getData())
                .formArgs(info)
                .startTime(info.get(AippConst.INST_CREATE_TIME_KEY))
                .endTime(info.getOrDefault(AippConst.INST_FINISH_TIME_KEY, null))
                .aippInstanceLogs(null)
                .build();
    }

    private boolean checkParameter(String formId, String version) {
        if (StringUtils.isEmpty(formId) || Objects.equals(formId, AippConst.INVALID_FORM_ID)) {
            return false;
        }
        return StringUtils.isNotEmpty(version) && !Objects.equals(version, AippConst.INVALID_FORM_VERSION_ID);
    }

    private Map<String, Object> buildInfo(List<TaskProperty> props, Map<String, Object> businessData) {
        Map<String, Object> info = new HashMap<>();
        businessData.forEach((targetKey, targetValue) -> {
            if (props.stream().anyMatch(item -> item.getName().equals(targetKey))) {
                info.put(targetKey, targetValue);
            }
        });
        return info;
    }

    private void updateAippLog(List<AppBuilderFormProperty> formProperties, String aippId, String instanceId,
            OperationContext context, Map<String, Object> businessData) {
        Instance oldInstDetail = MetaInstanceUtils.getInstanceDetail(aippId, instanceId, context, metaInstanceService);
        String formId = oldInstDetail.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY);
        String formVersion = oldInstDetail.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY);
        AippLogData newLogData = FormUtils.buildLogDataWithFormData(formProperties, formId, formVersion, businessData);
        Long logId = this.getLodId(instanceId, context);
        aippLogService.updateLog(logId, JsonUtils.toJsonString(newLogData));
    }

    private void updateAippInstance(String aippId, List<TaskProperty> props, String instanceId,
            OperationContext context, Map<String, Object> businessData) {
        this.updateAippInstance(aippId, props, instanceId, context, businessData, (info) -> {});
    }

    private void updateAippInstance(String aippId, List<TaskProperty> props, String instanceId,
            OperationContext context, Map<String, Object> businessData, Consumer<Map<String, Object>> afterInfoBuild) {
        Map<String, Object> info = this.buildInfo(props, businessData);
        afterInfoBuild.accept(info);
        log.debug("build info {} businessData {}", info, businessData);
        InstanceDeclarationInfo instanceDeclarationInfo = InstanceDeclarationInfo.custom().info(info).build();
        this.metaInstanceService.patchMetaInstance(aippId, instanceId, instanceDeclarationInfo, context);
    }

    private Long getLodId(String instanceId, OperationContext context) {
        AippInstLog oldAippInstLog = aippLogService.queryLastInstanceFormLog(instanceId);
        if (oldAippInstLog == null) {
            log.error("instanceId {} log is null", instanceId);
            throw new AippException(context, AippErrCode.AIPP_INSTANCE_LOG_IS_NULL);
        }
        return oldAippInstLog.getLogId();
    }

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @param context 操作上下文
     * @param logId 日志id
     * @param isDebug 是否是调试状态
     * @return 返回一个Choir对象，用于流式处理
     */
    @Override
    public Choir<Object> resumeAndUpdateAippInstance(String instanceId, Map<String, Object> formArgs, Long logId,
            OperationContext context, boolean isDebug) {
        String metaVersionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Meta meta = this.metaService.retrieve(metaVersionId, context);
        String versionId = meta.getVersionId();

        // 更新表单数据
        Map<String, Object> businessData = ObjectUtils.cast(formArgs.get(AippConst.BS_DATA_KEY));
        setExtraBusinessData(context, businessData, meta, instanceId);

        // 获取旧的实例数据
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        // 获取人工节点开始时间戳 [记录人工节点时延]
        String smartFormTimeStr = instDetail.getInfo().get(AippConst.INST_SMART_FORM_TIME_KEY);
        LocalDateTime smartFormTime = StringUtils.isBlank(smartFormTimeStr)
                ? LocalDateTime.now()
                : LocalDateTime.parse(smartFormTimeStr, FORMATTER);
        long resumeDuration =
                Long.parseLong(StringUtils.blankIf(instDetail.getInfo().get(AippConst.INST_RESUME_DURATION_KEY), "0"));
        Duration duration = Duration.between(smartFormTime, LocalDateTime.now());
        businessData.put(AippConst.INST_RESUME_DURATION_KEY, String.valueOf(resumeDuration + duration.toMillis()));
        String createTime = instDetail.getInfo().get(AippConst.INST_CREATE_TIME_KEY);
        if (StringUtils.isNotEmpty(createTime)) {
            businessData.put(AippConst.INSTANCE_START_TIME, DateTimeConverter.INSTANCE.fromExternal(createTime));
        }

        // 持久化aipp实例表单记录
        String formId = instDetail.getInfo().get(AippConst.INST_CURR_FORM_ID_KEY);
        String formVersion = instDetail.getInfo().get(AippConst.INST_CURR_FORM_VERSION_KEY);
        String appId = ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        AppBuilderApp app = this.appFactory.create(appId);
        AippLogData logData =
                FormUtils.buildLogDataWithFormData(app.getFormProperties(), formId, formVersion, businessData);

        // 设置表单的渲染数据和填充数据
        logData.setFormAppearance(ObjectUtils.cast(formArgs.get(AippConst.FORM_APPEARANCE_KEY)));
        logData.setFormData(ObjectUtils.cast(formArgs.get(AippConst.FORM_DATA_KEY)));
        this.aippLogService.updateLog(logId, AippInstLogType.HIDDEN_FORM.name(), JsonUtils.toJsonString(logData));

        // 更新实例并清空当前表单数据
        this.updateAippInstance(versionId, meta.getProperties(), instanceId, context, businessData, this::clearFormId);

        String flowTraceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
        Validation.notNull(flowTraceId, "flowTraceId can not be null");
        String flowDefinitionId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_FLOW_DEF_ID_KEY));

        Locale locale = getLocale();

        return Choir.create(emitter -> {
            try {
                this.appChatSessionService.addSession(instanceId, new ChatSession<>(emitter, appId, isDebug, locale));
                this.flowInstanceService.resumeFlow(flowDefinitionId, flowTraceId, formArgs, context);
            } catch (JobberException e) {
                log.error("resume flow failed, flowDefinitionId:{}, flowTraceId:{}, formArgs:{}",
                        flowDefinitionId,
                        flowTraceId,
                        formArgs);
                throw new AippException(context, AippErrCode.RESUME_CHAT_FAILED);
            } catch (AippException e) {
                this.updateInstanceStatusError(versionId, instanceId, context);
                aippLogService.insertLog(AippInstLogType.ERROR.name(),
                        AippLogData.builder().msg(e.getMessage()).build(),
                        businessData);
            }
        });
    }

    private void updateInstanceStatusError(String versionId, String instanceId, OperationContext context) {
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ERROR.name())
                .build();
        this.metaInstanceService.patchMetaInstance(versionId, instanceId, declarationInfo, context);
    }

    @Override
    public String terminateInstance(String instanceId, Map<String, Object> msgArgs, Long logId,
            OperationContext context) {
        this.aippLogService.updateLogType(logId, AippInstLogType.HIDDEN_FORM.name());
        String message = this.terminateInstance(instanceId, msgArgs, context);
        this.runtimeInfoService.insertRuntimeInfo(instanceId,
                msgArgs,
                MetaInstStatusEnum.TERMINATED,
                StringUtils.EMPTY,
                context);
        return message;
    }

    private void clearFormId(Map<String, Object> info) {
        info.put(AippConst.INST_CURR_FORM_ID_KEY, AippConst.INVALID_FORM_ID);
        info.put(AippConst.INST_CURR_FORM_VERSION_KEY, AippConst.INVALID_FORM_VERSION_ID);
    }

    /**
     * 终止aipp实例
     *
     * @param context 操作上下文
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @return 终止对话后返回的信息
     */
    @Override
    public String terminateInstance(String instanceId, Map<String, Object> msgArgs, OperationContext context) {
        String versionId = this.metaInstanceService.getMetaVersionId(instanceId);
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(versionId, instanceId, context, metaInstanceService);
        Function<String, Boolean> handler = this::statusCheckHandler;
        Meta meta = this.metaService.retrieve(versionId, context);
        String aippId = meta.getId();
        if (!checkInstanceStatus(aippId, instDetail, handler)) {
            log.error("aipp {} inst{}, not allow terminate.", aippId, instanceId);
            throw new AippException(context, AippErrCode.TERMINATE_INSTANCE_FORBIDDEN);
        }
        String statusStr = instDetail.getInfo().get(AippConst.INST_STATUS_KEY);
        if (MetaInstStatusEnum.getMetaInstStatus(statusStr) == MetaInstStatusEnum.RUNNING) {
            String flowTraceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
            Validation.notNull(flowTraceId, "flowTraceId can not be null");
            try {
                this.flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);
            } catch (JobberException e) {
                log.error("terminate flow failed, flowTraceId:{}.", flowTraceId);
                throw new AippException(context, AippErrCode.TERMINATE_INSTANCE_FAILED);
            }

            // 更新实例状态
            InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                    .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                    .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.TERMINATED.name())
                    .build();
            this.metaInstanceService.patchMetaInstance(versionId, instanceId, info, context);
        }
        String message = this.getTerminateMessage(msgArgs);
        String version = meta.getVersion();
        this.aopAippLogService.insertLog(AippLogCreateDto.builder()
                .aippId(aippId)
                .version(version)
                .aippType(ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY)))
                .aippType(ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_AIPP_TYPE_KEY)))
                .instanceId(instanceId)
                .logType(AippInstLogType.MSG.name())
                .logData(JsonUtils.toJsonString(AippLogData.builder().msg(message).build()))
                .createUserAccount(context.getAccount())
                .path(this.aippLogService.buildPath(instanceId, null)) // 这块在子流程调用时，得考虑下
                .build());
        return message;
    }

    private String getTerminateMessage(Map<String, Object> msgArgs) {
        return msgArgs.get(AippConst.TERMINATE_MESSAGE_KEY) != null ? msgArgs.get(AippConst.TERMINATE_MESSAGE_KEY)
                .toString() : "已终止对话";
    }

    private boolean statusCheckHandler(String status) {
        short instStatus = MetaInstStatusEnum.getMetaInstStatus(status).getValue();
        return Stream.of(MetaInstStatusEnum.READY.getValue(), MetaInstStatusEnum.TERMINATED.getValue())
                .noneMatch(value -> value == instStatus);
    }

    /**
     * 终止aipp全部实例
     *
     * @param aippId aipp Id
     * @param versionId versionId
     * @param isDeleteLog 是否删除aipp log
     * @param context 操作上下文
     */
    @Override
    public void terminateAllPreviewInstances(String aippId, String versionId, boolean isDeleteLog,
            OperationContext context) {
        final int limit = 15;
        Stream<Instance> instances = MetaUtils.getAllFromRangedResult(limit,
                offset -> metaInstanceService.list(versionId, offset, limit, context));
        Function<String, Boolean> handler = status -> MetaInstStatusEnum.getMetaInstStatus(status).getValue()
                == MetaInstStatusEnum.RUNNING.getValue();
        instances
                // 只停止正在运行的
                .filter(instance -> checkInstanceStatus("versionId: " + versionId, instance, handler))
                .forEach(instance -> {
                    String flowTraceId = instance.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);
                    Validation.notNull(flowTraceId, "flowTraceId can not be null");
                    flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);

                    // 更新实例状态
                    InstanceDeclarationInfo info = InstanceDeclarationInfo.custom()
                            .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.TERMINATED.name())
                            .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                            .build();
                    metaInstanceService.patchMetaInstance(versionId, instance.getId(), info, context);
                });

        if (isDeleteLog) {
            aippLogService.deleteAippPreviewLog(aippId, context);
        }
    }

    @Override
    public AppBuilderAppStartDto startInstance(AppBuilderAppDto appDto, Map<String, Object> initContext,
            OperationContext context) {
        AippCreate aippCreate;
        final String genericableId = "modelengine.fit.jober.aipp.service.app.debug";
        final String fitableId = "default";
        this.validateApp(appDto.getId());
        try {
            aippCreate =
                    this.client.getRouter(genericableId).route(new FitableIdFilter(fitableId)).invoke(appDto, context);
        } catch (FitException t) {
            String errorMsg = t.getMessage();
            log.error("Error occurred when create debug aipp, error: {}", errorMsg);
            throw new AippException(AippErrCode.CREATE_DEBUG_AIPP_FAILED, errorMsg);
        }
        String instanceId = createAippInstance(aippCreate.getAippId(), aippCreate.getVersion(), initContext, context);
        return AppBuilderAppStartDto.builder().instanceId(instanceId).aippCreate(aippCreate).build();
    }

    private void validateApp(String appId) {
        this.appFactory.create(appId);
    }
}
