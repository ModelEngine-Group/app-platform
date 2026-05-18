/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge;

import static modelengine.fitframework.util.IoUtils.content;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.client.HttpClientException;
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
    private final ObjectSerializer serializer;

    public MockedDataMateKnowledgeBaseInnerController(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @PostMapping(path = "/knowledgeBase")
    public Map<String, Object> listRepos(@RequestBody MockedDataMateKnowledgeListQueryParam param) throws IOException {
        if (param.getName().equals("error")) {
            throw new HttpClientException("error");
        }
        String resourceName = "/listRepoResult.json";
        String jsonContent = content(DataMateResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }

    @PostMapping(path = "/knowledgebases/query")
    public Map<String, Object> retrieve(@RequestBody MockedDataMateRetrievalParam param) throws IOException {
        if (param.getQuery().equals("error")) {
            throw new HttpClientException("error");
        }
        String resourceName = "/retrieveResult.json";
        String jsonContent = content(DataMateResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }
}
