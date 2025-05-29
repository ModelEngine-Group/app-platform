/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowNodeFormInfo;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.AippNodeForms;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * flowInfo工具类.
 *
 * @author 张越
 * @since 2025/02/08
 */
public class FlowInfoUtil {
    /**
     * 构建 {@link AippNodeForms} 列表.
     *
     * @param flowInfo 流程信息.
     * @param formProperties 表单属性列表.
     * @return {@link AippNodeForms} 列表.
     */
    public static List<AippNodeForms> buildAippNodeForms(FlowInfo flowInfo,
            List<AppBuilderFormProperty> formProperties) {
        if (flowInfo.getFlowNodes() == null) {
            return Collections.emptyList();
        }
        return flowInfo.getFlowNodes().stream().filter(item -> item.getFlowNodeForm() != null).map(item -> {
            FlowNodeFormInfo form = item.getFlowNodeForm();
            List<FormMetaQueryParameter> parameter = Collections.singletonList(
                    new FormMetaQueryParameter(form.getFormId(), form.getVersion()));
            return AippNodeForms.builder()
                    .type(item.getType())
                    .metaInfo(FormUtils.buildFormMetaInfos(parameter, formProperties))
                    .build();
        }).collect(Collectors.toList());
    }
}
