/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_FLOW_DEF_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_INFOS_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_INPUT_KEY;
import static modelengine.fit.jober.aipp.enums.AppTypeEnum.APP;
import static modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl.getMetaByAppId;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.dto.AppInputParam;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.InputParamType;
import modelengine.fit.jober.aipp.enums.RestartModeEnum;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.genericable.AppBuilderAppService;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.aipp.util.*;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.merge.ConflictResolutionPolicy;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 历史会话服务实现类
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Component
public class AppChatServiceImpl implements AppChatService {
    private static final Logger LOGGER = Logger.get(AppChatServiceImpl.class);

    private static final int FROM_OTHER_CHAT = 2;

    private static final String DEFAULT_CHAT_NAME_PREFIX = "@appBuilderDebug-";

    private final AppBuilderAppFactory appFactory;

    private final AippChatMapper aippChatMapper;

    private final AippRunTimeService aippRunTimeService;

    private final AppBuilderAppService appService;

    private final AippLogService aippLogService;

    private final AppBuilderAppRepository appRepository;

    private final MetaService metaService;

    private final FlowsService flowsService;

    private final int maxQuestionLen;
    private final int maxUserContextLen;

    public AppChatServiceImpl(AppBuilderAppFactory appFactory, AippChatMapper aippChatMapper,
            AippRunTimeService aippRunTimeService, AppBuilderAppService appService, AippLogService aippLogService,
            AppBuilderAppRepository appRepository, MetaService metaService, FlowsService flowsService,
            @Value("${app.question.max-length}") Integer maxQuestionLen,
            @Value("${app.user-context.max-length}") Integer maxUserContextLen) {
        this.appFactory = appFactory;
        this.aippChatMapper = aippChatMapper;
        this.aippRunTimeService = aippRunTimeService;
        this.appService = appService;
        this.aippLogService = aippLogService;
        this.appRepository = appRepository;
        this.metaService = metaService;
        this.flowsService = flowsService;
        this.maxQuestionLen = maxQuestionLen != null ? maxQuestionLen : 20000;
        this.maxUserContextLen = maxUserContextLen != null ? maxUserContextLen : 500;
    }

    @Override
    public Choir<Object> chat(CreateAppChatRequest body, OperationContext context, boolean isDebug) {
        LOGGER.info("[perf] [{}] chat start, appId={}, isDebug={}",
                System.currentTimeMillis(),
                body.getAppId(),
                isDebug);
        this.validateApp(body.getAppId());
        AppBuilderApp app = this.appFactory.create(body.getAppId());
        if (isInvalidQuestion(app.getType(), body)) {
            throw new AippParamException(INPUT_PARAM_IS_INVALID, AippConst.BS_AIPP_QUESTION_KEY);
        }
        Map<String, Object> businessData = this.convertContextToBusinessData(body, isDebug);
        // 这里几行代码的顺序不可以调整，必须先把对话的appId查询出来，再去创建chatId
        String chatAppId = this.getAppId(body);
        boolean hasAtOtherApp = body.hasAtOtherApp();
        this.createChatId(body, hasAtOtherApp, businessData);
        // create instance —— 根据实际的那个app创建
        AppUtils.setAppChatInfo(body.getAppId(), isDebug);
        if (isDebug) {
            this.appService.updateFlow(chatAppId, context);
        }
        LOGGER.info("[perf] [{}] chat updateFlow end, appId={}", System.currentTimeMillis(), body.getAppId());
        this.addUserContext(body, businessData, isDebug, context, app.getType());
        Tuple tuple = this.aippRunTimeService.createInstanceByApp(chatAppId,
                body.getQuestion(),
                businessData,
                context,
                isDebug);
        LOGGER.info("[perf] [{}] chat createInstanceByApp end, appId={}", System.currentTimeMillis(), body.getAppId());
        // 这tuple的两个值都不可能为null
        try {
            this.saveChatInfos(body,
                    context,
                    ObjectUtils.cast(tuple.get(0).orElseThrow(this::generalServerException)),
                    chatAppId,
                    isDebug);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(TASK_NOT_FOUND);
        }
        LOGGER.info("[perf] [{}] chat saveChatInfos end, appId={}", System.currentTimeMillis(), body.getAppId());
        Choir<Object> result = ObjectUtils.cast(tuple.get(1).orElseThrow(this::generalServerException));
        LOGGER.info("[perf] [{}] chat end, appId={}, isDebug={}", System.currentTimeMillis(), body.getAppId(), isDebug);
        return result;
    }

