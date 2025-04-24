/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.jade.carver.tool.annotation.Attribute;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.knowledge.config.KnowledgeConfig;
import modelengine.jade.knowledge.dto.KnowledgeConfigDto;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.knowledge.po.KnowledgeConfigPo;
import modelengine.jade.knowledge.repository.KnowledgeCenterRepo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表示用户知识库配置信息的接口的实现
 *
 * @author 陈潇文
 * @since 2024-04-22
 */
@Component
@Group(name = "Knowledge_Center_Service_Impl")
public class KnowledgeCenterServiceImpl implements KnowledgeCenterService {
    private static final Logger log = Logger.get(KnowledgeCenterServiceImpl.class);
    private static final String FITABLE_ID = "knowledge.config.service.impl";
    private final KnowledgeConfig knowledgeConfig;
    private final KnowledgeCenterRepo knowledgeCenterRepo;

    /**
     * 构造方法。
     *
     * @param knowledgeConfig  表示知识库集参数的 {@link KnowledgeConfig}。
     * @param knowledgeCenterRepo 表示用于访问用户知识库配置数据的仓储接口的 {@link KnowledgeCenterRepo}。
     */
    public KnowledgeCenterServiceImpl(KnowledgeConfig knowledgeConfig,
                                      KnowledgeCenterRepo knowledgeCenterRepo) {
        this.knowledgeConfig = knowledgeConfig;
        this.knowledgeCenterRepo = knowledgeCenterRepo;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "添加知识库配置", description = "增加用户的知识库配置信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")
    })
    @Property(description = "增加用户的知识库配置信息")
    public void add(KnowledgeConfigDto knowledgeConfigDto) {
        log.info("start add user knowledge config for {}.", knowledgeConfigDto.getUserId());
        // todo 校验是否有不符合唯一性index的 有就抛异常
        this.knowledgeCenterRepo.insertKnowledgeConfig(this.getKnowledgeConfigPo(knowledgeConfigDto));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "修改知识库配置", description = "修改用户的知识库配置信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")
    })
    @Property(description = "修改用户的知识库配置信息")
    public void edit(KnowledgeConfigDto knowledgeConfigDto) {
        log.info("start edit user knowledge config for {}.", knowledgeConfigDto.getUserId());
        this.knowledgeCenterRepo.updateKnowledgeConfig(this.getKnowledgeConfigPo(knowledgeConfigDto));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "删除知识库配置", description = "删除用户的知识库配置信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")
    })
    @Property(description = "删除用户的知识库配置信息")
    public void delete(int id) {
        log.info("start delete user knowledge config, id: {}.", id);
        this.knowledgeCenterRepo.deleteKnowledgeConfigById(id);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "查询知识库配置", description = "查询用户的知识库配置信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")
    })
    @Property(description = "查询用户的知识库配置信息")
    public List<KnowledgeConfigDto> list(String userId) {
        log.info("start get user knowledge configs for {}.", userId);
        return this.knowledgeCenterRepo.listKnowledgeConfigByUserId(userId)
                .stream()
                .map(this::getKnowledgeConfigDto)
                .toList();
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "查询知识库集列表", description = "获取支持使用的知识库集列表", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")
    })
    @Property(description = "获取支持使用的知识库集列表")
    public List<KnowledgeDto> getSupportKnowledges(String userId) {
        return this.knowledgeConfig.getSupport();
    }

    private KnowledgeConfigPo getKnowledgeConfigPo(KnowledgeConfigDto knowledgeConfigDto) {
        return KnowledgeConfigPo.builder()
                .id(knowledgeConfigDto.getId())
                .name(knowledgeConfigDto.getName())
                .userId(knowledgeConfigDto.getUserId())
                .groupId(knowledgeConfigDto.getGroupId())
                .apiKey(knowledgeConfigDto.getApiKey())
                .isDefault(Boolean.compare(knowledgeConfigDto.getIsDefault(), false))
                .createdBy(knowledgeConfigDto.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedBy(knowledgeConfigDto.getUserId())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private KnowledgeConfigDto getKnowledgeConfigDto(KnowledgeConfigPo knowledgeConfigPo) {
        return KnowledgeConfigDto.builder()
                .id(knowledgeConfigPo.getId())
                .name(knowledgeConfigPo.getName())
                .groupId(knowledgeConfigPo.getGroupId())
                .userId(knowledgeConfigPo.getUserId())
                .apiKey(knowledgeConfigPo.getApiKey())
                .isDefault(knowledgeConfigPo.getIsDefault() == 1)
                .build();
    }
}
