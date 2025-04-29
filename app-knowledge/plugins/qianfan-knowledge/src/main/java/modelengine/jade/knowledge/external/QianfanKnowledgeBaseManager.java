/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.external;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClientResponseException;
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
import modelengine.jade.knowledge.code.KnowledgeManagerRetCode;
import modelengine.jade.knowledge.dto.QianfanKnowledgeListQueryParam;
import modelengine.jade.knowledge.dto.QianfanRetrievalParam;
import modelengine.jade.knowledge.entity.QianfanKnowledgeListEntity;
import modelengine.jade.knowledge.entity.QianfanResponse;
import modelengine.jade.knowledge.entity.QianfanRetrievalResult;
import modelengine.jade.knowledge.exception.KnowledgeException;

import java.util.HashMap;
import java.util.Map;

import static modelengine.fit.http.protocol.MessageHeaderNames.AUTHORIZATION;
import static modelengine.fit.http.protocol.MessageHeaderNames.CONTENT_TYPE;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.*;

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
    private final Map<Integer, KnowledgeManagerRetCode> exceptionMap = new HashMap<>();

    public QianfanKnowledgeBaseManager(@Value("${qianfan.url}") Map<String, String> qianfanUrls,
                                       HttpClassicClientFactory httpClientFactory) {
        this.qianfanUrls = qianfanUrls;
        this.httpClientFactory = httpClientFactory;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
        this.exceptionMap.put(500, INTERNAL_SERVICE_ERROR);
        this.exceptionMap.put(401, AUTHENTICATION_ERROR);
        this.exceptionMap.put(404, NOT_FOUND);
        this.exceptionMap.put(400, CLIENT_REQUEST_ERROR);
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
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.qianfanUrls.get("knowledgeList"));
        request.entity(Entity.createObject(request, param));
        request.headers().set(AUTHORIZATION, BEARER + apiKey);
        try {
            Object object = this.httpClient.get()
                    .exchangeForEntity(request, Object.class);
            Map<String, Object> response =
                    ObjectUtils.toCustomObject(Validation.notNull(object, "The response body is abnormal."), Map.class);
            QianfanResponse<QianfanKnowledgeListEntity> resp = QianfanResponse.from(response,
                    QianfanKnowledgeListEntity.class);
            return Validation.notNull(resp.getData(), "The response body is abnormal.");
        } catch (ClientException ex) {
            log.error(QUERY_KNOWLEDGE_LIST_ERROR.getMsg(), ex.getMessage());
            throw new KnowledgeException(QUERY_KNOWLEDGE_LIST_ERROR, ex.getMessage());
        } catch (HttpClientResponseException ex) {
            throw this.handleException(ex);
        }
    }

    /**
     * qianfan 知识库检索。
     *
     * @param apiKey 表示知识库接口鉴权api key的 {@link String}。
     * @param param 表示知识库检索查询参数的 {@link QianfanRetrievalParam}。
     * @return 表示知识库检索结果的 {@link QianfanRetrievalResult}。
     */
    public QianfanRetrievalResult retrieve(String apiKey, QianfanRetrievalParam param) {
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.qianfanUrls.get("knowledgeRetrieve"));
        request.entity(Entity.createObject(request, param));
        request.headers().set(AUTHORIZATION, BEARER + apiKey);
        request.headers().set(CONTENT_TYPE, CONTENT_TYPE_JSON);
        try {
            Object object = this.httpClient.get().exchangeForEntity(request, Object.class);
            Map<String, Object> response =
                    ObjectUtils.toCustomObject(Validation.notNull(object, "The response body is abnormal."), Map.class);
            QianfanResponse<QianfanRetrievalResult> resp = QianfanResponse.from(response, QianfanRetrievalResult.class);
            return Validation.notNull(resp.getData(), "The response body is abnormal.");
        } catch (ClientException ex) {
            log.error(QUERY_KNOWLEDGE_ERROR.getMsg(), ex.getMessage());
            throw new KnowledgeException(QUERY_KNOWLEDGE_ERROR, ex.getMessage());
        } catch (HttpClientResponseException ex) {
            throw this.handleException(ex);
        }
    }

    private KnowledgeException handleException(HttpClientResponseException ex) {
        int statusCode = ex.statusCode();
        log.error(this.exceptionMap.get(statusCode).getMsg(), ex);
        return new KnowledgeException(this.exceptionMap.get(statusCode), ex.getSimpleMessage());
    }

    private HttpClassicClient getHttpClient() {
        Map<String, Object> custom = MapBuilder.<String, Object>get()
                .put("client.http.secure.ignore-trust", true)
                .put("client.http.secure.ignore-hostname", true)
                .build();
        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder().custom(custom).build());
    }
}
