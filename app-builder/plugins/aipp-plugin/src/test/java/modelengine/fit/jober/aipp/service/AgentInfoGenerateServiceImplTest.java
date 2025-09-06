/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.impl.AgentInfoGenerateServiceImpl;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.PluginToolService;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 智能体信息生成服务测试类。
 *
 * @author 兰宇晨。
 * @since 2024-12-09。
 */
@ExtendWith(MockitoExtension.class)
public class AgentInfoGenerateServiceImplTest {
    private AgentInfoGenerateService agentInfoGenerateService;

    @Mock
    private AippModelService aippModelService;

    @Mock
    private AippModelCenter aippModelCenter;

    @Mock
    private PluginToolService toolService;

    @Mock
    private LocaleService localeService;

    @Mock
    private AppBuilderAppRepository appRepository;

    @BeforeEach
    void beforeAll() {
        this.agentInfoGenerateService = new AgentInfoGenerateServiceImpl(this.aippModelService,
                this.aippModelCenter,
                this.toolService,
                this.localeService,
                this.appRepository);
    }

    @Test
    @DisplayName("测试自动生成智能体名称")
    void shouldOkWhenGenerateName() {
        ModelAccessInfo model = Mockito.mock(ModelAccessInfo.class);
        when(model.getServiceName()).thenReturn("llm_model");
        when(model.getTag()).thenReturn("llm_tag");
        when(this.aippModelCenter.getDefaultModel(any(), any())).thenReturn(model);
        when(aippModelService.chat(anyString(), anyString(), anyDouble(), anyString())).thenReturn("NAME");
        assertThat(this.agentInfoGenerateService.generateName("DESC", new OperationContext())).isEqualTo("NAME");
    }

    @Test
    @DisplayName("测试自动生成智能体开场白")
    void shouldOkWhenGenerateGreeting() {
        ModelAccessInfo model = Mockito.mock(ModelAccessInfo.class);
        when(model.getServiceName()).thenReturn("llm_model");
        when(model.getTag()).thenReturn("llm_tag");
        when(this.aippModelCenter.getDefaultModel(any(), any())).thenReturn(model);
        when(aippModelService.chat(anyString(), anyString(), anyDouble(), anyString())).thenReturn("GREETING");
        assertThat(this.agentInfoGenerateService.generateGreeting("DESC", null)).isEqualTo("GREETING");
    }

    @Test
    @DisplayName("测试自动生成智能体提示词")
    void shouldOkWhenGeneratePrompt() {
        ModelAccessInfo model = Mockito.mock(ModelAccessInfo.class);
        when(model.getServiceName()).thenReturn("llm_model");
        when(model.getTag()).thenReturn("llm_tag");
        when(this.aippModelCenter.getDefaultModel(any(), any())).thenReturn(model);
        when(aippModelService.chat(anyString(), anyString(), anyDouble(), anyString())).thenReturn("PROMPT");
        assertThat(this.agentInfoGenerateService.generatePrompt("DESC", null)).isEqualTo("PROMPT");
    }

    @Test
    @DisplayName("测试自动选择工具")
    void shouldOkWhenSelectTools() {
        ModelListDto dto = this.getModelListDto();

        List<PluginToolData> pluginToolDataList = new ArrayList<>(Arrays.asList(new PluginToolData() {{
            setUniqueName("UNIQUENAME1");
            setName("NAME1");
            setDescription("DESC1");
        }}, new PluginToolData() {{
            setUniqueName("UNIQUENAME2");
            setName("NAME2");
            setDescription("DESC2");
        }}, new PluginToolData() {{
            setUniqueName("UNIQUENAME3");
            setName("NAME3");
            setDescription("DESC3");
        }}));
        ListResult<PluginToolData> pluginToolDataListResult = ListResult.create(pluginToolDataList, 3);

        ModelAccessInfo model = Mockito.mock(ModelAccessInfo.class);
        when(model.getServiceName()).thenReturn("llm_model");
        when(model.getTag()).thenReturn("llm_tag");
        when(this.aippModelCenter.getDefaultModel(any(), any())).thenReturn(model);
        when(this.aippModelService.chat(anyString(), anyString(), anyDouble(), anyString())).thenReturn("[1,2]");
        when(this.toolService.getPluginTools(any(PluginToolQuery.class))).thenReturn(pluginToolDataListResult);

        assertThat(this.agentInfoGenerateService.selectTools("DESC", "CREATOR", null)).containsExactly("UNIQUENAME2",
                "UNIQUENAME3");
    }

    @NotNull
    private ModelListDto getModelListDto() {
        List<ModelAccessInfo> modelAccessInfos = new ArrayList<>();
        modelAccessInfos.add(new ModelAccessInfo("MODEL", "TAG", null, null, null));
        ModelListDto dto = new ModelListDto();
        dto.setModels(modelAccessInfos);
        return dto;
    }
}