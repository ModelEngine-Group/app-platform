/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import modelengine.fitframework.broker.CommunicationType;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.DefaultFilter;
import modelengine.fitframework.exception.FitException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * store工具调用的测试
 *
 * @author 宋永坦
 * @since 2024/5/09
 */
class FlowStoreJoberTest {
    public static final String TOOL_ID = "toolId";

    private BrokerClient brokerClient;

    private Router router;

    private Invoker invoker;

    private FlowStoreJober target;

    private FlowDataConverter converter;

    @BeforeEach
    void before() {
        brokerClient = mock(BrokerClient.class);
        router = mock(Router.class);
        invoker = mock(Invoker.class);
        converter = mock(FlowDataConverter.class);

        target = new FlowStoreJober();
        target.setBrokerClient(brokerClient);
        target.setServiceMeta(
                FlowStoreJober.ServiceMeta.builder().uniqueName(TOOL_ID).params(Arrays.asList("name")).build());
        target.setConverter(converter);
        target.setProperties(new HashMap<>());
    }

    @Test
    @DisplayName("执行storeJober调用并输出结果的成功场景")
    void shouldReturnResultWhenExecuteGivenBrokerClientInvokeSuccess() {
        when(brokerClient.getRouter(FlowStoreJober.TOOL_EXECUTE_GENERICABLE_ID)).thenReturn(router);
        when(invoker.communicationType(CommunicationType.ASYNC)).thenReturn(invoker);
        when(router.route(any(DefaultFilter.class))).thenReturn(invoker);
        String expectArgs = "{\"name\":\"hello\"}";
        String expectToolResult = "tool result";
        when(invoker.invoke(any())).thenReturn("\"" + expectToolResult + "\"");
        when(converter.convertOutput(any())).thenAnswer(invocation -> {
            Map<String, Object> map = new HashMap<>();
            map.put("toolResult", invocation.getArgument(0));
            return map;
        });

        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.getBusinessData().put("name", "hello");
        List<FlowData> result = target.executeJober(Collections.singletonList(flowData));

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
        verify(invoker).invoke(arg1.capture(), arg2.capture());
        Assertions.assertEquals(TOOL_ID, arg1.getValue());
        Assertions.assertEquals(expectArgs, arg2.getValue());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectToolResult, result.get(0).getBusinessData().get("toolResult"));
    }

    @Test
    @DisplayName("执行storeJober调用并抛异常")
    void testInvokeStoreJoberFailed() {
        when(brokerClient.getRouter(FlowStoreJober.TOOL_EXECUTE_GENERICABLE_ID))
                .thenThrow(new FitException("exception"));
        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.getBusinessData().put("name", "hello");

        WaterflowException exception = assertThrows(WaterflowException.class, () -> {
            target.executeJober(Collections.singletonList(flowData));
        });

        Assertions.assertEquals(ErrorCodes.FLOW_STORE_JOBER_INVOKE_ERROR.getErrorCode(), exception.getCode());
    }
}