/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.convertor;

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
 * qianfan 内部数据的转换器接口。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
@Mapper
public interface ParamConvertor {
    ParamConvertor INSTANCE = Mappers.getMapper(ParamConvertor.class);

    /**
     * 将 {@link QianfanKnowledgeEntity} 转换为 {@link KnowledgeRepo}。
     *
     * @param entity 表示待转换的 {@link QianfanKnowledgeEntity}。
     * @return 转换完成的 {@link KnowledgeRepo}。
     */
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

    @Named("mapreferenceLimitToTop")
    default int mapReferenceLimitToTop(ReferenceLimit limit) {
        return limit.getValue();
    }

    @Named("mapSimilarityToPipeline")
    default QianfanPipelineConfigQueryParam mapSimilarityToPipeline(Float threshold) {
        QianfanPipelineQueryParam param = QianfanPipelineQueryParam
                .builder()
                .name("step1")
                .threshold(threshold)
                .top(400)
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
    @Mapping(target = "id", source = "documentId")
    @Mapping(target = "text", source = "content")
    @Mapping(target = "score", source = "retrievalScore")
    @Mapping(target = "metadata", source = "entity", qualifiedByName = "mapChunksEntityToMetadata")
    KnowledgeDocument convertToKnowledgeDocument(QianfanRetrievalChunksEntity entity);

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
