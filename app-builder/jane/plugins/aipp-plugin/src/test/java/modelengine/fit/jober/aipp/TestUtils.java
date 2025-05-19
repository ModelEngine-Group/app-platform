/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.common.RangedResultSet;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 测试工具类.
 */
public class TestUtils {
    /**
     * 测试用的对话实例id
     */
    public static final String DUMMY_FLOW_INSTANCE_ID = "testInstanceId";

    /**
     * 测试用的子对话实例id
     */
    public static final String DUMMY_CHILD_INSTANCE_ID = "testChildInstanceId";

    private static final String DUMMY_FLOW_CONFIG_ID = "testFlowConfigId";
    private static final String DUMMY_FLOW_DEF_ID = "testFlowDefId";
    private static final String DUMMY_FLOW_CONFIG_VERSION = "1.0.0";
    private static final List<String> TEST_TRACE_IDS = Collections.singletonList("testTraceId");
    private static final String APP_ID = "appId1";

    /**
     * 构建流程数据.
     *
     * @param businessData 业务数据.
     * @param dummyPrompt 提示词.
     * @return 流程数据.
     */
    public static List<Map<String, Object>> buildFlowDataWithExtraConfig(Map<String, Object> businessData,
            String dummyPrompt) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put(AippConst.BS_DATA_KEY, businessData);

        Map<String, Object> extraJober = Collections.singletonMap(AippConst.BS_MODEL_PROMPT_KEY, dummyPrompt);
        flowData.put(AippConst.CONTEXT_DATA_KEY, new HashMap<String, Object>() {{
            put(AippConst.BS_EXTRA_CONFIG_KEY, extraJober);
            put(AippConst.INST_FLOW_TRACE_IDS, TEST_TRACE_IDS);
            put("contextId", "testContextId");
        }});
        return Collections.singletonList(flowData);
    }

    /**
     * 构造任务对象.
     *
     * @return {@link AppTask} 对象.
     */
    public static AppTask buildTask() {
        return AppTask.asEntity()
                .setName("testName")
                .setAppSuiteId("testId")
                .setCreator("testUser")
                .setCreationTime(LocalDateTime.now())
                .setLastModificationTime(LocalDateTime.now())
                .setFlowConfigId(DUMMY_FLOW_CONFIG_ID)
                .setAttributeVersion(DUMMY_FLOW_CONFIG_VERSION)
                .setStatus(AippMetaStatusEnum.INACTIVE.getCode())
                .setFlowDefinitionId(DUMMY_FLOW_DEF_ID)
                .setAppId(APP_ID)
                .build();
    }

    /**
     * mock 元数据.
     *
     * @param metaExpected 元数据.
     * @param metaServiceMock 元数据服务mock对象.
     */
    public static void mockMetaListReturnSingleItem5(Meta metaExpected, MetaService metaServiceMock) {
        Mockito.doReturn(RangedResultSet.create(Collections.singletonList(metaExpected), 0L, 1, 1L))
                .when(metaServiceMock)
                .list(any(), eq(true), eq(0L), eq(1), any());
    }

    /**
     * mock恢复流程.
     *
     * @param flowInstanceServiceMock 流程实例服务mock.
     * @return {@link CountDownLatch} 闭锁.
     */
    public static CountDownLatch mockResumeFlow(FlowInstanceService flowInstanceServiceMock) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(flowInstanceServiceMock).resumeAsyncJob(any(), any(), any());
        return countDownLatch;
    }

    /**
     * mock元数据.
     *
     * @param metaExpected 元数据对象.
     * @param metaServiceMock 元数据服务mock.
     */
    public static void mockMetaListReturnSingleItem6(Meta metaExpected, MetaService metaServiceMock) {
        Mockito.doReturn(RangedResultSet.create(Collections.singletonList(metaExpected), 0L, 1, 1L))
                .when(metaServiceMock).list(any(), eq(true), eq(0L), eq(1), any());
    }

    /**
     * mock异步任务失败.
     *
     * @param flowInstanceServiceMock 流程实例服务mock.
     * @return {@link CountDownLatch} 闭锁.
     */
    public static CountDownLatch mockFailAsyncJob(FlowInstanceService flowInstanceServiceMock) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(flowInstanceServiceMock).failAsyncJob(any(), any(), any());
        return countDownLatch;
    }
}
