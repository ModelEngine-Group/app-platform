/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import modelengine.fit.jober.aipp.common.AppTaskRunnable;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.jade.common.globalization.LocaleService;

import lombok.Getter;
import modelengine.fitframework.util.ObjectUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 应用任务的装饰类.
 *
 * @author 张越
 * @since 2025-01-13
 */
@Getter
public class TaskDecorator implements AppTaskRunnable {
    private static final String UI_WORD_KEY = "aipp.service.impl.AippRunTimeServiceImpl";

    private static final Logger log = LoggerFactory.getLogger(TaskDecorator.class);

    private final AppTask task;
    private final AippLogService aippLogService;
    private final AppTaskInstanceService appTaskInstanceService;
    private final LocaleService localeService;

    private AppTaskRunnable proxy;

    private TaskDecorator(AppTask task, AippLogService aippLogService, AppTaskInstanceService appTaskInstanceService,
            LocaleService localeService) {
        this.task = task;
        this.proxy = task;
        this.aippLogService = aippLogService;
        this.appTaskInstanceService = appTaskInstanceService;
        this.localeService = localeService;
    }

    /**
     * 创建装饰器.
     *
     * @param task 任务对象.
     * @param aippLogService {@link AippLogService} 对象.
     * @param appTaskInstanceService {@link AppTaskInstanceService} 对象.
     * @param localeService {@link LocaleService} 对象.
     * @return {@link TaskDecorator} 装饰器对象.
     */
    public static TaskDecorator create(AppTask task, AippLogService aippLogService,
            AppTaskInstanceService appTaskInstanceService, LocaleService localeService) {
        return new TaskDecorator(task, aippLogService, appTaskInstanceService, localeService);
    }

    /**
     * 对instance的run接口进行装饰，为其添加chat相关能力.
     *
     * @return {@link TaskDecorator} 装饰器对象.
     */
    public TaskDecorator exceptionLog() {
        Object current = this.proxy;
        Object newProxy = Proxy.newProxyInstance(current.getClass().getClassLoader(),
                current.getClass().getInterfaces(), (p, method, args) -> {
                    if (method.getName().startsWith("run")) {
                        RunContext ctx = ObjectUtils.cast(args[0]);
                        try {
                            return method.invoke(current, args);
                        } catch (InvocationTargetException e) {
                            log.error("Error occurs when run a task:", e);
                            appTaskInstanceService.update(
                                    AppTaskInstance.asUpdate(this.task.getEntity().getTaskId(), ctx.getTaskInstanceId())
                                            .setFinishTime(LocalDateTime.now())
                                            .setStatus(MetaInstStatusEnum.ERROR.name())
                                            .build(), ctx.getOperationContext());
                            String msg = localeService.localize(UI_WORD_KEY);
                            this.aippLogService.insertLog(AippInstLogType.ERROR.name(),
                                    AippLogData.builder().msg(msg).build(), ctx.getBusinessData());
                            return null;
                        }
                    }
                    return method.invoke(current, args);
                });
        this.proxy = ObjectUtils.cast(newProxy);
        return this;
    }

    @Override
    public void run(RunContext context) {
        Optional.ofNullable(this.proxy).orElse(this.task).run(context);
    }

    @Override
    public void run(RunContext context, ChatSession<Object> chatSession) {
        Optional.ofNullable(this.proxy).orElse(this.task).run(context, chatSession);
    }
}
