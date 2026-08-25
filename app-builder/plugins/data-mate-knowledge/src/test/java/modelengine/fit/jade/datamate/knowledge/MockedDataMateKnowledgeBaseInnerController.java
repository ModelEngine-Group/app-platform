/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge;

import static modelengine.fit.http.protocol.MessageHeaderNames.AUTHORIZATION;
import static modelengine.fitframework.util.IoUtils.content;
import static modelengine.jade.authentication.context.HttpRequestUtils.AUTH_TOKEN_KEY;
import static modelengine.jade.authentication.context.HttpRequestUtils.CSRF_TOKEN_KEY;
import static modelengine.jade.authentication.context.HttpRequestUtils.REAL_IP_KEY;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 表示 DataMate 内部接口的打桩实现。
 *
 * @author songyongtan
 * @since 2026-02-11
 */
@Component
@RequestMapping(path = "/v2", group = "DataMate知识库内部接口打桩")
public class MockedDataMateKnowledgeBaseInnerController {
    private static final String EXPECTED_AUTHORIZATION = "Bearer 123";
    private static final String EXPECTED_AUTH_TOKEN = "auth-token";
    private static final String EXPECTED_CSRF_TOKEN = "csrf-token";
    private static final String EXPECTED_REAL_IP = "203.0.113.10";

    private final ObjectSerializer serializer;

    public MockedDataMateKnowledgeBaseInnerController(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @PostMapping(path = "/knowledgeBase")
    public Map<String, Object> listRepos(HttpClassicServerRequest request,
            @RequestBody MockedDataMateKnowledgeListQueryParam param) throws IOException {
        if ("error".equals(param.getName())) {
            throw new HttpClientException("error");
        }
        this.validateRequestContext(request, param.getName());
        String resourceName = "/listRepoResult.json";
        String jsonContent = content(DataMateResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }

    @PostMapping(path = "/knowledgebases/query")
    public Map<String, Object> retrieve(HttpClassicServerRequest request,
            @RequestBody MockedDataMateRetrievalParam param) throws IOException {
        if ("error".equals(param.getQuery())) {
            throw new HttpClientException("error");
        }
        this.validateRequestContext(request, param.getQuery());
        String resourceName = "/retrieveResult.json";
        String jsonContent = content(DataMateResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }

    private void validateRequestContext(HttpClassicServerRequest request, String testCase) {
        this.validate(EXPECTED_AUTHORIZATION.equals(request.headers().first(AUTHORIZATION).orElse(null)),
                "The authorization header is incorrect.");
        if ("cookie".equals(testCase)) {
            this.validateCookie(request, AUTH_TOKEN_KEY, EXPECTED_AUTH_TOKEN);
            this.validateCookie(request, CSRF_TOKEN_KEY, EXPECTED_CSRF_TOKEN);
            this.validateCookie(request, REAL_IP_KEY, EXPECTED_REAL_IP);
            return;
        }
        if ("partial-cookie".equals(testCase)) {
            this.validateCookie(request, AUTH_TOKEN_KEY, EXPECTED_AUTH_TOKEN);
            this.validateMissingCookie(request, CSRF_TOKEN_KEY);
            this.validateMissingCookie(request, REAL_IP_KEY);
            return;
        }
        this.validateMissingCookie(request, AUTH_TOKEN_KEY);
        this.validateMissingCookie(request, CSRF_TOKEN_KEY);
        this.validateMissingCookie(request, REAL_IP_KEY);
    }

    private void validateCookie(HttpClassicServerRequest request, String name, String expectedValue) {
        String actualValue = request.cookies().get(name).map(Cookie::value).orElse(null);
        this.validate(expectedValue.equals(actualValue), "The cookie is incorrect: " + name);
    }

    private void validateMissingCookie(HttpClassicServerRequest request, String name) {
        this.validate(request.cookies().get(name).isEmpty(), "The cookie should be absent: " + name);
    }

    private void validate(boolean expression, String message) {
        if (!expression) {
            throw new HttpClientException(message);
        }
    }
}
