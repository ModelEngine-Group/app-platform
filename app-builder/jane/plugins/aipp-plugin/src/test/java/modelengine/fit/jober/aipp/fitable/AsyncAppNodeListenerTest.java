/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.TestUtils;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.FlowDataConstant;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AsyncAppNodeListener} 的测试
 *
 * @author songyongtan
 * @since 2024/12/10
 */
@ExtendWith(MockitoExtension.class)
public class AsyncAppNodeListenerTest {
    @Mock
    private FlowInstanceService flowInstanceService;

    private AsyncAppNodeListener asyncAppNodeListener;

    @BeforeEach
    void setUp() {
        this.asyncAppNodeListener = new AsyncAppNodeListener(this.flowInstanceService);
    }

    @Test
    void shouldCallResumeAsyncJobWhenCallbackGivenValidContext() {
        String flowDataId = "flowDataId1";
        Map<String, Object> businessData = buildAppBasicBusinessData(flowDataId);

        Map<String, Object> output = MapBuilder.<String, Object>get().put("finalOutput", "result").build();
        String endNodeId = "endNodeId";
        List<Map<String, Object>> endExecuteInfo = Collections.singletonList(
                MapBuilder.<String, Object>get().put(FlowDataConstant.EXECUTE_INPUT_KEY, output).build());
        businessData.put(FlowDataConstant.BUSINESS_DATA_INTERNAL_KEY, MapBuilder.<String, Object>get()
                .put(FlowDataConstant.INTERNAL_EXECUTE_INFO_KEY,
                        MapBuilder.<String, Object>get().put(endNodeId, endExecuteInfo).build())
                .build());

        Map<String, Object> contextData =
                MapBuilder.<String, Object>get().put(FlowDataConstant.FLOW_NODE_ID, endNodeId).build();

        Map<String, Object> context = MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY, businessData)
                .put(AippConst.CONTEXT_DATA_KEY, contextData)
                .build();

        Mockito.doNothing().when(this.flowInstanceService).resumeAsyncJob(Mockito.any(), Mockito.any(), Mockito.any());

        this.asyncAppNodeListener.callback(Collections.singletonList(context));

        Mockito.verify(this.flowInstanceService, Mockito.times(1))
                .resumeAsyncJob(ArgumentMatchers.eq(flowDataId),
                        Mockito.eq(MapBuilder.<String, Object>get().put("output", output).build()), Mockito.any());
    }

    @Test
    void shouldNotCallResumeAsyncJobWhenCallbackGivenEmptyExecuteInfo() {
        String flowDataId = "flowDataId1";
        Map<String, Object> businessData = buildAppBasicBusinessData(flowDataId);

        String endNodeId = "endNodeId";
        Map<String, Object> contextData =
                MapBuilder.<String, Object>get().put(FlowDataConstant.FLOW_NODE_ID, endNodeId).build();

        Map<String, Object> context = MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY, businessData)
                .put(AippConst.CONTEXT_DATA_KEY, contextData)
                .build();

        this.asyncAppNodeListener.callback(Collections.singletonList(context));

        Mockito.verify(this.flowInstanceService, Mockito.times(0))
                .resumeAsyncJob(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldCallFailAsyncJobWhenHandleExceptionGivenValidContext() {
        String flowDataId = "flowDataId1";
        Map<String, Object> businessData = buildAppBasicBusinessData(flowDataId);

        String exceptionNodeId = "endNodeId";
        Map<String, Object> contextData =
                MapBuilder.<String, Object>get().put(FlowDataConstant.FLOW_NODE_ID, exceptionNodeId).build();

        Map<String, Object> context = MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY, businessData)
                .put(AippConst.CONTEXT_DATA_KEY, contextData)
                .build();

        FlowErrorInfo errorInfo = new FlowErrorInfo();
        errorInfo.setErrorCode(1);
        errorInfo.setErrorMessage("error {}");
        errorInfo.setArgs(new String[] {"arg1"});

        Mockito.doNothing().when(this.flowInstanceService).failAsyncJob(Mockito.any(), Mockito.any(), Mockito.any());

        this.asyncAppNodeListener.handleException(exceptionNodeId, Collections.singletonList(context), errorInfo);

        Mockito.verify(this.flowInstanceService, Mockito.times(1))
                .failAsyncJob(ArgumentMatchers.eq(flowDataId), Mockito.argThat(
                        joberErrorInfo -> joberErrorInfo.getCode() == errorInfo.getErrorCode()
                                && joberErrorInfo.getMessage().equals(errorInfo.getErrorMessage())
                                && joberErrorInfo.getArgs() == errorInfo.getArgs()), Mockito.any());
    }

    private static Map<String, Object> buildAppBasicBusinessData(String flowDataId) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, TestUtils.DUMMY_FLOW_INSTANCE_ID);
        businessData.put(AippConst.PARENT_INSTANCE_ID, "parentInstanceId");
        businessData.put(AippConst.PARENT_FLOW_DATA_ID, flowDataId);
        return businessData;
    }
}