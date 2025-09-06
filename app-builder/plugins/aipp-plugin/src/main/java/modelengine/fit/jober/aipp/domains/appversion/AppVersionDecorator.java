/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_CHAT_ORIGIN_APP_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_CHAT_ORIGIN_APP_VERSION_KEY;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.aipp.util.UsefulUtils;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;

/**
 * 装饰器.
 *
 * @author 张越
 * @since 2025-02-10
 */
public class AppVersionDecorator {
    private final AppVersion appVersion;
    private final AppChatRepository appChatRepository;
    private final AppVersion origin;

    private AppVersionDecorator(AppVersion appVersion, AppChatRepository appChatRepository) {
        this(appVersion, null, appChatRepository);
    }

    private AppVersionDecorator(AppVersion appVersion, AppVersion origin, AppChatRepository appChatRepository) {
        this.appVersion = appVersion;
        this.appChatRepository = appChatRepository;
        this.origin = origin;
    }

    /**
     * 对 appVersion 进行装饰.
     *
     * @param appVersion {@link AppVersion} 对象.
     * @param appChatRepository {@link AippChatService} 对象.
     * @return {@link AppVersionDecorator} 对象.
     */
    public static AppVersionDecorator decorate(AppVersion appVersion, AppChatRepository appChatRepository) {
        return new AppVersionDecorator(appVersion, appChatRepository);
    }

    /**
     * 对 appVersion 进行装饰.
     *
     * @param appVersion {@link AppVersion} 对象.
     * @param origin 最开始的 {@link AppVersion} 对象.
     * @param appChatRepository {@link AppChatRepository} 对象.
     * @return {@link AppVersionDecorator} 对象.
     */
    public static AppVersionDecorator decorate(AppVersion appVersion, AppVersion origin,
            AppChatRepository appChatRepository) {
        return new AppVersionDecorator(appVersion, origin, appChatRepository);
    }

    /**
     * 运行.
     *
     * @param context 上下文.
     * @param session 会话session.
     */
    public void run(RunContext context, ChatSession<Object> session) {
        this.appVersion.run(context, session);
        this.saveChat(context);
    }

    /**
     * 调试 AppVersion，和运行的唯一区别是不需要运行发布过的任务.
     *
     * @param context 运行上下文信息.
     * @param session 会话对象.
     */
    public void debug(RunContext context, ChatSession<Object> session) {
        this.appVersion.debug(context, session);
        this.saveChat(context);
    }

    /**
     * 通过指定任务id，以及任务实例id的方式，重新启动流程.
     *
     * @param instance 任务实例.
     * @param restartParams 重启参数.
     * @param session SSE会话.
     * @param context 操作人上下文对象.
     */
    public void restart(AppTaskInstance instance, Map<String, Object> restartParams, ChatSession<Object> session,
            OperationContext context) {
        this.appVersion.restart(instance, restartParams, session, context, this::saveChat);
    }

    private void saveChat(RunContext rc) {
        Map<String, String> attributes = MapBuilder.<String, String>get()
                .put(AippConst.ATTR_CHAT_INST_ID_KEY, rc.getTaskInstanceId())
                .put(AippConst.ATTR_CHAT_STATE_KEY, this.appVersion.getData().getState())
                .put(AippConst.BS_AIPP_ID_KEY, this.appVersion.getData().getAppSuiteId())
                .build();

        if (this.origin != null) {
            attributes.put(ATTR_CHAT_ORIGIN_APP_KEY, this.origin.getData().getAppId());
            attributes.put(ATTR_CHAT_ORIGIN_APP_VERSION_KEY, this.origin.getData().getVersion());
        } else {
            UsefulUtils.doIfNotBlank(rc.getDimensionId(), (dId) -> attributes.put(AippConst.BS_DIMENSION_ID_KEY, dId));
        }

        this.appChatRepository.saveChat(ChatCreateEntity.builder()
                .appId(this.appVersion.getData().getAppId())
                .appVersion(this.appVersion.getData().getVersion())
                .chatName(rc.getQuestion())
                .chatId(rc.getChatId())
                .taskInstanceId(rc.getTaskInstanceId())
                .attributes(attributes)
                .build(), rc.getOperationContext());
    }
}
