/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.impls;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import modelengine.fit.jade.aipp.tool.loop.LoopToolService;
import modelengine.fit.jade.aipp.tool.loop.dependencies.ToolCallService;
import modelengine.fit.jade.aipp.tool.loop.entities.Config;
import modelengine.fit.jade.aipp.tool.loop.entities.ToolInfo;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * LoopTool测试类
 */
class LoopToolServiceImplTest {
    private ToolCallService toolCallService;

    private AippRunTimeService aippRunTimeService;

    private LoopToolService loopToolService;


    @BeforeEach
    void setUp() {
        this.toolCallService = mock(ToolCallService.class);
        this.aippRunTimeService = mock(AippRunTimeService.class);
        this.loopToolService = new LoopToolServiceImpl(this.toolCallService, this.aippRunTimeService, 1);
    }

    @Test
    void baseCase() {
        this.doTest(new BaseTestCase());
    }

    @Test
    void multiLayerCase() {
        this.doTest(new MultiLayerTestCase());
    }

    @Test
    void realData() {
        this.doTest(new JsonDataBaseTestCase());
    }

    @Test
    void realData2() {
        this.doTest(new JsonDataBaseTestCase2());
    }

    @Test
    void shouldThrowExceptionWhenCallLoopToolGivenTerminatedAippInstance() {
        when(this.toolCallService.call(anyString(), anyMap())).thenReturn("1");
        String aippInstanceId = "1";
        HashMap<String, Object> args = new HashMap<>();
        String loopArgName = "arg1";
        args.put(loopArgName, List.of(1, 2, 3));
        Config config = new Config();
        config.setLoopKeys(List.of(loopArgName));
        ToolInfo toolInfo = new ToolInfo();
        toolInfo.setUniqueName("id");
        ToolInfo.ParamInfo paramInfo = new ToolInfo.ParamInfo();
        paramInfo.setName(loopArgName);
        toolInfo.setParams(List.of(paramInfo));
        when(this.aippRunTimeService.isInstanceRunning(eq(aippInstanceId), any())).thenReturn(false);

        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> this.loopToolService.loopTool(args,
                        config,
                        toolInfo,
                        MapBuilder.<String, Object>get().put(AippConst.CONTEXT_INSTANCE_ID, aippInstanceId).build()));

