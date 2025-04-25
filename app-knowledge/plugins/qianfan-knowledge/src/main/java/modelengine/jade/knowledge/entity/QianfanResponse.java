/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import lombok.Data;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * qianfan 接口返回值结构。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
public class QianfanResponse<T> {
    private T data;
    private String code;
    private String message;

    public static <T> QianfanResponse<T> from(Map<String, Object> context, Type type) {
        QianfanResponse<T> qianfanResponse = new QianfanResponse<>();
        qianfanResponse.data = ObjectUtils.toCustomObject(context, type);
        qianfanResponse.code = ObjectUtils.cast(context.get("code"));
        qianfanResponse.message = ObjectUtils.cast(context.get("message"));
        return qianfanResponse;
    }
}
