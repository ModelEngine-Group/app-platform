/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static modelengine.fitframework.util.IoUtils.content;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.knowledge.entity.QianfanResponse;

import java.io.IOException;
import java.util.Map;

/**
 * 表示千帆内部接口的打桩实现。
 *
 * @author 陈潇文
 * @since 2025-05-07
 */
@Component
@RequestMapping(path = "/v2", group = "千帆知识库内部接口打桩")
public class MockedQianfanKnowledgeBaseInnerController {
    private final ObjectSerializer serializer;

    public MockedQianfanKnowledgeBaseInnerController(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @PostMapping(path = "/knowledgeBase")
    public Map<String, Object> listRepos(@RequestBody MockedQianfanKnowledgeListQueryParam param,
            @RequestQuery(name = "Action", defaultValue = "DescribeKnowledgeBases") String action) throws IOException {
        if (param.getKeyword().equals("error")) {
            throw new HttpClientException("error");
        }
        String resourceName = "/listRepoResult.json";
        String jsonContent = content(QianfanResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }

    @PostMapping(path = "/knowledgebases/query")
    public Map<String, Object> listRepos(@RequestBody MockedQianfanRetrievalParam param) throws IOException {
        if (param.getQuery().equals("error")) {
            throw new HttpClientException("error");
        }
        String resourceName = "/retrieveResult.json";
        String jsonContent = content(QianfanResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }
}
