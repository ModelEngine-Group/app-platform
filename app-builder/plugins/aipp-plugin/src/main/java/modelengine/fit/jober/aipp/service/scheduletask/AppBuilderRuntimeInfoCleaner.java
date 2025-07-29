/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

import java.util.List;

/**
 * 应用编排运行时信息清理器。
 *
 * @author 杨祥宇
 * @since 2025-04-15
 */
@Component
public class AppBuilderRuntimeInfoCleaner {
    private static final Logger log = Logger.get(AppBuilderRuntimeInfoCleaner.class);

    private final AppBuilderRuntimeInfoRepository runtimeInfoRepo;

    public AppBuilderRuntimeInfoCleaner(AppBuilderRuntimeInfoRepository runtimeInfoRepo) {
        this.runtimeInfoRepo = runtimeInfoRepo;
    }

    /**
     * 清理对话运行时表数据，并备份。
     *
     * @param expiredDays 表示数据最大保留时长的 {@code int}。
     * @param limit 表示批量处理数量的 {@code int}。
     */
    public void clean(int expiredDays, int limit) {
        log.info("Start cleaning app builder runtime infos");
        try {
            while (true) {
                List<Long> expiredRuntimeInfoIds = this.runtimeInfoRepo.getExpiredRuntimeInfos(expiredDays, limit);
                if (expiredRuntimeInfoIds.isEmpty()) {
                    break;
                }
                this.runtimeInfoRepo.deleteRuntimeInfos(expiredRuntimeInfoIds);
            }
        } catch (Exception e) {
            log.error("cleaning app builder runtime infos failed, exception:", e);
        }
        log.info("Finish cleaning app builder runtime infos");
    }
}
