/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.convertor;

import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.po.EvalReportPo;
import modelengine.jade.app.engine.task.vo.EvalReportVo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务报告的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Mapper
public interface EvalReportConvertor {
    /**
     * 获取 EvalReportConvertor 的实现。
     */
    EvalReportConvertor INSTANCE = Mappers.getMapper(EvalReportConvertor.class);

    /**
     * 将 {@link EvalReportEntity} 转换为 {@link EvalReportPo}。
     *
     * @param entity 表示待转换的 {@link EvalReportEntity}。
     * @return 表示转换完成的 {@link EvalReportPo}。
     */
    EvalReportPo entityToPo(EvalReportEntity entity);

    /**
     * 将 {@link EvalReportVo} 转换为 {@link EvalReportEntity}。
     *
     * @param entity 表示待转换的 {@link EvalReportVo}。
     * @return 表示转换完成的 {@link EvalReportEntity}。
     */
    @Mapping(ignore = true, target = "histogram")
    EvalReportVo entityToVo(EvalReportEntity entity);
}