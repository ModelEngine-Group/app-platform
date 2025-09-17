/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.common.resp;

import modelengine.fitframework.annotation.Property;
import modelengine.jade.app.engine.base.common.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础应用响应类。
 *
 * @author 陈潇文
 * @since 2024-05-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 2401123375841604844L;

    @Property(description = "响应数据")
    private T data;

    @Property(description = "状态码")
    private Integer code;

    @Property(description = "状态信息")
    private String msg;

    /**
     * 请求成功返回数据。
     *
     * @param data 表示数据的 {@link T}。
     * @param <T> 表示泛型类型的 {@link T}。
     * @return 表示响应的 {@link Response}@{code <}{@link T}{@code >}。
     */
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(ResponseCode.OK.getErrorCode());
        response.setMsg(ResponseCode.OK.getMessage());
        response.setData(data);
        return response;
    }

    /**
     * 请求成功返回。
     *
     * @param <T> 表示泛型类型的 {@link T}。
     * @return 表示响应的 {@link Response}{@code <}{@link T}{@code >}。
     */
    public static <T> Response<T> success() {
        Response<T> response = new Response<>();
        response.setCode(ResponseCode.OK.getErrorCode());
        response.setMsg(ResponseCode.OK.getMessage());
        response.setData(null);
        return response;
    }

    /**
     * 请求成功返回。
     *
     * @param data 表示响应数据的 {@link T}。
     * @param code 表示响应码的 {@link ErrorCode}。
     * @return 表示响应的 {@link Response}{@code <}{@link T}{@code >}。
     */
    public static <T> Response<T> success(T data, ErrorCode code) {
        Response<T> response = new Response<>();
        response.setCode(code.getErrorCode());
        response.setMsg(code.getMessage());
        response.setData(data);
        return response;
    }

    /**
     * 请求成功返回。
     *
     * @param code 表示响应码的 {@link ErrorCode}。
     * @return 表示响应的 {@link Response}{@code <}{@link T}{@code >}。
     */
    public static <T> Response<T> success(ErrorCode code) {
        Response<T> response = new Response<>();
        response.setCode(code.getErrorCode());
        response.setMsg(code.getMessage());
        response.setData(null);
        return response;
    }

    /**
     * 请求失败返回。
     *
     * @param code 表示响应码的 {@link ErrorCode}。
     * @return 表示响应的 {@link Response}{@code <}{@link T}{@code >}。
     */
    public static <T> Response<T> err(ErrorCode code) {
        Response<T> response = new Response<>();
        response.setCode(code.getErrorCode());
        response.setMsg(code.getMessage());
        response.setData(null);
        return response;
    }

    /**
     * 请求失败返回。
     *
     * @param <T> 表示泛型类型的 {@link T}。
     * @return 表示响应的 {@link Response}{@code <}{@link T}{@code >}。
     */
    public static <T> Response<T> err() {
        Response<T> response = new Response<>();
        response.setCode(ResponseCode.INPUT_PARAM_IS_INVALID.getErrorCode());
        response.setMsg(ResponseCode.INPUT_PARAM_IS_INVALID.getMessage());
        response.setData(null);
        return response;
    }
}
