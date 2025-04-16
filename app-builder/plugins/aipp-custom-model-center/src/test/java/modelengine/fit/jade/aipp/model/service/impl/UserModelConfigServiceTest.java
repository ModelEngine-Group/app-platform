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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserModelConfigServiceTest {

    private UserModelConfigService userModelConfigService;

    @Mock
    private UserModelRepo userModelRepo;

    @BeforeEach
    void setUp() {
        this.userModelConfigService = new UserModelConfigService(userModelRepo);
    }

    @Test
    void shouldReturnUserModelListWhenUserHasModels() {
        String userId = "user1";
        String modelId = "m1";

        UserModelPo userModelPo = UserModelPo.builder()
                .userId(userId)
                .modelId(modelId)
                .isDefault(1)
                .createdAt(LocalDateTime.now())
                .build();

        ModelPo modelPo = ModelPo.builder()
                .modelId(modelId)
                .name("gpt")
                .baseUrl("http://xxx")
                .build();

        Mockito.when(userModelRepo.listUserModels(userId))
                .thenReturn(Collections.singletonList(userModelPo));
        Mockito.when(userModelRepo.listModels(Collections.singletonList(modelId)))
                .thenReturn(Collections.singletonList(modelPo));

        List<UserModelDetailDto> result = userModelConfigService.getUserModelList(userId);
        assertEquals(1, result.size());
        assertEquals("gpt", result.get(0).getModelName());
    }

    @Test
    void shouldAddUserModelSuccessfully() {
        String userId = "user1";
        String apiKey = "key";
        String modelName = "gpt";
        String baseUrl = "http://xxx";

        Mockito.when(userModelRepo.hasDefaultModel(userId)).thenReturn(1);

        String result = userModelConfigService.addUserModel(userId, apiKey, modelName, baseUrl);
        assertEquals("添加模型成功。", result);
        Mockito.verify(userModelRepo, Mockito.times(1)).insertModel(ArgumentMatchers.any(ModelPo.class));
        Mockito.verify(userModelRepo, Mockito.times(1)).insertUserModel(ArgumentMatchers.any(UserModelPo.class));
    }

    @Test
    void shouldDeleteModelWhenItIsNotDefault() {
        String userId = "user1";
        String modelId = "m1";

        UserModelPo userModelPo = UserModelPo.builder()
                .userId(userId)
                .modelId(modelId)
                .isDefault(0)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userModelRepo.listUserModels(userId)).thenReturn(Collections.singletonList(userModelPo));

        String result = userModelConfigService.deleteUserModel(userId, modelId);
        assertEquals("删除模型成功。", result);
        Mockito.verify(userModelRepo).deleteByModelId(modelId);
    }

    @Test
    void shouldSwitchDefaultModel() {
        String userId = "user1";
        String modelId = "m1";

        Mockito.when(userModelRepo.switchDefaultForUser(userId, modelId)).thenReturn(1);
        Mockito.when(userModelRepo.getModel(modelId))
                .thenReturn(ModelPo.builder().name("gpt").build());

        String result = userModelConfigService.switchDefaultModel(userId, modelId);
        assertEquals("已切换gpt为默认模型。", result);
    }
}
