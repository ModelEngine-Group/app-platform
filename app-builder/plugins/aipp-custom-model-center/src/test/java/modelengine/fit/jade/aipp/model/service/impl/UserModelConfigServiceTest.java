/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.jade.aipp.model.dto.UserModelDetailDto;
import modelengine.fit.jade.aipp.model.enums.ModelType;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.po.UserModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link UserModelConfigService} 的单元测试。
 *
 * @author 李智超
 * @since 2025-04-016
 */
@DisplayName("测试 UserModelConfigService")
@ExtendWith(MockitoExtension.class)
public class UserModelConfigServiceTest {
    private UserModelConfigService userModelConfigService;

    @Mock
    private UserModelRepo userModelRepo;

    @BeforeEach
    void setUp() {
        this.userModelConfigService = new UserModelConfigService(userModelRepo);
    }

    @Test
    @DisplayName("当用户拥有模型时，返回用户模型列表")
    void shouldReturnUserModelListWhenUserHasModels() {
        String userId = "user1";
        String modelId = "m1";

        UserModelPo userModelPo = UserModelPo.builder()
                .userId(userId)
                .modelId(modelId)
                .isDefault(1)
                .createdAt(LocalDateTime.now())
                .build();

        ModelPo modelPo = ModelPo.builder().modelId(modelId).name("gpt").baseUrl("http://testUrl").build();

        Mockito.when(userModelRepo.listUserModelsByUserId(userId)).thenReturn(Collections.singletonList(userModelPo));
        Mockito.when(userModelRepo.listModels(Collections.singletonList(modelId)))
                .thenReturn(Collections.singletonList(modelPo));

        List<UserModelDetailDto> result = userModelConfigService.getUserModelList(userId);
        assertEquals(1, result.size());
        assertEquals("gpt", result.get(0).getModelName());
    }

    @Test
    @DisplayName("成功添加用户模型")
    void shouldAddUserModelSuccessfully() {
        String userId = "user1";
        String apiKey = "key";
        String modelName = "gpt";
        String baseUrl = "http://testUrl";

        Mockito.when(userModelRepo.hasDefaultModel(userId, null)).thenReturn(Boolean.TRUE);

        String result = userModelConfigService.addUserModel(userId, apiKey, modelName,
                baseUrl, ModelType.CHAT_COMPLETIONS.value());
        assertEquals("添加模型成功。", result);
        Mockito.verify(userModelRepo, Mockito.times(1)).insertModel(ArgumentMatchers.any(ModelPo.class));
        Mockito.verify(userModelRepo, Mockito.times(1)).insertUserModel(ArgumentMatchers.any(UserModelPo.class));
    }

    @Test
    @DisplayName("删除非默认模型时，执行删除操作")
    void shouldDeleteModelWhenItIsNotDefault() {
        String userId = "user1";
        String modelId = "m1";

        UserModelPo userModelPo = UserModelPo.builder()
                .userId(userId)
                .modelId(modelId)
                .isDefault(0)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userModelRepo.listUserModelsByUserId(userId)).thenReturn(Collections.singletonList(userModelPo));

        String result = userModelConfigService.deleteUserModel(userId, modelId);
        assertEquals("删除模型成功。", result);
        Mockito.verify(userModelRepo).deleteByModelId(modelId);
    }

    @Test
    @DisplayName("成功切换默认模型")
    void shouldSwitchDefaultModel() {
        String userId = "user1";
        String modelId = "m1";

        Mockito.when(userModelRepo.switchDefaultUserModel(userId, modelId)).thenReturn(1);
        Mockito.when(userModelRepo.getModel(modelId)).thenReturn(ModelPo.builder().name("gpt").build());

        String result = userModelConfigService.switchDefaultModel(userId, modelId);
        assertEquals("已切换gpt为默认模型。", result);
    }
}
