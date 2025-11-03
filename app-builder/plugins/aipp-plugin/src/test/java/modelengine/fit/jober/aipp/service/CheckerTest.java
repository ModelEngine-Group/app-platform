/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.service.impl.LlmNodeChecker;
import modelengine.fit.jober.aipp.service.impl.RetrievalNodeChecker;
import modelengine.fit.jober.aipp.service.impl.ToolInvokeNodeChecker;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.jade.store.service.PluginToolService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link Checker} 的测试类
 */
@ExtendWith(MockitoExtension.class)
class CheckerTest {
    @Nested
    @DisplayName("模型节点校验")
    class TestLlmNodeChecker {
        private LlmNodeChecker llmNodeChecker;

        @Mock
        private AippModelCenter modelCenter;

        @Mock
        private PluginToolService pluginToolService;

        @BeforeEach
        public void before() {
            llmNodeChecker = new LlmNodeChecker(modelCenter, pluginToolService);
        }

        @Test
        @DisplayName("模型、工具流、工具配置存在，检查通过")
        public void testModelNodeSuccess() {
            String testNode =
                "{\"type\":\"llmNodeState\",\"nodeInfos\":[{\"nodeId\":\"llmnode1\",\"nodeName\":\"大模型1\","
                    + "\"configs\":[{\"id\":\"db5fdafa-4cbf-44ba-9cca-8a98f1f77112\",\"configName\":\"accessInfo\","
                    + "\"serviceName\":\"Fake Model\",\"tag\":\"INTERNAL\"},"
                    + "{\"id\":\"383537a2-4371-1744-be05-7b5ab50dec0b\",\"configName\":\"plugin\","
                    + "\"uniqueName\":\"2de8e815-187f-4aeb-9363-13e7836f4271\"},"
                    + "{\"id\":\"06cefe21-e092-a04e-90c6-75e94030e0e2\",\"configName\":\"plugin\","
                    + "\"uniqueName\":\"6834efb7-ba3d-f044-a875-4db8be8754b0\"}]}]}";
            AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
            when(pluginToolService.hasPluginTools(any())).thenReturn(Collections.singletonList(true));
            ModelAccessInfo modelAccessInfo = new ModelAccessInfo("Fake Model", "INTERNAL", "", "", null);
            when(pluginToolService.hasPluginTools(any())).thenReturn(Arrays.asList(true, true));
            when(modelCenter.fetchModelList(any(), any(), any())).thenReturn(
                    new ModelListDto(Collections.singletonList(modelAccessInfo), 1));
            List<CheckResult> results = this.llmNodeChecker.validate(appCheckDto,null);
            Assertions.assertEquals(results.size(), 1);
            Assertions.assertTrue(results.get(0).isValid());
        }

        @Test
        @DisplayName("模型、工具流、工具配置不存在，检查结果包含不可用配置")
        public void testModelNodeWithNonExistsConfigs() {
            String testNode =
                "{\"type\":\"llmNodeState\",\"nodeInfos\":[{\"nodeId\":\"jadewdnjbq\",\"nodeName\":\"大模型\","
                    + "\"configs\":[{\"id\":\"db5fdafa-4cbf-44ba-9cca-8a98f1f77111\",\"configName\":\"accessInfo\","
                    + "\"serviceName\":\"Fake Model\",\"tag\":\"INTERNAL\"},"
                    + "{\"id\":\"0afbb780-5b15-e741-a138-754fad3caa15\",\"configName\":\"plugin\","
                    + "\"uniqueName\":\"c373a626-f671-6040-8051-808185e9e5b4\"}]},{\"nodeId\":\"llmnode1\","
                    + "\"nodeName\":\"大模型1\",\"configs\":[{\"id\":\"db5fdafa-4cbf-44ba-9cca-8a98f1f77112\","
                    + "\"configName\":\"accessInfo\",\"serviceName\":\"Another Fake Model\",\"tag\":\"INTERNAL\"},"
                    + "{\"id\":\"383537a2-4371-1744-be05-7b5ab50dec0b\",\"configName\":\"plugin\","
                    + "\"uniqueName\":\"2de8e815-187f-4aeb-9363-13e7836f4271\"},"
                    + "{\"id\":\"06cefe21-e092-a04e-90c6-75e94030e0e2\",\"configName\":\"plugin\","
                    + "\"uniqueName\":\"6834efb7-ba3d-f044-a875-4db8be8754b0\"},"
                    + "{\"id\":\"0afbb780-5b15-e741-a138-754fad3caa15\",\"configName\":\"plugin\","
                    + "\"uniqueName\":\"c373a626-f671-6040-8051-808185e9e5b4\"}]}]}";
            AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
            when(pluginToolService.hasPluginTools(any())).thenReturn(Arrays.asList(false, false, false));
            ModelAccessInfo modelAccessInfo = new ModelAccessInfo("Fake Model", "EXTERNAL", "", "", null);
            when(modelCenter.fetchModelList(any(), any(), any())).thenReturn(
                    new ModelListDto(Collections.singletonList(modelAccessInfo), 1));
            List<CheckResult> results = this.llmNodeChecker.validate(appCheckDto, null);
            Assertions.assertEquals(results.size(), 2);
            Assertions.assertFalse(results.get(0).isValid());
            Assertions.assertEquals(results.get(0).getConfigChecks().size(), 2);
            Assertions.assertFalse(results.get(1).isValid());
            Assertions.assertEquals(results.get(1).getConfigChecks().size(), 4);
        }
    }

