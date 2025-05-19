/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.TestUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService;
import modelengine.fitframework.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link NaiveRAGComponent}的测试集
 *
 * @author 黄夏露
 * @since 2024-04-26
 */
@ExtendWith(MockitoExtension.class)
public class NaiveRAGComponentTest {
    private static final String NAIVE_RAG_KNOWLEDGE_KEY = "knowledge";
    private static final String NAIVE_RAG_QUERY_KEY = "query";
    private static final String NAIVE_RAG_MAXIMUM_KEY = "maximum";
    private static final String NAIVE_RAG_OUTPUT = "retrievalOutput";
    private static final String DUMMY_QUERY = "This is query.";
    private static final Integer DUMMY_MAXIMUM = 3;

    @Mock
    private KnowledgeBaseService knowledgeBaseServiceMock;

    private NaiveRAGComponent naiveRAGComponent;

    @BeforeEach
    void setUp() {
        this.naiveRAGComponent = new NaiveRAGComponent(knowledgeBaseServiceMock);
    }

    private Map<String, Object> genBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(NAIVE_RAG_QUERY_KEY, DUMMY_QUERY);
        businessData.put(NAIVE_RAG_MAXIMUM_KEY, DUMMY_MAXIMUM);
        return businessData;
    }

    @Test
    void shouldOkWhenNoKnowledge() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        Map<String, Object> exceptResult = new HashMap<String, Object>() {{
            put(NAIVE_RAG_OUTPUT, StringUtils.EMPTY);
        }};
        businessData.put("output", exceptResult);
        this.naiveRAGComponent.handleTask(flowData);
        verify(this.knowledgeBaseServiceMock, never()).vectorSearchKnowledgeTable(any());
        Assertions.assertEquals(businessData.get("output"), exceptResult);
    }

    @Test
    void shouldOkWhenKnowledgeIsEmpty() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        List<Map<String, Object>> knowldegeList = Collections.singletonList(new HashMap<>());
        businessData.put(NAIVE_RAG_KNOWLEDGE_KEY, knowldegeList);
        Map<String, Object> exceptResult = new HashMap<String, Object>() {{
            put(NAIVE_RAG_OUTPUT, StringUtils.EMPTY);
        }};
        businessData.put("output", exceptResult);
        this.naiveRAGComponent.handleTask(flowData);
        verify(this.knowledgeBaseServiceMock, never()).vectorSearchKnowledgeTable(any());
        Assertions.assertEquals(businessData.get("output"), exceptResult);
    }

    @Test
    void shouldOkWhenUseKnowledge() {
        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> knowldegeList = this.getDummyKnowledgeList();
        businessData.put(NAIVE_RAG_KNOWLEDGE_KEY, knowldegeList);
        String exceptNaiveRAGOutput = "This is naiveRAG result.";
        Map<String, Object> exceptResult = new HashMap<String, Object>() {{
            put("retrievalOutput", exceptNaiveRAGOutput);
        }};
        when(this.knowledgeBaseServiceMock.vectorSearchKnowledgeTable(any()))
                .thenReturn(Arrays.asList(exceptNaiveRAGOutput));
        List<Map<String, Object>> flowData = TestUtils.buildFlowDataWithExtraConfig(businessData, null);
        List<Map<String, Object>> resultFlowData = this.naiveRAGComponent.handleTask(flowData);
        Map<String, Object> resultBusinessData = DataUtils.getBusiness(resultFlowData);
        verify(this.knowledgeBaseServiceMock, times(1)).vectorSearchKnowledgeTable(any());
        Assertions.assertEquals(resultBusinessData.get("output"), exceptResult);
    }

    @NotNull
    private List<Long> getDummyTableIdList() {
        return new ArrayList<>(Arrays.asList(2L));
    }

    @NotNull
    private List<Map<String, Object>> getDummyKnowledgeList() {
        return new ArrayList<>(Arrays.asList(new HashMap<String, Object>() {{
            put("id", "1");
            put("serviceType", "RDB");
        }}, new HashMap<String, Object>() {{
            put("id", "2");
            put("serviceType", "VECTOR");
        }}));
    }
}
