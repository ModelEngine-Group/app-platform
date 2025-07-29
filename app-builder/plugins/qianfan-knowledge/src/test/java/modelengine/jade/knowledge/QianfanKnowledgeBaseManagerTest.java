/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.jade.knowledge.dto.QianfanKnowledgeListQueryParam;
import modelengine.jade.knowledge.dto.QianfanRetrievalParam;
import modelengine.jade.knowledge.entity.QianfanKnowledgeEntity;
import modelengine.jade.knowledge.entity.QianfanKnowledgeListEntity;
import modelengine.jade.knowledge.entity.QianfanRetrievalChunksEntity;
import modelengine.jade.knowledge.entity.QianfanRetrievalResult;
import modelengine.jade.knowledge.exception.KnowledgeException;
import modelengine.jade.knowledge.external.QianfanKnowledgeBaseManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link QianfanKnowledgeBaseManager} 的测试集。
 *
 * @author 陈潇文
 * @since 2025-05-06
 */
@MvcTest(classes = {MockedQianfanKnowledgeBaseInnerController.class, QianfanKnowledgeBaseManager.class})
public class QianfanKnowledgeBaseManagerTest {
    private String apiKey = "123";

    @Fit
    QianfanKnowledgeBaseManager manager;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setUp() throws Exception {
        Field qianfanUrls = manager.getClass().getDeclaredField("qianfanUrls");
        qianfanUrls.setAccessible(true);
        Map<String, String> urls = new HashMap<>();
        urls.put("knowledgeList",
                "http://localhost:" + mockMvc.getPort() + "/v2/knowledgeBase?Action=DescribeKnowledgeBases");
        urls.put("knowledgeRetrieve", "http://localhost:" + mockMvc.getPort() + "/v2/knowledgebases/query");
        qianfanUrls.set(manager, urls);
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("查询知识库列表成功")
    public void shouldOkWhenListRepo() {
        QianfanKnowledgeListQueryParam param = QianfanKnowledgeListQueryParam.builder().keyword("ok").build();
        QianfanKnowledgeListEntity entity = this.manager.listRepos(apiKey, param);
        assertThat(entity.getData().size()).isEqualTo(2);
        assertThat(entity.getData().get(0)).extracting(QianfanKnowledgeEntity::getId,
                QianfanKnowledgeEntity::getName,
                QianfanKnowledgeEntity::getDescription).containsExactly("1", "test1", "test1知识库");
        assertThat(entity.getData().get(1)).extracting(QianfanKnowledgeEntity::getId,
                QianfanKnowledgeEntity::getName,
                QianfanKnowledgeEntity::getDescription).containsExactly("2", "test2", "test2知识库");
    }

    @Test
    @DisplayName("查询知识库列表失败，抛出异常")
    public void shouldFailWhenListRepoThrowException() {
        QianfanKnowledgeListQueryParam param = QianfanKnowledgeListQueryParam.builder().keyword("error").build();
        assertThatThrownBy(() -> this.manager.listRepos(apiKey, param)).isInstanceOf(KnowledgeException.class)
                .extracting("code")
                .isEqualTo(130703005);
    }

    @Test
    @DisplayName("检索知识库成功")
    public void shouldOkWhenRetrieve() {
        QianfanRetrievalParam param = QianfanRetrievalParam.builder().query("ok").build();
        QianfanRetrievalResult result = this.manager.retrieve(apiKey, param);
        assertThat(result.getTotalCount()).isEqualTo(3);
        assertThat(result.getChunks().get(0)).extracting(QianfanRetrievalChunksEntity::getChunkId,
                        QianfanRetrievalChunksEntity::getContent,
                        QianfanRetrievalChunksEntity::getDocumentId,
                        QianfanRetrievalChunksEntity::getDocumentName,
                        QianfanRetrievalChunksEntity::getKnowledgebaseId)
                .containsExactly("chunk1", "content1", "doc1", "doc1.txt", "know1");
    }

    @Test
    @DisplayName("检索知识库失败，抛出异常")
    public void shouldFailWhenRetrieveThrowException() {
        QianfanRetrievalParam param = QianfanRetrievalParam.builder().query("error").build();
        assertThatThrownBy(() -> this.manager.retrieve(apiKey, param)).isInstanceOf(KnowledgeException.class)
                .extracting("code")
                .isEqualTo(130703005);
    }
}
