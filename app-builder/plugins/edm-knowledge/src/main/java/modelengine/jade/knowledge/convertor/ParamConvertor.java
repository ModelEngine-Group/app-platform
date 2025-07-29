/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.convertor;

import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.dto.EdmRepoRecord;
import modelengine.jade.knowledge.dto.EdmRetrievalParam;
import modelengine.jade.knowledge.dto.ListKnowledgeQueryParam;
import modelengine.jade.knowledge.external.EdmKnowledgeQueryParam;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * edm 内部数据的转换器接口。
 *
 * @author 何嘉斌
 * @since 2024-10-08
 */
@Mapper
public interface ParamConvertor {
    /**
     * 获取 ParamConvertor 的实现。
     */
    ParamConvertor INSTANCE = Mappers.getMapper(ParamConvertor.class);

    /**
     * 将 {@link ListRepoQueryParam} 转化为 {@link ListKnowledgeQueryParam}。
     *
     * @param queryParam 表示待转换的 {@link ListRepoQueryParam}。
     * @return 转换完成的 {@link ListKnowledgeQueryParam}。
     */
    @Mapping(target = "name", source = "repoName")
    ListKnowledgeQueryParam convertQueryParam(ListRepoQueryParam queryParam);

    /**
     * 将 {@link FlatKnowledgeOption} 转化为 {@link EdmRetrievalParam}。
     *
     * @param option 表示待转换的 {@link EdmRetrievalParam}。
     * @return 转换完成的 {@link FlatKnowledgeOption}。
     */
    @Mapping(target = "context", expression = "java(option.query())")
    @Mapping(target = "knowledgeId", expression = "java(option.repoIds())")
    @Mapping(target = "threshold", expression = "java(option.similarityThreshold())")
    EdmRetrievalParam convertRetrievalParam(FlatKnowledgeOption option);

    /**
     * 将 {@link EdmRepoRecord} 转化为 {@link KnowledgeRepo}。
     *
     * @param record 表示待转换的 {@link EdmRepoRecord}。
     * @return 转换完成的 {@link KnowledgeRepo}。
     */
    @Mapping(target = "id", expression = "java(record.getId().toString())")
    @Mapping(target = "type",
            expression = "java(modelengine.jade.knowledge.entity.KnowledgeTypeExternalEnum.from(record.getType())"
                    + ".value())")
    @Mapping(target = "createdAt", expression = "java(record.getCreatedAt().toLocalDateTime())")
    KnowledgeRepo convertKnowledgeRepo(EdmRepoRecord record);


    /**
     * 将 {@link ListKnowledgeQueryParam} 转化为 {@link EdmKnowledgeQueryParam}。
     *
     * @param param 表示待转换的 {@link ListKnowledgeQueryParam}。
     * @return 转换完成的 {@link EdmKnowledgeQueryParam}。
     */
    @Mapping(target = "pageNo", expression = "java(param.getPageIndex())")
    EdmKnowledgeQueryParam convertKnowledgeParam(ListKnowledgeQueryParam param);
}