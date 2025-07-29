/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;

/**
 * 百度千帆 接口返回值结构。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
public class QianfanResponse<T> {
    private T data;
    private String code;
    private String message;

    /**
     * 表示百度千帆知识库请求结构体。
     * @param context 表示http响应数据内容的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param type 表示响应数据期望解析类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @return 表示百度千帆知识库请求的 {@link QianfanResponse}。
     * @param <T> 表示泛型的 {@code <}{@link T}{@code >}。
     */
    public static <T> QianfanResponse<T> from(Map<String, Object> context, Class<T> type) {
        QianfanResponse<T> qianfanResponse = new QianfanResponse<>();
        ObjectMapper objectMapper = new ObjectMapper();
        qianfanResponse.data = objectMapper.convertValue(context, type);
        qianfanResponse.code = ObjectUtils.cast(context.get("code"));
        qianfanResponse.message = ObjectUtils.cast(context.get("message"));
        return qianfanResponse;
    }
}
