/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fit.jade.datamate.knowledge.dto.DataMateKnowledgeListQueryParam;
import modelengine.fit.jade.datamate.knowledge.dto.DataMateRetrievalParam;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateKnowledgeEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateKnowledgeListEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateRetrievalChunksEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateRetrievalResult;
import modelengine.fit.jade.datamate.knowledge.external.DataMateKnowledgeBaseManager;
import modelengine.jade.knowledge.exception.KnowledgeException;

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
 * 表示 {@link DataMateKnowledgeBaseManager} 的测试集。
 *
 * @author songyongtan
 * @since 2026-02-11
 */
@MvcTest(classes = {MockedDataMateKnowledgeBaseInnerController.class, DataMateKnowledgeBaseManager.class})
public class DataMateKnowledgeBaseManagerTest {
    private String apiKey = "123";

    @Fit
    DataMateKnowledgeBaseManager manager;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setUp() throws Exception {
        Field dataMateUrls = manager.getClass().getDeclaredField("dataMateUrls");
        dataMateUrls.setAccessible(true);
        Map<String, String> urls = new HashMap<>();
        urls.put("list", "http://localhost:" + mockMvc.getPort() + "/v2/knowledgeBase");
        urls.put("retrieve", "http://localhost:" + mockMvc.getPort() + "/v2/knowledgebases/query");
        dataMateUrls.set(manager, urls);
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
        DataMateKnowledgeListQueryParam param = DataMateKnowledgeListQueryParam.builder().name("ok").build();
        DataMateKnowledgeListEntity entity = this.manager.listRepos(apiKey, param);
        assertThat(entity.getContent().size()).isEqualTo(2);
        assertThat(entity.getContent().get(0)).extracting(DataMateKnowledgeEntity::getId,
                DataMateKnowledgeEntity::getName,
                DataMateKnowledgeEntity::getDescription).containsExactly("1", "test1", "test1知识库");
        assertThat(entity.getContent().get(1)).extracting(DataMateKnowledgeEntity::getId,
                DataMateKnowledgeEntity::getName,
                DataMateKnowledgeEntity::getDescription).containsExactly("2", "test2", "test2知识库");
    }

    @Test
    @DisplayName("查询知识库列表失败，抛出异常")
    public void shouldFailWhenListRepoThrowException() {
        DataMateKnowledgeListQueryParam param = DataMateKnowledgeListQueryParam.builder().name("error").build();
        assertThatThrownBy(() -> this.manager.listRepos(apiKey, param)).isInstanceOf(KnowledgeException.class)
                .extracting("code")
                .isEqualTo(130703005);
    }

    @Test
    @DisplayName("检索知识库成功")
    public void shouldOkWhenRetrieve() {
        DataMateRetrievalParam param = DataMateRetrievalParam.builder().query("ok").build();
        DataMateRetrievalResult result = this.manager.retrieve(apiKey, param);
        assertThat(result.getData().size()).isEqualTo(3);
        assertThat(result.getData().get(0).chunkId()).isEqualTo("chunk1");
        assertThat(result.getData().get(0).content()).isEqualTo("content1");
    }

    @Test
    @DisplayName("检索知识库失败，抛出异常")
    public void shouldFailWhenRetrieveThrowException() {
        DataMateRetrievalParam param = DataMateRetrievalParam.builder().query("error").build();
        assertThatThrownBy(() -> this.manager.retrieve(apiKey, param)).isInstanceOf(KnowledgeException.class)
                .extracting("code")
                .isEqualTo(130703005);
    }
}
