/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.retriever.RetrieverHandler;
import modelengine.jade.knowledge.retriever.support.DefaultRetrieverHandler;
import modelengine.jade.knowledge.router.KnowledgeServiceRouter;
import modelengine.jade.knowledge.util.RetrieverServiceUtils;

import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fitframework.broker.client.Invoker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link DefaultRetrieverHandler} 的测试。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
@ExtendWith(MockitoExtension.class)
public class RetrieverHandlerTest {
    private static final String DOCUMENT_TEXT_DUMMY = "test_text";

    @Mock
    private Invoker invoker;

    private RetrieverHandler handler;

    @Mock
    private KnowledgeServiceRouter knowledgeServiceRouter;

    @BeforeEach
    void setUp() {
        handler = new DefaultRetrieverHandler(knowledgeServiceRouter);
        when(knowledgeServiceRouter.getRouter(any(), anyString(), anyString())).thenReturn(invoker);
        when(invoker.invoke(anyString(), any()))
                .thenReturn(Collections.singletonList(
                        new KnowledgeDocument("id", DOCUMENT_TEXT_DUMMY, 0.5, null)));
    }

    @Test
    void shouldOkWhenRetrieveWithSingleQuery() {
        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setGroupId("default");
        List<MeasurableDocument> documents = this.handler.handle(Collections.singletonList("query"), retrieverOption);
        assertThat(documents).hasSize(1).extracting(MeasurableDocument::text).containsExactly(DOCUMENT_TEXT_DUMMY);
    }

    @Test
    void shouldOkWhenRetrieveWithMultiQuery() {
        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setGroupId("default");
        List<MeasurableDocument> documents = this.handler.handle(Arrays.asList("query0", "query1"), retrieverOption);
        assertThat(documents).hasSize(2).extracting(MeasurableDocument::text)
                .containsExactly(DOCUMENT_TEXT_DUMMY, DOCUMENT_TEXT_DUMMY);
    }
}
