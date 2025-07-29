/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.convertor;

import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.po.EvalCasePo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务用例的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalCaseConvertor {
    /**
     * 获取 EvalTaskCaseConvertor 的实现。
     */
    EvalCaseConvertor INSTANCE = Mappers.getMapper(EvalCaseConvertor.class);

    /**
     * 将 {@link EvalCaseEntity} 转换为 {@link EvalCasePo}。
     *
     * @param entity 表示待转换的 {@link EvalCaseEntity}。
     * @return 表示转换完成的 {@link EvalCasePo}。
     */
    EvalCasePo entityToPo(EvalCaseEntity entity);
}