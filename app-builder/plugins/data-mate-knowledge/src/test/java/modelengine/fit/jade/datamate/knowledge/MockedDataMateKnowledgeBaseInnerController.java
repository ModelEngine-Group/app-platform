/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge;

import static modelengine.fit.http.protocol.MessageHeaderNames.AUTHORIZATION;
import static modelengine.fitframework.util.IoUtils.content;

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
    private static final String USER_HEADER = "User";
    private static final String EXPECTED_AUTHORIZATION = "Bearer 123";
    private static final String EXPECTED_USER = "test-user";
    private static final String EXPECTED_RETRIEVE_USER = "admin";

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
        this.validateRequestContext(request, "user".equals(param.getName()) ? EXPECTED_USER : null);
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
        this.validateRequestContext(request, EXPECTED_RETRIEVE_USER);
        String resourceName = "/retrieveResult.json";
        String jsonContent = content(DataMateResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }

    private void validateRequestContext(HttpClassicServerRequest request, String expectedUser) {
        this.validate(EXPECTED_AUTHORIZATION.equals(request.headers().first(AUTHORIZATION).orElse(null)),
                "The authorization header is incorrect.");
        if (expectedUser != null) {
            this.validate(expectedUser.equals(request.headers().first(USER_HEADER).orElse(null)),
                    "The User header is incorrect.");
            return;
        }
        this.validate(request.headers().first(USER_HEADER).isEmpty(), "The User header should be absent.");
    }

    private void validate(boolean expression, String message) {
        if (!expression) {
            throw new HttpClientException(message);
        }
    }
}
