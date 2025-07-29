/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.jade.common.vo.PageVo;

import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.dto.EdmRepoRecord;
import modelengine.jade.knowledge.entity.EdmListRepoEntity;
import modelengine.jade.knowledge.entity.EdmRetrievalResult;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.enums.KnowledgeTypeEnum;
import modelengine.jade.knowledge.external.EdmKnowledgeBaseManager;
import modelengine.jade.knowledge.service.EdmKnowledgeRepoServiceImpl;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EdmKnowledgeRepoServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-09-26
 */
@FitTestWithJunit(includeClasses = {EdmKnowledgeRepoServiceImpl.class})
public class EdmKnowledgeRepoServiceImplTest {
    private final String apiKey = "key";

    @Fit
    private EdmKnowledgeRepoServiceImpl repoService;

    @Mock
    private EdmKnowledgeBaseManager edmManager;

    @Mock
    private KnowledgeI18nService knowledgeI18nService;

    @BeforeEach
    void setup() {
        when(this.knowledgeI18nService.localizeText(any(IndexType.class))).thenReturn(new KnowledgeI18nInfo("语义检索",
                "基于文本的含义检索出最相关的内容"));
        when(this.knowledgeI18nService.localizeText("rerankParam")).thenReturn("结果重排");
        when(this.knowledgeI18nService.localizeText("rerankParam.description")).thenReturn(
                "将初步检索到的候选文档按照与用户查询的相关性进行重新排序");
        doAnswer(arguments -> {
            if (arguments.getArgument(0) == FilterType.REFERENCE_TOP_K) {
                return new KnowledgeI18nInfo("引用上限", "最大召回知识条数");
            }
            if (arguments.getArgument(0) == FilterType.SIMILARITY_THRESHOLD) {
                return new KnowledgeI18nInfo("最低相关度", "检索文本的最低相关度");
            }
            return null;
        }).when(this.knowledgeI18nService).localizeText(any(FilterType.class));
    }

    @Test
    @DisplayName("查询知识库列表成功")
    void shouldOkWhenListRepo() {
        LocalDateTime time = LocalDateTime.now();
        EdmListRepoEntity entity = new EdmListRepoEntity();
        EdmRepoRecord record = new EdmRepoRecord();
        record.setId(1L);
        record.setName("name1");
        record.setDescription("desc1");
        record.setType("TABLE");
        record.setCreatedAt(Timestamp.valueOf(time));
        record.setStatus("completed");
        entity.setRecords(Collections.singletonList(record));
        entity.setTotal(1);

        when(this.edmManager.listRepos(any())).thenReturn(entity);
        ListRepoQueryParam param = new ListRepoQueryParam();
        param.setRepoName("name1");
        param.setPageIndex(1);
        param.setPageSize(10);

        PageVo<KnowledgeRepo> repos = this.repoService.listRepos(apiKey, param);
        assertThat(repos).isNotNull();
        assertThat(repos.getTotal()).isEqualTo(1);
        assertThat(repos.getItems().get(0)).extracting(KnowledgeRepo::id,
                        KnowledgeRepo::name,
                        KnowledgeRepo::description,
                        KnowledgeRepo::type,
                        KnowledgeRepo::createdAt)
                .containsExactly("1", "name1", "desc1", KnowledgeTypeEnum.RDB.toString(), time);
    }

    @Test
    @DisplayName("查询检索知识内容成功")
    void shouldOkWhenRetrieve() {
        Object meta = "{\"meta1\":\"info1\"}";
        EdmRetrievalResult result = new EdmRetrievalResult();
        result.setId(1L);
        result.setContent("content");
        result.setScore(80f);
        result.setMetaInfo(meta);
        when(this.edmManager.retrieve(any())).thenReturn(Collections.singletonList(result));

        FlatKnowledgeOption flatOption = new FlatKnowledgeOption(KnowledgeOption.custom()
                .query("query")
                .repoIds(Collections.emptyList())
                .indexType(IndexType.SEMANTIC)
                .similarityThreshold(0.5f)
                .build());
        List<KnowledgeDocument> documents = this.repoService.retrieve(apiKey, flatOption);
        assertThat(documents).isNotNull();
        assertThat(documents.size()).isEqualTo(1);
        assertThat(documents.get(0)).extracting(KnowledgeDocument::id,
                KnowledgeDocument::text,
                KnowledgeDocument::metadata).containsExactly("1", "content", Collections.emptyMap());
    }

    @Test
    @DisplayName("知识库获取配置成功")
    void shouldOkWhenGetProperty() {
        KnowledgeProperty property = this.repoService.getProperty(apiKey);
        assertThat(property.indexType().size()).isEqualTo(1);
        assertThat(property.filterConfig().size()).isEqualTo(2);
        assertThat(property.rerankConfig().size()).isEqualTo(1);
        assertThat(property.indexType().get(0)).extracting(KnowledgeProperty.IndexInfo::type,
                        KnowledgeProperty.IndexInfo::name,
                        KnowledgeProperty.IndexInfo::description)
                .containsExactly(IndexType.SEMANTIC.value(), "语义检索", "基于文本的含义检索出最相关的内容");
    }
}