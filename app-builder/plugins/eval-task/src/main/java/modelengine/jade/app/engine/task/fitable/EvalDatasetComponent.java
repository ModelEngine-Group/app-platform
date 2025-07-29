/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_DATA_KEY;
import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_CONTEXT;

import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.service.EvalListDataService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.app.engine.task.exception.EvalTaskException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示评估数据集节点的 Fitable 实现。
 *
 * @author 兰宇晨
 * @since 2024-8-20
 */
@Component
public class EvalDatasetComponent implements FlowableService {
    private final ObjectSerializer serializer;

    private final EvalListDataService evalListDataService;

    public EvalDatasetComponent(@Fit(alias = "json") ObjectSerializer serializer,
            EvalListDataService evalListDataService) {
        this.serializer = serializer;
        this.evalListDataService = evalListDataService;
    }

    /**
     * 数据集节点构造器。
     *
     * @param flowData 流程执行上下文数据
     * @return 流程执行上下文数据，包含模型执行结果
     */
    @Fitable("modelengine.jade.app.engine.task.EvalDatasetComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(BS_DATA_KEY)) {
            throw new EvalTaskException(EVAL_TASK_CONTEXT);
        }

        List<Map<String, Object>> returnFlowData = new ArrayList<>();
        Map<String, Object> flowDataCopy = new HashMap<>(flowData.get(0));

        Map<String, Object> businessData = ObjectUtils.cast(flowData.get(0).get(BS_DATA_KEY));
        Map<String, Object> evalDataInfo = ObjectUtils.cast(businessData.get("testSet"));
        Long id = Long.valueOf(ObjectUtils.cast(evalDataInfo.get("id")));
        Long version = Long.valueOf(ObjectUtils.cast(evalDataInfo.get("version")));
        int quantity = ObjectUtils.cast(businessData.getOrDefault("evalDatasetQuantity", 1));
        EvalDataQueryParam param = new EvalDataQueryParam();
        param.setDatasetId(id);
        param.setVersion(version);
        param.setPageIndex(1);
        param.setPageSize(quantity);

        List<EvalDataEntity> res = this.evalListDataService.listEvalData(param).getItems();

        for (EvalDataEntity entity : res) {
            Map<String, Object> singleFlowData = new HashMap<>(flowDataCopy);
            Map<String, Object> businessDataCopy =
                    serializer.deserialize(serializer.serialize(businessData), Map.class);
            businessDataCopy.put("output", serializer.deserialize(entity.getContent(), Object.class));
            singleFlowData.put(BS_DATA_KEY, businessDataCopy);
            returnFlowData.add(singleFlowData);
        }
        return returnFlowData;
    }
}