/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.carver.tool.service.ToolGroupService;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.knowledge.KnowledgeI18nInfo;
import modelengine.jade.knowledge.KnowledgeI18nService;
import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.KnowledgeRepo;
import modelengine.jade.knowledge.KnowledgeRepoService;
import modelengine.jade.knowledge.ListRepoQueryParam;
import modelengine.jade.knowledge.SchemaItem;
import modelengine.jade.knowledge.config.KnowledgeConfig;
import modelengine.jade.knowledge.controller.vo.KnowledgePropertyVo;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.router.KnowledgeServiceRouter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 知识库服务接口。
 *
 * @author 邱晓霞
 * @since 2024-09-29
 */
@Component
@RequestMapping(path = {"/knowledge-manager"})
public class KnowledgeController {

    private final KnowledgeI18nService knowledgeI18nService;

    private final ToolGroupService toolGroupService;

    private final KnowledgeConfig knowledgeConfig;

    private final KnowledgeServiceRouter knowledgeServiceRouter;

    /**
     * 表示 {@link KnowledgeController} 的构造器。
     *
     * @param knowledgeI18nService 表示获取知识库国际化服务的 {@link KnowledgeI18nService}。
     * @param toolGroupService 工具组服务
     * @param knowledgeConfig 表示知识库集配的 {@link KnowledgeConfig}。
     * @param knowledgeServiceRouter 表示知识库服务路由处理类的 {@link KnowledgeServiceRouter}。
     */
    public KnowledgeController(KnowledgeI18nService knowledgeI18nService, ToolGroupService toolGroupService,
                               KnowledgeConfig knowledgeConfig, KnowledgeServiceRouter knowledgeServiceRouter) {
        this.knowledgeI18nService = knowledgeI18nService;
        this.toolGroupService = toolGroupService;
        this.knowledgeConfig = knowledgeConfig;
        this.knowledgeServiceRouter = knowledgeServiceRouter;
    }

    /**
     * 查询知识库列表。
     *
     * @param groupId 表示调用的知识库服务的唯一标识的 {@link String}。
     * @param param 表示查询参数的 {@link ListRepoQueryParam}。
     * @return 表示知识库分页结果的 {@link PageVo}{@code <}{@link KnowledgeRepo}{@code >}。
     */
    @GetMapping("/list/repos")
    public PageVo<KnowledgeRepo> getRepoList(@RequestParam(value = "groupId", required = false) String groupId,
            @RequestBean @Validated ListRepoQueryParam param) {
        return this.knowledgeServiceRouter.getRouter(KnowledgeRepoService.class, KnowledgeRepoService.GENERICABLE_LIST_REPOS, groupId)
                .invoke(UserContextHolder.get().getName(), param);
    }

    /**
     * 查询知识库组标识列表。
     *
     * @return 表示知识库组标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @GetMapping("/list/groups")
    public List<KnowledgeDto> getRepoInfo() {
        return this.knowledgeConfig.getSupport();
    }

    /**
     * 查询知识库支持的检索参数信息。
     *
     * @param groupId 表示调用的知识库服务的唯一标识的 {@link String}。
     * @return 表示检索参数信息的 {@link KnowledgeProperty}。
     */
    @GetMapping("/properties")
    public KnowledgePropertyVo getProperty(@RequestParam(value = "groupId", required = false) String groupId) {
        KnowledgeProperty property =  this.knowledgeServiceRouter.getRouter(KnowledgeRepoService.class, KnowledgeRepoService.GENERICABLE_GET_PROPERTY, groupId)
                .invoke(UserContextHolder.get().getName());
        Set<String> enableIndexType = property.indexType().stream().map(SchemaItem::type).collect(Collectors.toSet());
        List<KnowledgeProperty.IndexInfo> disableIndexType = Arrays.stream(IndexType.values())
                .filter(type -> !enableIndexType.contains(type.value()))
                .map(type -> {
                    KnowledgeI18nInfo i18nInfo = this.knowledgeI18nService.localizeText(type);
                    return new KnowledgeProperty.IndexInfo(type, i18nInfo.getName(), i18nInfo.getDescription());
                })
                .collect(Collectors.toList());
        return new KnowledgePropertyVo(disableIndexType, property.indexType(), property.filterConfig(),
                property.rerankConfig());
    }
}