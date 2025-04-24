/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.knowledge.dto.EdmRepoRecord;
import modelengine.jade.knowledge.dto.EdmRetrievalParam;
import modelengine.jade.knowledge.dto.ListKnowledgeQueryParam;
import modelengine.jade.knowledge.entity.EdmListRepoEntity;
import modelengine.jade.knowledge.entity.EdmUrls;
import modelengine.jade.knowledge.external.EdmKnowledgeBaseManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;

/**
 * 表示 {@link EdmKnowledgeBaseManager} 的测试集
 *
 * @author 何嘉斌
 * @since 2024-09-26
 */
@MvcTest(classes = {MockedEdmKnowledgeBaseInnerController.class, EdmKnowledgeBaseManager.class, EdmUrls.class})
public class EdmKnowledgeBaseManagerTest {
    @Fit
    EdmKnowledgeBaseManager manager;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setUp() throws Exception {
        Field edmHost = manager.getClass().getDeclaredField("edmHost");
        edmHost.setAccessible(true);
        edmHost.set(manager, "http://localhost:" + mockMvc.getPort());
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
        ListKnowledgeQueryParam param = new ListKnowledgeQueryParam();
        param.setName("ok");
        EdmListRepoEntity entity = manager.listRepos(param);
        assertThat(entity.getRecords()).extracting(EdmRepoRecord::getId,
                        EdmRepoRecord::getName,
                        EdmRepoRecord::getDescription,
                        EdmRepoRecord::getType,
                        EdmRepoRecord::getCreatedAt)
                .containsExactly(tuple(2L,
                                "lxh-2",
                                "description",
                                "VECTOR",
                                Timestamp.valueOf("2024-09-26 16:16:21.054")),
                        tuple(1L, "lxh-k", "", "VECTOR", Timestamp.valueOf("2024-09-26 12:12:14.320")));
    }

    @Test
    @DisplayName("查询知识库列表失败，抛出异常")
    public void shouldFailWhenListRepoThrowsException() {
        ListKnowledgeQueryParam param = new ListKnowledgeQueryParam();
        param.setName("error");
        assertThatThrownBy(() -> manager.listRepos(param)).isInstanceOf(ModelEngineException.class)
                .extracting("message")
                .isEqualTo("The feature of linking to external knowledge base will be launched on 2025-04-30, "
                        + "so stay tuned.");
        ;
    }

    @Test
    @DisplayName("查询检索知识内容失败，抛出异常")
    public void shouldFailWhenRetrieveThrowsException() {
        EdmRetrievalParam param = new EdmRetrievalParam();
        param.setContext("error");
        assertThatThrownBy(() -> manager.retrieve(param)).isInstanceOf(ModelEngineException.class)
                .extracting("message")
                .isEqualTo("The feature of linking to external knowledge base will be launched on 2025-04-30, "
                        + "so stay tuned.");
    }
}