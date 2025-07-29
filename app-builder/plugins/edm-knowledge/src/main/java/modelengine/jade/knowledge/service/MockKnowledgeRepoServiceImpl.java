/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.KnowledgeRepoService;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.document.KnowledgeDocument;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import java.util.List;

/**
 * mock的实现
 *
 * @author 何嘉斌
 * @since 2024-09-24
 */
public class MockKnowledgeRepoServiceImpl implements KnowledgeRepoService {
    @Override
    public PageVo<KnowledgeRepo> listRepos(String apiKey, ListRepoQueryParam param) {
        return null;
    }

    @Override
    public KnowledgeProperty getProperty(String apiKey) {
        return null;
    }

    @Override
    public List<KnowledgeDocument> retrieve(String apiKey, FlatKnowledgeOption option) {
        return null;
    }
}
