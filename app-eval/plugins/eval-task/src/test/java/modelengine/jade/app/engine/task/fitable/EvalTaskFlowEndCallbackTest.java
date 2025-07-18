/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_CONTEXT;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_INPUT_PARAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.exception.EvalTaskException;
import modelengine.jade.app.engine.task.mapper.EvalCaseMapper;
import modelengine.jade.app.engine.task.mapper.EvalRecordMapper;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.service.EvalInstanceService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link EvalTaskFlowEndCallback} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-21
 */
@IntegrationTest(scanPackages = {"modelengine.jade.app.engine.task", "modelengine.fitframework.serialization"})
@Sql(before = "sql/test_create_table.sql")
public class EvalTaskFlowEndCallbackTest {
    private static final List<Map<String, String>> INPUTS = Arrays.asList(MapBuilder.<String, String>get()
                    .put("input", "1+1")
                    .put("output", "2")
                    .put("expected", "2")
                    .build(),
            MapBuilder.<String, String>get().put("input", "3+1").put("output", "3").put("expected", "4").build());

    @Fit
    private EvalTaskFlowEndCallback evalTaskFlowEndCallback;

    @Spy
    private EvalCaseService evalCaseService;

    @Fit
    private EvalInstanceService evalInstanceService;

    @Fit
    private EvalCaseMapper evalCaseMapper;

    @Fit
    private EvalRecordMapper evalRecordMapper;

    @Fit
    private ObjectSerializer serializer;

    private static List<Map<String, Object>> buildTestData() {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> algorithmOutput = new HashMap<>();

        Map<String, Object> nodeOutput1 = new HashMap<>();
        nodeOutput1.put("isPass", "true");
        nodeOutput1.put("input", INPUTS.get(0));
        nodeOutput1.put("score", "100.0");
        nodeOutput1.put("nodeId", "nodeId1");
        nodeOutput1.put("nodeName", "nodeName1");

        Map<String, Object> nodeOutput2 = new HashMap<>();
        nodeOutput2.put("isPass", "false");
        nodeOutput2.put("input", INPUTS.get(1));
        nodeOutput2.put("score", "0.0");
        nodeOutput2.put("nodeId", "nodeId2");
        nodeOutput2.put("nodeName", "nodeName2");

        algorithmOutput.put("nodeOutput1", nodeOutput1);
        algorithmOutput.put("nodeOutput2", nodeOutput2);
        businessData.put("evalOutput", algorithmOutput);
        businessData.put("isDebug", "false");
        context.put("businessData", businessData);
        context.put("traceId", Collections.singleton("trace2"));
        return Collections.singletonList(context);
    }

    private static List<Map<String, Object>> buildDebugData() {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> algorithmOutput = new HashMap<>();

        Map<String, Object> nodeOutput1 = new HashMap<>();
        Map<String, Object> nodeOutput2 = new HashMap<>();

        algorithmOutput.put("nodeOutput1", nodeOutput1);
        algorithmOutput.put("nodeOutput2", nodeOutput2);
        businessData.put("evalOutput", algorithmOutput);
        businessData.put("isDebug", "true");
        context.put("businessData", businessData);
        context.put("traceId", Collections.singleton("trace2"));
        return Collections.singletonList(context);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("流水线数据回调节点成功")
    void shouldOkWhenCallback() throws EvalTaskException {
        this.evalTaskFlowEndCallback.callback(buildTestData());

        List<EvalCaseEntity> caseEntities = this.evalCaseMapper.getCaseByInstanceId(4L);
        List<EvalRecordEntity> recordEntities = this.evalRecordMapper.getEntityByCaseIds(Collections.singletonList(2L));
        assertThat(caseEntities.size()).isEqualTo(1);
        assertThat(recordEntities.size()).isEqualTo(2);

        EvalCaseEntity caseEntity = caseEntities.get(0);
        assertThat(caseEntity).extracting(EvalCaseEntity::getId, EvalCaseEntity::getPass).containsExactly(2L, false);
        for (int i = 0; i < recordEntities.size(); i++) {
            EvalRecordEntity recordEntity = recordEntities.get(i);
            assertThat(recordEntity).extracting(EvalRecordEntity::getId,
                            EvalRecordEntity::getInput,
                            EvalRecordEntity::getNodeId,
                            EvalRecordEntity::getNodeName,
                            EvalRecordEntity::getScore)
                    .containsExactly(Long.valueOf(i + 3),
                            serializer.serialize(INPUTS.get(1 - i)),
                            StringUtils.format("nodeId{0}", (1 - i) + 1),
                            StringUtils.format("nodeName{0}", (1 - i) + 1),
                            (double) 100 * i);
        }
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("流水线数据回调节点失败")
    void shouldOkWhenCallbackWithDebugData() throws EvalTaskException {
        this.evalTaskFlowEndCallback.callback(buildDebugData());

        List<EvalCaseEntity> caseEntities = this.evalCaseMapper.getCaseByInstanceId(2L);
        List<EvalRecordEntity> recordEntities = this.evalRecordMapper.getEntityByCaseIds(Collections.singletonList(2L));
        assertThat(caseEntities.size()).isEqualTo(0);
        assertThat(recordEntities.size()).isEqualTo(0);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql"})
    @DisplayName("流水线数据回调节点失败")
    void shouldFailWhenCallbackWithEmptyContext() throws EvalTaskException {
        EvalTaskException ex = assertThrows(EvalTaskException.class,
                () -> this.evalTaskFlowEndCallback.callback(Collections.emptyList()));
        assertThat(ex.getCode()).isEqualTo(EVAL_TASK_CONTEXT.getCode());
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql"})
    @DisplayName("流水线数据不包含上下文信息时，回调节点失败")
    void shouldFailWhenCallbackWithoutBusinessData() throws EvalTaskException {
        EvalTaskException ex = assertThrows(EvalTaskException.class,
                () -> this.evalTaskFlowEndCallback.callback(Collections.singletonList(Collections.emptyMap())));
        assertThat(ex.getCode()).isEqualTo(EVAL_TASK_INPUT_PARAM.getCode());
        assertThat(ex.getMessage()).isEqualTo("Input param is empty, empty param is businessData.");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql"})
    @DisplayName("流水线数据不包含流水线 ID 时，回调节点失败")
    void shouldFailWhenCallbackWithoutTraceId() throws EvalTaskException {
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> businessData = new HashMap<>();
        context.put("businessData", businessData);

        EvalTaskException ex = assertThrows(EvalTaskException.class,
                () -> this.evalTaskFlowEndCallback.callback(Collections.singletonList(context)));
        assertThat(ex.getCode()).isEqualTo(EVAL_TASK_INPUT_PARAM.getCode());
        assertThat(ex.getMessage()).isEqualTo("Input param is empty, empty param is traceId.");
    }
}