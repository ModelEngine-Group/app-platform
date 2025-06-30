/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 任意genericable调用的测试
 *
 * @author 宋永坦
 * @since 2024/4/23
 */
class FlowGenericableJoberTest {
    private BrokerClient brokerClient;

    private Router router;

    private Invoker invoker;

    private FlowGenericableJober target;

    private FlowDataConverter converter;

    @BeforeEach
    void before() {
        brokerClient = mock(BrokerClient.class);
        invoker = mock(Invoker.class);
        router = mock(Router.class);
        converter = mock(FlowDataConverter.class);

        when(invoker.communicationType(any())).thenReturn(invoker);
        when(router.route(any())).thenReturn(invoker);
        when(brokerClient.getRouter((String) any())).thenReturn(router);

        target = new FlowGenericableJober();
        target.setBrokerClient(brokerClient);
        target.setFitables(new HashSet<>(Arrays.asList("fitableId")));
        target.loadFitableId();
        target.setGenericableConfig(
                FlowGenericableJober.GenericableConfig.builder().id("xxx").params(Arrays.asList("name")).build());
        target.setConverter(converter);
        target.setProperties(new HashMap<>());
    }

    @Test
    @DisplayName("执行genericableJober调用并输出结果的成功场景")
    void shouldReturnResultWhenExecuteGivenBrokerClientInvokeSuccess() throws Throwable {
        when(router.route(argThat(filter -> filter instanceof FitableIdFilter))).thenReturn(invoker);
        String expectGenericableResult = "helloWorld";
        when(invoker.invoke(any())).thenReturn(expectGenericableResult);
        when(converter.convertOutput(any())).thenAnswer(invocation -> {
            Map<String, Object> map = new HashMap<>();
            map.put("_result", invocation.getArgument(0));
            return map;
        });

        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        List<FlowData> result = target.executeJober(Collections.singletonList(flowData));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectGenericableResult, result.get(0).getBusinessData().get("_result"));
    }

    @Test
    @DisplayName("执行genericableJober调用并抛异常")
    void testGenericableJoberJoberFailed() {
        when(brokerClient.getRouter(anyString())).thenThrow(new FitException("exception"));
        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.getBusinessData().put("name", "hello");

        WaterflowException exception = assertThrows(WaterflowException.class, () -> {
            target.executeJober(Collections.singletonList(flowData));
        });

        Assertions.assertEquals(ErrorCodes.FLOW_GENERICALBE_JOBER_INVOKE_ERROR.getErrorCode(), exception.getCode());
    }
}