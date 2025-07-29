/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static modelengine.fitframework.util.IoUtils.content;

import modelengine.jade.knowledge.dto.EdmRetrievalParam;
import modelengine.jade.knowledge.dto.ListKnowledgeQueryParam;
import modelengine.jade.knowledge.entity.EdmResponse;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.io.IOException;
import java.util.Map;

/**
 * 表示 edm 内部接口的打桩实现。
 *
 * @author 何嘉斌
 * @since 2024-09-26
 */
@Component
@RequestMapping(path = "/knowledge-bases/v1/inner", group = "edm知识库内部接口打桩")
public class MockedEdmKnowledgeBaseInnerController {
    private final ObjectSerializer serializer;

    public MockedEdmKnowledgeBaseInnerController(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 获取当前edm知识库列表。
     *
     * @param param 知识库列表查询参数的 {@link ListKnowledgeQueryParam}。
     * @return 表示 client 返回的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IOException 表示读取 Json 失败的 {@link IOException}。
     */
    @PostMapping(path = "/list/query")
    public Map<String, Object> listRepos(@RequestBody ListKnowledgeQueryParam param) throws IOException {
        if (param.getName().equals("error")) {
            throw new HttpClientException("error");
        }
        String resourceName = "/listRepoResult.json";
        String jsonContent = content(EdmResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }

    /**
     * 在edm 知识库进行只是检索。
     *
     * @param param 表示知识库 ID 列表的 {@link EdmRetrievalParam}。
     * @return 表示 client 返回的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IOException 表示读取 Json 失败的 {@link IOException}。
     */
    @PostMapping(path = "/rag/search")
    public Map<String, Object> retrieve(@RequestBody EdmRetrievalParam param) throws IOException {
        if (param.getContext().equals("error")) {
            throw new HttpClientException("error");
        }
        String resourceName = "/searchResult.json";
        String jsonContent = content(EdmResponse.class, resourceName);
        return serializer.deserialize(jsonContent, Map.class);
    }
}