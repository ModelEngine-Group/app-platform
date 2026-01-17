/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;

/**
 * DataMate 接口返回值结构。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
public class DataMateResponse<T> {
    private T data;
    private String code;
    private String message;

    /**
     * 表示 DataMate 知识库请求结构体。
     *
     * @param context 表示 http 响应数据内容的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param type 表示响应数据期望解析类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @return 表示 DataMate 知识库请求的 {@link DataMateResponse}。
     * @param <T> 表示泛型的 {@code <}{@link T}{@code >}。
     */
    public static <T> DataMateResponse<T> from(Map<String, Object> context, Class<T> type) {
        DataMateResponse<T> response = new DataMateResponse<>();
        ObjectMapper objectMapper = new ObjectMapper();
        response.data = objectMapper.convertValue(context.get("data"), type);
        response.code = ObjectUtils.cast(context.get("code"));
        response.message = ObjectUtils.cast(context.get("message"));
        return response;
    }
}

