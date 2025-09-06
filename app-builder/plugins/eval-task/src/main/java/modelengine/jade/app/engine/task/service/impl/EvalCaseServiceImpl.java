/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import modelengine.jade.app.engine.task.convertor.EvalCaseConvertor;
import modelengine.jade.app.engine.task.dto.EvalCaseQueryParam;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.mapper.EvalCaseMapper;
import modelengine.jade.app.engine.task.po.EvalCasePo;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.service.EvalRecordService;
import modelengine.jade.app.engine.task.vo.EvalCaseVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalCaseService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Component
public class EvalCaseServiceImpl implements EvalCaseService {
    private final EvalCaseMapper evalCaseMapper;
    private final EvalRecordService evalRecordService;

    public EvalCaseServiceImpl(EvalCaseMapper evalCaseMapper, EvalRecordService evalRecordService) {
        this.evalCaseMapper = evalCaseMapper;
        this.evalRecordService = evalRecordService;
    }

    @Override
    @Transactional
    public void createEvalCase(EvalCaseEntity entity, List<EvalRecordEntity> records) {
        EvalCasePo evalCasePo = EvalCaseConvertor.INSTANCE.entityToPo(entity);
        this.evalCaseMapper.create(evalCasePo);
        records.forEach(record -> record.setTaskCaseId(evalCasePo.getId()));
        this.evalRecordService.createEvalRecord(records);
    }

    @Override
    public List<EvalCaseEntity> getCaseByInstanceId(Long instanceId) {
        return this.evalCaseMapper.getCaseByInstanceId(instanceId);
    }

    @Override
    public int countByInstanceId(Long instanceId) {
        return this.evalCaseMapper.countByInstanceId(instanceId);
    }

    @Override
    public PageVo<EvalCaseVo> listEvalCase(EvalCaseQueryParam queryParam) {
        List<EvalCaseEntity> caseEntities = evalCaseMapper.listEvalCase(queryParam);
        List<Long> caseIds = caseEntities.stream().map(EvalCaseEntity::getId).collect(Collectors.toList());
        List<EvalRecordEntity> recordEntities = evalRecordService.getEntityByCaseIds(caseIds);
        Map<Long, List<EvalRecordEntity>> recordMap =
                recordEntities.stream().collect(Collectors.groupingBy(EvalRecordEntity::getTaskCaseId));

        List<EvalCaseVo> caseVos = caseEntities.stream().map(entity -> {
            EvalCaseVo vo = new EvalCaseVo();
            vo.setEvalCaseEntity(entity);
            vo.setEvalRecordEntities(recordMap.get(entity.getId()));
            return vo;
        }).collect(Collectors.toList());

        int total = this.evalCaseMapper.countEvalCase(queryParam.getInstanceId());
        return PageVo.of(total, caseVos);
    }
}