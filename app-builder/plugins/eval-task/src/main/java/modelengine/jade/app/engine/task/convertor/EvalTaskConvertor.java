/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.convertor;

import modelengine.jade.app.engine.task.dto.EvalTaskCreateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.entity.EvalTaskEntity;
import modelengine.jade.app.engine.task.po.EvalTaskPo;
import modelengine.jade.app.engine.task.vo.EvalTaskVo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 定义评估任务的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Mapper
public interface EvalTaskConvertor {
    /**
     * 获取 EvalTaskConvertor 的实现。
     */
    EvalTaskConvertor INSTANCE = Mappers.getMapper(EvalTaskConvertor.class);

    /**
     * 将 {@link EvalTaskCreateDto} 转换为 {@link EvalTaskEntity}。
     *
     * @param createDto 表示待转换的 {@link EvalTaskCreateDto}。
     * @return 表示转换完成的 {@link EvalTaskEntity}。
     */
    EvalTaskEntity convertDtoToEntity(EvalTaskCreateDto createDto);

    /**
     * 将 {@link EvalTaskEntity} 转换为 {@link EvalTaskPo}。
     *
     * @param entity 表示待转换的 {@link EvalTaskEntity}。
     * @return 表示转换完成的 {@link EvalTaskPo}。
     */
    EvalTaskPo entityToPo(EvalTaskEntity entity);

    /**
     * 将 {@link EvalTaskEntity} 和 {@link EvalInstanceEntity} 拼接成 {@link EvalTaskVo}。
     *
     * @param evalTaskEntity 表示待转换的 {@link EvalTaskEntity}。
     * @param evalInstanceEntity 表示待转换的 {@link EvalInstanceEntity}。
     * @return 表示转换完成的 {@link EvalTaskVo}。
     */
    @Mapping(source = "evalTaskEntity.id", target = "id")
    @Mapping(source = "evalTaskEntity.name", target = "name")
    @Mapping(source = "evalTaskEntity.description", target = "description")
    @Mapping(source = "evalTaskEntity.status", target = "status")
    @Mapping(source = "evalTaskEntity.createdBy", target = "createdBy")
    @Mapping(source = "evalTaskEntity.updatedBy", target = "updatedBy")
    @Mapping(source = "evalTaskEntity.createdAt", target = "createdAt")
    @Mapping(source = "evalTaskEntity.updatedAt", target = "updatedAt")
    @Mapping(source = "evalTaskEntity.appId", target = "appId")
    @Mapping(source = "evalTaskEntity.workflowId", target = "workflowId")
    @Mapping(source = "evalInstanceEntity.id", target = "instanceId")
    @Mapping(source = "evalInstanceEntity.status", target = "instanceStatus")
    @Mapping(source = "evalInstanceEntity.passCount", target = "passCount")
    @Mapping(source = "evalInstanceEntity.passRate", target = "passRate")
    @Mapping(source = "evalInstanceEntity.createdBy", target = "instanceCreatedBy")
    @Mapping(source = "evalInstanceEntity.createdAt", target = "instanceCreatedAt")
    @Mapping(source = "evalInstanceEntity.finishedAt", target = "instanceFinishedAt")
    EvalTaskVo mapToVo(EvalTaskEntity evalTaskEntity, EvalInstanceEntity evalInstanceEntity);
}