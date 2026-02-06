/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.datamate.knowledge.entity.DataMateKnowledgeEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateKnowledgeListEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateRetrievalChunksEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateRetrievalResult;
import modelengine.fit.jade.datamate.knowledge.external.DataMateKnowledgeBaseManager;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.KnowledgeI18nInfo;
import modelengine.jade.knowledge.KnowledgeI18nService;
import modelengine.jade.knowledge.KnowledgeOption;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.ReferenceLimit;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link DataMateKnowledgeRepoServiceImpl} 的测试类。
 * 不使用 @Nested，确保 FitTestWithJunit 能正确注入 @Mock 与 @Fit。
 */
@FitTestWithJunit(includeClasses = {DataMateKnowledgeRepoServiceImpl.class})
class DataMateKnowledgeRepoServiceImplTest {

    private static final String API_KEY = "test-api-key";

    @Mock
    private DataMateKnowledgeBaseManager knowledgeBaseManager;

    @Mock
    private KnowledgeI18nService knowledgeI18nService;

    @Fit
    private DataMateKnowledgeRepoServiceImpl knowledgeRepoService;

    @Test
    @DisplayName("listRepos - 查询知识库列表成功")
    void shouldReturnPageWhenListRepos() {
        DataMateKnowledgeEntity entity = DataMateKnowledgeEntity.builder()
                .id("kb-1")
                .name("测试知识库")
                .description("描述")
                .createdAt("2025-12-15T10:00:00")
                .embeddingModel("embedding-v1")
                .build();

        DataMateKnowledgeListEntity listEntity = DataMateKnowledgeListEntity.builder()
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .content(Collections.singletonList(entity))
                .build();

        ListRepoQueryParam param = new ListRepoQueryParam();
        param.setPageIndex(1);
        param.setPageSize(10);
        param.setRepoName("test");

        when(knowledgeBaseManager.listRepos(anyString(), any())).thenReturn(listEntity);

        PageVo<KnowledgeRepo> result = knowledgeRepoService.listRepos(API_KEY, param);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0))
                .extracting(KnowledgeRepo::id, KnowledgeRepo::name, KnowledgeRepo::description, KnowledgeRepo::type)
                .containsExactly("kb-1", "测试知识库", "描述", "embedding-v1");
    }

    @Test
    @DisplayName("listRepos - 列表为空时返回空分页")
    void shouldReturnEmptyPageWhenContentEmpty() {
        DataMateKnowledgeListEntity listEntity = DataMateKnowledgeListEntity.builder()
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .content(Collections.emptyList())
                .build();

        ListRepoQueryParam param = new ListRepoQueryParam();
        param.setPageIndex(1);
        param.setPageSize(10);

        when(knowledgeBaseManager.listRepos(anyString(), any())).thenReturn(listEntity);

        PageVo<KnowledgeRepo> result = knowledgeRepoService.listRepos(API_KEY, param);

        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("listRepos - content 为 null 时按空列表处理")
    void shouldReturnEmptyPageWhenContentNull() {
        DataMateKnowledgeListEntity listEntity = DataMateKnowledgeListEntity.builder()
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .content(null)
                .build();

        ListRepoQueryParam param = new ListRepoQueryParam();
        param.setPageIndex(1);
        param.setPageSize(10);

        when(knowledgeBaseManager.listRepos(anyString(), any())).thenReturn(listEntity);

        PageVo<KnowledgeRepo> result = knowledgeRepoService.listRepos(API_KEY, param);

        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("listRepos - totalElements 为 null 时 total 为 0")
    void shouldUseZeroTotalWhenTotalElementsNull() {
        DataMateKnowledgeListEntity listEntity = DataMateKnowledgeListEntity.builder()
                .page(0)
                .size(10)
                .totalElements(null)
                .totalPages(null)
                .content(Collections.emptyList())
                .build();

        ListRepoQueryParam param = new ListRepoQueryParam();
        param.setPageIndex(1);
        param.setPageSize(10);

        when(knowledgeBaseManager.listRepos(anyString(), any())).thenReturn(listEntity);

        PageVo<KnowledgeRepo> result = knowledgeRepoService.listRepos(API_KEY, param);

        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("listRepos - param 为 null 时抛出异常")
    void shouldThrowWhenListReposParamNull() {
        assertThatThrownBy(() -> knowledgeRepoService.listRepos(API_KEY, null))
                .hasMessageContaining("query param");
    }

    @Test
    @DisplayName("getProperty - 获取检索配置成功")
    void shouldReturnPropertyWhenGetProperty() {
        when(knowledgeI18nService.localizeText(any(IndexType.class)))
                .thenReturn(new KnowledgeI18nInfo("语义检索", "描述"));
        doAnswer(inv -> {
            Object arg = inv.getArgument(0);
            if (arg == FilterType.REFERENCE_TOP_K) {
                return new KnowledgeI18nInfo("引用上限", "最大召回条数");
            }
            if (arg == FilterType.SIMILARITY_THRESHOLD) {
                return new KnowledgeI18nInfo("最低相关度", "相似度阈值");
            }
            return new KnowledgeI18nInfo("", "");
        }).when(knowledgeI18nService).localizeText(any(FilterType.class));
        when(knowledgeI18nService.localizeText("rerankParam")).thenReturn("结果重排");
        when(knowledgeI18nService.localizeText("rerankParam.description")).thenReturn("重排描述");

        KnowledgeProperty property = knowledgeRepoService.getProperty(API_KEY);

        assertThat(property.getIndexType()).hasSize(3);
        assertThat(property.getIndexType())
                .extracting(KnowledgeProperty.IndexInfo::type)
                .containsExactly(IndexType.SEMANTIC.value(), IndexType.FULL_TEXT.value(), IndexType.HYBRID.value());
        assertThat(property.getFilterConfig()).hasSize(2);
        assertThat(property.getRerankConfig()).hasSize(1);
    }

    @Test
    @DisplayName("retrieve - 检索知识库成功")
    void shouldReturnDocumentsWhenRetrieve() {
        DataMateRetrievalChunksEntity.RagChunk ragChunk = new DataMateRetrievalChunksEntity.RagChunk();
        ragChunk.setId("chunk-1");
        ragChunk.setText("检索到的文本内容");
        ragChunk.setMetadata("{\"original_file_id\":\"file-1\",\"file_name\":\"doc.pdf\"}");

        DataMateRetrievalChunksEntity chunk = new DataMateRetrievalChunksEntity();
        chunk.setEntity(ragChunk);
        chunk.setScore(0.85);
        chunk.setPrimaryKey("pk-1");

        DataMateRetrievalResult result = new DataMateRetrievalResult();
        result.setData(Collections.singletonList(chunk));

        ReferenceLimit referenceLimit = new ReferenceLimit();
        referenceLimit.setValue(3);
        referenceLimit.setType(FilterType.REFERENCE_TOP_K.value());

        FlatKnowledgeOption option = new FlatKnowledgeOption(KnowledgeOption.custom()
                .query("用户问题")
                .repoIds(Collections.singletonList("kb-1"))
                .indexType(IndexType.SEMANTIC)
                .similarityThreshold(0.5f)
                .referenceLimit(referenceLimit)
                .build());

        when(knowledgeBaseManager.retrieve(anyString(), any())).thenReturn(result);

        List<KnowledgeDocument> documents = knowledgeRepoService.retrieve(API_KEY, option);

        assertThat(documents).hasSize(1);
        assertThat(documents.get(0).getId()).isEqualTo("chunk-1");
        assertThat(documents.get(0).getText()).isEqualTo("检索到的文本内容");
        assertThat(documents.get(0).getScore()).isEqualTo(0.85);
        assertThat(documents.get(0).getMetadata())
                .containsEntry("fileId", "file-1")
                .containsEntry("fileName", "doc.pdf")
                .containsEntry("primaryKey", "pk-1");
    }

    @Test
    @DisplayName("retrieve - 检索结果 data 为 null 时返回空列表")
    void shouldReturnEmptyListWhenDataNull() {
        DataMateRetrievalResult result = new DataMateRetrievalResult();
        result.setData(null);

        FlatKnowledgeOption option = new FlatKnowledgeOption(KnowledgeOption.custom()
                .query("query")
                .repoIds(Collections.emptyList())
                .indexType(IndexType.SEMANTIC)
                .referenceLimit(new ReferenceLimit())
                .build());

        when(knowledgeBaseManager.retrieve(anyString(), any())).thenReturn(result);

        List<KnowledgeDocument> documents = knowledgeRepoService.retrieve(API_KEY, option);

        assertThat(documents).isEmpty();
    }

    @Test
    @DisplayName("retrieve - option 为 null 时抛出异常")
    void shouldThrowWhenRetrieveOptionNull() {
        assertThatThrownBy(() -> knowledgeRepoService.retrieve(API_KEY, null))
                .hasMessageContaining("knowledge option");
    }
}
