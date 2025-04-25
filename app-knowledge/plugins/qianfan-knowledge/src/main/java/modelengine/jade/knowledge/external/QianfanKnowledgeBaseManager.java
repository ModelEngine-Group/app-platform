/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.external;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.knowledge.dto.QianfanKnowledgeListQueryParam;
import modelengine.jade.knowledge.entity.QianfanKnowledgeListEntity;
import modelengine.jade.knowledge.entity.QianfanResponse;
import modelengine.jade.knowledge.exception.KnowledgeException;

import java.lang.reflect.Type;
import java.util.Map;

import static modelengine.fit.http.protocol.MessageHeaderNames.AUTHORIZATION;
import static modelengine.fit.http.protocol.MessageHeaderNames.CONTENT_TYPE;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.QUERY_KNOWLEDGE_LIST_ERROR;

/**
 * 表示 qianfan 知识库的调用工具。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Component
public class QianfanKnowledgeBaseManager {
    private static final Logger log = Logger.get(QianfanKnowledgeBaseManager.class);
    private static final String BEARER = "Bearer ";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private final Map<String, String> qianfanUrls;
    private final HttpClassicClientFactory httpClientFactory;
    private final LazyLoader<HttpClassicClient> httpClient;

    public QianfanKnowledgeBaseManager(@Value("${qianfan.url}") Map<String, String> qianfanUrls,
                                       HttpClassicClientFactory httpClientFactory) {
        this.qianfanUrls = qianfanUrls;
        this.httpClientFactory = httpClientFactory;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
    }

    /**
     * 获取 qianfan 知识库列表。
     *
     * @param apiKey 表示知识库接口鉴权api key的 {@link String}。
     * @param param 表示知识库列表查询参数的 {@link QianfanKnowledgeListQueryParam}。
     * @return 表示知识库列表的 {@link QianfanKnowledgeListEntity}。
     */
    public QianfanKnowledgeListEntity listRepos(String apiKey, QianfanKnowledgeListQueryParam param) {
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.qianfanUrls.get("knowledge-list"));
        request.entity(Entity.createObject(request, param));
        request.headers().set(AUTHORIZATION, BEARER + apiKey);
        request.headers().set(CONTENT_TYPE, CONTENT_TYPE_JSON);
        try {
            Object object = this.httpClient.get()
                    .exchangeForEntity(request,
                            TypeUtils.parameterized(QianfanResponse.class, new Type[] {QianfanKnowledgeListEntity.class}));
            QianfanResponse<QianfanKnowledgeListEntity> response =
                    ObjectUtils.cast(Validation.notNull(object, "The response body is abnormal."));
            return Validation.notNull(response.getData(), "The response body is abnormal.");
        } catch (HttpClientException | ClientException ex) {
            log.error(QUERY_KNOWLEDGE_LIST_ERROR.getMsg(), ex);
            throw new KnowledgeException(QUERY_KNOWLEDGE_LIST_ERROR, ex);
        }
    }

    private HttpClassicClient getHttpClient() {
        Map<String, Object> custom = MapBuilder.<String, Object>get()
                .put("client.http.secure.ignore-trust", true)
                .put("client.http.secure.ignore-hostname", true)
                .build();
        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder().custom(custom).build());
    }
}
