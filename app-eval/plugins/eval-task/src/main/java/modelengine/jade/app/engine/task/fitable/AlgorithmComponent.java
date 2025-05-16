/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_DATA_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.CONTEXT_DATA_KEY;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_ALGORITHM_TOOL_ERROR;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_CONTEXT;

import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fel.tool.service.ToolExecuteService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.app.engine.task.entity.EvalAlgorithmEntity;
import modelengine.jade.app.engine.task.entity.EvalAlgorithmInputEntity;
import modelengine.jade.app.engine.task.entity.EvalNodeEntity;
import modelengine.jade.app.engine.task.entity.EvalOutputEntity;
import modelengine.jade.app.engine.task.exception.EvalTaskException;
import modelengine.jade.app.engine.task.service.EvalAlgorithmService;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.store.service.ToolService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示评估算法节点的 fitable 实现。
 *
 * @author 兰宇晨
 * @since 2024-8-20
 */
@Component
public class AlgorithmComponent implements FlowableService {
    private final ObjectSerializer serializer;
    private final ObjectMapper mapper;
    private final ToolExecuteService toolExecuteService;
    private final ToolService toolService;
    private final EvalAlgorithmService evalAlgorithmService;
    private final EvalInstanceService evalInstanceService;

    public AlgorithmComponent(@Fit(alias = "json") ObjectSerializer serializer, ToolExecuteService toolExecuteService,
            ToolService toolService, EvalAlgorithmService evalAlgorithmService,
            EvalInstanceService evalInstanceService) {
        this.serializer = serializer;
        this.mapper = new ObjectMapper();
        this.toolExecuteService = toolExecuteService;
        this.toolService = toolService;
        this.evalAlgorithmService = evalAlgorithmService;
        this.evalInstanceService = evalInstanceService;
    }

    /**
     * 评估算法节点构造器。
     *
     * @param flowData 流程执行上下文数据
     * @return 流程执行上下文数据，包含模型执行结果
     */
    @Fitable("modelengine.jade.app.engine.task.AlgorithmComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(BS_DATA_KEY)) {
            throw new EvalTaskException(EVAL_TASK_CONTEXT);
        }
        Map<String, Object> businessData = ObjectUtils.cast(flowData.get(0).get(BS_DATA_KEY));
        Map<String, Object> contextData = ObjectUtils.cast(flowData.get(0).get(CONTEXT_DATA_KEY));

        EvalAlgorithmInputEntity inputEntity = this.mapper.convertValue(businessData, EvalAlgorithmInputEntity.class);
        EvalNodeEntity nodeEntity = this.mapper.convertValue(contextData, EvalNodeEntity.class);

        double score = this.executeTool(inputEntity.getUniqueName(), inputEntity.getAlgorithmArgs());

        EvalOutputEntity evalOutputEntity = new EvalOutputEntity(inputEntity.getAlgorithmArgs(),
                nodeEntity.getNodeMetaId(),
                nodeEntity.getNodeName(),
                score,
                score >= inputEntity.getPassScore(),
                inputEntity.getPassScore());
        businessData.put("output",
                this.mapper.convertValue(evalOutputEntity, new TypeReference<Map<String, Object>>() {}));

        if (this.shouldInsertAlgorithm(nodeEntity.getNodeMetaId(), businessData)) {
            this.insertAlgorithm(nodeEntity.getNodeMetaId(),
                    nodeEntity.getNodeName(),
                    inputEntity.getUniqueName(),
                    inputEntity.getPassScore(),
                    contextData);
        }

        return flowData;
    }

    private boolean shouldInsertAlgorithm(String nodeId, Map<String, Object> businessData) {
        return (!this.evalAlgorithmService.exist(nodeId)) && Objects.equals(businessData.get("isDebug"), false);
    }

    private void insertAlgorithm(String nodeId, String nodeName, String uniqueName, double passScore,
            Map<String, Object> contextData) {
        String evalAlgorithmSchema = serializer.serialize(this.toolService.getTool(uniqueName).getSchema());
        EvalAlgorithmEntity entity = new EvalAlgorithmEntity();
        entity.setNodeName(nodeName);
        entity.setNodeId(nodeId);
        entity.setAlgorithmSchema(evalAlgorithmSchema);
        entity.setPassScore(passScore);
        entity.setTaskId(getTaskId(contextData));
        this.evalAlgorithmService.insert(Collections.singletonList(entity));
    }

    private double executeTool(String uniqueName, Map<String, Object> args) {
        String invokeResult = this.toolExecuteService.execute(uniqueName, this.serializer.serialize(args));
        try {
            return Double.parseDouble(invokeResult);
        } catch (NumberFormatException e) {
            throw new EvalTaskException(EVAL_ALGORITHM_TOOL_ERROR, e.getMessage());
        }
    }

    private Long getTaskId(Map<String, Object> contextData) {
        List<String> traceIds = ObjectUtils.cast(contextData.get("flowTraceIds"));
        return this.evalInstanceService.getTaskIdByTraceId(traceIds.get(0));
    }
}