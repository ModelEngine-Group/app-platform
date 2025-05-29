/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import modelengine.fit.jober.aipp.common.ResourceLoader;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AippFormComponentDto;
import modelengine.fit.jober.aipp.init.serialization.AippComponentInitiator;
import modelengine.fitframework.plugin.Plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * 表示 {@link AippComponentInitiator} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-20
 */
@DisplayName("测试 AippComponentInitiatorTest")
public class AippComponentInitiatorTest {
    private static final String RESOURCE_PATH = "component";
    private static final String FLOW_ZH_PATH = "/flow_zh.json";
    private static final String FLOW_EN_PATH = "/flow_en.json";
    private static final String FORM_ZH_PATH = "/form_zh.json";
    private static final String FORM_EN_PATH = "/form_en.json";
    private static final String BASIC_NODE_ZH_PATH = "/basic_node_zh.json";
    private static final String BASIC_NODE_EN_PATH = "/basic_node_en.json";
    private static final String JSON_STRING = "{\"groups\": [], \"items\": []}";

    private AippComponentInitiator aippComponentInitiator;
    private Plugin plugin;
    private MockedStatic<ResourceLoader> resourceLoaderMockedStatic;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.plugin = mock(Plugin.class);
        this.aippComponentInitiator = new AippComponentInitiator(this.plugin);
        this.resourceLoaderMockedStatic = mockStatic(ResourceLoader.class);
        this.resourceLoaderMockedStatic.when(
                () -> ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_ZH_PATH)).thenReturn(JSON_STRING);
        this.resourceLoaderMockedStatic.when(
                () -> ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_ZH_PATH)).thenReturn(JSON_STRING);
        this.resourceLoaderMockedStatic.when(
                        () -> ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASIC_NODE_ZH_PATH))
                .thenReturn(JSON_STRING);
        this.resourceLoaderMockedStatic.when(
                () -> ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FLOW_EN_PATH)).thenReturn(JSON_STRING);
        this.resourceLoaderMockedStatic.when(
                () -> ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + FORM_EN_PATH)).thenReturn(JSON_STRING);
        this.resourceLoaderMockedStatic.when(
                        () -> ResourceLoader.loadFileData(this.plugin, RESOURCE_PATH + BASIC_NODE_EN_PATH))
                .thenReturn(JSON_STRING);
        Field field = AippComponentInitiator.class.getDeclaredField("COMPONENT_DATA");
        field.setAccessible(true);
        Map<String, Object> mockedComponentData = (Map<String, Object>) field.get(this.aippComponentInitiator);
        mockedComponentData.put(AippConst.FORM_COMPONENT_DATA_ZH_KEY, JSON_STRING);
        mockedComponentData.put(AippConst.FLOW_COMPONENT_DATA_ZH_KEY, JSON_STRING);
    }

    @AfterEach
    void tearDown() {
        this.resourceLoaderMockedStatic.close();
    }

    @Test
    @DisplayName("初始化数据时，返回成功")
    void shouldSuccessWhenLoadComponentData()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method = AippComponentInitiator.class.getDeclaredMethod("loadComponentData");
        method.setAccessible(true);
        method.invoke(this.aippComponentInitiator);
        Field field = AippComponentInitiator.class.getDeclaredField("COMPONENT_DATA");
        Map<String, Object> mockedComponentData = (Map<String, Object>) field.get(this.aippComponentInitiator);
        assertThat(mockedComponentData.get(AippConst.FLOW_COMPONENT_DATA_ZH_KEY)).isEqualTo(JSON_STRING);
        assertThat(mockedComponentData.get(AippConst.FORM_COMPONENT_DATA_ZH_KEY)).isEqualTo(JSON_STRING);
        assertThat(mockedComponentData.get(AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY)).isEqualTo(JSON_STRING);
        assertThat(mockedComponentData.get(AippConst.FLOW_COMPONENT_DATA_EN_KEY)).isEqualTo(JSON_STRING);
        assertThat(mockedComponentData.get(AippConst.FORM_COMPONENT_DATA_EN_KEY)).isEqualTo(JSON_STRING);
        assertThat(mockedComponentData.get(AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY)).isEqualTo(JSON_STRING);
    }

    @Test
    @DisplayName("获取国际化消息对象时，返回成功")
    void shouldSuccessWhenGetLocaleObject() {
        AippFormComponentDto dto = AippComponentInitiator.getLocaleObject(AippConst.FLOW_COMPONENT_DATA_EN_KEY,
                AippConst.FORM_COMPONENT_DATA_ZH_KEY, AippFormComponentDto.class);
        assertThat(dto.getGroups()).isEqualTo(new ArrayList<>());
        assertThat(dto.getItems()).isEqualTo(new ArrayList<>());
    }
}