    private boolean isInvalidQuestion(String appType, CreateAppChatRequest request) {
        return StringUtils.equals(APP.code(), appType) && (request.getQuestion() == null || !StringUtils.lengthBetween(
                request.getQuestion(),
                0,
                this.maxQuestionLen,
                true,
                true));
    }

    @Override
    public Choir<Object> restartChat(String instanceId, Map<String, Object> additionalContext,
            OperationContext operationContext) {
        String path = this.aippLogService.getParentPath(instanceId);
        String parentInstanceId = path.split(AippLogUtils.PATH_DELIMITER)[1];
        if (StringUtils.isEmpty(parentInstanceId)) {
            LOGGER.error("parentInstanceId is empty.");
            throw new AippException(AippErrCode.RE_CHAT_FAILED, instanceId);
        }
        // 这个方法查询的chatList，0号位一定是原对话，1号位一定是at对话（如果有的话），详见本类saveChatInfo方法
        List<String> chatIds = this.aippChatMapper.selectChatIdByInstanceId(parentInstanceId);
        if (chatIds.isEmpty()) {
            throw new IllegalArgumentException(StringUtils.format("The instance id {0} does not match any chat id.",
                    parentInstanceId));
        }
        List<QueryChatRsp> chatList = this.aippChatMapper.selectChatListByChatIds(chatIds);
        if (CollectionUtils.isEmpty(chatList)) {
            LOGGER.error("chatList is empty.");
            throw new AippParamException(AippErrCode.RE_CHAT_FAILED, parentInstanceId);
        }
        String restartMode = ObjectUtils.cast(additionalContext.getOrDefault(AippConst.RESTART_MODE,
                RestartModeEnum.OVERWRITE.getMode()));
        additionalContext.put(AippConst.RESTART_MODE, restartMode);
        CreateAppChatRequest body = this.buildChatBody(parentInstanceId, additionalContext, chatList);
        if (StringUtils.equals(RestartModeEnum.OVERWRITE.getMode(), restartMode)) {
            this.aippChatMapper.deleteWideRelationshipByInstanceId(parentInstanceId);
            this.aippLogService.deleteInstanceLog(parentInstanceId);
        }
        boolean isDebug = AppState.INACTIVE.getName()
                .equals(JsonUtils.parseObject(chatList.get(0).getAttributes()).get(AippConst.ATTR_CHAT_STATE_KEY));
        return this.chat(body, operationContext, isDebug);
    }

    private CreateAppChatRequest buildChatBody(String parentInstanceId, Map<String, Object> additionalContextParam,
            List<QueryChatRsp> chatList) {
        Map<String, Object> additionalContext = additionalContextParam;
        CreateAppChatRequest.CreateAppChatRequestBuilder bodyBuilder = CreateAppChatRequest.builder();
        List<AippInstLog> instLogs = this.aippLogService.queryLogsByInstanceIdAndLogTypes(parentInstanceId,
                Arrays.asList(AippInstLogType.QUESTION.name(), AippInstLogType.HIDDEN_QUESTION.name()));
        AippInstLog questionLog = instLogs.get(0);
        Map<String, Object> logData = JsonUtils.parseObject(questionLog.getLogData());
        String question = ObjectUtils.cast(logData.get("msg"));
        if (logData.containsKey(BUSINESS_INFOS_KEY)) {
            Map<String, Object> infos = ObjectUtils.cast(logData.get(BUSINESS_INFOS_KEY));
            if (infos != null && infos.containsKey(BUSINESS_INPUT_KEY)) {
                Map<String, Object> input = ObjectUtils.cast(infos.get(BUSINESS_INPUT_KEY));
                Map<String, Object> mergedContext =
                        MapUtils.merge(additionalContext, input, ConflictResolutionPolicy.OVERRIDE);
                additionalContext = mergedContext;
            }
        }
        bodyBuilder.question(question);
        bodyBuilder.chatId(chatList.get(0).getChatId());
        bodyBuilder.appId(chatList.get(0).getAppId());
        CreateAppChatRequest.Context.ContextBuilder contextBuilder = CreateAppChatRequest.Context.builder();
        contextBuilder.userContext(additionalContext);
        if (additionalContext.containsKey(AippConst.BS_DIMENSION_ID_KEY)) {
            contextBuilder.dimensionId(ObjectUtils.cast(additionalContext.get(AippConst.BS_DIMENSION_ID_KEY)));
        }
        if (chatList.size() == FROM_OTHER_CHAT) {
            contextBuilder.atChatId(chatList.get(1).getChatId());
        }
        return bodyBuilder.context(contextBuilder.build()).build();
    }

