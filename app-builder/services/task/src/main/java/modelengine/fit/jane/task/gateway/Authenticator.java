/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fit.http.server.HttpClassicServerRequest;

/**
 * 为系统提供认证器。
 *
 * @author 梁济时
 * @since 2023-11-15
 */
public interface Authenticator {
    /**
     * 验证用户。
     *
     * @param httpRequest 表示待验证的 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @return 表示符合验证的用户信息的 {@link String}。
     */
    User authenticate(HttpClassicServerRequest httpRequest);
}
