/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service.scheduletasks;

import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.transaction.Transactional;

import java.util.List;

/**
 * 定时清理流程中已完成的context
 * 包括成功、失败、终止的流程数据
 *
 * @author 杨祥宇
 * @since 2025-04-02
 */
@Component
public class CleanContextSchedule {
    private static final Logger log = Logger.get(CleanContextSchedule.class);
    private static final int LIMIT = 1000;
    private final FlowTraceRepo flowTraceRepo;
    private final FlowContextRepo flowContextRepo;
    private final int expiredDays;

    public CleanContextSchedule(FlowTraceRepo flowTraceRepo, @Fit(alias = "flowContextPersistRepo") FlowContextRepo
            flowContextRepo, @Value("${jane.flowsEngine.contextExpiredDays}") int expiredDays) {
        this.flowTraceRepo = flowTraceRepo;
        this.flowContextRepo = flowContextRepo;
        this.expiredDays = expiredDays;
    }

    /**
     * 每天凌晨3点定时清理超期EXPIRED_DAYS天的流程运行数据
     * 多实例并发执行分析：会并发执行getExpiredTrace()查询，可能导致重复获取相同traceIds
     * 重复删除trace以及context数据不会对结果有影响
     *
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 3 * * ?")
    public void cleanContextSchedule() {
        log.info("Start clean flow expired contexts");
        try {
            List<String> traceIds = flowTraceRepo.getExpiredTrace(expiredDays, LIMIT);
            while (!traceIds.isEmpty()) {
                deleteFlowContext(traceIds);
                traceIds = flowTraceRepo.getExpiredTrace(expiredDays, LIMIT);
                SleepUtil.sleep(60000);
            }
        } catch (Exception ex) {
            log.error("Clean context error, error message: {}" + ex.getMessage());
        }
    }

    /**
     * 根据traceId列表删除trace和context数据
     *
     * @param traceIds 表示流程trace id列表的{@link List}{@code <}{@link String}{@code >}。
     */
    @Transactional
    public void deleteFlowContext(List<String> traceIds) {
        flowContextRepo.deleteByTraceIdList(traceIds);
        flowTraceRepo.deleteByIdList(traceIds);
    }

}
