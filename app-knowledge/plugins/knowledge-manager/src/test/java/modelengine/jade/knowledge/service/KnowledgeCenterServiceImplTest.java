/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fit.security.Decryptor;
import modelengine.fit.security.Encryptor;
import modelengine.jade.knowledge.config.KnowledgeConfig;
import modelengine.jade.knowledge.dto.KnowledgeConfigDto;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.knowledge.exception.KnowledgeException;
import modelengine.jade.knowledge.po.KnowledgeConfigPo;
import modelengine.jade.knowledge.repository.KnowledgeCenterRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link KnowledgeCenterServiceImpl} 的单元测试。
 *
 * @author 陈潇文
 * @since 2025-04-29
 */
@DisplayName("测试 KnowledgeCenterServiceImpl")
@ExtendWith(MockitoExtension.class)
public class KnowledgeCenterServiceImplTest {
    private KnowledgeCenterServiceImpl knowledgeCenterService;

    @Mock
    private KnowledgeConfig knowledgeConfig;

    @Mock
    private KnowledgeCenterRepo knowledgeCenterRepo;

    @Mock
    private Encryptor encryptor;

    @Mock
    private Decryptor decryptor;

    @BeforeEach
    void setUp() {
        this.knowledgeCenterService = new KnowledgeCenterServiceImpl(knowledgeConfig, knowledgeCenterRepo, encryptor, decryptor);
    }

    @Test
    @DisplayName("成功添加知识库配置")
    void shouldAddKnowledgeConfigSuccessfully() {
        String userId = "user1";
        String groupId = "group1";
        String apiKey = "key";
        String name = "test config";

        KnowledgeConfigDto configDto = KnowledgeConfigDto.builder()
                .userId(userId)
                .groupId(groupId)
                .apiKey(apiKey)
                .name(name)
                .isDefault(true)
                .build();

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        knowledgeCenterService.add(configDto);
        Mockito.verify(knowledgeCenterRepo).insertKnowledgeConfig(ArgumentMatchers.any(KnowledgeConfigPo.class));
    }

    @Test
    @DisplayName("成功修改知识库配置")
    void shouldEditKnowledgeConfigSuccessfully() {
        String userId = "user1";
        String groupId = "group1";
        String apiKey = "key";
        String name = "test config";
        Long id = 1L;

        KnowledgeConfigDto configDto = KnowledgeConfigDto.builder()
                .id(id)
                .userId(userId)
                .groupId(groupId)
                .apiKey(apiKey)
                .name(name)
                .isDefault(true)
                .build();

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        knowledgeCenterService.edit(configDto);
        Mockito.verify(knowledgeCenterRepo).updateKnowledgeConfig(ArgumentMatchers.any(KnowledgeConfigPo.class));
    }

    @Test
    @DisplayName("成功删除知识库配置")
    void shouldDeleteKnowledgeConfigSuccessfully() {
        Long id = 1L;
        String userId = "user1";
        String groupId = "group1";

        KnowledgeConfigPo configPo = KnowledgeConfigPo.builder()
                .id(id)
                .userId(userId)
                .groupId(groupId)
                .isDefault(1)
                .build();

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(configPo));

        knowledgeCenterService.delete(id);
        Mockito.verify(knowledgeCenterRepo).deleteKnowledgeConfigById(id);
    }

    @Test
    @DisplayName("成功查询知识库配置列表")
    void shouldListKnowledgeConfigsSuccessfully() {
        String userId = "user1";
        String groupId = "group1";
        String apiKey = "key";
        String name = "test config";

        KnowledgeConfigPo configPo = KnowledgeConfigPo.builder()
                .userId(userId)
                .groupId(groupId)
                .apiKey(apiKey)
                .name(name)
                .isDefault(1)
                .build();

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(configPo));
        Mockito.when(encryptor.encrypt(apiKey)).thenReturn("encrypted_key");

        List<KnowledgeConfigDto> result = knowledgeCenterService.list(userId);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).getName());
    }

    @Test
    @DisplayName("成功获取支持的知识库列表")
    void shouldGetSupportKnowledgesSuccessfully() {
        String userId = "user1";
        List<KnowledgeDto> supportList = Collections.singletonList(new KnowledgeDto());
        Mockito.when(knowledgeConfig.getSupport()).thenReturn(supportList);

        List<KnowledgeDto> result = knowledgeCenterService.getSupportKnowledges(userId);
        assertEquals(supportList, result);
    }

    @Test
    @DisplayName("成功获取API Key")
    void shouldGetApiKeySuccessfully() {
        String userId = "user1";
        String groupId = "group1";
        String apiKey = "key";
        String defaultValue = "default";

        KnowledgeConfigPo configPo = KnowledgeConfigPo.builder()
                .userId(userId)
                .groupId(groupId)
                .apiKey(apiKey)
                .isDefault(1)
                .build();

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(configPo));

        String result = knowledgeCenterService.getApiKey(userId, groupId, defaultValue);
        assertEquals(apiKey, result);
    }

    @Test
    @DisplayName("当没有找到API Key时返回默认值")
    void shouldReturnDefaultValueWhenNoApiKeyFound() {
        String userId = "user1";
        String groupId = "group1";
        String defaultValue = "default";

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        String result = knowledgeCenterService.getApiKey(userId, groupId, defaultValue);
        assertEquals(defaultValue, result);
    }

    @Test
    @DisplayName("当配置不唯一时抛出异常")
    void shouldThrowExceptionWhenConfigNotUnique() {
        String userId = "user1";
        String groupId = "group1";
        String apiKey = "key";
        String name = "test config";

        KnowledgeConfigDto configDto = KnowledgeConfigDto.builder()
                .userId(userId)
                .groupId(groupId)
                .apiKey(apiKey)
                .name(name)
                .isDefault(true)
                .build();

        KnowledgeConfigPo existingConfig = KnowledgeConfigPo.builder()
                .userId(userId)
                .groupId(groupId)
                .apiKey(apiKey)
                .name(name)
                .isDefault(1)
                .build();

        Mockito.when(knowledgeCenterRepo.listKnowledgeConfigByCondition(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(existingConfig));

        assertThrows(KnowledgeException.class, () -> knowledgeCenterService.add(configDto));
    }
} 