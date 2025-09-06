/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.waterflow.entity.JoberErrorInfo;
import modelengine.fit.waterflow.spi.FlowCallbackService;

import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.common.FlowDataConstant;
import modelengine.fit.waterflow.entity.FlowErrorInfo;
import modelengine.fit.jober.util.FlowDataUtils;
import modelengine.fit.waterflow.spi.FlowExceptionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 应用或工具流作为异步job节点执行的结果处理（目前应用和工具流工作机制一致，复用同一套处理）
 *
 * @author songyongtan
 * @since 2024/12/9
 */
@Component
public class AsyncAppNodeListener implements FlowCallbackService, FlowExceptionService {
    private static final Logger LOG = Logger.get(AsyncAppNodeListener.class);

    private static final String OUTPUT_KEY = "output";

    private final FlowInstanceService flowInstanceService;

    /**
     * 构造函数
     *
     * @param flowInstanceService flowInstanceService实例
     */
    public AsyncAppNodeListener(FlowInstanceService flowInstanceService) {
        this.flowInstanceService = flowInstanceService;
    }

    @Fitable("modelengine.fit.jober.aipp.fitable.AsyncAppNodeListener")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        // 节点上的应用执行完成后恢复对应节点的继续执行
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        Map<String, Object> contextData = DataUtils.getContextData(contexts);
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String parentFlowDataId = ObjectUtils.cast(businessData.get(AippConst.PARENT_FLOW_DATA_ID));
        LOG.info("[AppNodeListener] handle callback. instanceId={}, parentInstanceId={}, parentFlowDataId={}",
                instanceId, parentInstanceId, parentFlowDataId);

        List<Map<String, Object>> executeInfo =
                FlowDataUtils.getExecuteInfo(businessData, FlowDataUtils.getNodeId(contextData));
        if (executeInfo.isEmpty()) {
            LOG.error("Can not find the node app output. parentInstanceId={}, parentFlowDataId={}, instanceId={}",
                    parentInstanceId, parentFlowDataId, instanceId);
            return;
        }
        // 结束节点上的入参等于应用的出参
        Map<String, Object> appOutput = MapBuilder.<String, Object>get()
                .put(OUTPUT_KEY, executeInfo.get(executeInfo.size() - 1).get(FlowDataConstant.EXECUTE_INPUT_KEY))
                .build();
        this.flowInstanceService.resumeAsyncJob(parentFlowDataId, appOutput, new OperationContext());
        LOG.info("[AppNodeListener] handle callback end. instanceId={}, parentInstanceId={}, parentFlowDataId={}",
                instanceId, parentInstanceId, parentFlowDataId);
    }

    @Fitable("modelengine.fit.jober.aipp.fitable.AsyncAppNodeListener")
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> contexts, FlowErrorInfo errorInfo) {
        // 应用执行失败时，将对应节点同步设置为失败
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String parentFlowDataId = ObjectUtils.cast(businessData.get(AippConst.PARENT_FLOW_DATA_ID));
        LOG.info("[AppNodeListener] handle exception start. instanceId={}, parentInstanceId={}, parentFlowDataId={}",
                instanceId, parentInstanceId, parentFlowDataId);
        this.flowInstanceService.failAsyncJob(parentFlowDataId,
                new JoberErrorInfo(errorInfo.getErrorMessage(), errorInfo.getErrorCode(), errorInfo.getArgs()),
                new OperationContext());
        LOG.info("[AppNodeListener] handle exception end. instanceId={}, parentInstanceId={}, parentFlowDataId={}",
                instanceId, parentInstanceId, parentFlowDataId);
    }
}
