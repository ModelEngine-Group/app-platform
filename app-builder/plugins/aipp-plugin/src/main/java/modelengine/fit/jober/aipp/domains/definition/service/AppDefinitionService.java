/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.definition.service;

import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;

/**
 * 定义服务接口.
 *
 * @author 张越
 * @since 2025-02-08
 */
public interface AppDefinitionService {
    /**
     * 获取相同的定义.
     *
     * @param aippDto 参数.
     * @return {@link FlowDefinitionResult} 对象.
     */
    FlowDefinitionResult getSameFlowDefinition(AippDto aippDto);
}
