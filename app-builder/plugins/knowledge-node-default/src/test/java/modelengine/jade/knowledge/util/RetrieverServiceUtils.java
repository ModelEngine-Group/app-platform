/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.util;

import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.enums.IndexType;

import java.util.Collections;

/**
 * 检索服务的工具方法。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
public class RetrieverServiceUtils {
    /**
     * 构造检索配置实体。
     *
     * @return 表示检索配置的 {@link RetrieverOption}。
     */
    public static RetrieverOption buildRetrieverOption() {
        RetrieverOption retrieverOption = new RetrieverOption();
        retrieverOption.setApiKey("apiKey");
        retrieverOption.setRepoIds(Collections.singletonList("repoId"));
        retrieverOption.setIndexType(new KnowledgeProperty.IndexInfo(IndexType.SEMANTIC, "name", "description"));
        retrieverOption.setRerankParam(new RetrieverOption.RerankParam(false, "model", "baseUri", 2));
        return retrieverOption;
    }
}