        Assertions.assertEquals("Already terminated. [aippInstanceId=1]", exception.getMessage());
        verify(this.toolCallService, times(1)).call(anyString(), anyMap());
    }

    private void doTest(LoopToolTestCase testCase) {
        when(this.toolCallService.call(anyString(), anyMap())).then(testCase.getAnswer());
        String aippInstanceId = "1";
        when(this.aippRunTimeService.isInstanceRunning(eq(aippInstanceId), any())).thenReturn(true);
        List<Object> loopArgs = this.loopToolService.loopTool(testCase.getArgs(), testCase.getConfig(),
                testCase.getToolInfo(), MapBuilder.<String, Object>get().put("instanceId", aippInstanceId).build());
        List<Object> expectResult = testCase.getExpectResult();
        verify(this.toolCallService, times(expectResult.size())).call(anyString(), anyMap());
        for (int i = 0; i < expectResult.size(); i++) {
            Assertions.assertEquals(expectResult.get(i), loopArgs.get(i));
        }
    }

    private static class JsonDataBaseTestCase extends LoopToolTestCase {
        protected Map<String, Object> buildArgs() {
            return JSONObject.parseObject(
                    "{\"location\":[\"insert\",\"delete\",\"update\",\"select\"]}");
        }

        protected Config buildConfig() {
            return JSONObject.parseObject("{\"loopKeys\":[\"location\"]}", Config.class);
        }

        protected ToolInfo buildToolInfo() {
            String toolInfoStr
                    = "{\"uniqueName\":\"d97fd0a5-73b8-45ca-b8ab-d250a70be2ef\",\"pluginName\":\"mobile_rain_today\",\"params\":[{\"name\":\"location\"}],\"return\":{\"type\":\"array\"},\"tags\":[\"FIT\",\"TEST\"]}";
            return JSONObject.parseObject(toolInfoStr, ToolInfo.class);
        }

        @Override
        protected List<Object> buildExpectResult() {
            return Lists.newArrayList("insert", "delete", "update", "select");
        }

        @Override
        protected Answer buildAnswer() {
            return invocationOnMock -> {
                Object argument = invocationOnMock.getArguments()[1];
                return ObjectUtils.<Map<String, Object>>cast(argument).get("location");
            };
        }
    }

    private static class JsonDataBaseTestCase2 extends LoopToolTestCase {
        protected Map<String, Object> buildArgs() {
            return JSONObject.parseObject(
                    "{\"db_name\":\"23\",\"dsl\":[\"insert\",\"delete\",\"update\",\"select\"]}\n");
        }

        protected Config buildConfig() {
            return JSONObject.parseObject("{\"loopKeys\":[\"dsl\"]}", Config.class);
        }

        protected ToolInfo buildToolInfo() {
            String toolInfoStr
                    = "{\"uniqueName\":\"f694dd1c-3cf4-47f6-bbdb-e26f9aa6f361\","
                    + "\"pluginName\":\"环比查询\","
                    + "\"params\":[{\"name\":\"db_name\"},{\"name\":\"dsl\"}],\"return\":{\"type\":\"object\"}}";
            return JSONObject.parseObject(toolInfoStr, ToolInfo.class);
        }

        @Override
        protected List<Object> buildExpectResult() {
            return Lists.newArrayList("insert", "delete", "update", "select");
        }

        @Override
        protected Answer buildAnswer() {
            return invocationOnMock -> {
                Object argument = invocationOnMock.getArguments()[1];
                return ObjectUtils.<Map<String, Object>>cast(argument).get("dsl");
            };
        }
    }

    private static class MultiLayerTestCase extends LoopToolTestCase {
        protected Map<String, Object> buildArgs() {
            Map<String, Object> args = new HashMap<>();
            args.put("name", "dog");
            HashMap<Object, Object> info = new HashMap<>();
            args.put("info", info);
            ArrayList<Object> ages = new ArrayList<>();
            ages.add(1);
            ages.add(2);
            ages.add(3);
            info.put("age", ages);
            return args;
        }

        protected Config buildConfig() {
            Config config = new Config();
            ArrayList<String> loopKeys = new ArrayList<>();
            loopKeys.add("info.age");
            config.setLoopKeys(loopKeys);
            return config;
        }

        protected ToolInfo buildToolInfo() {
            ToolInfo entity = new ToolInfo();
            entity.setUniqueName("dogHealth");
            ArrayList<ToolInfo.ParamInfo> paramInfos = new ArrayList<>();
            paramInfos.add(this.buildParamInfo("name"));
            paramInfos.add(this.buildParamInfo("info"));
            entity.setParams(paramInfos);
            return entity;
        }

        @Override
        protected List<Object> buildExpectResult() {
            return IntStream.range(1, 4).mapToObj(this::buildExpectValue).collect(Collectors.toList());
        }

        @Override
        protected Answer buildAnswer() {
            return invocationOnMock -> {
                Object argument = invocationOnMock.getArguments()[1];
                return ObjectUtils.<Map<String, Object>>cast(argument).get("info");
            };
        }

        private Map<String, Object> buildExpectValue(int value1) {
            HashMap<String, Object> age = new HashMap<>();
            age.put("age", value1);
            return age;
        }
    }

    private static class BaseTestCase extends LoopToolTestCase {
        protected Map<String, Object> buildArgs() {
            Map<String, Object> args = new HashMap<>();
            args.put("name", "dog");
            ArrayList<Object> ages = new ArrayList<>();
            ages.add(1);
            ages.add(2);
            ages.add(3);
            args.put("age", ages);
            return args;
        }

        protected Config buildConfig() {
            Config config = new Config();
            ArrayList<String> loopKeys = new ArrayList<>();
            loopKeys.add("age");
            config.setLoopKeys(loopKeys);
            return config;
        }

        protected ToolInfo buildToolInfo() {
            ToolInfo entity = new ToolInfo();
            entity.setUniqueName("dogHealth");
            ArrayList<ToolInfo.ParamInfo> paramInfos = new ArrayList<>();
            paramInfos.add(this.buildParamInfo("name"));
            paramInfos.add(this.buildParamInfo("age"));
            entity.setParams(paramInfos);
            return entity;
        }

        @Override
        protected List<Object> buildExpectResult() {
            return Lists.newArrayList(1, 2, 3);
        }

        @Override
        protected Answer buildAnswer() {
            return invocationOnMock -> {
                Object argument = invocationOnMock.getArguments()[1];
                return ObjectUtils.<Map<String, Object>>cast(argument).get("age");
            };
        }
    }

    @Data
    private static abstract class LoopToolTestCase {
        private final Map<String, Object> args;

        private final Config config;

        private final ToolInfo toolInfo;

        private final List<Object> expectResult;

        private final Answer answer;

        public LoopToolTestCase() {
            this.args = this.buildArgs();
            this.config = this.buildConfig();
            this.toolInfo = this.buildToolInfo();
            this.expectResult = this.buildExpectResult();
            this.answer = this.buildAnswer();
        }

        protected abstract Map<String, Object> buildArgs();

        protected abstract Config buildConfig();

        protected abstract ToolInfo buildToolInfo();

        protected abstract List<Object> buildExpectResult();

        protected abstract Answer buildAnswer();

        protected ToolInfo.ParamInfo buildParamInfo(String name) {
            ToolInfo.ParamInfo paramInfo = new ToolInfo.ParamInfo();
            paramInfo.setName(name);
            return paramInfo;
        }
    }
}