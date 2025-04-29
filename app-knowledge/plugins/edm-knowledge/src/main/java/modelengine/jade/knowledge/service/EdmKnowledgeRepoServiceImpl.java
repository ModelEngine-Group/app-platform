/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.FilterConfig;
import modelengine.jade.knowledge.KnowledgeI18nInfo;
import modelengine.jade.knowledge.KnowledgeI18nService;
import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.KnowledgeRepoService;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.ReferenceLimit;
import modelengine.jade.knowledge.convertor.ParamConvertor;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.dto.EdmRetrievalParam;
import modelengine.jade.knowledge.dto.ListKnowledgeQueryParam;
import modelengine.jade.knowledge.entity.EdmListRepoEntity;
import modelengine.jade.knowledge.entity.EdmRetrievalResult;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.external.EdmKnowledgeBaseManager;
import modelengine.jade.knowledge.support.FlatFilterConfig;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示知识库服务在 edm 中的实现
 *
 * @author 何嘉斌
 * @since 2024-09-24
 */
@Component
public class EdmKnowledgeRepoServiceImpl implements KnowledgeRepoService {
    /**
     * edm知识库 fitable Id
     */
    public static final String FITABLE_ID_DEFAULT = "edmKnowledge";

    private static final int DEFAULT_TOP_K = 3;

    private static final int MAX_TOP_K = 10;

    private static final float DEFAULT_THRESHOLD = 0.5f;

    private static final Set<String> AVAILABLE_STATUS_SET = new HashSet<>();

    private final EdmKnowledgeBaseManager knowledgeBaseManager;

    private final ObjectSerializer serializer;

    private final KnowledgeI18nService knowledgeI18nService;

    public EdmKnowledgeRepoServiceImpl(EdmKnowledgeBaseManager knowledgeBaseManager,
            @Fit(alias = "json") ObjectSerializer serializer, KnowledgeI18nService knowledgeI18nService) {
        this.knowledgeBaseManager = knowledgeBaseManager;
        this.serializer = serializer;
        this.knowledgeI18nService = knowledgeI18nService;
        AVAILABLE_STATUS_SET.add("partial_success");
        AVAILABLE_STATUS_SET.add("completed");
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public PageVo<KnowledgeRepo> listRepos(String apiKey, ListRepoQueryParam param) {
        Validation.notNull(param, "The query param cannot be null.");
        ListKnowledgeQueryParam queryParam = ParamConvertor.INSTANCE.convertQueryParam(param);
        queryParam.setStatus(Collections.singletonList("completed"));
        EdmListRepoEntity repoEntity = this.knowledgeBaseManager.listRepos(queryParam);

        List<KnowledgeRepo> repos = repoEntity.getRecords()
                .stream()
                .filter(repoRecord -> AVAILABLE_STATUS_SET.contains(repoRecord.getStatus()))
                .map(ParamConvertor.INSTANCE::convertKnowledgeRepo)
                .collect(Collectors.toList());
        return PageVo.of(repoEntity.getTotal(), repos);
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public List<KnowledgeDocument> retrieve(String apiKey, FlatKnowledgeOption option) {
        Validation.notNull(option, "The knowledge option cannot be null.");
        EdmRetrievalParam retrievalParam = ParamConvertor.INSTANCE.convertRetrievalParam(option);
        retrievalParam.setTopK(this.getTopK(option));
        retrievalParam.setThreshold(Optional.ofNullable(retrievalParam.getThreshold()).orElse(DEFAULT_THRESHOLD));
        List<EdmRetrievalResult> retrievalResults = this.knowledgeBaseManager.retrieve(retrievalParam);
        return retrievalResults.stream()
                .map(result -> new KnowledgeDocument(result.getId().toString(),
                        result.getContent(),
                        result.getScore(),
                        null))
                .collect(Collectors.toList());
    }

    private Integer getTopK(FlatKnowledgeOption option) {
        ReferenceLimit referenceLimit = option.referenceLimit();
        if (referenceLimit == null) {
            return DEFAULT_TOP_K;
        }
        if (Objects.equals(referenceLimit.type(), FilterType.REFERENCE_TOP_K.value())) {
            return Validation.between(referenceLimit.value(),
                    0,
                    MAX_TOP_K,
                    StringUtils.format("The topK must between 0 and {0}", MAX_TOP_K));
        }
        return DEFAULT_TOP_K;
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public KnowledgeProperty getProperty(String apiKey) {
        KnowledgeI18nInfo semanticInfo = this.knowledgeI18nService.localizeText(IndexType.SEMANTIC);
        KnowledgeProperty.IndexInfo indexType = new KnowledgeProperty.IndexInfo(IndexType.SEMANTIC,
                semanticInfo.getName(),
                semanticInfo.getDescription());
        KnowledgeI18nInfo referenceInfo = this.knowledgeI18nService.localizeText(FilterType.REFERENCE_TOP_K);
        FlatFilterConfig topKFilter = new FlatFilterConfig(FilterConfig.custom()
                .name(referenceInfo.getName())
                .description(referenceInfo.getDescription())
                .type(FilterType.REFERENCE_TOP_K)
                .minimum(1)
                .maximum(MAX_TOP_K)
                .defaultValue(DEFAULT_TOP_K)
                .build());
        KnowledgeI18nInfo relevancyInfo = this.knowledgeI18nService.localizeText(FilterType.SIMILARITY_THRESHOLD);
        FlatFilterConfig similarityFilter = new FlatFilterConfig(FilterConfig.custom()
                .name(relevancyInfo.getName())
                .description(relevancyInfo.getDescription())
                .type(FilterType.SIMILARITY_THRESHOLD)
                .minimum(0)
                .maximum(10)
                .defaultValue(DEFAULT_THRESHOLD)
                .build());
        KnowledgeI18nInfo rerankInfo = new KnowledgeI18nInfo(this.knowledgeI18nService.localizeText("rerankParam"),
                this.knowledgeI18nService.localizeText("rerankParam.description"));
        KnowledgeProperty.RerankConfig rerankConfig =
                new KnowledgeProperty.RerankConfig("boolean", rerankInfo.getName(), rerankInfo.getDescription(), false);
        return new KnowledgeProperty(Collections.singletonList(indexType),
                Arrays.asList(topKFilter, similarityFilter),
                Collections.singletonList(rerankConfig));
    }
}