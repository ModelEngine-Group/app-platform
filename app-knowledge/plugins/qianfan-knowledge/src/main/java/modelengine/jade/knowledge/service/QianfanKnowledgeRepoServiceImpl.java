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
import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.KnowledgeRepoService;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.convertor.ParamConvertor;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.dto.QianfanKnowledgeListQueryParam;
import modelengine.jade.knowledge.entity.PageVoKnowledgeList;
import modelengine.jade.knowledge.entity.QianfanKnowledgeEntity;
import modelengine.jade.knowledge.entity.QianfanKnowledgeListEntity;
import modelengine.jade.knowledge.external.QianfanKnowledgeBaseManager;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import java.util.ArrayList;
import java.util.List;
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
    private final QianfanKnowledgeBaseManager knowledgeBaseManager;

    public QianfanKnowledgeRepoServiceImpl(QianfanKnowledgeBaseManager knowledgeBaseManager) {
        this.knowledgeBaseManager = knowledgeBaseManager;
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public PageVo<KnowledgeRepo> listRepos(String apiKey, ListRepoQueryParam param) {
        Validation.notNull(param, "The query param cannot be null.");
        int min = param.getPageIndex() * param.getPageSize();
        int max = min + param.getPageSize();
        int times = max / querySize;
        int maxKeys = max % 100;
        PageVoKnowledgeList pageVoKnowledgeList = this.queryKnowledgeList(apiKey, param, times, maxKeys);
        List<KnowledgeRepo> repos = IntStream.range(min, max + 1)
                .mapToObj(pageVoKnowledgeList.getKnowledgeEntityList()::get)
                .map(ParamConvertor.INSTANCE::convertToKnowledgeRepo)
                .toList();
        return PageVo.of(pageVoKnowledgeList.getTotal(), repos);
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public KnowledgeProperty getProperty(String apiKey) {
        return null;
    }

    @Override
    @Fitable(FITABLE_ID_DEFAULT)
    public List<KnowledgeDocument> retrieve(String apiKey, FlatKnowledgeOption option) {
        return List.of();
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
