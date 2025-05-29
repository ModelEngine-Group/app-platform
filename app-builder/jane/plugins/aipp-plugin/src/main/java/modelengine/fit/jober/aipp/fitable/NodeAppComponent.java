/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.jober.util.FlowDataUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 应用作为节点的组件处理, 该实现为异步job的方式
 *
 * @author songyongtan
 * @since 2024/12/12
 */
@Component
public class NodeAppComponent implements FlowableService {
    private static final Logger LOG = Logger.get(NodeAppComponent.class);

    private static final String APP_NODE_LISTENER_FITABLE_ID =
            "modelengine.fit.jober.aipp.fitable.AsyncAppNodeListener";

    private static final String APP_INPUT_PARAMS = "inputParams";

    private final AippRunTimeService aippRunTimeService;

    /**
     * 构造函数
     *
     * @param aippRunTimeService 启动应用的服务对象
     */
    public NodeAppComponent(AippRunTimeService aippRunTimeService) {
        this.aippRunTimeService = aippRunTimeService;
    }

    /**
     * 节点执行应用的实现
     * 该组件的入参格式如下：
     * {
     *   "aippId": "",
     *   "version": "",
     *   "inputParams": {
     *     "key1": "value1"
     *   }
     * }
     *
     * @param flowDataList 流程执行上下文数据
     * @return flowDataList 异步job情况下无实际意义
     */
    @Fitable("modelengine.fit.jober.aipp.fitable.NodeAppComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowDataList) {
        AppStartParam appStartParam = getAppStartParam(DataUtils.getFirstFlowData(flowDataList));

        LOG.info("[NodeAppComponent] start app start, parentInstanceId={}, parentFlowDataId={}, app={}:{}.",
                appStartParam.getParentInstanceId(), appStartParam.getParentFlowDataId(), appStartParam.getAippId(),
                appStartParam.getVersion());

        Map<String, Object> appInitContext = MapBuilder.<String, Object>get()
                .put(AippConst.BS_INIT_CONTEXT_KEY, appStartParam.getInputParams())
                .build();
        String instanceId = this.aippRunTimeService.createAippInstance(appStartParam.getAippId(),
                appStartParam.getVersion(), appInitContext, appStartParam.getOperationContext());

        LOG.info("[NodeAppComponent] start app end, parentInstanceId={}, parentFlowDataId={}, instanceId={}.",
                appStartParam.getParentInstanceId(), appStartParam.getParentFlowDataId(), instanceId);

        return flowDataList;
    }

    private static AppStartParam getAppStartParam(Map<String, Object> flowData) {
        Map<String, Object> businessData = FlowDataUtils.getBusinessData(flowData);
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentFlowDataId = FlowDataUtils.getFlowDataId(flowData);
        String aippId = Validation.notBlank(ObjectUtils.cast(businessData.get(AippConst.AIPP_ID)),
                () -> new JobberParamException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.AIPP_ID));
        String version = Validation.notBlank(ObjectUtils.cast(businessData.get(AippConst.ATTR_VERSION_KEY)),
                () -> new JobberParamException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.ATTR_VERSION_KEY));
        Map<String, Object> inputParams = Validation.notNull(ObjectUtils.cast(businessData.get(APP_INPUT_PARAMS)),
                () -> new JobberParamException(ErrorCodes.INPUT_PARAM_IS_EMPTY, APP_INPUT_PARAMS));
        inputParams.put(AippConst.PARENT_INSTANCE_ID, parentInstanceId);
        inputParams.put(AippConst.PARENT_CALLBACK_ID, APP_NODE_LISTENER_FITABLE_ID);
        inputParams.put(AippConst.PARENT_EXCEPTION_FITABLE_ID, APP_NODE_LISTENER_FITABLE_ID);
        inputParams.put(AippConst.PARENT_FLOW_DATA_ID, parentFlowDataId);
        DataUtils.putFromMapIfPresent(businessData, AippConst.CONTEXT_USER_ID, inputParams);
        DataUtils.putFromMapIfPresent(businessData, AippConst.BS_HTTP_CONTEXT_KEY, inputParams);
        DataUtils.putFromMapIfPresent(businessData, AippConst.BS_AIPP_MEMORIES_KEY, inputParams);

        return new AppStartParam(aippId, version, parentInstanceId, parentFlowDataId, inputParams,
                DataUtils.getOpContext(businessData));
    }

    /**
     * 应用启动相关参数
     *
     * @author songyongtan
     * @since 2024/12/12
     */
    @Getter
    @AllArgsConstructor
    private static class AppStartParam {
        private String aippId;

        private String version;

        private String parentInstanceId;

        private String parentFlowDataId;

        private Map<String, Object> inputParams;

        private OperationContext operationContext;
    }
}
