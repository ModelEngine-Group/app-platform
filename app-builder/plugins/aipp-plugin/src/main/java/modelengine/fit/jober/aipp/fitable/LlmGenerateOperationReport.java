/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * 生成经营报告
 *
 * @author 李鑫
 * @since 2024/3/20
 */
@Component
public class LlmGenerateOperationReport implements FlowableService {
    private static final Logger log = Logger.get(LlmGenerateOperationReport.class);

    public LlmGenerateOperationReport() {
    }

    /**
     * 根据聊天记录生成经营报告的实现
     *
     * @param flowData 流程执行上下文数据，包含聊天记录的数据
     * @return 流程执行上下文数据，包含生成的经营报告
     */
    @Fitable("modelengine.fit.jober.aipp.fitable.LLMGenerateOperationReport")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("LLMGenerateOperationReport businessData {}", businessData);
        businessData.put(AippConst.INST_OPERATION_REPORT_KEY, "");
        return flowData;
    }
}
