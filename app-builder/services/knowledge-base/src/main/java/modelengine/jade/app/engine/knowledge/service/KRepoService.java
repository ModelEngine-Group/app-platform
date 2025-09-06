/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.knowledge.dto.KRepoDto;
import modelengine.jade.app.engine.knowledge.params.RepoQueryParam;

import java.util.List;

/**
 * 知识库服务的管理接口。
 *
 * @since 2024-05-18
 */
public interface KRepoService {
    /**
     * 根据名称查找知识库
     *
     * @param param 知识库查询参数
     * @return 知识库列表
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.queryReposByName")
    List<KRepoDto> queryReposByName(RepoQueryParam param);

    /**
     * 根据名称查找知识库数量
     *
     * @param param 知识库查询参数
     * @return 知识库数量
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.queryReposCount")
    int queryReposCount(RepoQueryParam param);

    /**
     * 获取所有知识库。
     *
     * @return 返回知识库列表。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.getAllRepos")
    List<KRepoDto> getAllRepos();

    /**
     * 根据ID获取知识库。
     *
     * @param id 表示id的 {@link Long}。
     * @return 返回对应的知识库记录
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.getById")
    KRepoDto getById(Long id);

    /**
     * 创建知识库。
     *
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.create")
    void create(KRepoDto kRepoDto);

    /**
     * 删除知识库。
     *
     * @param id 表示知识库ID的 {@link Long}。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.delete")
    void delete(Long id);

    /**
     * 更新知识库
     *
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.KRepoService.update")
    void update(KRepoDto kRepoDto);
}
