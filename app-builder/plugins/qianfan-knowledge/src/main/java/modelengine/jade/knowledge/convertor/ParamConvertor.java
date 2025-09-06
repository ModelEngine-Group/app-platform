/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.convertor;

import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.ReferenceLimit;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.dto.QianfanPipelineConfigQueryParam;
import modelengine.jade.knowledge.dto.QianfanPipelineQueryParam;
import modelengine.jade.knowledge.dto.QianfanRetrievalParam;
import modelengine.jade.knowledge.entity.QianfanKnowledgeEntity;
import modelengine.jade.knowledge.entity.QianfanRetrievalChunksEntity;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 百度千帆 内部数据的转换器接口。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
@Mapper
public interface ParamConvertor {
    ParamConvertor INSTANCE = Mappers.getMapper(ParamConvertor.class);
    int TOP = 400;

    /**
     * 将 {@link QianfanKnowledgeEntity} 转换为 {@link KnowledgeRepo}。
     *
     * @param entity 表示待转换的 {@link QianfanKnowledgeEntity}。
     * @return 转换完成的 {@link KnowledgeRepo}。
     */
    @Mapping(target = "type", source = "entity", qualifiedByName = "mapIndexTypeToType")
    @Mapping(target = "createdAt", source = "entity", qualifiedByName = "stringToLocalDateTime")
    KnowledgeRepo convertToKnowledgeRepo(QianfanKnowledgeEntity entity);

    /**
     * 将千帆知识库的检索 type 映射为 平台知识库元数据 type。
     * @param entity 表示待转换的 {@link QianfanKnowledgeEntity}。
     * @return 表示转换完成的 {@link String}。
     */
    @Named("mapIndexTypeToType")
    default String mapIndexTypeToType(QianfanKnowledgeEntity entity) {
        if (entity == null || entity.getConfig() == null || entity.getConfig().getIndex() == null) {
            return null;
        }
        return entity.getConfig().getIndex().getType();
    }

    /**
     * 将千帆知识库的 createdAt 映射为 平台知识库元数据 createdAt。
     * @param entity 表示待转换的 {@link QianfanKnowledgeEntity}。
     * @return 表示转换完成的 {@link LocalDateTime}。
     */
    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(QianfanKnowledgeEntity entity) {
        String dateStr = entity.getCreatedAt();
        if (dateStr == null || StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * 将 {@link FlatKnowledgeOption} 转换为 {@link QianfanRetrievalParam}。
     *
     * @param option 表示待转换的 {@link FlatKnowledgeOption}。
     * @return 转换完成的 {@link QianfanRetrievalParam}。
     */
    @Mapping(target = "knowledgebaseIds", source = "repoIds")
    @Mapping(target = "type", source = "indexType")
    @Mapping(target = "top", source = "referenceLimit", qualifiedByName = "mapReferenceLimitToTop")
    @Mapping(target = "pipelineConfig", source = "similarityThreshold", qualifiedByName = "mapSimilarityToPipeline")
    QianfanRetrievalParam convertToRetrievalParam(FlatKnowledgeOption option);

    /**
     * 将平台检索请求 ReferenceLimit 映射为 千帆检索请求 top。
     * @param limit 表示待转换的 {@link ReferenceLimit}。
     * @return 转换完成的 {@link int}。
     */
    @Named("mapReferenceLimitToTop")
    default int mapReferenceLimitToTop(ReferenceLimit limit) {
        return limit.getValue();
    }

    /**
     * 将平台检索请求 threshold 映射为 千帆检索请求 pipeline。
     * @param threshold 表示待转换的 {@link Float}。
     * @return 转换完成的 {@link QianfanPipelineConfigQueryParam}。
     */
    @Named("mapSimilarityToPipeline")
    default QianfanPipelineConfigQueryParam mapSimilarityToPipeline(Float threshold) {
        QianfanPipelineQueryParam param = QianfanPipelineQueryParam.builder()
                .name("step1")
                .threshold(threshold)
                .top(TOP)
                .type("elastic_search")
                .build();
        return QianfanPipelineConfigQueryParam.builder().pipeline(Collections.singletonList(param)).build();
    }

    /**
     * 将 {@link QianfanRetrievalChunksEntity} 转换为 {@link KnowledgeDocument}。
     *
     * @param entity 表示待转换的 {@link QianfanRetrievalChunksEntity}。
     * @return 转换完成的 {@link KnowledgeDocument}。
     */
    @Mapping(target = "id", expression = "java(entity.chunkId())")
    @Mapping(target = "text", expression = "java(entity.content())")
    @Mapping(target = "score", expression = "java(entity.retrievalScore())")
    @Mapping(target = "metadata", source = "entity", qualifiedByName = "mapChunksEntityToMetadata")
    KnowledgeDocument convertToKnowledgeDocument(QianfanRetrievalChunksEntity entity);

    /**
     * 将千帆检索结果 entity 映射为 平台检索结果 metadata。
     * @param entity 表示待转换的 {@link QianfanRetrievalChunksEntity}。
     * @return 转换完成的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Named("mapChunksEntityToMetadata")
    default Map<String, Object> mapChunksEntityToMetadata(QianfanRetrievalChunksEntity entity) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("datasetName", entity.getKnowledgebaseId());
        metadata.put("datasetVersionId", entity.getKnowledgebaseId());
        metadata.put("fileId", entity.getDocumentId());
        metadata.put("fileName", entity.getDocumentName());
        return metadata;
    }
}
