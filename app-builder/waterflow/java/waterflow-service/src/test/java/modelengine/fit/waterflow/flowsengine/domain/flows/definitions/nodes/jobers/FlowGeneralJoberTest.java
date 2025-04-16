/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.exception.FitException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * FlowGeneralJober对应测试类
 *
 * @author 杨祥宇
 * @since 2024/10/15
 */
class FlowGeneralJoberTest {
    private BrokerClient brokerClient;

    private FlowGeneralJober target;

    @BeforeEach
    void before() {
        brokerClient = mock(BrokerClient.class);

        target = new FlowGeneralJober();
        target.setBrokerClient(brokerClient);
        target.setFitables(new HashSet<>(Arrays.asList("fitableId")));
        target.setProperties(new HashMap<>());
        target.setFitablesConfig(new HashMap<>());
    }

    @Test
    @DisplayName("执行GeneralJober调用并抛异常")
    void testGeneralJoberFailed() {
        when(brokerClient.getRouter(FlowableService.class, "b735c87f5e7e408d852d8440d0b2ecdf"))
                .thenThrow(new FitException("exception"));
        FlowData flowData = FlowData.builder().businessData(new HashMap<>()).contextData(new HashMap<>()).build();
        flowData.getBusinessData().put("name", "hello");

        WaterflowException exception = assertThrows(WaterflowException.class, () -> {
            target.executeJober(Collections.singletonList(flowData));
        });

        Assertions.assertEquals(ErrorCodes.FLOW_GENERAL_JOBER_INVOKE_ERROR.getErrorCode(), exception.getCode());
    }
}