/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fit.http.client.HttpClassicClientFactory.Config;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.HttpResponseStatus;

import java.io.IOException;
import java.util.Locale;

/**
 * Http 操作工具类
 *
 * @author 廖航
 * @since 2024-01-15
 */
public class HttpUtils {
    /**
     * 执行HTTP请求
     *
     * @param request 表示 HTTP 请求的{@link HttpClassicClientRequest}
     * @return HTTP 回复
     * @throws IOException 当 HTTP 请求发送失败，或者 HTTP 回复内容不存在时，抛出该异常
     */
    public static HttpClassicClientResponse<Object> execute(HttpClassicClientRequest request) throws IOException {
        return request.exchange();
    }

    /**
     * 发送HTTP请求
     *
     * @param httpRequest 表示 HTTP 请求的{@link HttpClassicClientRequest}
     * @return HTTP 回复
     * @throws IOException 当 HTTP 请求发送失败，或者 HTTP 回复内容不存在时，抛出该异常
     */
    public static String sendHttpRequest(HttpClassicClientRequest httpRequest) throws IOException {
        try (HttpClassicClientResponse<Object> response = HttpUtils.execute(httpRequest)) {
            if (response.statusCode() != HttpResponseStatus.OK.statusCode()) {
                throw new IOException(String.format(Locale.ROOT,
                        "send http fail. url=%s result=%d",
                        httpRequest.requestUri(),
                        response.statusCode()));
            }
            if (!response.textEntity().isPresent()) {
                throw new IOException(String.format(Locale.ROOT,
                        "get empty response entity, url=%s",
                        httpRequest.requestUri()));
            }
            return response.textEntity().get().content();
        }
    }

    /**
     * 获取HttpClient 请求配置
     *
     * @param socketTimeout 读取内存超时时长
     * @return 请求配置
     */
    public static Config requestConfig(int socketTimeout) {
        final int connectTimeout = 5000;
        final int connectRequestTimeout = 5000;
        return HttpClassicClientFactory.Config.builder()
                .connectTimeout(connectTimeout)
                .socketTimeout(socketTimeout)
                .connectionRequestTimeout(connectRequestTimeout)
                .build();
    }
}
