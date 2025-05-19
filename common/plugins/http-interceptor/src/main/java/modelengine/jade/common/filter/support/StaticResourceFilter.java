/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import modelengine.fit.http.server.DoHttpServerFilterException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.HttpRequestUtils;

import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 用于静态资源的过滤器类。
 *
 * @author 邬涨财
 * @since 2023-07-13
 */
@Component
public class StaticResourceFilter implements HttpServerFilter {
    private static final Logger log = Logger.get(StaticResourceFilter.class);
    private static final String DEFAULT_MATCH_PATTERNS = "/**";
    private static final String URL_SEPARATOR = ",";
    private static final List<String> STATIC_EXTENSIONS = Arrays.asList(".html",
            ".css",
            ".js",
            ".jpg",
            ".jpeg",
            ".png",
            ".gif",
            ".svg",
            ".woff",
            ".woff2",
            ".ttf",
            ".eot",
            ".json",
            ".xml",
            ".pdf");
    private static final String FILTER_ENABLE_VALUE = "enable";
    private final List<String> matchPatterns;
    private final List<String> mismatchPatterns;
    private final boolean isStaticResourceFilterEnabled;

    public StaticResourceFilter(@Value("${include}") String includeUrls, @Value("${exclude}") String excludeUrls,
            @Value("${filter.static-resource-filter}") String staticResourceFilterEnabled) {
        this.matchPatterns = this.splitUrls(ObjectUtils.nullIf(includeUrls, DEFAULT_MATCH_PATTERNS));
        this.mismatchPatterns = this.splitUrls(ObjectUtils.nullIf(excludeUrls, StringUtils.EMPTY));
        this.isStaticResourceFilterEnabled = StringUtils.equals(staticResourceFilterEnabled, FILTER_ENABLE_VALUE);
    }

    private List<String> splitUrls(String urls) {
        return StringUtils.isBlank(urls) ? Collections.emptyList() : Arrays.asList(urls.split(URL_SEPARATOR));
    }

    @Override
    public String name() {
        return "static-resource-filter";
    }

    @Override
    public int priority() {
        return Order.MEDIUM;
    }

    @Override
    public List<String> matchPatterns() {
        return this.matchPatterns;
    }

    @Override
    public List<String> mismatchPatterns() {
        return this.mismatchPatterns;
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) throws DoHttpServerFilterException {
        if (!this.isStaticResourceFilterEnabled) {
            chain.doFilter(request, response);
            return;
        }
        String path = request.path();
        if (this.isStaticPath(path)) {
            this.setResponseHeader(request, response);
        }
        chain.doFilter(request, response);
    }

    private void setResponseHeader(HttpClassicServerRequest req, HttpClassicServerResponse resp) {
        Optional<String> originOptional = HttpRequestUtils.header(req, "Origin");
        if (originOptional.isPresent() && StringUtils.isNotBlank(originOptional.get())) {
            String origin = originOptional.get();
            log.info("Set access header. path:[{}]", req.path());
            resp.headers().set("Access-Control-Allow-Origin", origin);
            resp.headers().set("Access-Control-Allow-Credentials", "true");
        }
        resp.headers().set("Content-Security-Policy", "script-src 'self' 'unsafe-inline'");
    }

    private boolean isStaticPath(String uri) {
        for (String ext : STATIC_EXTENSIONS) {
            if (uri.toLowerCase(Locale.ROOT).endsWith(ext)) {
                return true;
            }
        }
        String mimeType = URLConnection.guessContentTypeFromName(uri);
        return !StringUtils.isEmpty(mimeType);
    }
}
