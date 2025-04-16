/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;

/**
 * 数据库定时清理数据任务。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
@Component
public class AppBuilderDbCleanSchedule {
    private static final Logger log = Logger.get(AppBuilderDbCleanSchedule.class);

    private static final int LIMIT = 1000;

    public static final int FILE_MAX_NUM = 15;

    private final int nonBusinessDataTtl;

    private final int businessDataTtl;

    private final AippInstanceLogCleaner aippInstanceLogCleaner;

    private final ChatSessionCleaner chatSessionCleaner;

    private final AppBuilderRuntimeInfoCleaner appBuilderRuntimeInfoCleaner;

    public AppBuilderDbCleanSchedule(@Value("${app-engine.ttl.nonBusinessData}") int nonBusinessDataTtl,
            @Value("${app-engine.ttl.businessData}") int businessDataTtl, AippInstanceLogCleaner aippInstanceLogCleaner,
            ChatSessionCleaner chatSessionCleaner, AppBuilderRuntimeInfoCleaner appBuilderRuntimeInfoCleaner) {
        this.nonBusinessDataTtl = nonBusinessDataTtl;
        this.businessDataTtl = businessDataTtl;
        this.aippInstanceLogCleaner = aippInstanceLogCleaner;
        this.chatSessionCleaner = chatSessionCleaner;
        this.appBuilderRuntimeInfoCleaner = appBuilderRuntimeInfoCleaner;
    }

    /**
     * 每天凌晨 3 点定时清理超期指定天数的应用相关数据。
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 * * * ?")
    public void appBuilderDbCleanSchedule() {
        try {
            // 清理非业务数据
            aippInstanceLogCleaner.cleanAippInstancePreviewLog(nonBusinessDataTtl, LIMIT);
            appBuilderRuntimeInfoCleaner.appBuilderRuntimeInfoCleaner(nonBusinessDataTtl, LIMIT);

            // 注意：未来业务相关的表新增字段时，这里的备份文件也要新增字段
            // 清理业务数据
            aippInstanceLogCleaner.cleanAippInstanceNormalLog(businessDataTtl, LIMIT);
            chatSessionCleaner.chatSessionCleaner(businessDataTtl, LIMIT);
        } catch (Exception e) {
            log.error("App builder Db Clean Error, exception:", e);
        }
    }
}
