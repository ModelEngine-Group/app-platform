/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import modelengine.jade.app.engine.task.convertor.EvalRecordConvertor;
import modelengine.jade.app.engine.task.dto.EvalRecordQueryParam;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.mapper.EvalRecordMapper;
import modelengine.jade.app.engine.task.po.EvalRecordPo;
import modelengine.jade.app.engine.task.service.EvalRecordService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalRecordService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Component
public class EvalRecordServiceImpl implements EvalRecordService {
    private final EvalRecordMapper evalRecordMapper;

    public EvalRecordServiceImpl(EvalRecordMapper evalRecordMapper) {
        this.evalRecordMapper = evalRecordMapper;
    }

    @Override
    public void createEvalRecord(List<EvalRecordEntity> result) {
        List<EvalRecordPo> resultPo =
                result.stream().map(EvalRecordConvertor.INSTANCE::entityToPo).collect(Collectors.toList());
        this.evalRecordMapper.create(resultPo);
    }

    @Override
    public PageVo<EvalRecordEntity> listEvalRecord(EvalRecordQueryParam queryParam) {
        List<EvalRecordEntity> instanceEntities = this.evalRecordMapper.listEvalRecord(queryParam);
        int instanceCount = this.evalRecordMapper.countEvalRecord(queryParam);
        return PageVo.of(instanceCount, instanceEntities);
    }

    @Override
    public List<EvalRecordEntity> getEntityByCaseIds(List<Long> caseIds) {
        return this.evalRecordMapper.getEntityByCaseIds(caseIds);
    }
}