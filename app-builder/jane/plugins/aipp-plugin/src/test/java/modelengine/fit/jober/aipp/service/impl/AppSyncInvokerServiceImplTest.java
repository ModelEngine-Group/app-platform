/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AppCoordinate;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.fit.jober.common.FlowDataConstant;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AppSyncInvokerServiceImpl} 的测试。
 *
 * @author 宋永坦
 * @since 2025-07-25
 */
@ExtendWith(MockitoExtension.class)
class AppSyncInvokerServiceImplTest {
    private final AppCoordinate appCoordinate = new AppCoordinate("tenantId1", "aippId1", "1.0.0");
    private final OperationContext operationContext = new OperationContext();

    @Mock
    private AippRunTimeService aippRunTimeService;
    private AppSyncInvokerServiceImpl appSyncInvokerService;

    @BeforeEach
    void setUp() {
        this.appSyncInvokerService = new AppSyncInvokerServiceImpl(this.aippRunTimeService);
    }

    @Test
    void shouldGetResultWhenInvokeGivenResponse() {
        String expectResult = "result";

        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, new HashMap<>()).build();
        Mockito.when(this.aippRunTimeService.createAippInstance(this.appCoordinate.getAippId(),
                this.appCoordinate.getVersion(),
                initContext,
                this.operationContext)).thenAnswer(invocation -> {
            Map<String, Object> inputInitContext = invocation.getArgument(2);
            String endNode = "endNode";
            Map<String, Object> flowData = buildFlowData(inputInitContext, endNode, expectResult);
            this.appSyncInvokerService.callback(Collections.singletonList(flowData));
            return "subInstanceId";
        });

        Object result = this.appSyncInvokerService.invoke(this.appCoordinate, initContext, 1, this.operationContext);

        Assertions.assertEquals(expectResult, result);
    }

    @Test
    void shouldThrowExceptionWhenInvokeGivenExceptionResponse() {
        String expectResult = "result";
        String errorMessage = "test error";
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, new HashMap<>()).build();
        Mockito.when(this.aippRunTimeService.createAippInstance(this.appCoordinate.getAippId(),
                this.appCoordinate.getVersion(),
                initContext,
                this.operationContext)).thenAnswer(invocation -> {
            Map<String, Object> inputInitContext = invocation.getArgument(2);
            String nodeId = "nodeId";
            Map<String, Object> flowData = buildFlowData(inputInitContext, nodeId, expectResult);
            FlowErrorInfo errorInfo = new FlowErrorInfo();
            errorInfo.setErrorCode(1111);
            errorInfo.setErrorMessage(errorMessage);
            this.appSyncInvokerService.handleException(nodeId, Collections.singletonList(flowData), errorInfo);
            return "subInstanceId";
        });

        assertThatThrownBy(() -> this.appSyncInvokerService.invoke(this.appCoordinate,
                initContext,
                1,
                this.operationContext)).isInstanceOf(FitException.class).hasMessage(errorMessage);
    }

    @Test
    void shouldThrowExceptionWhenInvokeGivenNoResponse() {
        Map<String, Object> initContext =
                MapBuilder.<String, Object>get().put(AippConst.BS_INIT_CONTEXT_KEY, new HashMap<>()).build();
        Mockito.when(this.aippRunTimeService.createAippInstance(appCoordinate.getAippId(),
                appCoordinate.getVersion(),
                initContext,
                operationContext)).thenAnswer(invocation -> {
            Map<String, Object> inputInitContext = invocation.getArgument(2);
            String endNode = "endNode";
            Map<String, Object> flowData = buildFlowData(inputInitContext, endNode, null);
            this.appSyncInvokerService.callback(Collections.singletonList(flowData));
            return "subInstanceId";
        });

        assertThatThrownBy(() -> this.appSyncInvokerService.invoke(this.appCoordinate,
                initContext,
                1,
                this.operationContext)).isInstanceOf(FitException.class).hasMessage("No response");
    }

    private static Map<String, Object> buildFlowData(Map<String, Object> initContext, String endNode,
            String expectResult) {
        Map<String, Object> flowData = new HashMap<>();
        List<Map<Object, Object>> input = null;
        if (expectResult != null) {
            input = Collections.singletonList(MapBuilder.get().put("input", expectResult).build());
        }
        HashMap<String, Object> bussinessData =
                new HashMap<>(ObjectUtils.<Map<String, Object>>cast(initContext.get(AippConst.BS_INIT_CONTEXT_KEY)));
        bussinessData.put(FlowDataConstant.BUSINESS_DATA_INTERNAL_KEY,
                MapBuilder.get()
                        .put(FlowDataConstant.INTERNAL_EXECUTE_INFO_KEY, MapBuilder.get().put(endNode, input).build())
                        .build());
        flowData.put(AippConst.BS_DATA_KEY, bussinessData);
        flowData.put(AippConst.CONTEXT_DATA_KEY, MapBuilder.get().put(FlowDataConstant.FLOW_NODE_ID, endNode).build());
        return flowData;
    }
}