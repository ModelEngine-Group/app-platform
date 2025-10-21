/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.appfactory.task;

import modelengine.fit.waterflow.service.SingleFlowRuntimeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.util.ThreadUtils;

/**
 * 定时清理流程中已完成的上下文。
 * <p>包括成功、失败、终止的流程数据。</p>
 *
 * @author 杨祥宇
 * @since 2025-04-02
 */
@Component
public class CleanFlowInstances {
    private static final Logger LOG = Logger.get(CleanFlowInstances.class);
    private static final int LIMIT = 1000;
    private static final int BATCH_INTERNAL = 1000;

    private final int expiredDays;
    private final SingleFlowRuntimeService singleFlowRuntimeService;

    public CleanFlowInstances(@Value("${jane.flowsEngine.contextExpiredDays}") int expiredDays,
            SingleFlowRuntimeService singleFlowRuntimeService) {
        this.expiredDays = expiredDays;
        this.singleFlowRuntimeService = singleFlowRuntimeService;
    }

    /**
     * 每天凌晨 3 点定时清理超指定天数的流程运行数据。
     * <p>指定天数来源于 {@code ${jane.flowsEngine.contextExpiredDays}} 配置的值。</p>
     * <p>多实例并发执行分析：会并发执行超期链路信息查询，可能导致重复获取相同 {@code traceIds}，重复删除 {@code traceIds}
     * 以及上下文数据不会对结果有影响。</p>
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 3 * * ?")
    public void cleanContextSchedule() {
        LOG.info("Starting expired flow instances cleaning");
        try {
            while (this.singleFlowRuntimeService.cleanInstances(this.expiredDays, LIMIT)) {
                ThreadUtils.sleep(BATCH_INTERNAL);
            }
        } catch (Exception ex) {
            LOG.error("Clean expired flow instances error. [errorMessage={}]", ex.getMessage());
            LOG.error("Exception:", ex);
        } finally {
            LOG.info("Finished expired flow instances cleaning.");
        }
    }
}
