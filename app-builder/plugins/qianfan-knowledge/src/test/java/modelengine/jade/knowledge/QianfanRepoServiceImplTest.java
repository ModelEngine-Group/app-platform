/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.entity.QianfanKnowledgeEntity;
import modelengine.jade.knowledge.entity.QianfanKnowledgeListEntity;
import modelengine.jade.knowledge.entity.QianfanRetrievalChunksEntity;
import modelengine.jade.knowledge.entity.QianfanRetrievalResult;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.external.QianfanKnowledgeBaseManager;
import modelengine.jade.knowledge.service.QianfanKnowledgeRepoServiceImpl;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link QianfanKnowledgeRepoServiceImpl} 的测试集。
 *
 * @author 陈潇文
 * @since 2025-05-06
 */
@FitTestWithJunit(includeClasses = {QianfanKnowledgeRepoServiceImpl.class})
public class QianfanRepoServiceImplTest {
    private static final String apiKey = "123";

    @Mock
    private QianfanKnowledgeBaseManager knowledgeBaseManager;

    @Fit
    private QianfanKnowledgeRepoServiceImpl knowledgeRepoService;

    @Mock
    private KnowledgeI18nService knowledgeI18nService;

    @Test
    @DisplayName("查询知识库列表成功")
    void shouldOkWhenListRepo() {
        QianfanKnowledgeEntity.QianfanKnowledgeConfigIndexEntity index =
                new QianfanKnowledgeEntity.QianfanKnowledgeConfigIndexEntity();
        index.setType("type");
        index.setEsUrl("");

        QianfanKnowledgeEntity.QianfanKnowledgeConfigEntity config =
                new QianfanKnowledgeEntity.QianfanKnowledgeConfigEntity();
        config.setIndex(index);

        LocalDateTime time = LocalDateTime.now();
        QianfanKnowledgeEntity entity = QianfanKnowledgeEntity.builder()
                .id("1")
                .name("test1")
                .description("test1")
                .createdAt(time.toString())
                .config(config)
                .build();

        QianfanKnowledgeListEntity listEntity = QianfanKnowledgeListEntity.builder()
                .isTruncated(false)
                .total(1)
                .data(Collections.singletonList(entity))
                .build();

        ListRepoQueryParam param = new ListRepoQueryParam();
        param.setPageIndex(1);
        param.setPageSize(10);

        when(this.knowledgeBaseManager.listRepos(anyString(), any())).thenReturn(listEntity);

        PageVo<KnowledgeRepo> repos = this.knowledgeRepoService.listRepos(apiKey, param);
        assertThat(repos.getTotal()).isEqualTo(1);
        assertThat(repos.getItems().get(0)).extracting(KnowledgeRepo::id,
                KnowledgeRepo::name,
                KnowledgeRepo::description,
                KnowledgeRepo::createdAt,
                KnowledgeRepo::type).containsExactly("1", "test1", "test1", time, "type");
    }

    @Test
    @DisplayName("检索知识库成功")
    void shouldOkWhenRetrieve() {
        QianfanRetrievalResult retrievalResult = this.buildQianfanRetrievalResult();

        when(this.knowledgeBaseManager.retrieve(anyString(), any())).thenReturn(retrievalResult);

        ReferenceLimit referenceLimit = new ReferenceLimit();
        referenceLimit.setValue(3);
        referenceLimit.setType(FilterType.REFERENCE_TOP_K.value());

        FlatKnowledgeOption flatOption = new FlatKnowledgeOption(KnowledgeOption.custom()
                .query("query")
                .repoIds(Collections.emptyList())
                .indexType(IndexType.SEMANTIC)
                .similarityThreshold(0.5f)
                .referenceLimit(referenceLimit)
                .build());
        List<KnowledgeDocument> result = this.knowledgeRepoService.retrieve(apiKey, flatOption);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).extracting(KnowledgeDocument::id, KnowledgeDocument::text, KnowledgeDocument::score)
                .containsExactly("1", "content", 0.5);
        assertThat(result.get(0).getMetadata()).containsEntry("datasetName", "knowledge1")
                .containsEntry("datasetVersionId", "knowledge1")
                .containsEntry("fileId", "document1")
                .containsEntry("fileName", "documentName1");
    }

    private QianfanRetrievalResult buildQianfanRetrievalResult() {
        QianfanRetrievalChunksEntity chunk = new QianfanRetrievalChunksEntity();
        chunk.setChunkId("1");
        chunk.setContent("content");
        chunk.setChunkType("type");
        chunk.setKnowledgebaseId("knowledge1");
        chunk.setDocumentId("document1");
        chunk.setDocumentName("documentName1");
        chunk.setRetrievalScore(0.5f);
        chunk.setRankScore(0.6f);

        QianfanRetrievalResult retrievalResult = new QianfanRetrievalResult();
        retrievalResult.setTotalCount(1);
        retrievalResult.setChunks(Collections.singletonList(chunk));
        return retrievalResult;
    }

    @Test
    @DisplayName("获取检索配置成功")
    void shouldOkWhenGetProperty() {
        when(this.knowledgeI18nService.localizeText(any(IndexType.class))).thenReturn(new KnowledgeI18nInfo("语义检索",
                "基于文本的含义检索出最相关的内容"));
        doAnswer(arguments -> {
            if (arguments.getArgument(0) == FilterType.REFERENCE_TOP_K) {
                return new KnowledgeI18nInfo("引用上限", "最大召回知识条数");
            }
            if (arguments.getArgument(0) == FilterType.SIMILARITY_THRESHOLD) {
                return new KnowledgeI18nInfo("最低相关度", "检索文本的最低相关度");
            }
            return null;
        }).when(this.knowledgeI18nService).localizeText(any(FilterType.class));
        when(this.knowledgeI18nService.localizeText("rerankParam")).thenReturn("结果重排");
        when(this.knowledgeI18nService.localizeText("rerankParam.description")).thenReturn(
                "将初步检索到的候选文档按照与用户查询的相关性进行重新排序");

        KnowledgeProperty property = this.knowledgeRepoService.getProperty(apiKey);
        assertThat(property.getIndexType().size()).isEqualTo(3);
        assertThat(property.getIndexType()).extracting(KnowledgeProperty.IndexInfo::type)
                .containsExactly(IndexType.SEMANTIC.value(), IndexType.FULL_TEXT.value(), IndexType.HYBRID.value());
        assertThat(property.getIndexType().get(0)).extracting(KnowledgeProperty.IndexInfo::type,
                        KnowledgeProperty.IndexInfo::name,
                        KnowledgeProperty.IndexInfo::description)
                .containsExactly(IndexType.SEMANTIC.value(), "语义检索", "基于文本的含义检索出最相关的内容");
        assertThat(property.getFilterConfig().size()).isEqualTo(2);
        assertThat(property.getRerankConfig().size()).isEqualTo(1);
    }
}
