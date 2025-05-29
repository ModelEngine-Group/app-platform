/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fitframework.inspection.Validation.isInstanceOf;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * business & flow data 操作工具类
 *
 * @author 方誉州
 * @since 2024/6/14
 */
public class DataUtils {
    /**
     * prompt格式： $(key) 或 ${key}，嵌套场景只提取最里面的key; 待屏蔽$(}和${) 括号混用的场景
     */
    public static final Pattern PROMPT_PATTERN = Pattern.compile("\\$[({]([^(){}]+)[)}]");

    /**
     * prompt的正则pattern中表示key的group
     */
    private static final int KEY_GROUP = 1;
    private static final Logger log = Logger.get(DataUtils.class);

    /**
     * 返回business data中key对象的file_path字段值
     *
     * @param businessData business data
     * @param key 待查找file_path字段的对象的键值
     * @return 返回非空的file_path字段值
     */
    public static String getFilePath(Map<String, Object> businessData, String key) {
        // businessData中的Object是fastjson2的JSONObject导致本项目fastjson的JSONObject无法转换
        Map<String, Object> fileInfo = JsonUtils.parseObject(JsonUtils.toJsonString(businessData.get(key)));
        return notBlank(ObjectUtils.cast(fileInfo.get("file_path")), "filename cannot be blank");
    }

    /**
     * 根据business data和prompt模板填充prompt
     *
     * @param businessData business data
     * @param promptTemplate prompt模板
     * @return 返回填充后的prompt
     * @throws IllegalArgumentException 当prompt模板出现business data中不存在的key
     */
    public static String parsePrompt(Map<String, Object> businessData, String promptTemplate)
            throws IllegalArgumentException {
        if (StringUtils.isBlank(promptTemplate)) {
            return promptTemplate;
        }
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = PROMPT_PATTERN.matcher(promptTemplate);
        while (matcher.find()) {
            String key = matcher.group(KEY_GROUP);
            if (StringUtils.isBlank(key)) {
                log.warn("invalid key in prompt: {}", promptTemplate);
                continue;
            }
            // 兼容key前后带有空格场景
            String value = ObjectUtils.cast(businessData.get(key.trim()));
            Validation.notNull(value, key + " key not exist");
            // 追加替换后的匹配
            matcher.appendReplacement(stringBuffer, value);
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    /**
     * 获取flowData中的Aipp agent参数
     *
     * @param flowData 流程执行上下文数据
     * @return 返回Aipp agent参数
     */
    public static Map<String, Object> getAgentParams(List<Map<String, Object>> flowData) {
        return ObjectUtils.cast(getBusiness(flowData).get(AippConst.BS_AGENT_PARAM_KEY));
    }

    /**
     * 从contextData中获取agent id
     *
     * @param contextData context data
     * @return 返回
     */
    public static String getAgentId(Map<String, Object> contextData) {
        if (contextData.containsKey(AippConst.BS_EXTRA_CONFIG_KEY)) {
            Map<String, Object> cfgObj = ObjectUtils.cast(contextData.get(AippConst.BS_EXTRA_CONFIG_KEY));
            if (cfgObj.containsKey(AippConst.BS_AGENT_ID_KEY)) {
                return ObjectUtils.cast(cfgObj.get(AippConst.BS_AGENT_ID_KEY));
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 从流程执行上下文数据中获取第一个flowData
     *
     * @param flowDataList 流程执行上下文数据
     * @return flowData
     * @throws JobberException 当列表为空时
     */
    public static Map<String, Object> getFirstFlowData(List<Map<String, Object>> flowDataList) {
        if (flowDataList.isEmpty()) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.FLOW_DATA);
        }
        return ObjectUtils.cast(flowDataList.get(0));
    }

    /**
     * 获取 flowData中的business data
     *
     * @param flowData 流程执行上下文数据
     * @return 返回business data
     * @throws JobberException 当flowData为空或不存在business data时
     */
    public static Map<String, Object> getBusiness(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(AippConst.BS_DATA_KEY)) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.BS_DATA_KEY);
        }
        return ObjectUtils.cast(flowData.get(0).get(AippConst.BS_DATA_KEY));
    }

    /**
     * 获取 flowData中的context data
     *
     * @param flowData 流程执行上下文数据
     * @return 返回context data
     * @throws JobberException 当flowData为空或不存在context data时
     */
    public static Map<String, Object> getContextData(List<Map<String, Object>> flowData) {
        if (flowData.isEmpty() || !flowData.get(0).containsKey(AippConst.CONTEXT_DATA_KEY)) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, AippConst.CONTEXT_DATA_KEY);
        }
        return (Map<String, Object>) flowData.get(0).get(AippConst.CONTEXT_DATA_KEY);
    }

    /**
     * 从businessData中获取operation context
     *
     * @param businessData business data
     * @return operation context
     */
    public static OperationContext getOpContext(Map<String, Object> businessData) {
        Object contextObject = businessData.get(AippConst.BS_HTTP_CONTEXT_KEY);
        return JsonUtils.parseObject(contextObject instanceof String ? ObjectUtils.cast(contextObject) : "null",
                OperationContext.class);
    }

    /**
     * 从flowData中获取flow的trace id
     *
     * @param flowData 流程执行上下文数据
     * @return 返回flow的trace id
     * @throws JobberException 当flow的trace id为空时
     */
    public static String getFlowTraceId(List<Map<String, Object>> flowData) {
        List<String> traces = ObjectUtils.cast(getContextData(flowData).get(AippConst.INST_FLOW_TRACE_IDS));
        Validation.isFalse(traces.isEmpty(),
                () -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "Flow trace id can not be empty."));
        return traces.get(0);
    }

    /**
     * 当源map指定的name存在时，从该map添加指定的kv到目标map中
     *
     * @param from 源map
     * @param name 指定的name
     * @param to 目标map
     */
    public static void putFromMapIfPresent(Map<String, Object> from, String name, Map<String, Object> to) {
        if (from.containsKey(name)) {
            to.put(name, from.get(name));
        }
    }

    /**
     * 从 businessData 中获取 appId。
     *
     * @param businessData 表示业务数据的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}
     * @return 表示 appId 的 {@link String}
     */
    public static String getAppId(Map<String, Object> businessData) {
        Object appId = notNull(businessData.get(AippConst.ATTR_APP_ID_KEY), "App id cannot be null.");
        return isInstanceOf(appId,
                String.class,
                "App id cast error, it must be string. [type={0}]",
                appId.getClass().getName());
    }
}
