/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 工具流相关接口
 *
 * @author 李鑫
 * @since 2024/4/24
 */
public interface WaterFlowService {
    /**
     * 接口的genericableId
     */
    String GENERICABLE_WATER_FLOW_INVOKER = "07b51bd246594c159d403164369ce1db";

    /**
     * 调用工具流统一入口
     *
     * @param tenantId 租户id
     * @param aippId 应用id
     * @param version 应用版本
     * @param inputParams 启动参数列表
     * @return 工具流实例id
     */
    @Genericable(id = GENERICABLE_WATER_FLOW_INVOKER)
    String invoke(String tenantId, String aippId, String version, Map<String, Object> inputParams);
}
