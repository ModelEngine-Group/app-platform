/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.service.impl;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.repository.UserModelRepo;
import modelengine.fit.jade.aipp.model.service.AippModelCenterExtension;
import modelengine.fit.jane.common.entity.OperationContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class CustomAippModelCenterTest {
    private CustomAippModelCenter customAippModelCenter;

    @Mock
    private UserModelRepo userModelRepo;

    @Mock
    private AippModelCenterExtension defaultModelCenter;

    @BeforeEach
    void setUp() {
        this.customAippModelCenter = new CustomAippModelCenter(this.userModelRepo, this.defaultModelCenter);
    }

    @Test
    void shouldGetResultWhenFetchModelListGivenUserModelRepoHasData() {
        String userId = "user1";
        ModelPo modelPo = ModelPo.builder()
                .modelId("id")
                .name("gpt")
                .baseUrl("http://testUrl")
                .type("type")
                .tag("tag1")
                .build();
        Mockito.when(this.userModelRepo.listModelsByUserId(userId)).thenReturn(Collections.singletonList(modelPo));
        OperationContext context = new OperationContext();
        context.setOperator(userId);

        ModelListDto modelList = this.customAippModelCenter.fetchModelList("type", "scene", context);

        Assertions.assertNotNull(modelList);
        Assertions.assertEquals(1, modelList.getTotal());
        Assertions.assertEquals(1, modelList.getModels().size());
        ModelAccessInfo targetModelAccessInfo = modelList.getModels().get(0);
        Assertions.assertEquals(modelPo.getName(), targetModelAccessInfo.getServiceName());
        Assertions.assertEquals("tag1,user1", targetModelAccessInfo.getTag());
        Mockito.verify(this.defaultModelCenter, Mockito.times(0))
                .fetchModelList(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldGetDefaultWhenFetchModelListGivenUserModelRepoNoData() {
        String userId = "user1";
        String type = "type";
        String scene = "scene";
        ModelPo modelPo = ModelPo.builder()
                .modelId("id")
                .name("gpt")
                .baseUrl("http://testUrl")
                .type(type)
                .tag("tag1")
                .build();
        ModelAccessInfo model1 = ModelAccessInfo.builder().serviceName("gpt").baseUrl("").tag("").build();
        Mockito.when(this.userModelRepo.listModelsByUserId(userId)).thenReturn(Collections.emptyList());
        ModelListDto expectModelList = ModelListDto.builder()
                .models(Collections.singletonList(model1))
                .total(1)
                .build();
        OperationContext context = new OperationContext();
        context.setOperator(userId);
        Mockito.when(this.defaultModelCenter.fetchModelList(type, scene, context)).thenReturn(expectModelList);

        ModelListDto modelList = this.customAippModelCenter.fetchModelList(type, scene, context);

        Assertions.assertEquals(expectModelList, modelList);
        Mockito.verify(this.defaultModelCenter, Mockito.times(1))
                .fetchModelList(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldGetResultWhenGetModelAccessInfoGivenUserModelRepoHasData() {
        String userId = "user1";
        ModelPo modelPo = ModelPo.builder()
                .modelId("id")
                .name("gpt")
                .baseUrl("http://testUrl")
                .type("type")
                .tag("tag1")
                .build();
        ModelAccessPo modelAccessPo = ModelAccessPo.builder().modelPO(modelPo).apiKey("key").build();
        Mockito.when(this.userModelRepo.getModelAccessInfo(userId, modelPo.getTag(), modelPo.getName()))
                .thenReturn(modelAccessPo);
        OperationContext context = new OperationContext();
        context.setOperator(userId);

        ModelAccessInfo modelAccessInfo = this.customAippModelCenter.getModelAccessInfo("tag1,user1", modelPo.getName(),
                context);

        Assertions.assertNotNull(modelAccessInfo);
        Assertions.assertEquals(modelPo.getName(), modelAccessInfo.getServiceName());
        Assertions.assertEquals("tag1,user1", modelAccessInfo.getTag());
        Assertions.assertEquals(modelPo.getBaseUrl(), modelAccessInfo.getBaseUrl());
        Assertions.assertEquals(modelAccessPo.getApiKey(), modelAccessInfo.getAccessKey());
        Mockito.verify(this.defaultModelCenter, Mockito.times(0))
                .getModelAccessInfo(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldGetDefaultWhenGetModelAccessInfoGivenUserModelRepoNoData() {
        String userId = "user1";
        String type = "type";
        ModelPo modelPo = ModelPo.builder()
                .modelId("id")
                .name("gpt")
                .baseUrl("http://testUrl")
                .type(type)
                .tag("tag1")
                .build();
        ModelAccessInfo expectModel = ModelAccessInfo.builder().serviceName("gpt").baseUrl("").tag("").build();
        OperationContext context = new OperationContext();
        context.setOperator(userId);
        Mockito.when(this.defaultModelCenter.getModelAccessInfo(modelPo.getTag(), modelPo.getName(), context))
                .thenReturn(expectModel);

        ModelAccessInfo modelAccessInfo = this.customAippModelCenter.getModelAccessInfo(modelPo.getTag(),
                modelPo.getName(), context);

        Assertions.assertEquals(expectModel, modelAccessInfo);
        Mockito.verify(this.defaultModelCenter, Mockito.times(1))
                .getModelAccessInfo(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void shouldGetResultWhenGetDefaultModelGivenUserModelRepoHasData() {
        String userId = "user1";
        ModelPo modelPo = ModelPo.builder()
                .modelId("id")
                .name("gpt")
                .baseUrl("http://testUrl")
                .type("type")
                .tag("tag1")
                .build();
        Mockito.when(this.userModelRepo.getDefaultModel(userId)).thenReturn(modelPo);
        OperationContext context = new OperationContext();
        context.setOperator(userId);

        ModelAccessInfo modelAccessInfo = this.customAippModelCenter.getDefaultModel(modelPo.getType(), context);

        Assertions.assertNotNull(modelAccessInfo);
        Assertions.assertEquals(modelPo.getName(), modelAccessInfo.getServiceName());
        Assertions.assertEquals("tag1,user1", modelAccessInfo.getTag());
        Assertions.assertEquals(modelPo.getBaseUrl(), modelAccessInfo.getBaseUrl());
        Mockito.verify(this.defaultModelCenter, Mockito.times(0)).getDefaultModel(Mockito.any(), Mockito.any());
    }

    @Test
    void shouldGetDefaultWhenGetDefaultModelGivenUserModelRepoNoData() {
        String userId = "user1";
        String type = "type";
        ModelPo modelPo = ModelPo.builder()
                .modelId("id")
                .name("gpt")
                .baseUrl("http://testUrl")
                .type(type)
                .tag("tag1")
                .build();
        ModelAccessInfo expectModel = ModelAccessInfo.builder().serviceName("gpt").baseUrl("").tag("").build();
        OperationContext context = new OperationContext();
        context.setOperator(userId);
        Mockito.when(this.defaultModelCenter.getDefaultModel(modelPo.getType(), context)).thenReturn(expectModel);

        ModelAccessInfo modelAccessInfo = this.customAippModelCenter.getDefaultModel(modelPo.getType(), context);

        Assertions.assertEquals(expectModel, modelAccessInfo);
        Mockito.verify(this.defaultModelCenter, Mockito.times(1)).getDefaultModel(Mockito.any(), Mockito.any());
    }
}