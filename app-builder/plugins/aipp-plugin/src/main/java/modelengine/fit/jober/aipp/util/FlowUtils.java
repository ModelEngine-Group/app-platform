/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.AppInputParam;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Flow 相关工具类
 *
 * @author 邬涨财
 * @since 2025-01-12
 */
public class FlowUtils {
    /**
     * 获取输入参数信息列表
     *
     * @param service 表示流程服务类
     * @param flowDefinitionId 表示流程定义id
     * @param context 表示操作上下文
     * @return 输入参数信息列表
     */
    public static List<AppInputParam> getAppInputParams(FlowsService service, String flowDefinitionId,
                                                        OperationContext context) {
        FlowInfo flowInfo = CacheUtils.getPublishedFlowWithCache(service, flowDefinitionId, context);
        return flowInfo.getInputParamsByName("input").stream().map(rawParam -> {
            AppInputParam param = new AppInputParam();
            param.setName(ObjectUtils.cast(rawParam.get("name")));
            param.setType(ObjectUtils.cast(rawParam.get("type")));
            param.setDisplayName(ObjectUtils.cast(rawParam.get("displayName")));
            param.setRequired(ObjectUtils.cast(rawParam.getOrDefault("isRequired", true)));
            param.setVisible(ObjectUtils.cast(rawParam.getOrDefault("isVisible", true)));
            return param;
        }).collect(Collectors.toList());
    }
}
