/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.convertor;

import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.dto.AippDetailDto;
import modelengine.fit.jober.aipp.dto.AippOverviewRspDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 定义Meta数据转换器接口
 *
 * @author 刘信宏
 * @since 2023-12-14
 */
@Mapper
public interface MetaConvertor {
    /**
     * 获取MetaConvertor的实例
     */
    MetaConvertor INSTANCE = Mappers.getMapper(MetaConvertor.class);

    /**
     * 将Meta转换为{@link AippOverviewRspDto}
     *
     * @param meta Meta数据
     * @return AippOverviewRspDto
     */
    @Mapping(target = "createdAt", source = "creationTime")
    @Mapping(target = "updatedAt", source = "lastModificationTime")
    @Mapping(target = "updater", source = "lastModifier")
    @Mapping(target = "aippId", source = "id")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "publishAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    AippOverviewRspDto toAippOverviewRspDto(Meta meta);

    /**
     * 将Meta转换为AippDetailDto
     *
     * @param meta Meta数据
     * @return AippDetailDto
     */
    @Mapping(target = "createdAt", source = "creationTime")
    @Mapping(target = "updatedAt", source = "lastModificationTime")
    @Mapping(target = "updater", source = "lastModifier")
    @Mapping(target = "aippId", source = "id")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "publishAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "flowViewData", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "icon", ignore = true)
    AippDetailDto toAippDetailDto(Meta meta);
}
