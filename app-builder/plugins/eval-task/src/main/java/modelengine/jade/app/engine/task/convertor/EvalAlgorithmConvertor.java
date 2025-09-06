/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.convertor;

import modelengine.jade.app.engine.task.entity.EvalAlgorithmEntity;
import modelengine.jade.app.engine.task.po.EvalAlgorithmPo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估算法的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Mapper
public interface EvalAlgorithmConvertor {
    /**
     *  评估算法的转换器实例。
     */
    EvalAlgorithmConvertor INSTANCE = Mappers.getMapper(EvalAlgorithmConvertor.class);

    /**
     * 将 {@link EvalAlgorithmEntity} 转换为 {@link EvalAlgorithmPo}。
     *
     * @param entity 表示待转换的 {@link EvalAlgorithmEntity}。
     * @return 表示转换完成的 {@link EvalAlgorithmPo}。
     */
    EvalAlgorithmPo entityToPo(EvalAlgorithmEntity entity);
}