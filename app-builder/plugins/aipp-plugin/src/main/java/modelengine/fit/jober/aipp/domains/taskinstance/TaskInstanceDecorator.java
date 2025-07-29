/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import modelengine.fit.jober.aipp.common.AppTaskRunnable;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;

import lombok.Getter;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fitframework.util.ObjectUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 应用实例的装饰类.
 *
 * @author 张越
 * @since 2025-01-10
 */
@Getter
public class TaskInstanceDecorator implements AppTaskRunnable {
    private static final Logger log = LoggerFactory.getLogger(TaskInstanceDecorator.class);

    private final AppTaskInstance instance;

    private AppTaskRunnable proxy;

    private TaskInstanceDecorator(AppTaskInstance instance) {
        this.instance = instance;
        this.proxy = instance;
    }

    /**
     * 创建装饰器.
     *
     * @param instance 实例对象
     * @return {@link TaskInstanceDecorator} 装饰器对象.
     */
    public static TaskInstanceDecorator create(AppTaskInstance instance) {
        return new TaskInstanceDecorator(instance);
    }

    /**
     * 对instance的run接口进行装饰，为其添加chat相关能力.
     *
     * @param appChatSessionService {@link AppChatSessionService} 对象.
     * @param appChatSSEService {@link AppChatSseService} 对象.
     * @return {@link TaskInstanceDecorator} 装饰器对象.
     */
    public TaskInstanceDecorator chat(AppChatSessionService appChatSessionService,
            AppChatSseService appChatSSEService) {
        AppTaskRunnable current = this.proxy;
        Object newProxy = Proxy.newProxyInstance(current.getClass().getClassLoader(),
                current.getClass().getInterfaces(), (p, method, args) -> {
                    if (method.getName().startsWith("run") && method.getParameterCount() == 2) {
                        return interceptChat(method, args, current, appChatSessionService, appChatSSEService);
                    }
                    return method.invoke(current, args);
                });
        this.proxy = ObjectUtils.cast(newProxy);
        return this;
    }

    private Object interceptChat(Method method, Object[] args, AppTaskRunnable current,
            AppChatSessionService appChatSessionService, AppChatSseService appChatSSEService)
            throws IllegalAccessException, InvocationTargetException {
        ChatSession<Object> session = ObjectUtils.cast(args[1]);
        if (session == null) {
            return method.invoke(current, args);
        }
        RunContext ctx = ObjectUtils.cast(args[0]);
        appChatSessionService.addSession(this.instance.getId(), session);
        sendReady(this.instance, ctx, appChatSSEService);
        Object result = method.invoke(current, args);

        // enable memory并且是user_select时，不在结束后发送ready信息，和原逻辑保持一致.
        if (!ctx.getMemoryConfig().getEnableMemory() || !ctx.isUserCustomMemory()) {
            sendReady(this.instance, ctx, appChatSSEService);
        }
        return result;
    }

    private void sendReady(AppTaskInstance instance, RunContext ctx, AppChatSseService appChatSSEService) {
        appChatSSEService.send(instance.getId(), AppChatRsp.builder()
                .instanceId(instance.getId())
                .status(FlowTraceStatus.READY.name())
                .atChatId(ObjectUtils.cast(ctx.getAtChatId()))
                .chatId(ObjectUtils.cast(ctx.getOriginChatId()))
                .build());
    }

    /**
     * 对instance的run接口进行装饰，为其添加异常处理及日志相关能力.
     *
     * @param instanceService {@link AppTaskInstanceService} 对象.
     * @param logService {@link AippLogService} 对象.
     * @return {@link TaskInstanceDecorator} 装饰器对象.
     */
    public TaskInstanceDecorator exceptionLog(AppTaskInstanceService instanceService, AippLogService logService) {
        AppTaskRunnable current = this.proxy;
        Object newProxy = Proxy.newProxyInstance(current.getClass().getClassLoader(),
                current.getClass().getInterfaces(),
                (p, method, args) -> this.wrapException(instanceService, logService, method, args, current));
        this.proxy = ObjectUtils.cast(newProxy);
        return this;
    }

    private Object wrapException(AppTaskInstanceService instanceService, AippLogService logService, Method method,
            Object[] args, AppTaskRunnable current) throws IllegalAccessException, InvocationTargetException {
        if (!method.getName().startsWith("run")) {
            return method.invoke(current, args);
        }

        RunContext ctx = ObjectUtils.cast(args[0]);
        try {
            return method.invoke(current, args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof AippException) {
                instanceService.update(AppTaskInstance.asUpdate(this.instance.getTaskId(), this.instance.getId())
                        .setFinishTime(LocalDateTime.now())
                        .setStatus(MetaInstStatusEnum.ERROR.name())
                        .build(), ctx.getOperationContext());
                // 更新日志类型为HIDDEN_FORM
                logService.insertLogWithInterception(AippInstLogType.ERROR.name(), AippLogData.builder().msg(e.getMessage()).build(),
                        ctx.getBusinessData());
            }
            return null;
        }
    }

    @Override
    public void run(RunContext context) {
        Optional.ofNullable(this.proxy).orElse(this.instance).run(context);
    }

    @Override
    public void run(RunContext context, ChatSession<Object> chatSession) {
        Optional.ofNullable(this.proxy).orElse(this.instance).run(context, chatSession);
    }
}
