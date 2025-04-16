/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.UserModelDetailDto;

import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.po.UserModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fit.jade.aipp.model.service.UserModelConfig;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.jade.carver.tool.annotation.Attribute;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表示用户模型信息用于插件的持久化层的接口的实现。
 *
 * @author 李智超
 * @since 2025-04-09
 */
@Component
@Group(name = "User_Model_Config_Service")
public class UserModelConfigService implements UserModelConfig {
    private static final Logger log = Logger.get(UserModelConfig.class);
    private static final String FITABLE_ID = "aipp.model.service.impl";
    public static final String DEFAULT_MODEL_TYPE = "chat_completions";
    private final UserModelRepo userModelRepo;

    /**
     * 构造方法。
     *
     * @param userModelRepo 用于访问用户模型数据的仓库接口实例。
     */
    public UserModelConfigService(UserModelRepo userModelRepo) {
        this.userModelRepo = userModelRepo;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "获取用户模型列表", description = "根据用户标识来查询该用户可用的模型列表", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "返回该用户可用的模型列表")
    public List<UserModelDetailDto> getUserModelList(String userId) {
        log.info("start get model list for {}.", userId);
        List<UserModelPo> userModelPos = this.userModelRepo.listUserModels(userId);
        if (CollectionUtils.isEmpty(userModelPos)) {
            log.warn("No user model records found for userId={}.", userId);
            return Collections.emptyList();
        }

        List<String> modelIds =
                userModelPos.stream().map(UserModelPo::getModelId).distinct().collect(Collectors.toList());
        List<ModelPo> modelPos = this.userModelRepo.listModels(modelIds);

        // 构建 modelId → ModelPo 映射
        Map<String, ModelPo> modelMap = modelPos.stream()
                .map(model -> Map.entry(model.getModelId(), model))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        return userModelPos.stream().map(userModel -> {
            ModelPo model = modelMap.get(userModel.getModelId());
            return UserModelDetailDto.builder()
                    .createdAt(userModel.getCreatedAt())
                    .modelId(userModel.getModelId())
                    .userId(userModel.getUserId())
                    .modelName(model != null ? model.getName() : null)
                    .baseUrl(model != null ? model.getBaseUrl() : null)
                    .isDefault(userModel.getIsDefault())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "添加模型", description = "为用户添加可用的模型信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "为用户添加可用的模型信息")
    public String addUserModel(String userId, String apiKey, String modelName, String baseUrl) {
        log.info("start add user model for {}.", userId);
        String modelId = UUID.randomUUID().toString().replace("-", "");
        int isDefault = this.userModelRepo.hasDefaultModel(userId);

        ModelPo modelPo = ModelPo.builder()
                .modelId(modelId)
                .name(modelName)
                .tag(modelId)
                .baseUrl(baseUrl)
                .type(DEFAULT_MODEL_TYPE)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
        this.userModelRepo.insertModel(modelPo);

        UserModelPo userModelPo = UserModelPo.builder()
                .userId(userId)
                .modelId(modelId)
                .apiKey(apiKey)
                .isDefault(isDefault)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
        this.userModelRepo.insertUserModel(userModelPo);
        return "添加模型成功。";
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "删除模型", description = "删除用户绑定的模型信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "删除用户绑定的模型信息")
    public String deleteUserModel(String userId, String modelId) {
        log.info("start delete user model for {}.", userId);
        List<UserModelPo> userModels = this.userModelRepo.listUserModels(userId);
        if (CollectionUtils.isEmpty(userModels)) {
            return "删除模型失败，当前用户没有任何模型记录。";
        }

        UserModelPo target = userModels.stream()
                .filter(m -> Objects.equals(m.getModelId(), modelId))
                .findFirst()
                .orElse(null);
        if (target == null) {
            return "删除模型失败，该模型不属于当前用户。";
        }
        this.userModelRepo.deleteByModelId(modelId);
        // 如果删除的不是默认模型，直接返回
        if (target.getIsDefault() != 1) {
            return "删除模型成功。";
        }
        userModels.remove(target);
        // 如果没有默认模型，但还有其他记录，则设置最新创建的为默认
        if (CollectionUtils.isNotEmpty(userModels)) {
            UserModelPo latestUserModel = userModels.stream()
                    .filter(m -> m.getCreatedAt() != null)
                    .max(Comparator.comparing(UserModelPo::getCreatedAt))
                    .orElse(null);

            if (latestUserModel != null) {
                this.userModelRepo.switchDefaultForUser(userId, latestUserModel.getModelId());
                return String.format("删除默认模型成功，已将%s设为默认模型。",
                        this.userModelRepo.getModel(latestUserModel.getModelId()).getName());
            }
        }
        return "删除模型成功，当前无默认模型。";
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @ToolMethod(name = "切换默认模型", description = "将指定模型设置为用户的默认模型", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "MODEL")
    })
    @Property(description = "将指定模型设置为用户的默认模型")
    public String switchDefaultModel(String userId, String modelId) {
        log.info("start switch default model for {}.", userId);
        int rows = this.userModelRepo.switchDefaultForUser(userId, modelId);
        if (rows == 0) {
            return "未查到对应模型。";
        }
        return String.format("已切换%s为默认模型。", this.userModelRepo.getModel(modelId).getName());
    }
}