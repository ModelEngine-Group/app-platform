/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.PluginToolService;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ToolExceptionHandle测试用例.
 *
 * @author 李智超
 * @since 2024-10-08
 */
public class ToolExceptionHandleTest {
    @Mock
    private LocaleService localeService;

    @Mock
    private PluginToolService pluginToolService;

    private ToolExceptionHandle toolExceptionHandle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        toolExceptionHandle = new ToolExceptionHandle(localeService, pluginToolService);
    }

    @Test
    void testGetFixErrorMsg_withDialogue_andToolId() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(90002957);
        errorMessage.setNodeName("Node1");
        String[] args = {};
        errorMessage.setArgs(args);
        errorMessage.setErrorMessage("工具调用异常，请检查工具后重试。");
        Map<String, String> properties = new HashMap<>();
        properties.put("toolId", "tool123");
        errorMessage.setProperties(properties);
        PluginToolData pluginToolDataMock = mock(PluginToolData.class);
        doReturn(pluginToolDataMock).when(pluginToolService).getPluginTool("tool123");
        doReturn("Tool Name").when(pluginToolDataMock).getName();
        doReturn("工具调用异常，请检查工具后重试。").doReturn("{0}节点执行出错，出错原因：{1}工具执行出错，{2}")
                .when(localeService)
                .localize(any(Locale.class), anyString(), any());
        String result = toolExceptionHandle.getFixErrorMsg(errorMessage, Locale.CHINESE, true);
        assertEquals("Node1节点执行出错，出错原因：Tool Name工具执行出错，工具调用异常，请检查工具后重试。", result);
    }

    @Test
    void testGetFixErrorMsg_withoutDialogue_andToolId() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(90002957);
        errorMessage.setNodeName("Node1");
        String[] args = {};
        errorMessage.setArgs(args);
        errorMessage.setErrorMessage("工具调用异常，请检查工具后重试。");
        Map<String, String> properties = new HashMap<>();
        properties.put("toolId", "tool123");
        errorMessage.setProperties(properties);
        PluginToolData pluginToolDataMock = mock(PluginToolData.class);
        doReturn(pluginToolDataMock).when(pluginToolService).getPluginTool("tool123");
        doReturn("").when(pluginToolDataMock).getName();
        doReturn("工具调用异常，请检查工具后重试。").doReturn("执行出错，出错原因：{0}")
                .when(localeService)
                .localize(any(Locale.class), anyString(), any());
        String result = toolExceptionHandle.getFixErrorMsg(errorMessage, Locale.CHINESE, false);
        assertEquals("执行出错，出错原因：工具调用异常，请检查工具后重试。", result);
    }

    @Test
    void testHandleFitException_withSpecificErrorCode() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(0x7F010001);

        ToolExceptionHandle.handleFitException(errorMessage);

        assertEquals(AippErrCode.FIT_TOOL_LOOKUP_ERROR.getErrorCode(), errorMessage.getErrorCode());
    }

    @Test
    void testHandleFitException_withUnrecognizedErrorCode() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(0x12345678);

        ToolExceptionHandle.handleFitException(errorMessage);

        // Error code should remain unchanged as it is unrecognized.
        assertEquals(0x12345678, errorMessage.getErrorCode());
    }

    @Test
    void testGetFixErrorMsg_withNullErrorMessage() {
        String result = toolExceptionHandle.getFixErrorMsg(null, Locale.CHINESE, true);
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    void testGetFixErrorMsg_withNullErrorCode() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(null);
        String result = toolExceptionHandle.getFixErrorMsg(errorMessage, Locale.CHINESE, true);
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    void testGetFixErrorMsg_withEmptyToolId() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(90002957);
        errorMessage.setNodeName("Node1");
        String[] args = {};
        errorMessage.setArgs(args);
        errorMessage.setErrorMessage("工具调用异常，请检查工具后重试。");
        Map<String, String> properties = new HashMap<>();
        properties.put("toolId", "");
        errorMessage.setProperties(properties);

        doReturn("工具调用异常，请检查工具后重试。").doReturn("{0}节点执行出错，原因：{1}")
                .when(localeService)
                .localize(any(Locale.class), anyString(), any());

        String result = toolExceptionHandle.getFixErrorMsg(errorMessage, Locale.CHINESE, true);
        assertEquals("Node1节点执行出错，原因：工具调用异常，请检查工具后重试。", result);
    }

    @Test
    void testHandleFitException_withNullErrorMessage() {
        ToolExceptionHandle.handleFitException(null);
        // No assertion needed, just ensure no exception is thrown
    }

    @Test
    void testHandleFitException_withNullErrorCode() {
        FlowErrorInfo errorMessage = new FlowErrorInfo();
        errorMessage.setErrorCode(null);

        ToolExceptionHandle.handleFitException(errorMessage);

        // No change expected as errorCode is null
        assertEquals(null, errorMessage.getErrorCode());
    }
}