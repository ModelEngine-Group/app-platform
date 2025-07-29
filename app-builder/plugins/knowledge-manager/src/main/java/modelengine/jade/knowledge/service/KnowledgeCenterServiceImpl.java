/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import modelengine.fel.tool.annotation.Attribute;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.security.Decryptor;
import modelengine.fit.security.Encryptor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LongUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.knowledge.code.KnowledgeManagerRetCode;
import modelengine.jade.knowledge.condition.KnowledgeConfigQueryCondition;
import modelengine.jade.knowledge.config.KnowledgeConfig;
import modelengine.jade.knowledge.dto.KnowledgeConfigDto;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.knowledge.exception.KnowledgeException;
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
    private final Encryptor encryptor;
    private final Decryptor decryptor;

    /**
     * 构造方法。
     *
     * @param knowledgeConfig 表示知识库集参数的 {@link KnowledgeConfig}。
     * @param knowledgeCenterRepo 表示用于访问用户知识库配置数据的仓储接口的 {@link KnowledgeCenterRepo}。
     */
    public KnowledgeCenterServiceImpl(KnowledgeConfig knowledgeConfig, KnowledgeCenterRepo knowledgeCenterRepo,
            Encryptor encryptor, Decryptor decryptor) {
        this.knowledgeConfig = knowledgeConfig;
        this.knowledgeCenterRepo = knowledgeCenterRepo;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "添加知识库配置", description = "增加用户的知识库配置信息",
            extensions = {@Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")})
    @Property(description = "增加用户的知识库配置信息")
    public void add(KnowledgeConfigDto knowledgeConfigDto) {
        log.info("Start add user knowledge config. [userId={}]", knowledgeConfigDto.getUserId());
        this.isConfigUnique(knowledgeConfigDto);
        List<KnowledgeConfigPo> result =
                this.knowledgeCenterRepo.listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition.builder()
                        .userId(knowledgeConfigDto.getUserId())
                        .groupId(knowledgeConfigDto.getGroupId())
                        .build());
        if (result.stream().noneMatch(config -> config.getIsDefault() == 1)) {
            knowledgeConfigDto.setIsDefault(true);
        } else if (knowledgeConfigDto.getIsDefault()) {
            // 需要保证一个知识库平台只能有一个默认使用的API key
            KnowledgeConfigQueryCondition condition = KnowledgeConfigQueryCondition.builder()
                    .userId(knowledgeConfigDto.getUserId())
                    .groupId(knowledgeConfigDto.getGroupId())
                    .build();
            this.knowledgeCenterRepo.updateOthersIsDefaultFalse(condition);
        }
        knowledgeConfigDto.setKnowledgeConfigId(Entities.generateId());
        this.knowledgeCenterRepo.insertKnowledgeConfig(this.getKnowledgeConfigPo(knowledgeConfigDto));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "修改知识库配置", description = "修改用户的知识库配置信息",
            extensions = {@Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")})
    @Property(description = "修改用户的知识库配置信息")
    public void edit(KnowledgeConfigDto knowledgeConfigDto) {
        log.info("Start edit user knowledge config. [userId={}]", knowledgeConfigDto.getUserId());
        if (!this.isUpdateValidate(knowledgeConfigDto)) {
            log.error("Edit user knowledge config failed. [id={}, groupId={}, userId={}]",
                    knowledgeConfigDto.getId(),
                    knowledgeConfigDto.getGroupId(),
                    knowledgeConfigDto.getUserId());
            return;
        }
        this.isConfigUnique(knowledgeConfigDto);
        List<KnowledgeConfigPo> result =
                this.knowledgeCenterRepo.listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition.builder()
                        .userId(knowledgeConfigDto.getUserId())
                        .groupId(knowledgeConfigDto.getGroupId())
                        .build());
        if (result.size() == 1 && !knowledgeConfigDto.getIsDefault()) {
            throw new KnowledgeException(KnowledgeManagerRetCode.SHOULD_HAS_AT_LEAST_ONE_DEFAULT);
        }
        this.knowledgeCenterRepo.updateKnowledgeConfig(this.getKnowledgeConfigPo(knowledgeConfigDto));
        KnowledgeConfigQueryCondition condition = KnowledgeConfigQueryCondition.builder()
                .id(knowledgeConfigDto.getId())
                .userId(knowledgeConfigDto.getUserId())
                .groupId(knowledgeConfigDto.getGroupId())
                .build();
        if (knowledgeConfigDto.getIsDefault()) {
            this.knowledgeCenterRepo.updateOthersIsDefaultFalse(condition);
        } else {
            this.knowledgeCenterRepo.updateNewestIsDefaultTrue(condition);
        }
    }

    private boolean isUpdateValidate(KnowledgeConfigDto knowledgeConfigDto) {
        return LongUtils.between(knowledgeConfigDto.getId(), 1, Long.MAX_VALUE) && StringUtils.isNotBlank(
                knowledgeConfigDto.getUserId()) && StringUtils.isNotBlank(knowledgeConfigDto.getGroupId());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "删除知识库配置", description = "删除用户的知识库配置信息",
            extensions = {@Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")})
    @Property(description = "删除用户的知识库配置信息")
    public void delete(Long id) {
        log.info("Start delete user knowledge config. [id={}]", id);
        List<KnowledgeConfigPo> configPoList =
                this.knowledgeCenterRepo.listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition.builder()
                        .id(id)
                        .build());
        this.knowledgeCenterRepo.deleteKnowledgeConfigById(id);
        if (CollectionUtils.isEmpty(configPoList)) {
            return;
        }
        KnowledgeConfigPo configPo = configPoList.get(0);
        KnowledgeConfigDto knowledgeConfigDto = this.getKnowledgeConfigDto(configPo);
        if (knowledgeConfigDto.getIsDefault()) {
            KnowledgeConfigQueryCondition condition = KnowledgeConfigQueryCondition.builder()
                    .userId(knowledgeConfigDto.getUserId())
                    .groupId(knowledgeConfigDto.getGroupId())
                    .build();
            this.knowledgeCenterRepo.updateNewestIsDefaultTrue(condition);
        }
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "查询知识库配置", description = "查询用户的知识库配置信息",
            extensions = {@Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")})
    @Property(description = "查询用户的知识库配置信息")
    public List<KnowledgeConfigDto> list(String userId) {
        log.info("Start get user knowledge configs. [userId={}]", userId);
        return this.knowledgeCenterRepo.listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition.builder()
                .userId(userId)
                .build()).stream().map(this::getKnowledgeConfigDto).toList();
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "查询知识库集列表", description = "获取支持使用的知识库集列表",
            extensions = {@Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "KNOWLEDGE")})
    @Property(description = "获取支持使用的知识库集列表")
    public List<KnowledgeDto> getSupportKnowledges(String userId) {
        return this.knowledgeConfig.getSupportList();
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public String getApiKey(String knowledgeConfigId, String defaultValue) {
        if (StringUtils.isEmpty(knowledgeConfigId)) {
            return defaultValue;
        }
        KnowledgeConfigQueryCondition cond =
                KnowledgeConfigQueryCondition.builder().knowledgeConfigId(knowledgeConfigId).build();
        List<KnowledgeConfigPo> result = this.knowledgeCenterRepo.listKnowledgeConfigByCondition(cond);
        if (result.isEmpty()) {
            return defaultValue;
        }
        this.validateConfigNum(result);
        return this.decryptor.decrypt(result.get(0).getApiKey());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public String getKnowledgeConfigId(String userId, String groupId) {
        KnowledgeConfigQueryCondition cond =
                KnowledgeConfigQueryCondition.builder().userId(userId).groupId(groupId).isDefault(1).build();
        List<KnowledgeConfigPo> result = this.knowledgeCenterRepo.listKnowledgeConfigByCondition(cond);
        if (result.isEmpty()) {
            return "";
        }
        this.validateConfigNum(result);
        return result.get(0).getKnowledgeConfigId();
    }

    private void validateConfigNum(List<KnowledgeConfigPo> result) {
        if (result.size() > 1) {
            throw new KnowledgeException(KnowledgeManagerRetCode.QUERY_CONFIG_LENGTH_MORE_THAN_ONE,
                    result.get(0).getName());
        }
    }

    private void isConfigUnique(KnowledgeConfigDto knowledgeConfigDto) {
        List<KnowledgeConfigPo> result =
                this.knowledgeCenterRepo.listKnowledgeConfigByCondition(KnowledgeConfigQueryCondition.builder()
                        .userId(knowledgeConfigDto.getUserId())
                        .groupId(knowledgeConfigDto.getGroupId())
                        .apiKey(knowledgeConfigDto.getApiKey())
                        .build());
        if (!result.isEmpty()) {
            throw new KnowledgeException(KnowledgeManagerRetCode.CONFIG_IS_EXISTED);
        }
    }

    private KnowledgeConfigPo getKnowledgeConfigPo(KnowledgeConfigDto knowledgeConfigDto) {
        return KnowledgeConfigPo.builder()
                .id(knowledgeConfigDto.getId())
                .name(knowledgeConfigDto.getName())
                .userId(knowledgeConfigDto.getUserId())
                .groupId(knowledgeConfigDto.getGroupId())
                .apiKey(this.encryptor.encrypt(knowledgeConfigDto.getApiKey()))
                .isDefault(Boolean.compare(knowledgeConfigDto.getIsDefault(), false))
                .createdBy(knowledgeConfigDto.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedBy(knowledgeConfigDto.getUserId())
                .updatedAt(LocalDateTime.now())
                .knowledgeConfigId(knowledgeConfigDto.getKnowledgeConfigId())
                .build();
    }

    private KnowledgeConfigDto getKnowledgeConfigDto(KnowledgeConfigPo knowledgeConfigPo) {
        return KnowledgeConfigDto.builder()
                .id(knowledgeConfigPo.getId())
                .name(knowledgeConfigPo.getName())
                .groupId(knowledgeConfigPo.getGroupId())
                .userId(knowledgeConfigPo.getUserId())
                .apiKey(this.decryptor.decrypt(knowledgeConfigPo.getApiKey()))
                .isDefault(knowledgeConfigPo.getIsDefault() == 1)
                .build();
    }
}
