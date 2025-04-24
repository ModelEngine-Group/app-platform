/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;
import modelengine.jade.knowledge.dto.KnowledgeConfigDto;

import java.util.List;

/**
 * 表示用户知识库配置信息的接口
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
@Group(name = "Knowledge_Center_Service")
public interface KnowledgeCenterService {
    /**
     * 增加用户的知识库配置信息
     * @param knowledgeConfigDto 表示用户知识库配置dto的 {@link KnowledgeConfigDto}。
     */
    @ToolMethod(name = "add_user_knowledge_config", description = "增加用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.addUserKnowledgeConfig")
    void add(@Property(description = "知识库配置dto", required = true) KnowledgeConfigDto knowledgeConfigDto);

    /**
     * 修改用户的知识库配置信息
     * @param knowledgeConfigDto 表示用户知识库配置dto的 {@link KnowledgeConfigDto}。
     */
    @ToolMethod(name = "edit_user_knowledge_config", description = "修改用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.editUserKnowledgeConfig")
    void edit(@Property(description = "知识库配置dto", required = true) KnowledgeConfigDto knowledgeConfigDto);

    /**
     * 删除用户的知识库配置信息
     * @param id 表示知识库配置id的 {@link String}。
     */
    @ToolMethod(name = "delete_user_knowledge_config", description = "删除用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.deleteUserKnowledgeConfig")
    void delete(@Property(description = "知识库配置id", required = true) int id);

    /**
     * 查询用户的知识库配置信息
     * @param userId 表示用户id的 {@link String}。
     */
    @ToolMethod(name = "list_user_knowledge_config", description = "查询用户的知识库配置信息")
    @Genericable(id = "knowledge.center.service.listUserKnowledgeConfig")
    List<KnowledgeConfigDto> list(@Property(description = "用户id", required = true) String userId);
}
