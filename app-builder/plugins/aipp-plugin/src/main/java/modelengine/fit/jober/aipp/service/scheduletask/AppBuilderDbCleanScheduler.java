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
 * 数据库清理定时任务执行器。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
@Component
public class AppBuilderDbCleanScheduler {
    private static final Logger log = Logger.get(AppBuilderDbCleanScheduler.class);

    /**
     * 表示待清理的数据行数上限。
     */
    private static final int LIMIT = 1000;

    /**
     * 表示备份文件的最大数量。
     */
    public static final int FILE_MAX_NUM = 15;

    private final int nonBusinessDataTtl;
    private final int businessDataTtl;
    private final AippInstanceLogCleaner aippInstanceLogCleaner;
    private final ChatSessionCleaner chatSessionCleaner;
    private final AppBuilderRuntimeInfoCleaner appBuilderRuntimeInfoCleaner;

    /**
     * 表示用对话清理器和运行时日志清理器构造 {@link AppBuilderDbCleanScheduler} 的实例。
     *
     * @param nonBusinessDataTtl 表示非业务数据的过期时间的 {@link String}。
     * @param businessDataTtl 表示业务数据的过期时间的 {@link String}。
     * @param aippInstanceLogCleaner 表示日志清理器的 {@link AippInstanceLogCleaner}。
     * @param chatSessionCleaner 表示对话清理器的 {@link ChatSessionCleaner}。
     * @param appBuilderRuntimeInfoCleaner 表示运行时信息清理器的 {@link AppBuilderRuntimeInfoCleaner}。
     */
    public AppBuilderDbCleanScheduler(@Value("${app-engine.ttl.nonBusinessData}") int nonBusinessDataTtl,
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
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 3 * * ?")
    public void appBuilderDbCleanSchedule() {
        try {
            // 清理非业务数据
            this.aippInstanceLogCleaner.cleanAippInstancePreviewLog(this.nonBusinessDataTtl, LIMIT);
            this.appBuilderRuntimeInfoCleaner.clean(this.nonBusinessDataTtl, LIMIT);

            // 清理业务数据
            this.aippInstanceLogCleaner.cleanAippInstanceNormalLog(this.businessDataTtl, LIMIT);
            this.chatSessionCleaner.clean(this.businessDataTtl, LIMIT);
        } catch (Exception e) {
            log.error("App builder Db Clean Error, exception:", e);
        }
    }
}
