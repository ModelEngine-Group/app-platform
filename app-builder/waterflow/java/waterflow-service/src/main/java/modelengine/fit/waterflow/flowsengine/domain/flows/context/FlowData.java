/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.common.utils.ByteArraySerialiseUtilV1;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_SYSTEM_ERROR;

/**
 * 流程实例运行时承载的业务数据
 * 使用对象存储代替JSONObject，FIT不支持传JSONObject
 *
 * @author 高诗意
 * @since 2023/08/25
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowData {
    private static final Logger log = Logger.get(FlowData.class);

    /**
     * 流程启动人
     */
    private String operator;

    /**
     * 流程启动的时间
     */
    private LocalDateTime startTime;

    /**
     * 流程执行所需的业务参数
     */
    private Map<String, Object> businessData;

    /**
     * 流程执行时引擎内部数据
     */
    private Map<String, Object> contextData;

    /**
     * 流程执行时非落盘数据
     */
    private Map<String, Object> passData;

    /**
     * 流程执行时发生的异常错误信息
     */
    private String errorMessage;

    /**
     * 流程执行时发生的异常结构化信息
     */
    private ContextErrorInfo errorInfo;

    /**
     * 通过JSON字符串获取flowData对象
     * 从数据库获取数据对象
     *
     * @param jsonData flowData的json字符串
     * @return flowData对象
     */
    public static FlowData parseFromJson(String jsonData) {
        JSONObject flowData = JSONObject.parseObject(jsonData, JSONObject.class,
                ByteArraySerialiseUtilV1.getMapParserConfig());
        Map<String, Object> contextDataMap = Optional.ofNullable(flowData.getJSONObject("contextData"))
                .orElse(new JSONObject())
                .toJavaObject(new TypeReference<HashMap<String, Object>>() {});

        Map<String, Object> businessDataMap = Optional.ofNullable(flowData.getJSONObject("businessData"))
                .orElse(new JSONObject())
                .toJavaObject(new TypeReference<HashMap<String, Object>>() {});

        Map<String, Object> passDataMap = Optional.ofNullable(flowData.getJSONObject("passData"))
                .orElse(new JSONObject())
                .toJavaObject(new TypeReference<HashMap<String, Object>>() {});

        JSONObject errorInfoJson = Optional.ofNullable(flowData.getJSONObject("errorInfo")).orElse(new JSONObject());
        ContextErrorInfo contextErrorInfo = getContextErrorInfo(errorInfoJson);

        LocalDateTime currentTime = LocalDateTime.now();

        return FlowData.builder()
                .operator(flowData.getString(Constant.OPERATOR_KEY))
                .startTime(currentTime)
                .businessData(businessDataMap)
                .contextData(contextDataMap)
                .passData(passDataMap)
                .errorMessage(flowData.getString("errorMessage"))
                .errorInfo(contextErrorInfo)
                .build();
    }

    private static ContextErrorInfo getContextErrorInfo(JSONObject errorInfoJson) {
        Integer errorCode = null;
        try {
            errorCode = Optional.ofNullable(errorInfoJson.getString("errorCode"))
                    .map(Integer::parseInt)
                    .orElse(null);
        } catch (NumberFormatException ex) {
            // 兼容error code为message信息的版本
            if (Objects.equals(errorInfoJson.getString("errorCode"), FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR.getMessage())) {
                errorCode = FLOW_OHSCRIPT_INVOKE_FITABLE_ERROR.getErrorCode();
            } else {
                errorCode = FLOW_SYSTEM_ERROR.getErrorCode();
            }
        }
        return ContextErrorInfo.builder()
                .errorCode(errorCode)
                .errorMessage(errorInfoJson.getString("errorMessage"))
                .fitableId(errorInfoJson.getString("fitableId"))
                .nodeName(errorInfoJson.getString("nodeName"))
                .build();
    }

    /**
     * 从Map中解析
     *
     * @param data flowData的Map对象
     * @return flowData对象
     */
    public static FlowData parseFrom(Map<String, Object> data) {
        Map<String, Object> errorInfoMap = ObjectUtils.cast(
                Optional.ofNullable(data.get("errorInfo")).orElse(new HashMap<String, Object>()));
        ContextErrorInfo contextErrorInfo = ContextErrorInfo.builder()
                .errorCode(ObjectUtils.cast(errorInfoMap.get("errorCode")))
                .errorMessage(ObjectUtils.cast(errorInfoMap.get("errorMessage")))
                .fitableId(ObjectUtils.cast(errorInfoMap.get("fitableId")))
                .nodeName(ObjectUtils.cast(errorInfoMap.get("nodeName")))
                .build();

        Map<String, Object> contextDataMap = ObjectUtils.cast(
                Optional.ofNullable(data.get("contextData")).orElse(new HashMap<String, Object>()));
        Map<String, Object> businessDataMap = ObjectUtils.cast(
                Optional.ofNullable(data.get("businessData")).orElse(new HashMap<String, Object>()));
        Map<String, Object> passDataMap = ObjectUtils.cast(
                Optional.ofNullable(data.get("passData")).orElse(new HashMap<String, Object>()));
        LocalDateTime currentTime = LocalDateTime.now();
        return FlowData.builder()
                .operator(ObjectUtils.cast(data.get(Constant.OPERATOR_KEY)))
                .startTime(currentTime)
                .businessData(businessDataMap)
                .contextData(contextDataMap)
                .passData(passDataMap)
                .errorMessage(ObjectUtils.cast(data.get("errorMessage")))
                .errorInfo(contextErrorInfo)
                .build();
    }

    /**
     * 从FlowData中拷贝上下文关键信息
     *
     * @param data flowData对象
     * @return flowData对象
     */
    public static FlowData copyContextData(FlowData data) {
        return FlowData.builder()
                .operator(ObjectUtils.cast(data.operator))
                .startTime(data.startTime)
                .businessData(data.businessData)
                .contextData(new HashMap<>(data.contextData))
                .errorMessage(data.errorMessage)
                .errorInfo(data.errorInfo)
                .build();
    }

    /**
     * 获取flowData的json字符串
     * 保存到数据库时使用
     *
     * @return json字符串
     */
    public String translateToJson() {
        FlowData data = FlowData.builder()
                .businessData(this.businessData)
                .contextData(this.contextData)
                .passData(null)
                .errorMessage(this.errorMessage)
                .operator(this.operator)
                .startTime(this.startTime)
                .errorInfo(this.errorInfo)
                .build();
        return JSONObject.toJSONString(data, ByteArraySerialiseUtilV1.getSerializeConfig(),
                SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 获取flowData中流程启动的应用
     *
     * @return 流程启动的应用
     */
    public String getApplication() {
        return ObjectUtils.cast(businessData.getOrDefault("application", ""));
    }

    /**
     * 获取flowData对应的数据contextId
     *
     * @return 数据contextId
     */
    public String getContextId() {
        return ObjectUtils.cast(contextData.getOrDefault(Constant.CONTEXT_ID, ""));
    }
}
