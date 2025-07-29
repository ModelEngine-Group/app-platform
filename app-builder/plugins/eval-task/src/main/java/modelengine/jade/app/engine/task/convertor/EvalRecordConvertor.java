/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.convertor;

import modelengine.jade.app.engine.task.dto.EvalRecordCreateDto;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.po.EvalRecordPo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * 定义评估任务用例结果的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalRecordConvertor {
    /**
     * 获取 EvalRecordConvertor 的实现。
     */
    EvalRecordConvertor INSTANCE = Mappers.getMapper(EvalRecordConvertor.class);

    /**
     * 将 {@link EvalRecordEntity} 转换为 {@link EvalRecordPo}。
     *
     * @param entity 表示待转换的 {@link EvalRecordEntity}。
     * @return 表示转换完成的 {@link EvalRecordPo}。
     */
    EvalRecordPo entityToPo(EvalRecordEntity entity);

    /**
     * 将 {@link EvalRecordCreateDto} 转换为 {@link EvalRecordEntity}。
     *
     * @param createDto 表示待转换的 {@link EvalRecordCreateDto}。
     * @return 表示转换完成的 {@link EvalRecordCreateDto}。
     */
    @Mapping(ignore = true, target = "input")
    EvalRecordEntity dtoToEntity(EvalRecordCreateDto createDto);

    /**
     * 将 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >} 转换为 {@link EvalRecordEntity}。
     *
     * @param map 表示待转换的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     * @return 表示转换完成的 {@link EvalRecordEntity}。
     */
    @Mapping(target = "input", source = "Input")
    @Mapping(target = "score", source = "Score")
    @Mapping(target = "nodeId", source = "NodeId")
    EvalRecordEntity mapToRecordEntity(Map<String, String> map);
}