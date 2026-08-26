/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.authentication.context;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Http 请求数据工具方法。
 *
 * @author 刘信宏
 * @since 2024-08-07
 */
public class HttpRequestUtils {
    private static final String UNKNOWN_IP = "unknown";

    /**
     * 获取语言。
     *
     * @param request 表示请求的 {@link HttpClassicServerRequest}。
     * @return 表示语言 的{@link String}。
     */
    public static String getAcceptLanguages(HttpClassicServerRequest request) {
        return request.cookies()
                .get("locale")
                .map(Cookie::value)
                .orElseGet(() -> request.headers()
                        .first("Accept-Language")
                        .orElseGet(() -> request.headers().first("accept-language").orElse(StringUtils.EMPTY)));
    }

    /**
     * 获取用户 Ip。
     *
     * @param request 表示请求的 {@link HttpClassicServerRequest}。
     * @return 表示用户 ip 的{@link String}。
     */
    public static String getUserIp(HttpClassicServerRequest request) {
        return compute(Arrays.asList(HttpRequestUtils::getForwardedIp,
                HttpRequestUtils::getProxyClientIp,
                HttpRequestUtils::getWlProxyClientIp,
                HttpRequestUtils::getHttpClientIp,
                HttpRequestUtils::getHttpForwardedFor), request);
    }

    /**
     * 获取请求的头信息
     *
     * @param request 请求信息
     * @param name 表示需要获取的头
     * @return 获取到的头信息
     */
    public static Optional<String> header(HttpClassicServerRequest request, String name) {
        return request.headers()
                .names()
                .stream()
                .filter(value -> StringUtils.equalsIgnoreCase(value, name))
                .findFirst()
                .flatMap(request.headers()::first);
    }

    private static String compute(List<Function<HttpClassicServerRequest, Optional<String>>> mappers,
            HttpClassicServerRequest request) {
        Optional<String> optional = Optional.empty();
        for (Function<HttpClassicServerRequest, Optional<String>> mapper : mappers) {
            optional = mapper.apply(request);
            if (optional.isPresent()) {
                break;
            }
        }
        return optional.orElseGet(() -> request.remoteAddress().hostAddress());
    }

    private static Optional<String> getForwardedIp(HttpClassicServerRequest request) {
        return header(request, "X-Forwarded-For").map(value -> StringUtils.split(value, ','))
                .map(Stream::of)
                .orElse(Stream.empty())
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .filter(HttpRequestUtils::knownIp)
                .findFirst();
    }

    private static Optional<String> getProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "Proxy-Client-IP").filter(HttpRequestUtils::knownIp);
    }

    private static Optional<String> getWlProxyClientIp(HttpClassicServerRequest request) {
        return header(request, "WL-Proxy-Client-IP").filter(HttpRequestUtils::knownIp);
    }

    private static Optional<String> getHttpClientIp(HttpClassicServerRequest request) {
        return header(request, "HTTP_CLIENT_IP").filter(HttpRequestUtils::knownIp);
    }

    private static Optional<String> getHttpForwardedFor(HttpClassicServerRequest request) {
        return header(request, "HTTP_X_FORWARDED_FOR").filter(HttpRequestUtils::knownIp);
    }

    private static boolean knownIp(String ip) {
        return !StringUtils.equalsIgnoreCase(ip, UNKNOWN_IP);
    }
}