    private void validateApp(String appId) {
        AppBuilderApp appBuilderApp = this.appRepository.selectWithId(appId);
        if (appBuilderApp == null || StringUtils.isEmpty(appBuilderApp.getId())) {
            throw new AippException(AippErrCode.APP_NOT_FOUND_WHEN_CHAT);
        }
    }

    private void saveChatInfos(CreateAppChatRequest body, OperationContext context, String instId,
            String chatAppId, boolean isDebug) throws AippTaskNotFoundException {
        AppBuilderApp app = this.appFactory.create(body.getAppId());
        Map<String, String> attributes = new HashMap<>();
        Meta meta = CacheUtils.getMetaByAppId(this.metaService, chatAppId, isDebug, context);
        if (meta == null) {
            LOGGER.error("Cannot find meta for chat app. [appId={}, instId={}]", chatAppId, instId);
            throw new AippTaskNotFoundException(TASK_NOT_FOUND);
        }
        String aippId = meta.getId();
        attributes.put(AippConst.ATTR_CHAT_INST_ID_KEY, instId);
        attributes.put(AippConst.ATTR_CHAT_STATE_KEY, app.getState());
        attributes.put(AippConst.BS_AIPP_ID_KEY, aippId);
        if (body.getContext() != null && StringUtils.isNotBlank(body.getContext().getDimensionId())) {
            attributes.put(AippConst.BS_DIMENSION_ID_KEY, body.getContext().getDimensionId());
        }
        String chatId = body.getChatId();
        this.buildAndInsertChatInfo(app, attributes, body.getQuestion(), chatId, context.getOperator());
        this.buildAndInsertWideRelationInfo(instId, chatId);
        if (body.hasAtOtherApp()) {
            AppBuilderApp chatApp = this.appFactory.create(chatAppId);
            // 被@的应用的对话
            Map<String, String> originAttributes = new HashMap<>();
            originAttributes.put(AippConst.ATTR_CHAT_INST_ID_KEY, instId);
            originAttributes.put(AippConst.ATTR_CHAT_STATE_KEY, chatApp.getState());
            originAttributes.put(AippConst.ATTR_CHAT_ORIGIN_APP_KEY, app.getId());
            originAttributes.put(AippConst.ATTR_CHAT_ORIGIN_APP_VERSION_KEY, app.getVersion());
            String atChatId = body.getContext().getAtChatId();
            this.buildAndInsertChatInfo(chatApp, originAttributes, body.getQuestion(), atChatId, context.getOperator());
            this.buildAndInsertWideRelationInfo(instId, atChatId);
        }
    }

    private Map<String, Object> convertContextToBusinessData(CreateAppChatRequest body, boolean isDebug) {
        Map<String, Object> businessData = new HashMap<>();
        if (body.getContext().getUseMemory() != null) {
            businessData.put(AippConst.BS_AIPP_USE_MEMORY_KEY, body.getContext().getUseMemory());
        }
        businessData.put("dimension", body.getContext().getDimension());
        businessData.put("isDebug", isDebug);
        return businessData;
    }

    private void addUserContext(CreateAppChatRequest body, Map<String, Object> businessData, boolean isDebug,
            OperationContext context, String appType) {
        Meta meta = CacheUtils.getMetaByAppId(this.metaService, body.getAppId(), isDebug, context);
        String flowDefinitionId = ObjectUtils.cast(meta.getAttributes().get(ATTR_FLOW_DEF_ID_KEY));
        List<AppInputParam> inputParams = FlowUtils.getAppInputParams(this.flowsService, flowDefinitionId, context);
        if (StringUtils.equals(APP.code(), appType)) {
            inputParams = inputParams.stream()
                    .filter(param -> !StringUtils.equals("Question", param.getName()))
                    .collect(Collectors.toList());
        }
        if (MapUtils.isEmpty(body.getContext().getUserContext())) {
            if (inputParams.stream().noneMatch((AppInputParam::isRequired))) {
                return;
            }
            LOGGER.error("No user context when starting a chat.");
            throw new AippParamException(INPUT_PARAM_IS_INVALID, "user context");
        }
        Map<String, Object> userContext = body.getContext().getUserContext();
        this.validateUserContext(userContext, inputParams);
        businessData.putAll(userContext);
    }

