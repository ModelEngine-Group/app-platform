/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.dependencies.impls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.jade.aipp.tool.loop.dependencies.ToolCallService;
import modelengine.fit.jade.tool.SyncToolCall;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具调用实现
 *
 * @author 夏斐
 * @since 2025/3/11
 */
@Component
public class ToolCallServiceImpl implements ToolCallService {
    private static final Logger log = Logger.get(ToolCallServiceImpl.class);

    private final SyncToolCall syncToolCall;

    public ToolCallServiceImpl(@Fit SyncToolCall syncToolCall) {
        this.syncToolCall = syncToolCall;
    }

    @Override
    public Object call(String uniqueName, Map<?, ?> toolArgs) {
        String argStr = JSONObject.toJSONString(toolArgs);
        log.warn("ToolCallService. uniqueName:{}, toolArgs:{}", uniqueName, argStr);
        return JSONArray.parse(syncToolCall.call(uniqueName, argStr, new HashMap<>()));
    }
}
