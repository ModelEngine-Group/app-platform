/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service.impl;

import static modelengine.jade.knowledge.code.KnowledgeRetrievalRetCode.INVALID_PARAMETER_TYPE_ERROR;

import modelengine.fel.core.document.DocumentPostProcessor;
import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fel.core.document.support.RerankOption;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.knowledge.convertor.RetrieverOptionConvertor;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.entity.RetrieverServiceOption;
import modelengine.jade.knowledge.postprocessor.FactoryOption;
import modelengine.jade.knowledge.postprocessor.PostProcessorFactory;
import modelengine.jade.knowledge.retriever.RetrieverHandler;
import modelengine.jade.knowledge.service.KnowledgeRepoInfo;
import modelengine.jade.knowledge.service.RetrieverService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检索节点服务默认实现。
 *
 * @author 刘信宏
 * @since 2024-09-27
 */
@Component
public class RetrieverServiceImpl implements RetrieverService {
    private final RetrieverHandler retrieverHandler;
    private final PostProcessorFactory postProcessorFactory;
    private final String baseRerankUri;
    private final KnowledgeCenterService knowledgeCenterService;

    /**
     * 使用检索处理器和文档后处理器初始化 {@link RetrieverServiceImpl} 对象。
     *
     * @param retrieverHandler 表示检索处理器的 {@link RetrieverHandler}。
     * @param postProcessorFactory 表示文档后处理器工厂的 {@link PostProcessorFactory}。
     * @param baseRerankUri 表示文档重排服务的资源标识符的 {@link String}。
     * @param knowledgeCenterService 表示知识库配置服务的 {@link KnowledgeCenterService}。
     */
    public RetrieverServiceImpl(RetrieverHandler retrieverHandler, PostProcessorFactory postProcessorFactory,
            @Value("${openai-urls.internal}") String baseRerankUri, KnowledgeCenterService knowledgeCenterService) {
        this.retrieverHandler = Validation.notNull(retrieverHandler, "The retriever handler cannot be null.");
        this.postProcessorFactory = Validation.notNull(postProcessorFactory, "The factory cannot be null.");
        this.baseRerankUri = Validation.notBlank(baseRerankUri, "The rerank uri cannot be blank.");
        this.knowledgeCenterService = knowledgeCenterService;
    }

    @Fitable("knowledge.service.invoke")
    @Override
    public List<KnowledgeDocument> invoke(Object query, List<KnowledgeRepoInfo> knowledgeRepos,
            RetrieverServiceOption option, String userId) {
        if (CollectionUtils.isEmpty(knowledgeRepos)) {
            return Collections.emptyList();
        }
        Validation.notNull(query, "The query cannot be null.");
        Validation.lessThanOrEquals(knowledgeRepos.size(), 5, "The knowledge repository cannot greater than 5.");
        this.retrieverServiceOptionValidation(option);
        List<String> normalizeQuery = this.normalizeQuery(query);
        RetrieverOption retrieverOption = this.getRetrieverOption(knowledgeRepos, option, userId);
        List<MeasurableDocument> documents = this.retrieverHandler.handle(normalizeQuery, retrieverOption);
        FactoryOption factoryOption = this.buildFactoryOption(normalizeQuery, option.getRerankParam());
        List<DocumentPostProcessor> postProcessors = this.postProcessorFactory.create(factoryOption);
        for (DocumentPostProcessor postProcessor : postProcessors) {
            if (CollectionUtils.isEmpty(documents)) {
                return Collections.emptyList();
            }
            documents = postProcessor.invoke(documents);
        }
        return documents.stream()
                .map(doc -> new KnowledgeDocument(doc, doc.score()))
                .limit(option.getReferenceLimit().value())
                .collect(Collectors.toList());
    }

    private RetrieverOption getRetrieverOption(List<KnowledgeRepoInfo> knowledgeRepos, RetrieverServiceOption option,
            String userId) {
        String apiKey = this.knowledgeCenterService.getApiKey(userId, option.getGroupId(), StringUtils.EMPTY);
        RetrieverOption retrieverOption = RetrieverOptionConvertor.INSTANCE.fromRetrieverServiceOption(option, apiKey);
        retrieverOption.setRepoIds(knowledgeRepos.stream().map(KnowledgeRepoInfo::id).collect(Collectors.toList()));
        return retrieverOption;
    }

    private void retrieverServiceOptionValidation(RetrieverServiceOption option) {
        Validation.notNull(option, "The retriever option cannot be null.");
        Validation.notNull(option.getRerankParam(), "The rerank parameter cannot be null.");
        Validation.notNull(option.getReferenceLimit(), "The reference limit cannot be null.");
        Validation.notNull(option.getIndexType(), "The index type cannot be null.");
    }

    private List<String> normalizeQuery(Object query) {
        if (query instanceof String) {
            return Collections.singletonList(ObjectUtils.cast(query));
        }
        if (query instanceof List && ((List<?>) query).stream().allMatch(item -> item instanceof String)) {
            return ObjectUtils.cast(query);
        }

        throw new ModelEngineException(INVALID_PARAMETER_TYPE_ERROR, query.getClass().getName());
    }

    private FactoryOption buildFactoryOption(List<String> query, RetrieverOption.RerankParam rerankParam) {
        if (!rerankParam.isEnableRerank()) {
            return new FactoryOption(false, null);
        }
        RerankOption rerankOption = RerankOption.custom()
                .baseUri(ObjectUtils.nullIf(rerankParam.getBaseUri(), this.baseRerankUri))
                .model(rerankParam.getModel())
                .topN(rerankParam.getTopK())
                .query(String.join("\n", query))
                .build();
        return new FactoryOption(true, rerankOption);
    }
}
