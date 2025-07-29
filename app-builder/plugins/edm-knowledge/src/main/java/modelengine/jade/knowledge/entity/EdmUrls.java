/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import lombok.Data;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

/**
 * 表示 edm 内部接口的 url 对象。
 *
 * @author 何嘉斌
 * @since 2024-09-29
 */
@Data
@Component
public class EdmUrls {
    @Value("${edm.endpoint:https://backend:8002}")
    private String edmHost;

    @Value("${edm.url.repo-list:/knowledge-bases/v1/inner/list/query}")
    private String edmRepoListUrl;

    @Value("${edm.url.rag-search-url:/knowledge-bases/v1/inner/rag/search}")
    private String edmRagSearchUrl;
}