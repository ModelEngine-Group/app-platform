/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.OBTAIN_HISTORY_CONVERSATION_FAILED;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.dto.chat.ChatDto;
import modelengine.fit.jober.aipp.dto.chat.ChatInfoRspDto;
import modelengine.fit.jober.aipp.dto.chat.CreateChatRequest;
import modelengine.fit.jober.aipp.dto.chat.MessageInfo;
import modelengine.fit.jober.aipp.dto.chat.QueryChatInfoRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.po.MsgInfoPO;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.fit.jober.common.RangedResultSet;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AippChatServiceImpl
 *
 * @since 2024-06-07
 */
@Component
public class AippChatServiceImpl implements AippChatService {
    private static final Logger LOGGER = Logger.get(AippChatServiceImpl.class);
    private static final String NORMAL_CHAT = "normal";
    private static final String FROM_OTHER_CHAT = "fromOtherApp";

    private final AippChatMapper aippChatMapper;
    private final MetaService metaService;
    private final AppBuilderAppMapper appBuilderAppMapper;
    private final AppTaskService appTaskService;
    private final AppVersionRepository appVersionRepository;
    private final AppChatRepository appChatRepository;
    private final AippLogService aippLogService;
    private final AppBuilderAppRepository appRepository;

    @Fit
    private modelengine.fit.jober.aipp.genericable.AippRunTimeService aippRunTimeService;

    public AippChatServiceImpl(AippChatMapper aippChatMapper, AppBuilderAppMapper appBuilderAppMapper,
            AppTaskService appTaskService, AppVersionRepository appVersionRepository,
            AppChatRepository appChatRepository, MetaService metaService, AippLogService aippLogService,
            AppBuilderAppRepository appRepository) {
        this.aippChatMapper = aippChatMapper;
        this.metaService = metaService;
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.appTaskService = appTaskService;
        this.appVersionRepository = appVersionRepository;
        this.appChatRepository = appChatRepository;
        this.aippLogService = aippLogService;
        this.appRepository = appRepository;
    }

    @Override
    public QueryChatRsp createChat(CreateChatRequest body, OperationContext context) {
        if (body.getOriginApp() != null) {
            String chatId = UUIDUtil.uuid();
            body.setChatId(chatId);
        }
        Map<String, Object> initContext = body.getInitContext();
        Map<String, Object> result = (Map<String, Object>) initContext.get("initContext");
        String chatName = getChatName(result);
        String originChatId = UUIDUtil.uuid();
        AppBuilderAppPo appInfo = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        return QueryChatRsp.builder()
                .appId(appInfo.getId())
                .aippVersion(body.getAippVersion())
                .aippId(body.getAippId())
                .chatName(chatName)
                .chatId(body.getChatId())
                .originChatId(originChatId)
                .msgId(this.persistChat(body, context, originChatId, chatName))
                .version(appInfo.getVersion())
                .massageList(new ArrayList<>())
                .updateTime(LocalDateTime.now().toString())
                .build();
    }

    private String persistChat(CreateChatRequest body, OperationContext context, String chatId, String unCutChatName) {
        String chatName = (unCutChatName.length() > 64) ? unCutChatName.substring(0, 32) : unCutChatName;
        String instId = this.aippRunTimeService.createAippInstance(body.getAippId(),
                body.getAippVersion(),
                body.getInitContext(),
                context);
        Map<String, String> attributesMap = new HashMap<>();
        attributesMap.put("instId", instId);
        if (body.getOriginApp() != null) {
            attributesMap.put("originApp", body.getOriginApp());
            attributesMap.put("originAppVersion", body.getOriginAppVersion());
            this.persistOriginAppChat(body, context, chatId, chatName, instId);
        }
        AppBuilderAppPo appInfo = this.convertAippToApp(body.getAippId(), body.getAippVersion(), context);
        attributesMap.putIfAbsent(AippConst.ATTR_CHAT_STATE_KEY, appInfo.getState());
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(appInfo.getId())
                .version(appInfo.getVersion())
                .attributes(JsonUtils.toJsonString(attributesMap))
                .chatId(body.getChatId() == null ? chatId : body.getChatId())
                .chatName(chatName)
                .status(AippConst.CHAT_STATUS)
                .updater(context.getName())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .creator(context.getName())
                .build();
        ChatAndInstanceMap wideRelationInfo = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid())
                .instanceId(instId)
                .chatId(body.getChatId() == null ? chatId : body.getChatId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.aippChatMapper.insertChat(chatInfo);
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
        return instId;
    }

