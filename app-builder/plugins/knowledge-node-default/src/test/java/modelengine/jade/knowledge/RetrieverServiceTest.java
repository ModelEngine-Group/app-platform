/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.enums.ReferenceType;
import modelengine.jade.knowledge.postprocessor.FactoryOption;
import modelengine.jade.knowledge.postprocessor.PostProcessorFactory;
import modelengine.jade.knowledge.retriever.RetrieverHandler;
import modelengine.jade.knowledge.router.KnowledgeServiceRouter;
import modelengine.jade.knowledge.service.KnowledgeRepoInfo;
import modelengine.jade.knowledge.service.RetrieverService;
import modelengine.jade.knowledge.service.impl.RetrieverServiceImpl;
import modelengine.jade.knowledge.util.RetrieverServiceUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link RetrieverServiceImpl} 的测试。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
@FitTestWithJunit(includeClasses = {RetrieverServiceImpl.class})
public class RetrieverServiceTest {
    private static final String DOCUMENT_TEXT_DUMMY = "test_text";

    @Fit
    private RetrieverService retrieverService;
    @Mock
    private RetrieverHandler retrieverHandler;
    @Mock
    private PostProcessorFactory postProcessorFactory;
    @Mock
    private KnowledgeCenterService knowledgeCenterService;
    @Mock
    private KnowledgeServiceRouter knowledgeServiceRouter;
    @Mock
    private Invoker invoker;

    @BeforeEach
    void setUp() {
        KnowledgeDocument document = new KnowledgeDocument("id", DOCUMENT_TEXT_DUMMY, 0.5, null);
        when(this.retrieverHandler.handle(anyList(),
                any())).thenReturn(Collections.singletonList(new MeasurableDocument(document, document.score())));

        when(this.postProcessorFactory.create(any(FactoryOption.class)))
                .thenReturn(Collections.singletonList(docs -> docs));
        when(this.knowledgeCenterService.getApiKey(any(), any())).thenReturn("");
    }

    @AfterEach
    void tearDown() {
        clearInvocations(this.retrieverHandler, this.postProcessorFactory);
    }

    @Test
    void shouldOkWhenRetrieveHandlerSuccess() {
        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setReferenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 3));
        List<KnowledgeDocument> documents = this.retrieverService.invoke("query",
                Collections.singletonList(new KnowledgeRepoInfo("repoId")),
                retrieverOption);

        assertThat(documents).hasSize(1).extracting(KnowledgeDocument::text).containsExactly(DOCUMENT_TEXT_DUMMY);
    }

    @Test
    void shouldOkWhenRetrieveHandlerWithRerankParam() {
        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setReferenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 3));
        retrieverOption.setRerankParam(new RetrieverOption.RerankParam(true, "model", "baseUri", 2));
        List<KnowledgeDocument> documents = this.retrieverService.invoke("query",
                Collections.singletonList(new KnowledgeRepoInfo("repoId")),
                retrieverOption);

        assertThat(documents).hasSize(1).extracting(KnowledgeDocument::text).containsExactly(DOCUMENT_TEXT_DUMMY);
        verify(this.postProcessorFactory).create(argThat(FactoryOption::isEnableRerank));
    }

    @Test
    void shouldOkWhenRetrieveHandlerWithEmptyRsp() {
        when(this.retrieverHandler.handle(anyList(), any())).thenReturn(Collections.emptyList());

        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setReferenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 3));
        List<KnowledgeDocument> documents = this.retrieverService.invoke("query",
                Collections.singletonList(new KnowledgeRepoInfo("repoId")),
                retrieverOption);
        assertThat(documents).hasSize(0);
    }

    @Test
    void shouldOkWhenPostProcessorWithEmptyRsp() {
        when(this.postProcessorFactory.create(any(FactoryOption.class)))
                .thenReturn(Collections.singletonList(docs -> Collections.emptyList()));

        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setReferenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 3));
        List<KnowledgeDocument> documents = this.retrieverService.invoke("query",
                Collections.singletonList(new KnowledgeRepoInfo("repoId")),
                retrieverOption);
        assertThat(documents).hasSize(0);
    }

    @Test
    void shouldFailWhenRetrieveWithAbnormalQuery() {
        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setReferenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 3));
        assertThatThrownBy(() -> this.retrieverService.invoke(Arrays.asList("query", 0.6F),
                Collections.singletonList(new KnowledgeRepoInfo("repoId")), retrieverOption)).isInstanceOf(
                ModelEngineException.class);

        assertThatThrownBy(() -> this.retrieverService.invoke(0.5F,
                Collections.singletonList(new KnowledgeRepoInfo("repoId")), retrieverOption)).isInstanceOf(
                ModelEngineException.class);
    }

    @Test
    void shouldOkWhenRetrieveWithEmptyKnowledgeRepo() {
        RetrieverOption retrieverOption = RetrieverServiceUtils.buildRetrieverOption();
        retrieverOption.setReferenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 3));
        List<KnowledgeDocument> documents =
                this.retrieverService.invoke("query", Collections.emptyList(), retrieverOption);
        List<KnowledgeDocument> documents2 = this.retrieverService.invoke("query", null, retrieverOption);
        assertThat(documents).hasSize(0);
        assertThat(documents2).hasSize(0);
    }
}
