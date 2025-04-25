/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.convertor;

import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.entity.QianfanKnowledgeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * qianfan 内部数据的转换器接口。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
@Mapper
public interface ParamConvertor {
    ParamConvertor INSTANCE = Mappers.getMapper(ParamConvertor.class);

    @Mapping(target = "type", source = "entity", qualifiedByName = "mapIndexTypeToType")
    @Mapping(target = "createdAt", source = "createAt", qualifiedByName = "stringToLocalDateTime")
    KnowledgeRepo convertToKnowledgeRepo(QianfanKnowledgeEntity entity);

    @Named("mapIndexTypeToType")
    default String mapIndexTypeToType(QianfanKnowledgeEntity entity) {
        if (entity == null || entity.getConfig() == null || entity.getConfig().getIndex() == null) {
            return null;
        }
        return entity.getConfig().getIndex().getType();
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }
}
