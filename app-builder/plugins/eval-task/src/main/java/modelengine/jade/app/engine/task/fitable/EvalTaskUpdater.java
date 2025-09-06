/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.ENTITY_NOT_FOUND;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_INPUT_PARAM;

import modelengine.fit.waterflow.entity.FlowTransCompletionInfo;
import modelengine.fit.waterflow.spi.FlowCompletedService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.exception.EvalTaskException;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.app.engine.task.service.EvalRecordService;
import modelengine.jade.app.engine.task.service.EvalReportService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * 评估流程结束回调节点。
 *
 * @author 何嘉斌
 * @since 2024-08-22
 */
@Component
public class EvalTaskUpdater implements FlowCompletedService {
    private final EvalInstanceService evalInstanceService;

    private final EvalCaseService evalCaseService;

    private final EvalRecordService evalRecordService;

    private final EvalReportService evalReportService;

    private final ObjectSerializer serializer;

    public EvalTaskUpdater(EvalInstanceService evalInstanceService, EvalCaseService evalCaseService,
            EvalRecordService evalRecordService, EvalReportService evalReportService,
            @Fit(alias = "json") ObjectSerializer serializer) {
        this.evalInstanceService = evalInstanceService;
        this.evalCaseService = evalCaseService;
        this.evalRecordService = evalRecordService;
        this.evalReportService = evalReportService;
        this.serializer = serializer;
    }

    @Override
    @Fitable(id = "modelengine.jade.app.engine.task.fitable.EvalTaskUpdater")
    public void callback(FlowTransCompletionInfo info) {
        Long instanceId = 0L;
        try {
            Validation.notEmpty(info.getFlowTraceIds(),
                    () -> new EvalTaskException(EVAL_TASK_INPUT_PARAM, "FLowTraceIds"));
            String traceId = info.getFlowTraceIds().get(0);

            instanceId = this.evalInstanceService.getEvalInstanceId(traceId);
            if (instanceId == null) {
                return;
            }

            this.tryCreateReport(instanceId);
            this.tryUpdateInstance(instanceId);
        } catch (EvalTaskException e) {
            EvalInstanceEntity instanceEntity = new EvalInstanceEntity();
            instanceEntity.setId(instanceId);
            instanceEntity.setStatus(EvalInstanceStatusEnum.FAILED);
            instanceEntity.setFinishedAt(LocalDateTime.now());
            this.evalInstanceService.updateEvalInstance(instanceEntity);
        }
    }

    private List<Double> normalize(List<Double> list) {
        BigDecimal min = BigDecimal.valueOf(Collections.min(list));
        BigDecimal max = BigDecimal.valueOf(Collections.max(list));

        if (max.compareTo(min) == 0) {
            return list.stream().map(value -> max.doubleValue()).collect(Collectors.toList());
        }
        return list.stream()
                .map(value -> ((value - min.doubleValue()) / (max.doubleValue() - min.doubleValue())) * 100)
                .collect(Collectors.toList());
    }

    private String listToBins(List<Double> list) {
        int[] bins = new int[10];
        list.forEach(val -> bins[(int) Math.min(val / 10.0, 9)]++);
        return this.serializer.serialize(bins);
    }

    private void tryCreateReport(Long instanceId) {
        List<EvalCaseEntity> caseEntities = this.evalCaseService.getCaseByInstanceId(instanceId);
        Validation.notEmpty(caseEntities, () -> new EvalTaskException(ENTITY_NOT_FOUND, "caseId", instanceId));
        List<EvalRecordEntity> recordEntities = this.evalRecordService.getEntityByCaseIds(caseEntities.stream()
                .map(EvalCaseEntity::getId)
                .collect(Collectors.toList()));

        Map<String, List<EvalRecordEntity>> algoRecords =
                recordEntities.stream().collect(Collectors.groupingBy(EvalRecordEntity::getNodeId));

        List<EvalReportEntity> reportEntities = algoRecords.entrySet().stream().map(entry -> {
            String nodeId = entry.getKey();
            List<EvalRecordEntity> records = entry.getValue();
            List<Double> scores =
                    this.normalize(records.stream().map(EvalRecordEntity::getScore).collect(Collectors.toList()));
            OptionalDouble avg = scores.stream().mapToDouble(Double::doubleValue).average();
            EvalReportEntity entity = new EvalReportEntity();
            entity.setNodeId(nodeId);
            entity.setInstanceId(instanceId);
            entity.setAverageScore((avg.isPresent() && !Double.valueOf(avg.getAsDouble()).isNaN())
                    ? avg.getAsDouble()
                    : 0);
            entity.setHistogram(this.listToBins(scores));
            return entity;
        }).collect(Collectors.toList());

        this.evalReportService.createEvalReport(reportEntities);
    }

    private void tryUpdateInstance(Long instanceId) {
        List<EvalCaseEntity> caseEntities = this.evalCaseService.getCaseByInstanceId(instanceId);
        Validation.notEmpty(caseEntities, () -> new EvalTaskException(ENTITY_NOT_FOUND, "caseId", instanceId));

        EvalInstanceEntity instanceEntity = new EvalInstanceEntity();
        instanceEntity.setId(instanceId);
        instanceEntity.setStatus(EvalInstanceStatusEnum.SUCCESS);
        instanceEntity.setPassCount((int) caseEntities.stream().filter(EvalCaseEntity::getPass).count());
        instanceEntity.setPassRate((double) instanceEntity.getPassCount() / caseEntities.size());
        instanceEntity.setFinishedAt(LocalDateTime.now());
        this.evalInstanceService.updateEvalInstance(instanceEntity);
    }
}