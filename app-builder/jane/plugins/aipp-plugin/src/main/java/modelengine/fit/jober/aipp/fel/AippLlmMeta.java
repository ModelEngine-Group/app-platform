/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fel;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.util.FlowDataUtils;

import lombok.Getter;
import lombok.Setter;
import modelengine.fel.core.chat.Prompt;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * AippLlmMeta
 *
 * @author 易文渊
 * @since 2024-04-24
 */
@Getter
public class AippLlmMeta {
    private List<Map<String, Object>> flowData;
    private Map<String, Object> businessData;
    private OperationContext context;
    private String versionId;
    private String instId;
    private String flowDataId;

    @Setter
    private Prompt trace;
    @Setter
    private Map<String, Object> promptMetadata;

    private AippLlmMeta() {}

    /**
     * 根据businessData解析大模型节点元数据。
     *
     * @param flowData 表示携带元数据的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code ,}
     *                               {@link Object}{@code >}{@code >}。
     * @return 返回表示大模型节点元数据的 {@link AippLlmMeta}。
     */
    public static AippLlmMeta parse(List<Map<String, Object>> flowData) {
        AippLlmMeta aippLlmMeta = new AippLlmMeta();
        Validation.notEmpty(flowData,
                () -> new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.FLOW_DATA));
        aippLlmMeta.flowData = flowData;
        aippLlmMeta.businessData = DataUtils.getBusiness(flowData);
        aippLlmMeta.versionId = ObjectUtils.cast(aippLlmMeta.businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        aippLlmMeta.instId = ObjectUtils.cast(aippLlmMeta.businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        aippLlmMeta.context = DataUtils.getOpContext(aippLlmMeta.businessData);
        aippLlmMeta.flowDataId = FlowDataUtils.getFlowDataId(flowData.get(0));
        return aippLlmMeta;
    }
}