    private void persistOriginAppChat(CreateChatRequest body, OperationContext context, String chatId, String chatName,
            String instId) {
        // @应用对话，插入主应用记录
        Map<String, String> attributesMapOrigin = new HashMap<>();
        attributesMapOrigin.put("instId", instId);
        AppBuilderAppPo appBuilderAppPO = this.appBuilderAppMapper.selectWithId(body.getOriginApp());
        attributesMapOrigin.put(AippConst.ATTR_CHAT_STATE_KEY, appBuilderAppPO.getState());
        ChatInfo chatInfoOrigin = ChatInfo.builder()
                .appId(body.getOriginApp())
                .version(body.getOriginAppVersion())
                .attributes(JsonUtils.toJsonString(attributesMapOrigin))
                .chatId(chatId)
                .chatName(chatName)
                .status(AippConst.CHAT_STATUS)
                .updater(context.getName())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .creator(context.getName())
                .build();
        ChatAndInstanceMap wideRelationInfoOrigin = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid())
                .instanceId(instId)
                .chatId(chatId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        this.aippChatMapper.insertChat(chatInfoOrigin);
        this.aippChatMapper.insertWideRelationship(wideRelationInfoOrigin);
    }

    private AppBuilderAppPo convertAippToApp(String aippId, String appVersion, OperationContext context) {
        AppTask task = this.appTaskService.getLatest(aippId, appVersion, context).orElseThrow(() -> {
            LOGGER.error("The app task is not found. [appSuiteId={}, version={}]", aippId, appVersion);
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });
        return this.appBuilderAppMapper.selectWithId(task.getEntity().getAppId());
    }

    private QueryChatRequest buildQueryHistoryChatRequest(QueryChatRequest body)
        throws AippTaskNotFoundException {
        QueryChatRequest request = QueryChatRequest.builder().build();
        request.setAppState(body.getAppState());
        request.setLimit(body.getLimit());
        request.setOffset(body.getOffset());
        this.validate(body);
        if (body.getAippId() != null) {
            request.setAippId(body.getAippId());
            return request;
        }
        if (body.getAppId() != null) {
            String appSuiteId = this.appVersionRepository.getAppSuiteIdByAppId(body.getAppId());
            request.setAippId(appSuiteId);
            return request;
        }
        return request;
    }

    /**
     * 判断入参是否合理：当前应用删除时，会有 aippId、aippVersion、appId、appVersion 都为空的场景
     *
     * @param body 表示待校验的入参
     */
    private void validate(QueryChatRequest body) {
        String aippId = body.getAippId();
        String appId = body.getAppId();
        if (StringUtils.isEmpty(aippId) && StringUtils.isEmpty(appId)) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
    }

