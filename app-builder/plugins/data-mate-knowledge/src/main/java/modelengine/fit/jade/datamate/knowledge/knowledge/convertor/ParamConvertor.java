/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.ReferenceLimit;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.fit.jade.datamate.knowledge.knowledge.dto.DataMateRetrievalParam;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.DataMateKnowledgeEntity;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.DataMateRetrievalChunksEntity;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * DataMate 内部数据的转换器接口。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Mapper
public interface ParamConvertor {
    ParamConvertor INSTANCE = Mappers.getMapper(ParamConvertor.class);
    int TOP = 400;

    /**
     * 将 {@link DataMateKnowledgeEntity} 转换为 {@link KnowledgeRepo}。
     *
     * @param entity 表示待转换的 {@link DataMateKnowledgeEntity}。
     * @return 转换完成的 {@link KnowledgeRepo}。
     */
    @Mapping(target = "type", source = "entity", qualifiedByName = "mapIndexTypeToType")
    @Mapping(target = "createdAt", source = "entity", qualifiedByName = "stringToLocalDateTime")
    KnowledgeRepo convertToKnowledgeRepo(DataMateKnowledgeEntity entity);

    /**
     * 将 DataMate 知识库的检索 type 映射为 平台知识库元数据 type。
     *
     * @param entity 表示待转换的 {@link DataMateKnowledgeEntity}。
     * @return 表示转换完成的 {@link String}。
     */
    @Named("mapIndexTypeToType")
    default String mapIndexTypeToType(DataMateKnowledgeEntity entity) {
        return entity == null ? null : entity.getEmbeddingModel();
    }

    /**
     * 将 DataMate 知识库的 createdAt 映射为 平台知识库元数据 createdAt。
     *
     * @param entity 表示待转换的 {@link DataMateKnowledgeEntity}。
     * @return 表示转换完成的 {@link LocalDateTime}。
     */
    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(DataMateKnowledgeEntity entity) {
        String dateStr = entity.getCreatedAt();
        if (dateStr == null || StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * 将 {@link FlatKnowledgeOption} 转换为 {@link DataMateRetrievalParam}。
     *
     * @param option 表示待转换的 {@link FlatKnowledgeOption}。
     * @return 转换完成的 {@link DataMateRetrievalParam}。
     */
    @Mapping(target = "knowledgeBaseIds", source = "repoIds")
    @Mapping(target = "query", source = "query")
    @Mapping(target = "topK", source = "referenceLimit", qualifiedByName = "mapReferenceLimitToTop")
    @Mapping(target = "threshold", source = "similarityThreshold")
    DataMateRetrievalParam convertToRetrievalParam(FlatKnowledgeOption option);

    /**
     * 将平台检索请求 ReferenceLimit 映射为 DataMate 检索请求 top。
     *
     * @param limit 表示待转换的 {@link ReferenceLimit}。
     * @return 转换完成的 {@link int}。
     */
    @Named("mapReferenceLimitToTop")
    default int mapReferenceLimitToTop(ReferenceLimit limit) {
        if (limit == null) {
            return TOP;
        }
        return limit.getValue();
    }

    /**
     * 将 {@link DataMateRetrievalChunksEntity} 转换为 {@link KnowledgeDocument}。
     *
     * @param entity 表示待转换的 {@link DataMateRetrievalChunksEntity}。
     * @return 转换完成的 {@link KnowledgeDocument}。
     */
    @Mapping(target = "id", expression = "java(entity.chunkId())")
    @Mapping(target = "text", expression = "java(entity.content())")
    @Mapping(target = "score", expression = "java(entity.retrievalScore())")
    @Mapping(target = "metadata", source = ".", qualifiedByName = "mapChunksEntityToMetadata")
    KnowledgeDocument convertToKnowledgeDocument(DataMateRetrievalChunksEntity entity);

    /**
     * 将 DataMate 检索结果 entity 映射为 平台检索结果 metadata。
     *
     * @param entity 表示待转换的 {@link DataMateRetrievalChunksEntity}。
     * @return 转换完成的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Named("mapChunksEntityToMetadata")
    default Map<String, Object> mapChunksEntityToMetadata(DataMateRetrievalChunksEntity entity) {
        Map<String, Object> metadata = new HashMap<>();
        if (entity == null || entity.getEntity() == null) {
            return metadata;
        }
        metadata.put("primaryKey", entity.getPrimaryKey());
        String rawMetadata = entity.getEntity().getMetadata();
        if (!StringUtils.isEmpty(rawMetadata)) {
            try {
                Map<String, Object> parsed = new ObjectMapper().readValue(rawMetadata, Map.class);
                metadata.put("fileId", parsed.get("original_file_id"));
                metadata.put("fileName", parsed.get("file_name"));
                metadata.putAll(parsed);
            } catch (JsonProcessingException ex) {
                metadata.put("metadata", rawMetadata);
            }
        }
        return metadata;
    }
}

