/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.convertor;

import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jober.entity.task.TaskProperty;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 任务属性转换器
 *
 * @author 刘信宏
 * @since 2023-12-18
 */
@Mapper
public interface TaskPropertyConvertor {
    /**
     * 获取TaskPropertyConvertor的实例
     */
    TaskPropertyConvertor INSTANCE = Mappers.getMapper(TaskPropertyConvertor.class);

    /**
     * 将MetaPropertyDeclarationInfo转换为TaskProperty
     *
     * @param info MetaPropertyDeclarationInfo数据
     * @return TaskProperty
     */
    @Mapping(target = "name", expression = "java(info.getName().getValue())")
    @Mapping(target = "dataType", expression = "java(info.getDataType().getValue())")
    @Mapping(target = "description", expression = "java(info.getDescription().getValue())")
    @Mapping(target = "scope", expression = "java(info.getScope().getValue())")
    @Mapping(target = "required", ignore = true)
    @Mapping(target = "identifiable", ignore = true)
    @Mapping(target = "appearance", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "categories", ignore = true)
    TaskProperty fromMetaPropertyDeclarationInfo(MetaPropertyDeclarationInfo info);

    /**
     * 将TaskProperty 转换为 MetaPropertyDeclarationInfo
     *
     * @param taskProperty TaskProperty数据
     * @return MetaPropertyDeclarationInfo
     */
    @Mapping(target = "name", expression = "java(modelengine.fit.jane.Undefinable.defined(taskProperty.getName()))")
    @Mapping(target = "dataType",
            expression = "java(modelengine.fit.jane.Undefinable.defined(taskProperty.getDataType()))")
    @Mapping(target = "description",
            expression = "java(modelengine.fit.jane.Undefinable.defined(taskProperty.getDescription()))")
    @Mapping(target = "scope", expression = "java(modelengine.fit.jane.Undefinable.defined(taskProperty.getScope()))")
    @Mapping(target = "required", ignore = true)
    @Mapping(target = "identifiable", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    MetaPropertyDeclarationInfo toMetaPropertyDeclarationInfo(TaskProperty taskProperty);
}
