/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AippFlowComponentDto;
import modelengine.fit.jober.aipp.dto.AippFormComponentDto;
import modelengine.fit.jober.aipp.init.serialization.AippComponentInitiator;
import modelengine.fitframework.annotation.Component;

/**
 * 组件查询接口
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/aipp-component", group = "aipp组件管理接口")
public class AippComponentController {
    /**
     * 查询表单组件
     *
     * @param tenantId tenantId
     * @return Rsp<AippFlowComponentDto>
     */
    @GetMapping(value = "/form", description = "获取表单的组件列表")
    public Rsp<AippFormComponentDto> queryFormComponent(@PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(AippComponentInitiator.getLocaleObject(AippConst.FORM_COMPONENT_DATA_EN_KEY,
                AippConst.FORM_COMPONENT_DATA_ZH_KEY, AippFormComponentDto.class));
    }

    /**
     * 查询流程组件
     *
     * @param tenantId tenantId
     * @return Rsp<AippFlowComponentDto>
     */
    @GetMapping(value = "/flow", description = "获取flow的组件列表")
    public Rsp<AippFlowComponentDto> queryFlowComponent(@PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(AippComponentInitiator.getLocaleObject(AippConst.FLOW_COMPONENT_DATA_EN_KEY,
                AippConst.FLOW_COMPONENT_DATA_ZH_KEY, AippFlowComponentDto.class));
    }
}
