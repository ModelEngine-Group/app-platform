/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.convertor;

import modelengine.fit.dynamicform.entity.FormMetaItem;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jober.entity.task.TaskProperty;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 表单元数据转换器
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
@Mapper
public interface FormMetaConvertor {
    /**
     * 获取FormMetaConvertor的实例
     */
    FormMetaConvertor INSTANCE = Mappers.getMapper(FormMetaConvertor.class);

    /**
     * 将FormMetaItem转换为MetaPropertyDeclarationInfo
     *
     * @param formMeta 表单Meta数据
     * @return MetaPropertyDeclarationInfo
     */
    @Mapping(target = "name", expression = "java(modelengine.fit.jane.Undefinable.defined(formMeta.getKey()))")
    @Mapping(target = "dataType", expression = "java(modelengine.fit.jane.Undefinable.defined(formMeta.getType()))")
    @Mapping(target = "description", expression = "java(modelengine.fit.jane.Undefinable.defined(formMeta.getName()))")
    @Mapping(target = "required", ignore = true)
    @Mapping(target = "identifiable", ignore = true)
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "attribute", ignore = true)
    MetaPropertyDeclarationInfo toMetaPropertyDeclarationInfo(FormMetaItem formMeta);

    /**
     * 将FormMetaItem转换为TaskProperty
     *
     * @param formMeta FormMetaItem数据
     * @return TaskProperty
     */
    @Mapping(target = "name", expression = "java(formMeta.getKey())")
    @Mapping(target = "dataType", expression = "java(formMeta.getType())")
    @Mapping(target = "description", expression = "java(formMeta.getName())")
    @Mapping(target = "scope", ignore = true)
    @Mapping(target = "required", ignore = true)
    @Mapping(target = "identifiable", ignore = true)
    @Mapping(target = "appearance", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "categories", ignore = true)
    TaskProperty toTaskProperty(FormMetaItem formMeta);
}
