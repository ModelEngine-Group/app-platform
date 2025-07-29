/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.runtime.entity.RuntimeData;

import java.util.Optional;

/**
 * 流程运行时服务接口.
 *
 * @author 张越
 * @since 2024-05-25
 */
public interface AippFlowRuntimeInfoService {
    /**
     * 获取流程运行时数据.
     *
     * @param aippId 实例id.
     * @param version 版本号.
     * @param instanceId 实例id.
     * @param context 用户上下文.
     * @return {@link RuntimeData} 运行时数据.
     */
    Optional<RuntimeData> getRuntimeData(String aippId, String version, String instanceId, OperationContext context);
}
