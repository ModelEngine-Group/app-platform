/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.impls;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import modelengine.fit.jade.aipp.tool.loop.LoopToolService;
import modelengine.fit.jade.aipp.tool.loop.dependencies.ToolCallService;
import modelengine.fit.jade.aipp.tool.loop.entities.Config;
import modelengine.fit.jade.aipp.tool.loop.entities.ToolInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.annotation.Attribute;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 循环工具的实现
 *
 * @author 夏斐
 * @since 2025/3/10
 */
@Component
@Group(name = "LoopToolImpl")
public class LoopToolServiceImpl implements LoopToolService {
    private static final String DEFAULT_OPERATOR = "Jade";

    private static final OperationContext operationContext;

    static {
        operationContext = new OperationContext();
        operationContext.setOperator(DEFAULT_OPERATOR);
    }

    private final ToolCallService toolCallService;

    private final AippRunTimeService aippRunTimeService;

    private final Cache<String, Boolean> aippInstanceStatusCache;

    public LoopToolServiceImpl(ToolCallService toolCallService, AippRunTimeService aippRunTimeService,
            @Value("${loop-call.cache.duration}") Integer cacheDuration) {
        this.toolCallService = toolCallService;
        this.aippRunTimeService = aippRunTimeService;
        this.aippInstanceStatusCache = Caffeine.newBuilder()
                .expireAfterAccess(Validation.between(cacheDuration,
                        1,
                        300000,
                        "The cache duration must between 1 and 300000."), TimeUnit.MILLISECONDS)
                .maximumSize(1000)
                .build();
    }

    @Override
    @Fitable("default")
    @ToolMethod(name = "loopToolDefault", description = "用于循环执行工具", extensions = {
            @Attribute(key = "tags", value = "FIT")
    })
    @Property(description = "循环执行工具的结果")
    public List<Object> loopTool(Map<String, Object> loopArgs, Config config, ToolInfo toolInfo,
            Map<String, Object> context) {
        if (CollectionUtils.isEmpty(config.getLoopKeys())) {
            throw new IllegalArgumentException("no loop key!");
        }
        String loopKey = config.getLoopKeys().get(0);

        Object value = loopArgs;
        String lastKey = null;
        Map<String, Object> lastMap = loopArgs;
        for (String key : loopKey.split("\\.")) {
            if (!(value instanceof Map)) {
                throw new IllegalArgumentException("loop value wrong!");
            } else {
                lastKey = key;
                lastMap = ObjectUtils.cast(value);
                value = lastMap.get(key);
            }
        }
        Object loopData = value;
        if (!(loopData instanceof List<?>)) {
            throw new IllegalArgumentException("input value of [" + loopKey + "] is not an array!");
        }
        return this.loopCall(loopArgs, toolInfo, (List<?>) loopData, lastMap, lastKey, context);
    }

    private List<Object> loopCall(Map<String, Object> loopArgs, ToolInfo toolInfo, List<?> loopData,
            Map<String, Object> lastMap, String lastKey, Map<String, Object> context) {
        String aippInstanceId = ObjectUtils.cast(ObjectUtils.nullIf(context, new HashMap<>())
                .getOrDefault(AippConst.CONTEXT_INSTANCE_ID, StringUtils.EMPTY));
        List<Object> list = new ArrayList<>();
        for (Object loopDatum : loopData) {
            lastMap.put(lastKey, loopDatum);
            Map<String, Object> toolArgs = toolInfo.getParams()
                    .stream()
                    .collect(Collectors.toMap(ToolInfo.ParamInfo::getName, param -> loopArgs.get(param.getName())));
            // 循环展开的参数，通过序列化的方式复制，防止同进程调用场景下，直接返回时，多条数据的覆盖污染问题
            Map<String, Object> args = JSONObject.parseObject(JSONObject.toJSONString(toolArgs), toolArgs.getClass());
            Object apply = this.toolCallService.call(toolInfo.getUniqueName(), args);
            list.add(apply);
            if (StringUtils.isNotEmpty(aippInstanceId) && !this.isInstanceRunning(aippInstanceId)) {
                throw new IllegalStateException(StringUtils.format("Already terminated. [aippInstanceId={0}]", aippInstanceId));
            }
        }
        return list;
    }

    private Boolean isInstanceRunning(String aippInstanceId) {
        return aippInstanceStatusCache.get(aippInstanceId,
                __ -> this.aippRunTimeService.isInstanceRunning(aippInstanceId, operationContext));
    }
}