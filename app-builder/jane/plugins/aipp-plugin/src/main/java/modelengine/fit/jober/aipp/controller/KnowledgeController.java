/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.condition.KnowledgeQueryCondition;
import modelengine.fit.jober.aipp.service.KnowledgeService;
import modelengine.jade.app.engine.knowledge.dto.KRepoDto;
import modelengine.jade.app.engine.knowledge.dto.KTableDto;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

/**
 * 知识库服务相关接口。
 *
 * @author 黄夏露
 * @since 2024-04-23
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/knowledge", group = "知识库相关操作")
public class KnowledgeController extends AbstractController {
    private final KnowledgeService knowledgeService;

    /**
     * 构造函数，初始化认证器和知识库服务。
     *
     * @param authenticator 认证器
     * @param knowledgeService 知识库服务
     */
    public KnowledgeController(Authenticator authenticator, KnowledgeService knowledgeService) {
        super(authenticator);
        this.knowledgeService = knowledgeService;
    }

    /**
     * 获取知识库列表。
     *
     * @param httpRequest Http 请求
     * @param cond 检索条件
     * @param pageNum 页码
     * @param pageSize 单页大小
     * @return 分页返回知识库列表详细信息
     */
    @GetMapping(path = "/repos", description = "获取知识库列表")
    public Rsp<PageResponse<KRepoDto>> listKnowledgeRepo(HttpClassicServerRequest httpRequest,
            @RequestBean KnowledgeQueryCondition cond,
            @RequestQuery(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Rsp.ok(this.knowledgeService.listKnowledgeRepo(cond, pageNum, pageSize));
    }

    /**
     * 根据知识库 id 获取知识表列表。
     *
     * @param httpRequest Http 请求
     * @param repoId 知识库 id
     * @param pageNum 页码
     * @param pageSize 单页大小
     * @return 分页返回知识表列表详细信息
     */
    @GetMapping(path = "/repos/{repo_id}/tables", description = "根据知识库 id 获取知识表列表")
    public Rsp<PageResponse<KTableDto>> listKnowledgeTables(HttpClassicServerRequest httpRequest,
            @PathVariable("repo_id") Long repoId,
            @RequestQuery(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Rsp.ok(this.knowledgeService.listKnowledgeTables(repoId, pageNum, pageSize));
    }
}
