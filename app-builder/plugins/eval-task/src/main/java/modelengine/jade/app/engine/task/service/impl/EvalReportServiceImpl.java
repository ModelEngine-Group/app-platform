/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import modelengine.jade.app.engine.task.convertor.EvalReportConvertor;
import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.mapper.EvalReportMapper;
import modelengine.jade.app.engine.task.po.EvalReportPo;
import modelengine.jade.app.engine.task.service.EvalReportService;
import modelengine.jade.app.engine.task.vo.EvalReportVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalReportService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Component
public class EvalReportServiceImpl implements EvalReportService {
    private final EvalReportMapper reportMapper;
    private final ObjectSerializer serializer;

    public EvalReportServiceImpl(EvalReportMapper reportMapper, @Fit(alias = "json") ObjectSerializer serializer) {
        this.reportMapper = reportMapper;
        this.serializer = serializer;
    }

    @Override
    public void createEvalReport(List<EvalReportEntity> entities) {
        List<EvalReportPo> reportPo =
                entities.stream().map(EvalReportConvertor.INSTANCE::entityToPo).collect(Collectors.toList());
        this.reportMapper.create(reportPo);
    }

    @Override
    public PageVo<EvalReportVo> listEvalReport(EvalReportQueryParam queryParam) {
        List<EvalReportEntity> entities = this.reportMapper.listEvalReport(queryParam);
        int reportCount = this.reportMapper.countEvalReport(queryParam);
        List<EvalReportVo> evalReportVo = entities.stream().map(entity -> {
            EvalReportVo vo = EvalReportConvertor.INSTANCE.entityToVo(entity);
            vo.setHistogram(serializer.deserialize(entity.getHistogram(), int[].class));
            return vo;
        }).collect(Collectors.toList());
        return PageVo.of(reportCount, evalReportVo);
    }
}