    @Override
    public QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context)
            throws AippTaskNotFoundException {
        QueryChatRsp rsp = new QueryChatRsp();
        QueryChatRequest request = this.buildQueryHistoryChatRequest(body);
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(request, chatId, context.getAccount());
        if (chatResult != null && !chatResult.isEmpty() && chatResult.get(0) != null) {
            rsp = chatResult.get(0);
        } else {
            return rsp;
        }
        List<ChatDto> result = this.aippChatMapper.selectChat(chatId, body.getOffset(), body.getLimit());
        getChatAppInfo(result, body.getAippId(), context);
        List<MessageInfo> msgList = new ArrayList<>();
        result.forEach((chat) -> {
            AippLogData data = JsonUtils.parseObject(chat.getLogData(), AippLogData.class);
            String content = data.getMsg();
            MessageInfo messageInfo = MessageInfo.builder()
                    .contentType(0)
                    .content(Collections.singletonList(content))
                    .role((AippInstLogType.QUESTION.name().equals(chat.getLogType())) ? "USER" : "SYSTEM")
                    .createTime(chat.getCreateTime())
                    .msgId(chat.getMsgId())
                    .appName(chat.getAppName())
                    .appIcon(chat.getAppIcon())
                    .build();
            msgList.add(messageInfo);
        });
        Integer total = this.aippChatMapper.countChat(chatId);
        Map<String, Object> mapTypes = JsonUtils.parseObject(rsp.getMsgId());
        rsp.setMsgId(String.valueOf(mapTypes.get("instId")));
        rsp.setAippId(body.getAippId());
        rsp.setAippVersion(body.getAippVersion());
        rsp.setTotal(total * 2);
        rsp.setMassageList(msgList);
        return rsp;
    }

    private void getChatAppInfo(List<ChatDto> chatList, String originAippId, OperationContext context) {
        // 620出包需要 与logService的getAippLogWithAppInfo逻辑雷同 后续要整改
        List<String> atAippIds = chatList.stream()
                .map(ChatDto::getAippId)
                .filter(aippId -> !Objects.equals(aippId, originAippId))
                .collect(Collectors.toList());

        RangedResultSet<AppTask> resultSet = this.appTaskService.getTasks(AppTask.asQueryEntity(0, atAippIds.size())
                .latest()
                .addAppSuiteIds(atAippIds)
                .build(), context);
        if (!resultSet.isEmpty()) {
            Map<String, AppTask> taskMap = resultSet.getResults().stream()
                    .collect(Collectors.toMap(t -> t.getEntity().getAppSuiteId(), Function.identity()));
            chatList.stream().filter(c -> taskMap.containsKey(c.getAippId())).forEach(c -> {
                AppTask task = taskMap.get(c.getAippId());
                c.setAppName(task.getEntity().getName());
                c.setAppIcon(task.getEntity().getIcon());
            });
        }
    }

    @Override
    public RangedResultSet<QueryChatRspDto> queryChatList(QueryChatRequest body, OperationContext context) {
        QueryChatRequest request;
        try {
            request = this.buildQueryHistoryChatRequest(body);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(OBTAIN_HISTORY_CONVERSATION_FAILED);
        }
        List<QueryChatRsp> result = this.aippChatMapper.selectChatList(request, null, context.getAccount());
        List<String> instanceIds = result.stream().map(this::getInstanceId).collect(Collectors.toList());
        List<MsgInfoPO> logs = new ArrayList<>();
        if (!instanceIds.isEmpty()) {
            logs = this.aippChatMapper.selectMsgByInstanceIds(instanceIds);
        }
        Map<String, String> finalLogs = logs.stream()
            .collect(Collectors.toMap(MsgInfoPO::getInstanceId, MsgInfoPO::getLogData));
        result.forEach((chat) -> {
            chat.setUpdateTimeStamp(Timestamp.valueOf(chat.getUpdateTime()).getTime());
            chat.setCurrentTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
            chat.setRecentInfo("暂无新消息");
            String instId = getInstanceId(chat);
            chat.setMsgId(instId);
            String log = finalLogs.get(instId);
            if (log != null) {
                AippLogData data = JsonUtils.parseObject(log, AippLogData.class);
                chat.setRecentInfo(data.getMsg());
            }
        });
        long total = this.aippChatMapper.getChatListCount(request, null, context.getAccount());
        return RangedResultSet.create(result.stream().map(this::buildQueryChatRspDto).collect(Collectors.toList()),
            body.getOffset(), body.getLimit(), total);
    }

    private String getInstanceId(QueryChatRsp chat) {
        Map<String, Object> mapTypes = JsonUtils.parseObject(chat.getAttributes());
        return String.valueOf(mapTypes.get("instId"));
    }

    private QueryChatRspDto buildQueryChatRspDto(QueryChatRsp rsp) {
        return QueryChatRspDto.builder()
                .appId(rsp.getAppId())
                .version(rsp.getVersion())
                .aippId(rsp.getAippId())
                .aippVersion(rsp.getAippVersion())
                .chatId(rsp.getChatId())
                .chatName(rsp.getChatName())
                .originChatId(rsp.getOriginChatId())
                .attributes(StringUtils.isBlank(rsp.getAttributes())
                        ? new HashMap<>()
                        : JsonUtils.parseObject(rsp.getAttributes()))
                .massageList(rsp.getMassageList())
                .msgId(rsp.getMsgId())
                .updateTime(rsp.getUpdateTime())
                .recentInfo(rsp.getRecentInfo())
                .updateTimeStamp(rsp.getUpdateTimeStamp())
                .currentTime(rsp.getCurrentTime())
                .total(rsp.getTotal())
                .build();
    }

    @Override
    public Void deleteChat(String chatId, String appId, OperationContext context) {
        this.validationApp(appId);
        if (StringUtils.isNotBlank(chatId)) {
            List<QueryChatRsp> queryChatRsps = this.aippChatMapper.selectChatListByChatIds(
                    Collections.singletonList(chatId));
            if (CollectionUtils.isEmpty(queryChatRsps)) {
                throw new AippException(AippErrCode.CHAT_NOT_FOUND);
            }
            this.aippChatMapper.deleteChat(chatId);
            return null;
        }
        String appSuiteId = this.appVersionRepository.getAppSuiteIdByAppId(appId);
        this.aippChatMapper.deleteAppByAippId(appSuiteId);
        return null;
    }

    private void validationApp(String appId) {
        if (StringUtils.isBlank(appId)) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        AppBuilderApp appBuilderApp = this.appRepository.selectWithId(appId);
        if (appBuilderApp == null || StringUtils.isEmpty(appBuilderApp.getId())) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
    }

    @Override
    public QueryChatRsp updateChat(String originChatId, CreateChatRequest body, OperationContext context)
            throws AippTaskNotFoundException {
        List<QueryChatRsp> chatResult = this.aippChatMapper.selectChatList(null, originChatId, context.getAccount());
        if (chatResult == null || chatResult.size() == 0 || chatResult.get(0) == null) {
            throw new IllegalArgumentException("chatId is not exist");
        }
        // 只查询该应用的近次记录
        Map<String, Object> bodyContext = body.getInitContext();
        bodyContext.put("chatId", originChatId);
        body.setInitContext(bodyContext);
        Map<String, Object> result = ObjectUtils.cast(bodyContext.get(AippConst.BS_INIT_CONTEXT_KEY));
        result.put("chatId", originChatId);
        bodyContext.put(AippConst.BS_INIT_CONTEXT_KEY, result);
        if (body.getOriginApp() != null && body.getChatId() == null) {
            // 首次@应用对话
            String chatId = UUIDUtil.uuid();
            body.setChatId(chatId);
        }
        String chatName = getChatName(result);
        this.persistChat(body, context, originChatId, chatName);
        QueryChatRequest queryBody = QueryChatRequest.builder()
                .aippId(body.getAippId())
                .aippVersion(body.getAippVersion())
                .offset(0)
                .limit(10)
                .build();
        return body.getChatId() == null
                ? queryChat(queryBody, originChatId, context)
                : queryChat(queryBody, body.getChatId(), context);
    }

    @Override
    public List<ChatInfoRspDto> queryChatInfo(QueryChatInfoRequest queryChatInfoRequest, OperationContext context) {
        List<QueryChatRsp> queryChatRsps = this.aippChatMapper.selectChatByCondition(
                queryChatInfoRequest.getCondition(), queryChatInfoRequest);
        return queryChatRsps.stream().map(this::buildChatInfoRspDto).collect(Collectors.toList());
    }

    @Override
    public void saveChatInfo(ChatCreateEntity chatCreateEntity, OperationContext context) {
        this.appChatRepository.saveChat(chatCreateEntity, context);
    }

    private ChatInfoRspDto buildChatInfoRspDto(QueryChatRsp queryChatRsp) {
        return ChatInfoRspDto.builder().chatId(queryChatRsp.getChatId()).build();
    }

    private String getChatName(Map<String, Object> initContext) {
        String chatName;
        if (initContext.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            Object data = initContext.get(AippConst.BS_AIPP_FILE_DESC_KEY);
            if (!(data instanceof Map)) {
                LOGGER.error("The file desc type is not map. [type={}]", data.getClass().getName());
                throw new AippException(AippErrCode.FILE_FORMAT_VERIFICATION_FAILED);
            }
            Map<String, String> fileDesc = ObjectUtils.cast(data);
            chatName = fileDesc.get("file_name");
        } else if (initContext.containsKey(AippConst.BS_AIPP_QUESTION_KEY)) {
            chatName = initContext.get(AippConst.BS_AIPP_QUESTION_KEY).toString();
        } else {
            throw new IllegalArgumentException("Chat has no question.");
        }
        return chatName;
    }
}
