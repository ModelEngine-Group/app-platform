/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.service;

import modelengine.fit.jade.datamate.knowledge.knowledge.convertor.ParamConvertor;
import modelengine.fit.jade.datamate.knowledge.knowledge.dto.DataMateKnowledgeListQueryParam;
import modelengine.fit.jade.datamate.knowledge.knowledge.dto.DataMateRetrievalParam;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.DataMateKnowledgeEntity;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.DataMateKnowledgeListEntity;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.DataMateRetrievalChunksEntity;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.DataMateRetrievalResult;
import modelengine.fit.jade.datamate.knowledge.knowledge.entity.PageVoKnowledgeList;
import modelengine.fit.jade.datamate.knowledge.knowledge.external.DataMateKnowledgeBaseManager;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.FilterConfig;
import modelengine.jade.knowledge.KnowledgeI18nInfo;
import modelengine.jade.knowledge.KnowledgeI18nService;
import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.KnowledgeRepoService;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.support.FlatFilterConfig;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示知识库服务在 DataMate 中的实现。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Component
public class DataMateKnowledgeRepoServiceImpl implements KnowledgeRepoService {
    /**
     * DataMate 知识库的服务唯一标识。
     */
    public static final String FITABLE_ID_DEFAULT = "dataMateKnowledge";

    private static final int DEFAULT_TOP_K = 3;
    private static final int MAX_TOP_K = 10;
    private static final float DEFAULT_THRESHOLD = 0.1f;

    private final DataMateKnowledgeBaseManager knowledgeBaseManager;
    private final KnowledgeI18nService knowledgeI18nService;

    public DataMateKnowledgeRepoServiceImpl(DataMateKnowledgeBaseManager knowledgeBaseManager,
            KnowledgeI18nService knowledgeI18nService) {
        this.knowledgeBaseManager = knowledgeBaseManager;
        this.knowledgeI18nService = knowledgeI18nService;
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public PageVo<KnowledgeRepo> listRepos(String apiKey, ListRepoQueryParam param) {
        Validation.notNull(param, "The query param cannot be null.");
        PageVoKnowledgeList pageVoKnowledgeList = this.queryKnowledgeList(apiKey, param);
        List<KnowledgeRepo> repos = pageVoKnowledgeList.getKnowledgeEntityList()
                .stream()
                .map(ParamConvertor.INSTANCE::convertToKnowledgeRepo)
                .toList();
        return PageVo.of(pageVoKnowledgeList.getTotal(), repos);
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public KnowledgeProperty getProperty(String apiKey) {
        KnowledgeI18nInfo semanticInfo = this.knowledgeI18nService.localizeText(IndexType.SEMANTIC);
        KnowledgeProperty.IndexInfo semanticIndex = new KnowledgeProperty.IndexInfo(IndexType.SEMANTIC,
                semanticInfo.getName(),
                semanticInfo.getDescription());
        KnowledgeI18nInfo fullTextInfo = this.knowledgeI18nService.localizeText(IndexType.FULL_TEXT);
        KnowledgeProperty.IndexInfo fullTextIndex = new KnowledgeProperty.IndexInfo(IndexType.FULL_TEXT,
                fullTextInfo.getName(),
                fullTextInfo.getDescription());
        KnowledgeI18nInfo hybridInfo = this.knowledgeI18nService.localizeText(IndexType.HYBRID);
        KnowledgeProperty.IndexInfo hybridIndex =
                new KnowledgeProperty.IndexInfo(IndexType.HYBRID, hybridInfo.getName(), hybridInfo.getDescription());
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
                .maximum(1)
                .defaultValue(DEFAULT_THRESHOLD)
                .build());
        KnowledgeI18nInfo rerankInfo = new KnowledgeI18nInfo(this.knowledgeI18nService.localizeText("rerankParam"),
                this.knowledgeI18nService.localizeText("rerankParam.description"));
        KnowledgeProperty.RerankConfig rerankConfig =
                new KnowledgeProperty.RerankConfig("boolean", rerankInfo.getName(), rerankInfo.getDescription(), false);
        return new KnowledgeProperty(Arrays.asList(semanticIndex, fullTextIndex, hybridIndex),
                Arrays.asList(topKFilter, similarityFilter),
                Collections.singletonList(rerankConfig));
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public List<KnowledgeDocument> retrieve(String apiKey, FlatKnowledgeOption option) {
        Validation.notNull(option, "The knowledge option cannot be null.");
        DataMateRetrievalParam param = ParamConvertor.INSTANCE.convertToRetrievalParam(option);
        DataMateRetrievalResult result = this.knowledgeBaseManager.retrieve(apiKey, param);
        List<DataMateRetrievalChunksEntity> chunks = result.getData() == null
                ? Collections.emptyList()
                : result.getData();
        return chunks
                .stream()
                .map(ParamConvertor.INSTANCE::convertToKnowledgeDocument)
                .collect(Collectors.toList());
    }

    private PageVoKnowledgeList queryKnowledgeList(String apiKey, ListRepoQueryParam param) {
        int page = Math.max(param.getPageIndex() - 1, 0);
        int size = param.getPageSize();
        DataMateKnowledgeListEntity listEntity = this.executeQuery(apiKey, param.getRepoName(), page, size);
        List<DataMateKnowledgeEntity> content = listEntity.getContent() == null
                ? Collections.emptyList()
                : listEntity.getContent();
        return PageVoKnowledgeList.builder()
                .knowledgeEntityList(content)
                .total(listEntity.getTotalElements() == null ? 0 : listEntity.getTotalElements())
                .build();
    }

    private DataMateKnowledgeListEntity executeQuery(String apiKey, String repoName, int page, int size) {
        DataMateKnowledgeListQueryParam queryParam = DataMateKnowledgeListQueryParam.builder()
                .name(repoName)
                .page(page)
                .size(size)
                .build();
        return this.knowledgeBaseManager.listRepos(apiKey, queryParam);
    }
}
