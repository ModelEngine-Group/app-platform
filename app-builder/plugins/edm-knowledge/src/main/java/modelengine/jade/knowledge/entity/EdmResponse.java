/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import static modelengine.fitframework.util.ObjectUtils.cast;

import lombok.Data;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * edm 接口返回值结构。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Data
public class EdmResponse<T> {
    private T data;
    private String msg;
    private int code;

    /**
     * 表示 edm 接口返回数据提取器的构建器。
     *
     * @param context 表示上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param type 表示数据类型的 {@link Type}。
     * @param <T> 表示上下文信息中的数据类型。
     * @return 表示解析出的上下文信息传输对象的 {@link EdmResponse}。
     */
    public static <T> EdmResponse<T> from(Map<String, Object> context, Type type) {
        EdmResponse<T> edmResponse = new EdmResponse<>();
        edmResponse.data = ObjectUtils.toCustomObject(context.get("data"), type);
        edmResponse.msg = cast(context.get("msg"));
        edmResponse.code = cast(context.get("code"));
        return edmResponse;
    }
}