    private void validateUserContext(Map<String, Object> userContext, List<AppInputParam> inputParams) {
        inputParams.forEach(param -> {
            String paramName = param.getName();
            if (param.isRequired()) {
                Validation.notNull(ObjectUtils.cast(userContext.get(paramName)),
                        () -> new AippParamException(INPUT_PARAM_IS_INVALID, paramName));
            }
            if (userContext.get(param.getName()) == null) {
                return;
            }
            boolean isValid;
            switch (InputParamType.getParamType(param.getType())) {
                case STRING_TYPE:
                    isValid = userContext.get(paramName) instanceof String
                            && StringUtils.lengthBetween((String) userContext.get(paramName),
                            1,
                            this.maxUserContextLen,
                            true,
                            true);
                    break;
                case BOOLEAN_TYPE:
                    isValid = userContext.get(paramName) instanceof Boolean;
                    break;
                case INTEGER_TYPE:
                    isValid =
                            userContext.get(paramName) instanceof Integer && ObjectUtils.between((int) userContext.get(
                                    paramName), -999999999, 999999999);
                    break;
                case NUMBER_TYPE:
                    isValid = isValidNumber(userContext.get(paramName));
                    break;
                default:
                    throw new AippParamException(INPUT_PARAM_IS_INVALID, paramName);
            }
            if (!isValid) {
                throw new AippParamException(INPUT_PARAM_IS_INVALID, paramName);
            }
        });
    }

    private boolean isValidNumber(Object value) {
        if (!(value instanceof Number)) {
            return false;
        }
        BigDecimal numberValue = new BigDecimal(value.toString());
        if (numberValue.compareTo(new BigDecimal("-999999999.99")) < 0
                || numberValue.compareTo(new BigDecimal("999999999.99")) > 0) {
            return false;
        }
        int scale = numberValue.scale();
        return scale <= 2;
    }

    private String getAppId(CreateAppChatRequest body) {
        String atChatId = body.getContext().getAtChatId();
        if (StringUtils.isNotBlank(atChatId)) {
            List<QueryChatRsp> chats = this.aippChatMapper.selectChatList(null, atChatId, null);
            if (CollectionUtils.isEmpty(chats)) {
                throw new AippException(AippErrCode.APP_CHAT_ERROR);
            }
            return chats.get(0).getAppId();
        }
        String atAppId = body.getContext().getAtAppId();
        if (StringUtils.isNotBlank(atAppId)) {
            return atAppId;
        }
        return body.getAppId();
    }

    private void createChatId(CreateAppChatRequest body, boolean hasAtOtherApp, Map<String, Object> businessData) {
        // body里没有chatId：第一次对话
        if (StringUtils.isBlank(body.getChatId())) {
            body.setChatId(UUIDUtil.uuid());
        }
        // 没有被@的chatId， @其它应用的第一次对话
        if (hasAtOtherApp && StringUtils.isBlank((body.getContext().getAtChatId()))) {
            body.getContext().setAtChatId(UUIDUtil.uuid());
            businessData.put(AippConst.BS_AT_CHAT_ID, body.getContext().getAtChatId());
        }
        businessData.put(AippConst.BS_CHAT_ID, body.getChatId());
    }

    private void buildAndInsertChatInfo(AppBuilderApp app, Map<String, String> attributes, String chatName,
            String chatId, String operator) {
        String cutChatName = this.generateChatName(chatName);
        LocalDateTime operateTime = LocalDateTime.now();
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(app.getId())
                .version(app.getVersion())
                .attributes(JsonUtils.toJsonString(attributes))
                .chatId(chatId)
                .chatName(cutChatName)
                .status(AippConst.CHAT_STATUS)
                .updater(operator)
                .createTime(operateTime)
                .updateTime(operateTime)
                .creator(operator)
                .build();
        this.aippChatMapper.insertChat(chatInfo);
    }

    private String generateChatName(String chatName) {
        if (chatName == null) {
            return DEFAULT_CHAT_NAME_PREFIX + UUIDUtil.uuid().substring(0, 6);
        }
        return chatName.length() > 64 ? chatName.substring(0, 32) : chatName;
    }

    private void buildAndInsertWideRelationInfo(String instId, String chatId) {
        LocalDateTime operateTime = LocalDateTime.now();
        ChatAndInstanceMap wideRelationInfo = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid())
                .instanceId(instId)
                .chatId(chatId)
                .createTime(operateTime)
                .updateTime(operateTime)
                .build();
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
    }

    private ServerInternalException generalServerException() {
        return new ServerInternalException("Except no null value but null!");
    }
}