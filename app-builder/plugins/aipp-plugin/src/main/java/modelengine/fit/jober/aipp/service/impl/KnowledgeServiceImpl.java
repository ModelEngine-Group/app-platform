/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.jade.app.engine.knowledge.dto.KRepoDto;
import modelengine.jade.app.engine.knowledge.dto.KTableDto;
import modelengine.jade.app.engine.knowledge.params.RepoQueryParam;
import modelengine.jade.app.engine.knowledge.service.KRepoService;
import modelengine.jade.app.engine.knowledge.service.KTableService;
import modelengine.jade.app.engine.knowledge.service.param.PageQueryParam;

import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.condition.KnowledgeQueryCondition;
import modelengine.fit.jober.aipp.service.KnowledgeService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

/**
 * 知识库服务接口实现
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Component
public class KnowledgeServiceImpl implements KnowledgeService {
    private final KRepoService kRepoService;

    private final KTableService kTableService;

    public KnowledgeServiceImpl(KRepoService kRepoService, KTableService kTableService) {
        // 独立环境的 eDataMate.query_list_url 暂时由外部环境注入，之后统一整改到配置文件中
        this.kRepoService = kRepoService;
        this.kTableService = kTableService;
    }

    @Override
    public PageResponse<KRepoDto> listKnowledgeRepo(KnowledgeQueryCondition cond, Integer pageNum, Integer pageSize) {
        RepoQueryParam param = this.buildRepoQueryParam(pageNum, pageSize, cond);
        return new PageResponse<>(this.safelyConvertToLongType(this.kRepoService.queryReposCount(param)), null,
                this.kRepoService.queryReposByName(param));
    }

    private RepoQueryParam buildRepoQueryParam(Integer pageNum, Integer pageSize, KnowledgeQueryCondition cond) {
        RepoQueryParam param = new RepoQueryParam();
        param.setOffset(pageNum);
        param.setSize(pageSize);
        if (StringUtils.isNotEmpty(cond.getName())) {
            param.setName(cond.getName());
        }
        return param;
    }

    @Override
    public PageResponse<KTableDto> listKnowledgeTables(Long repoId, Integer pageNum, Integer pageSize) {
        PageQueryParam param = new PageQueryParam();
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        return new PageResponse<>(this.safelyConvertToLongType(this.kTableService.getTableCountByRepoId(repoId)), null,
                this.kTableService.getByRepoId(repoId, param));
    }

    private Long safelyConvertToLongType(Number value) {
        return value.longValue();
    }
}
