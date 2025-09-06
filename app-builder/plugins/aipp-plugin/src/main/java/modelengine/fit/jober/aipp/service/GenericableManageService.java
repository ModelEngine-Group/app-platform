/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.FitableInfoDto;

import java.util.List;
import java.util.Map;

/**
 * genericable相关服务
 *
 * @author 孙怡菲
 * @since 2024-04-24
 */
public interface GenericableManageService {
    /**
     * 根据genericableId获取对应的genericable列表
     *
     * @param genericableId genericableId
     * @param pageNum 分页页码
     * @param pageSize 分页大小
     * @return 对应的genericable列表
     */
    List<FitableInfoDto> getFitablesByGenerableId(String genericableId, int pageNum, int pageSize);

    /**
     * 执行灵感大全服务
     *
     * @param fitableId 服务id
     * @param appId 应用id
     * @param appType 应用类型
     * @param operationContext 操作上下文
     * @return 执行结果
     */
    List<Map<String, Object>> executeInspirationFitable(String fitableId,
            String appId, String appType, OperationContext operationContext);
}
