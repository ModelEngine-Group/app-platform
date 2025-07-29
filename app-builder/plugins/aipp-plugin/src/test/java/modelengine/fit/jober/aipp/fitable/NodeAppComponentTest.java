/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.fit.jober.common.FlowDataConstant;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link NodeAppComponent} 的测试
 *
 * @author songyongtan
 * @since 2024/12/12
 */
@ExtendWith(MockitoExtension.class)
class NodeAppComponentTest {
    private static final String INPUT_PARAMS = "inputParams";

    @Mock
    private AippRunTimeService aippRunTimeService;

    private NodeAppComponent nodeAppComponent;

    @BeforeEach
    void setUp() {
        this.nodeAppComponent = new NodeAppComponent(this.aippRunTimeService);
    }

    @Test
    @Disabled
    void shouldCallCreateAippInstanceWhenHandleTaskGivenValidContext() {
        String instanceId = "parentInstanceId1";
        Map<String, Object> businessData = buildAppBasicBusinessData(instanceId);
        Map<String, Object> inputParams = MapBuilder.<String, Object>get().put("arg1", "value1").build();
        businessData.put(INPUT_PARAMS, inputParams);
        String flowDataId = "flowDataId1";
        String aippId = "aippId1";
        String version = "1.0.0";
        businessData.put(AippConst.AIPP_ID, aippId);
        businessData.put(AippConst.ATTR_VERSION_KEY, version);
        String expectParentFitableId = "modelengine.fit.jober.aipp.fitable.AsyncAppNodeListener";
        Map<String, Object> expectInputParams = MapBuilder.<String, Object>get()
                .put(AippConst.PARENT_INSTANCE_ID, instanceId)
                .put(AippConst.PARENT_CALLBACK_ID, expectParentFitableId)
                .put(AippConst.PARENT_EXCEPTION_FITABLE_ID, expectParentFitableId)
                .put(AippConst.PARENT_FLOW_DATA_ID, flowDataId)
                .build();
        expectInputParams.putAll(inputParams);
        Map<String, Object> expectInitContext = MapBuilder.<String, Object>get()
                .put(AippConst.BS_INIT_CONTEXT_KEY, expectInputParams)
                .build();
        Map<String, Object> contextData = MapBuilder.<String, Object>get()
                .put(FlowDataConstant.FLOW_DATA_ID, flowDataId)
                .build();
        Map<String, Object> context = MapBuilder.<String, Object>get()
                .put(AippConst.BS_DATA_KEY, businessData)
                .put(AippConst.CONTEXT_DATA_KEY, contextData)
                .build();

        Mockito.when(this.aippRunTimeService.createAippInstance(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                Mockito.eq(null))).thenReturn("instanceId1");

        this.nodeAppComponent.handleTask(Collections.singletonList(context));

        Mockito.verify(this.aippRunTimeService, Mockito.times(1))
                .createAippInstance(Mockito.eq(aippId), Mockito.eq(version), Mockito.eq(expectInitContext),
                        Mockito.any());
    }

    private static Map<String, Object> buildAppBasicBusinessData(String instanceId) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put(AippConst.BS_AIPP_INST_ID_KEY, instanceId);
        return businessData;
    }
}