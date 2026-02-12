/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.external;

import static modelengine.fit.http.protocol.MessageHeaderNames.AUTHORIZATION;
import static modelengine.fit.http.protocol.MessageHeaderNames.CONTENT_TYPE;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.AUTHENTICATION_ERROR;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.CLIENT_REQUEST_ERROR;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.INTERNAL_SERVICE_ERROR;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.NOT_FOUND;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.QUERY_KNOWLEDGE_ERROR;
import static modelengine.jade.knowledge.code.KnowledgeManagerRetCode.QUERY_KNOWLEDGE_LIST_ERROR;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClientResponseException;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.jade.datamate.knowledge.dto.DataMateKnowledgeListQueryParam;
import modelengine.fit.jade.datamate.knowledge.dto.DataMateRetrievalParam;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateKnowledgeListEntity;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateResponse;
import modelengine.fit.jade.datamate.knowledge.entity.DataMateRetrievalResult;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.code.KnowledgeManagerRetCode;
import modelengine.jade.knowledge.exception.KnowledgeException;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 DataMate 知识库的调用工具。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Component
public class DataMateKnowledgeBaseManager {
    private static final Logger log = Logger.get(DataMateKnowledgeBaseManager.class);
    private static final String BEARER = "Bearer ";
    private static final String CONTENT_TYPE_JSON = "application/json";
    /** 默认访问超时时间（秒）。 */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private final Map<String, String> dataMateUrls;
    private final HttpClassicClientFactory httpClientFactory;
    private final LazyLoader<HttpClassicClient> httpClient;
    private final Map<Integer, KnowledgeManagerRetCode> exceptionMap = new HashMap<>();
    /** 访问超时时间（秒），用于连接、读、请求超时。 */
    private final int timeoutSeconds;

    public DataMateKnowledgeBaseManager(@Value("${datamate.url}") Map<String, String> dataMateUrls,
            HttpClassicClientFactory httpClientFactory,
            @Value("${datamate.timeout:30}") int timeoutSeconds) {
        this.dataMateUrls = dataMateUrls;
        this.httpClientFactory = httpClientFactory;
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : DEFAULT_TIMEOUT_SECONDS;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
        this.exceptionMap.put(500, INTERNAL_SERVICE_ERROR);
        this.exceptionMap.put(401, AUTHENTICATION_ERROR);
        this.exceptionMap.put(404, NOT_FOUND);
        this.exceptionMap.put(400, CLIENT_REQUEST_ERROR);
    }

    /**
     * 获取 DataMate 知识库列表。
     *
     * @param apiKey 表示知识库接口鉴权 api key 的 {@link String}。
     * @param param 表示知识库列表查询参数的 {@link DataMateKnowledgeListQueryParam}。
     * @return 表示知识库列表的 {@link DataMateKnowledgeListEntity}。
     */
    public DataMateKnowledgeListEntity listRepos(String apiKey, DataMateKnowledgeListQueryParam param) {
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.dataMateUrls.get("list"));
        request.entity(Entity.createObject(request, param));
        if (StringUtils.isNotEmpty(apiKey)) {
            request.headers().set(AUTHORIZATION, BEARER + apiKey);
        }
        try {
            Object object = this.httpClient.get().exchangeForEntity(request, Object.class);
            Map<String, Object> response =
                    ObjectUtils.toCustomObject(Validation.notNull(object, "The response body is abnormal."), Map.class);
            DataMateResponse<DataMateKnowledgeListEntity> resp =
                    DataMateResponse.from(response, DataMateKnowledgeListEntity.class);
            return Validation.notNull(resp.getData(), "The response body is abnormal.");
        } catch (ClientException ex) {
            log.error(QUERY_KNOWLEDGE_LIST_ERROR.getMsg(), ex.getMessage());
            throw new KnowledgeException(QUERY_KNOWLEDGE_LIST_ERROR, ex, ex.getMessage());
        } catch (HttpClientResponseException ex) {
            throw this.handleException(ex);
        }
    }

    /**
     * DataMate 知识库检索。
     *
     * @param apiKey 表示知识库接口鉴权 api key 的 {@link String}。
     * @param param 表示知识库检索查询参数的 {@link DataMateRetrievalParam}。
     * @return 表示知识库检索结果的 {@link DataMateRetrievalResult}。
     */
    public DataMateRetrievalResult retrieve(String apiKey, DataMateRetrievalParam param) {
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.dataMateUrls.get("retrieve"));
        request.entity(Entity.createObject(request, param));
        if (StringUtils.isNotEmpty(apiKey)) {
            request.headers().set(AUTHORIZATION, BEARER + apiKey);
        }
        request.headers().set(CONTENT_TYPE, CONTENT_TYPE_JSON);
        try {
            Object object = this.httpClient.get().exchangeForEntity(request, Object.class);
            Map<String, Object> response =
                    ObjectUtils.toCustomObject(Validation.notNull(object, "The response body is abnormal."), Map.class);
            DataMateResponse<DataMateRetrievalResult> resp =
                    DataMateResponse.from(response, DataMateRetrievalResult.class);
            return Validation.notNull(resp.getData(), "The response body is abnormal.");
        } catch (ClientException ex) {
            log.error(QUERY_KNOWLEDGE_ERROR.getMsg(), ex.getMessage());
            throw new KnowledgeException(QUERY_KNOWLEDGE_ERROR, ex, ex.getMessage());
        } catch (HttpClientResponseException ex) {
            throw this.handleException(ex);
        }
    }

    private KnowledgeException handleException(HttpClientResponseException ex) {
        int statusCode = ex.statusCode();
        KnowledgeManagerRetCode retCode = this.exceptionMap.getOrDefault(statusCode, INTERNAL_SERVICE_ERROR);
        log.error(retCode.getMsg(), ex);
        return new KnowledgeException(retCode, ex, ex.getSimpleMessage());
    }

    private HttpClassicClient getHttpClient() {
        int timeoutMs = this.timeoutSeconds * 1000;
        Map<String, Object> custom = MapBuilder.<String, Object>get()
                .put("client.http.secure.ignore-trust", true)
                .put("client.http.secure.ignore-hostname", true)
                .build();
        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder()
                .custom(custom)
                .connectTimeout(timeoutMs)
                .socketTimeout(timeoutMs)
                .connectionRequestTimeout(timeoutMs)
                .build());
    }
}

