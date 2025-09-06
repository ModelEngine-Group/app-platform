/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 大模型上下文协议相关工具方法。
 *
 * @author 宋永坦
 * @since 2025-07-11
 */
public class McpUtils {
    /**
     * 获取 {@code baseUrl} 部分。
     *
     * @param url 目标地址。
     * @return {@code baseUrl} 部分。
     * @throws IllegalArgumentException 当目标地址不合法时。
     */
    public static String getBaseUrl(String url) {
        try {
            URI uri = new URI(url);
            String baseUrl = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                baseUrl += ":" + uri.getPort();
            }
            return baseUrl;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(StringUtils.format("The url is wrong. [url={0}]", url));
        }
    }

    /**
     * 获取 {@code sseEndpoint} 部分。
     *
     * @param url 目标地址。
     * @return {@code sseEndpoint} 部分。
     * @throws IllegalArgumentException 当目标地址不合法时。
     */
    public static String getSseEndpoint(String url) {
        try {
            URI uri = new URI(url);
            return uri.getPath();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(StringUtils.format("The url is wrong. [url={0}]", url));
        }
    }
}
