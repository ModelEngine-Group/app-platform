/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.external;

import static modelengine.jade.knowledge.code.EdmManagerRetCode.EDM_EXCHANGE_ERROR;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.knowledge.convertor.ParamConvertor;
import modelengine.jade.knowledge.dto.EdmRetrievalParam;
import modelengine.jade.knowledge.dto.ListKnowledgeQueryParam;
import modelengine.jade.knowledge.entity.EdmListRepoEntity;
import modelengine.jade.knowledge.entity.EdmResponse;
import modelengine.jade.knowledge.entity.EdmRetrievalResult;
import modelengine.jade.knowledge.entity.EdmUrls;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 表示 edm 知识库的调用工具。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Component
public class EdmKnowledgeBaseManager {
    private static final Logger log = Logger.get(EdmKnowledgeBaseManager.class);

    private final String edmHost;
    private final String edmRepoListUrl;
    private final String edmRagSearchUrl;

    private final HttpClassicClientFactory httpClientFactory;
    private final LazyLoader<HttpClassicClient> httpClient;

    public EdmKnowledgeBaseManager(EdmUrls urls, HttpClassicClientFactory httpClientFactory) {
        this.edmHost = urls.getEdmHost();
        this.edmRepoListUrl = urls.getEdmRepoListUrl();
        this.edmRagSearchUrl = urls.getEdmRagSearchUrl();
        this.httpClientFactory = httpClientFactory;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
    }

    /**
     * 获取当前edm知识库列表。
     *
     * @param param 知识库列表查询参数的 {@link ListKnowledgeQueryParam}。
     * @return 表示知识库列表的 {@link EdmListRepoEntity}。
     */
    public EdmListRepoEntity listRepos(ListKnowledgeQueryParam param) {
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.edmHost + this.edmRepoListUrl);
        request.entity(Entity.createObject(request, ParamConvertor.INSTANCE.convertKnowledgeParam(param)));
        try {
            Object object = this.httpClient.get()
                    .exchangeForEntity(request,
                            TypeUtils.parameterized(EdmResponse.class, new Type[] {EdmListRepoEntity.class}));
            EdmResponse<EdmListRepoEntity> resp =
                    ObjectUtils.cast(Validation.notNull(object, "The response body is abnormal."));
            return Validation.notNull(resp.getData(), "The response data is abnormal.");
        } catch (HttpClientException | ClientException ex) {
            log.error(EDM_EXCHANGE_ERROR.getMsg(), ex);
            throw new ModelEngineException(EDM_EXCHANGE_ERROR, ex);
        }
    }

    /**
     * 在edm 知识库进行只是检索。
     *
     * @param param 表示知识库 ID 列表的 {@link EdmRetrievalParam}。
     * @return 表示检索结果的 {@link List}{@code <}{@link EdmRetrievalResult}{@code >}。
     */
    public List<EdmRetrievalResult> retrieve(EdmRetrievalParam param) {
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.POST, this.edmHost + this.edmRagSearchUrl);
        request.entity(Entity.createObject(request, param));
        try {
            Object object = this.httpClient.get().exchangeForEntity(request, Object.class);
            Map<String, Object> response =
                    ObjectUtils.toCustomObject(Validation.notNull(object, "The response body is abnormal."), Map.class);
            EdmResponse<List<EdmRetrievalResult>> resp = EdmResponse.from(response,
                    TypeUtils.parameterized(List.class, new Type[] {EdmRetrievalResult.class}));
            return Validation.notNull(resp.getData(), "The response data is abnormal.");
        } catch (HttpClientException ex) {
            log.error(EDM_EXCHANGE_ERROR.getMsg(), ex);
            throw new ModelEngineException(EDM_EXCHANGE_ERROR, ex);
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