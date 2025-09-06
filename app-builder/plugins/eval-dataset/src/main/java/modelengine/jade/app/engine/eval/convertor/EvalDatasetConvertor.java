/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.convertor;

import modelengine.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDatasetEntity;
import modelengine.jade.app.engine.eval.po.EvalDatasetPo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估数据的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-01
 */
@Mapper
public interface EvalDatasetConvertor {
    /**
     * 获取 EvalConvertor 的实现。
     */
    EvalDatasetConvertor INSTANCE = Mappers.getMapper(EvalDatasetConvertor.class);

    /**
     * 将 {@link EvalDatasetCreateDto} 转化为 {@link EvalDatasetEntity}。
     *
     * @param createDto 表示待转换的 {@link EvalDatasetCreateDto}。
     * @return 转换完成的 {@link EvalDatasetEntity}。
     */
    EvalDatasetEntity convertDtoToEntity(EvalDatasetCreateDto createDto);

    /**
     * 将 {@link EvalDatasetUpdateDto} 转化为 {@link EvalDatasetEntity}。
     *
     * @param updateDto 表示待转换的 {@link EvalDatasetUpdateDto}。
     * @return 转换完成的 {@link EvalDatasetEntity}。
     */
    EvalDatasetEntity convertDtoToEntity(EvalDatasetUpdateDto updateDto);

    /**
     * 将 {@link EvalDatasetEntity} 转化为 {@link EvalDatasetPo}。
     *
     * @param entity 表示待转换的 {@link EvalDatasetEntity}。
     * @return 转换完成的 {@link EvalDatasetPo}。
     */
    EvalDatasetPo entityToPo(EvalDatasetEntity entity);
}