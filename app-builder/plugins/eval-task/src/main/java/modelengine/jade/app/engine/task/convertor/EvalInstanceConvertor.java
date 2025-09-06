/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.convertor;

import modelengine.jade.app.engine.task.dto.EvalInstanceUpdateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.po.EvalInstancePo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务实例的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Mapper
public interface EvalInstanceConvertor {
    /**
     * 获取 EvalTaskConvertor 的实现。
     */
    EvalInstanceConvertor INSTANCE = Mappers.getMapper(EvalInstanceConvertor.class);

    /**
     * 将 {@link EvalInstanceUpdateDto} 转换为 {@link EvalInstanceEntity}。
     *
     * @param updateDto 表示待转换的 {@link EvalInstanceUpdateDto}。
     * @return 表示转换完成的 {@link EvalInstanceEntity}。
     */
    EvalInstanceEntity convertDtoToEntity(EvalInstanceUpdateDto updateDto);

    /**
     * 将 {@link EvalInstanceEntity} 转换为 {@link EvalInstancePo}。
     *
     * @param entity 表示待转换的 {@link EvalInstanceEntity}。
     * @return 表示转换完成的 {@link EvalInstancePo}。
     */
    EvalInstancePo entityToPo(EvalInstanceEntity entity);
}