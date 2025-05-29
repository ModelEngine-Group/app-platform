/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 灵感大全Genericable
 *
 * @author 孙怡菲
 * @since 2024-04-25
 */
public interface AppInspirationService {
    /**
     * 获取定制化日志
     *
     * @param params 参数
     * @param aippId 应用Id
     * @param appType 应用类型
     * @param context 操作上下文
     * @return 返回定制化日志列表
     * @author 孙怡菲
     * @since 2024-04-25
     */
    @Genericable(id = "d01041a73e00ac46bedde08d02c6818e")
    List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params,
            String aippId, String appType, OperationContext context);
}
