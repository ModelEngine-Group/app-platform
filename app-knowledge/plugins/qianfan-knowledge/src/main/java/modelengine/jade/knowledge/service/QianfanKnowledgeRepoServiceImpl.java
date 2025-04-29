/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.*;
import modelengine.jade.knowledge.convertor.ParamConvertor;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.dto.QianfanKnowledgeListQueryParam;
import modelengine.jade.knowledge.dto.QianfanRetrievalParam;
import modelengine.jade.knowledge.entity.PageVoKnowledgeList;
import modelengine.jade.knowledge.entity.QianfanKnowledgeEntity;
import modelengine.jade.knowledge.entity.QianfanKnowledgeListEntity;
import modelengine.jade.knowledge.entity.QianfanRetrievalResult;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.external.QianfanKnowledgeBaseManager;
import modelengine.jade.knowledge.support.FlatFilterConfig;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 表示知识库服务在 qianfan 中的实现。
 *
 * @author 陈潇文
 * @since 2025-04-17
 */
@Component
public class QianfanKnowledgeRepoServiceImpl implements KnowledgeRepoService {
    public static final String FITABLE_ID_DEFAULT = "qianfanKnowledge";
    private static final int querySize = 100;
    private static final int DEFAULT_TOP_K = 3;
    private static final int MAX_TOP_K = 10;
    private static final float DEFAULT_THRESHOLD = 0.1f;

    private final QianfanKnowledgeBaseManager knowledgeBaseManager;
    private final KnowledgeI18nService knowledgeI18nService;

    public QianfanKnowledgeRepoServiceImpl(QianfanKnowledgeBaseManager knowledgeBaseManager,
            KnowledgeI18nService knowledgeI18nService) {
        this.knowledgeBaseManager = knowledgeBaseManager;
        this.knowledgeI18nService = knowledgeI18nService;
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public PageVo<KnowledgeRepo> listRepos(String apiKey, ListRepoQueryParam param) {
        Validation.notNull(param, "The query param cannot be null.");
        int max = param.getPageIndex() * param.getPageSize();
        int min = max - param.getPageSize();
        int times = max / querySize;
        int maxKeys = max % querySize;
        PageVoKnowledgeList pageVoKnowledgeList = this.queryKnowledgeList(apiKey, param, times, maxKeys);
        List<KnowledgeRepo> repos = IntStream.range(min, Math.min(max, pageVoKnowledgeList.getKnowledgeEntityList().size()))
                .mapToObj(pageVoKnowledgeList.getKnowledgeEntityList()::get)
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
        KnowledgeProperty.IndexInfo hybridIndex = new KnowledgeProperty.IndexInfo(IndexType.HYBRID,
                hybridInfo.getName(),
                hybridInfo.getDescription());
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
        QianfanRetrievalParam param = ParamConvertor.INSTANCE.convertToRetrievalParam(option);
        QianfanRetrievalResult result = this.knowledgeBaseManager.retrieve(apiKey, param);
        return result.getChunks().stream()
                .map(ParamConvertor.INSTANCE::convertToKnowledgeDocument)
                .collect(Collectors.toList());
    }

    private PageVoKnowledgeList queryKnowledgeList(String apiKey, ListRepoQueryParam param, int times, int maxKeys) {
        List<QianfanKnowledgeEntity> resultList = new ArrayList<>();
        String currentMarker = StringUtils.EMPTY;
        QianfanKnowledgeListEntity listEntity = QianfanKnowledgeListEntity.builder().total(0).build();
        // 执行常规分页查询
        for (int i = 0; i < times; i++) {
            listEntity = this.executeQuery(apiKey, param.getRepoName(), querySize, currentMarker);
            resultList.addAll(listEntity.getData());
            currentMarker = listEntity.getNextMarker();
        }
        // 执行最后一次查询
        if (maxKeys > 0) {
            listEntity = this.executeQuery(apiKey, param.getRepoName(), maxKeys, currentMarker);
            resultList.addAll(listEntity.getData());
        }
        return PageVoKnowledgeList
                .builder()
                .knowledgeEntityList(resultList)
                .total(listEntity.getTotal())
                .build();
    }

    private QianfanKnowledgeListEntity executeQuery(String apiKey, String repoName, int maxKeys, String marker) {
        QianfanKnowledgeListQueryParam queryParam = QianfanKnowledgeListQueryParam.builder()
                .keyword(repoName)
                .maxKeys(maxKeys)
                .marker(marker)
                .build();
        return this.knowledgeBaseManager.listRepos(apiKey, queryParam);
    }
}
