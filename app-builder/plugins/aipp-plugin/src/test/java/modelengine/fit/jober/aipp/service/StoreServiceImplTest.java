/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static modelengine.fit.jober.aipp.init.serialization.AippComponentInitiator.COMPONENT_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.PluginToolDto;
import modelengine.fit.jober.aipp.dto.StoreNodeInfoDto;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.service.impl.StoreServiceImpl;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.carver.ListResult;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.EcoTaskService;
import modelengine.jade.store.service.HuggingFaceModelService;
import modelengine.jade.store.service.PluginToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 市场相关接口测试。
 *
 * @author 鲁为
 * @since 2024-08-02
 */
@DisplayName("测试 StoreServiceImpl")
public class StoreServiceImplTest {
    private static final String RESOURCE_PATH = "component";
    private static final String BASIC_NODE_ZH_PATH = "/basic_node_zh.json";
    private static final String BASIC_NODE_EN_PATH = "/basic_node_en.json";
    private static final String EVALUATION_NODE_ZH_PATH = "/evaluation_node_zh.json";
    private static final String EVALUATION_NODE_EN_PATH = "/evaluation_node_en.json";
    private static final Map<String, String> TAGS = MapBuilder.<String, String>get()
            .put("CODENODESTATE", "codeNodeState")
            .put("QUERYOPTIMIZATIONNODESTATE", "queryOptimizationNodeState")
            .build();

    private PluginToolService pluginToolService;
    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() throws IOException {
        pluginToolService = mock(PluginToolService.class);
        EcoTaskService ecoTaskService = mock(EcoTaskService.class);
        HuggingFaceModelService huggingFaceModelService = mock(HuggingFaceModelService.class);
        AppBuilderAppMapper appBuilderAppMapper = mock(AppBuilderAppMapper.class);
        this.storeService = new StoreServiceImpl(pluginToolService,
                ecoTaskService,
                huggingFaceModelService,
                appBuilderAppMapper,
                TAGS);
        ClassLoader classLoader = StoreServiceImplTest.class.getClassLoader();
        COMPONENT_DATA.put(AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY,
                IoUtils.content(classLoader, RESOURCE_PATH + BASIC_NODE_ZH_PATH));
        COMPONENT_DATA.put(AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY,
                IoUtils.content(classLoader, RESOURCE_PATH + BASIC_NODE_EN_PATH));
        COMPONENT_DATA.put(AippConst.EVALUATION_NODE_COMPONENT_DATA_ZH_KEY,
                IoUtils.content(classLoader, RESOURCE_PATH + EVALUATION_NODE_ZH_PATH));
        COMPONENT_DATA.put(AippConst.EVALUATION_NODE_COMPONENT_DATA_EN_KEY,
                IoUtils.content(classLoader, RESOURCE_PATH + EVALUATION_NODE_EN_PATH));
    }

    @Test
    @DisplayName("当根据参数查询插件工具时，返回正确结果。")
    void shouldReturnCorrectToolWhenSearchPluginTools() {
        when(this.pluginToolService.getPluginTools(any(PluginToolQuery.class))).thenReturn(this.buildToolData());
        PluginToolDto res = this.storeService.getPlugins(new PluginToolQuery(), new OperationContext());
        assertThat(res.getPluginToolData().get(0).getUniqueName().equals("testUniqueName"));
    }

    private ListResult<PluginToolData> buildToolData() {
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setUniqueName("testUniqueName");
        return ListResult.create(Collections.singletonList(pluginToolData), 1);
    }

    @Test
    @DisplayName("当根据参数查询列表时，返回正确结果。")
    void shouldReturnCorrectListWhenSearchBasicList() {
        PluginToolData pluginToolData1 = new PluginToolData();
        pluginToolData1.setTags(new HashSet<>(Collections.singletonList("CODENODESTATE")));
        pluginToolData1.setUniqueName("testUniqueName1");
        PluginToolData pluginToolData2 = new PluginToolData();
        pluginToolData2.setTags(new HashSet<>(Collections.singletonList("QUERYOPTIMIZATIONNODESTATE")));
        pluginToolData2.setUniqueName("testUniqueName2");
        List<PluginToolData> list = new ArrayList<>();
        list.add(pluginToolData1);
        list.add(pluginToolData2);
        when(this.pluginToolService.getPluginTools(any(PluginToolQuery.class))).thenReturn(ListResult.create(list, 2));
        List<StoreNodeInfoDto> res = this.storeService.getNode("basic");
        assertThat(res.get(0).getName().equals("普通检索"));
    }

    @Test
    @DisplayName("当根据参数查询列表时，返回正确结果。")
    void shouldReturnCorrectListWhenSearchEvaluationList() {
        when(this.pluginToolService.getPluginTools(any(PluginToolQuery.class))).thenReturn(ListResult.empty());
        List<StoreNodeInfoDto> res = this.storeService.getNode("evaluation");
        assertThat(res.get(0).getName().equals("评估算法"));
    }
}