    @Nested
    @DisplayName("检索节点校验")
    class TestRetrievalNodeChecker {
        private RetrievalNodeChecker retrievalNodeChecker;

        @BeforeEach
        public void before() {
            retrievalNodeChecker = new RetrievalNodeChecker();
        }

        @Test
        @DisplayName("不进行校验，返回所有配置不合法")
        public void testRetrievalNode() {
            String testNode = "{\"type\":\"knowledgeRetrievalNodeState\",\"nodeInfos\":[{\"nodeId\":\"jadenthrcv\","
                + "\"nodeName\":\"知识检索\",\"configs\":[{\"id\":14,\"configName\":\"knowledgeRepos\","
                + "\"name\":\"k14\",\"description\":\"\",\"type\":\"VECTOR\",\"createdAt\":\"2024-12-02 12:41:14\","
                + "\"checked\":true},{\"id\":2,\"configName\":\"knowledgeRepos\",\"name\":\"k2\",\"description\":\"\","
                + "\"type\":\"VECTOR\",\"createdAt\":\"2024-12-02 12:37:17\",\"checked\":true}]},"
                + "{\"nodeId\":\"jadenthrc1\",\"nodeName\":\"知识检索1\",\"configs\":[{\"id\":11,"
                + "\"configName\":\"knowledgeRepos\",\"name\":\"k14\",\"description\":\"\",\"type\":\"VECTOR\","
                + "\"createdAt\":\"2024-12-02 12:41:14\",\"checked\":true}]}]}";
            AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
            List<CheckResult> results = this.retrievalNodeChecker.validate(appCheckDto, null);
            Assertions.assertTrue(results.isEmpty());
        }
    }

    @Nested
    @DisplayName("插件节点校验")
    class TestPluginNodeChecker {
        private ToolInvokeNodeChecker toolInvokeNodeChecker;

        @Mock
        private PluginToolService pluginToolService;

        @BeforeEach
        public void before() {
            toolInvokeNodeChecker = new ToolInvokeNodeChecker(pluginToolService);
        }

        @Test
        @DisplayName("插件配置存在，检查通过")
        public void testPluginNodeSuccess() {
            String testNode =
                "{\"type\":\"toolInvokeNodeState\",\"nodeInfos\":[{\"nodeId\":\"jadecn3l6n\",\"nodeName\":\"666\","
                    + "\"configs\":[{\"id\":\"jadecn3l6n\",\"configName\":\"toolInvokeNodeState\","
                    + "\"uniqueName\":\"20ac1975-2455-4283-9a2f-30c7b5108df6\"}]},{\"nodeId\":\"pluginNode2\","
                    + "\"nodeName\":\"555\",\"configs\":[{\"id\":\"jadecn3l6l\",\"configName\":\"toolInvokeNodeState\","
                    + "\"uniqueName\":\"3e8c4186-5609-8148-a92e-3da9e4a1a660\"}]}]}";
            AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
            when(pluginToolService.hasPluginTools(any())).thenReturn(Arrays.asList(true, true));
            List<CheckResult> results = this.toolInvokeNodeChecker.validate(appCheckDto, null);
            Assertions.assertEquals(results.size(), 2);
            Assertions.assertTrue(results.get(0).isValid());
            Assertions.assertTrue(results.get(1).isValid());
        }

        @Test
        @DisplayName("插件配置不存在，检查结果包含不可用配置")
        public void testPluginNodeWithNonExistsConfigs() {
            String testNode =
                "{\"type\":\"toolInvokeNodeState\",\"nodeInfos\":[{\"nodeId\":\"jadecn3l6n\",\"nodeName\":\"666\","
                    + "\"configs\":[{\"id\":\"jadecn3l6n\",\"configName\":\"toolInvokeNodeState\","
                    + "\"uniqueName\":\"20ac1975-2455-4283-9a2f-30c7b5108df6\"}]},{\"nodeId\":\"pluginNode2\","
                    + "\"nodeName\":\"555\",\"configs\":[{\"id\":\"jadecn3l6l\",\"configName\":\"toolInvokeNodeState\","
                    + "\"uniqueName\":\"3e8c4186-5609-8148-a92e-3da9e4a1a660\"}]}]}";
            AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
            when(pluginToolService.hasPluginTools(any())).thenReturn(Arrays.asList(true, false));
            List<CheckResult> results = this.toolInvokeNodeChecker.validate(appCheckDto, null);
            Assertions.assertEquals(results.size(), 2);
            Assertions.assertTrue(results.get(0).isValid());
            Assertions.assertFalse(results.get(1).isValid());
            Assertions.assertEquals(results.get(1).getConfigChecks().size(), 1);
        }
    }
}