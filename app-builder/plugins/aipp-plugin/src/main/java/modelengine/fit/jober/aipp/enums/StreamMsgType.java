/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流式响应消息。
 *
 * @author 刘信宏
 * @since 2024-12-06
 */
public enum StreamMsgType {
    /**
     * 表单消息。
     */
    FORM("FORM"),

    /**
     * 提示消息。
     */
    MSG("MSG"),

    /**
     * 带有元数据的结构化消息，如溯源场景的应用结束响应。
     */
    META_MSG("META_MSG"),

    /**
     * 溯源消息，表示应用响应的引用信息。
     */
    KNOWLEDGE("KNOWLEDGE"),

    /**
     * 错误提示消息。
     */
    ERROR("ERROR"),

    /**
     * 用户问题消息。
     */
    QUESTION("QUESTION"),

    /**
     * 文件消息。
     */
    FILE("FILE");

    private static final Map<String, StreamMsgType> ITEM_TYPE_MAP = Arrays.stream(StreamMsgType.values())
            .collect(Collectors.toMap(key -> StringUtils.toUpperCase(key.value()), Function.identity()));

    private final String value;

    StreamMsgType(String value) {
        this.value = value;
    }

    /**
     * 获取数据单元类型名称。
     *
     * @return 表示数据单元类型名称的 {@link String}。
     */
    public String value() {
        return this.value;
    }

    /**
     * 通过字符串构造 {@link StreamMsgType} 对象。
     *
     * @param value 表示流式响应消息的 {@link String}。
     * @return 表示流式响应消息的 {@link StreamMsgType}。
     */
    public static StreamMsgType from(String value) {
        String upperValue = StringUtils.toUpperCase(value);
        if (!ITEM_TYPE_MAP.containsKey(upperValue)) {
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, value);
        }
        return ITEM_TYPE_MAP.get(upperValue);
    }

    /**
     * 通过历史记录类型构造 {@link StreamMsgType} 对象。
     *
     * @param value 表示历史记录类型的 {@link AippInstLogType}。
     * @return 表示流式响应消息的 {@link StreamMsgType}。
     */
    public static StreamMsgType from(AippInstLogType value) {
        return from(value.name());
    }
}
