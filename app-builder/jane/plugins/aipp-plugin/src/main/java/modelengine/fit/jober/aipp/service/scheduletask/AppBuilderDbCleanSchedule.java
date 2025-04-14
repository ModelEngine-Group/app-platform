/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import modelengine.fit.jober.aipp.repository.AippChatRepository;
import modelengine.fit.jober.aipp.repository.AippInstanceLogRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;

import java.util.List;

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

    private final int nonBusinessDataTtl;

    private final int businessDataTtl;

    private final AippInstanceLogRepository instanceLogRepo;

    private final AppBuilderRuntimeInfoRepository runtimeInfoRepo;

    private final AippChatRepository chatRepo;

    public AppBuilderDbCleanSchedule(@Value("${app-engine.ttl.nonBusinessData}") int nonBusinessDataTtl,
            @Value("${app-engine.ttl.businessData}") int businessDataTtl, AippInstanceLogRepository instanceLogRepo,
            AppBuilderRuntimeInfoRepository runtimeInfoRepo, AippChatRepository chatRepo) {
        this.nonBusinessDataTtl = nonBusinessDataTtl;
        this.businessDataTtl = businessDataTtl;
        this.instanceLogRepo = instanceLogRepo;
        this.runtimeInfoRepo = runtimeInfoRepo;
        this.chatRepo = chatRepo;
    }

    /**
     * 每天凌晨 3 点定时清理超期指定天数的应用相关数据。
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 3 * * ?")
    public void appBuilderDbCleanSchedule() {
        aippInstanceLogCleaner();
        appBuilderRuntimeInfoCleaner();
        businessDataCleaner();
    }

    private void businessDataCleaner() {
        // todo 备份超期的sql文件
        aippInstanceLogCleaner();
        chatSessionCleaner();
    }

    private void chatSessionCleaner() {
        try {
            while (true) {
                List<String> expiredChatIds = chatRepo.getExpiredChatIds(businessDataTtl, LIMIT);
                if (expiredChatIds.isEmpty()) {
                    break;
                }
                chatRepo.forceDeleteChat(expiredChatIds);
            }
        } catch (Exception e) {
            log.error("Error occurred while business data cleaner, exception:.", e);
        }
    }

    private void aippInstanceNormalLogCleaner() {
        try {
            while (true) {
                List<Long> instanceLogIds = instanceLogRepo.getExpireNormalInstanceLogs(businessDataTtl, LIMIT);
                if (instanceLogIds.isEmpty()) {
                    break;
                }
                instanceLogRepo.forceDeleteInstanceLogs(instanceLogIds);
            }
        } catch (Exception e) {
            log.error("Error occurred while business data cleaner, exception:.", e);
        }
    }

    private void aippInstanceLogCleaner() {
        log.info("Start cleaning aipp instance logs");
        try {
            while (true) {
                List<Long> instanceLogIds = instanceLogRepo.getExpirePreviewInstanceLogs(nonBusinessDataTtl, LIMIT);
                if (instanceLogIds.isEmpty()) {
                    break;
                }
                instanceLogRepo.forceDeleteInstanceLogs(instanceLogIds);
            }
        } catch (Exception e) {
            log.error("clean instance logs failed, exception:", e);
        }
        log.info("Finish cleaning aipp instance logs");
    }

    private void appBuilderRuntimeInfoCleaner() {
        log.info("Start cleaning app builder runtime infos");
        try {
            while (true) {
                List<Long> expiredRuntimeInfoIds = runtimeInfoRepo.getExpiredRuntimeInfos(nonBusinessDataTtl, LIMIT);
                if (expiredRuntimeInfoIds.isEmpty()) {
                    break;
                }
                runtimeInfoRepo.deleteRuntimeInfos(expiredRuntimeInfoIds);

            }
        } catch (Exception e) {
            log.error("cleaning app builder runtime infos failed, exception:", e);
        }
        log.info("Finish cleaning app builder runtime infos");
    }
}
