/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.model;

import lombok.Getter;

/**
 * 全局标准返回体。
 *
 * @author 陈镕希
 * @since 2023-06-27
 */
@Getter
public class JoberResponse<T> extends modelengine.fit.jober.common.model.Response<T> {
    private final int code;

    private final String message;

    /**
     * 全局标准返回体默认构造器。
     *
     * @param data 返回的数据。
     * @param code 返回状态码。
     * @param message 返回附带消息的 {@link String}。
     * @param <T> 返回的数据类型。
     */
    public JoberResponse(T data, int code, String message) {
        super(data);
        this.code = code;
        this.message = message;
    }

    /**
     * 默认成功返回。
     *
     * @param data 返回的数据。
     * @param <T> 返回的数据类型。
     * @return 成功返回的 {@link JoberResponse}。
     */
    public static <T> JoberResponse<T> success(T data) {
        return new JoberResponse<>(data, 0, "OK");
    }

    /**
     * 默认失败返回。
     *
     * @param code 返回的状态码。
     * @param message 返回附带消息的 {@link String}。
     * @return 失败返回的 {@link JoberResponse}。
     */
    public static JoberResponse<String> fail(int code, String message) {
        return new JoberResponse<>(null, code, message);
    }
}
