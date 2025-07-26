/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.dependencies.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jade.aipp.tool.loop.dependencies.ToolCallService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;

import java.util.Map;

/**
 * 循环节点插件的工具调用服务的实现。
 *
 * @author 夏斐
 * @since 2025-03-11
 */
@Component
public class ToolCallServiceImpl implements ToolCallService {
    private static final Logger log = Logger.get(ToolCallServiceImpl.class);

    private final ToolExecuteService toolExecuteService;

    /**
     * 循环节点插件的工具调用服务的构造方法。
     *
     * @param toolExecuteService 表示工具执行服务的 {@link ToolExecuteService}。
     */
    public ToolCallServiceImpl(@Fit ToolExecuteService toolExecuteService) {
        this.toolExecuteService = toolExecuteService;
    }

    @Override
    public Object call(String uniqueName, Map<?, ?> toolArgs) {
        String argStr = JSONObject.toJSONString(toolArgs);
        log.debug("ToolCallService. [uniqueName={}, toolArgs={}]", uniqueName, argStr);
        return JSONArray.parse(this.toolExecuteService.execute(uniqueName, argStr));
    }
}
