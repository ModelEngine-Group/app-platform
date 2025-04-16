/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static modelengine.fit.waterflow.spi.FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * FlowNode对应测试类
 *
 * @author 杨祥宇
 * @since 2024/9/21
 */
class FlowNodeTest {
    @Test
    @DisplayName("测试节点异常回调成功")
    void testNotifyExceptionSuccess() {
        FlowNode node = getNode();

        node.notifyException(new WaterflowException(ErrorCodes.INPUT_PARAM_IS_INVALID), new ArrayList<>());

        verify(node.getBrokerClient(), times(2))
                .getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE);
    }

    @Test
    @DisplayName("测试节点异步调用异常回调成功")
    void testNotifyAsyncJoberExSuccess() {
        FlowNode node = getNode();

        node.notifyException(new WaterflowException(ErrorCodes.FLOW_EXECUTE_ASYNC_JOBER_FAILED), new ArrayList<>());

        verify(node.brokerClient, times(2)).getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE);
    }

    @Test
    @DisplayName("测试节点store jober调用异常回调成功")
    void testNotifyStoreJoberExSuccess() {
        FlowNode node = getNode();

        node.notifyException(new WaterflowException(new FitException("error"), ErrorCodes.FLOW_STORE_JOBER_INVOKE_ERROR,
                "toolId"), new ArrayList<>());

        verify(node.brokerClient, times(2)).getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE);
    }

    private FlowNode getNode() {
        FlowNode node = new FlowStateNode();
        node.setMetaId("node");
        Set<String> fitables = new HashSet<>();
        fitables.add("123");
        fitables.add("456");
        node.setExceptionFitables(fitables);
        BrokerClient brokerClient = Mockito.mock(BrokerClient.class);
        node.setBrokerClient(brokerClient);

        Router router = Mockito.mock(Router.class);
        Invoker invoker = Mockito.mock(Invoker.class);
        when(brokerClient.getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE))
                .thenReturn(router);
        when(router.route(any(FitableIdFilter.class))).thenReturn(invoker);
        when(invoker.invoke(anyString(), anyList(), any(FlowErrorInfo.class))).thenThrow(new FitException("exception"));
        return node;
    }
}