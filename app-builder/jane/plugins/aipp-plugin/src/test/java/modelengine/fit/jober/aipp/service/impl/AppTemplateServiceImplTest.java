/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.repository.AppTemplateRepository;
import modelengine.fit.jober.aipp.service.AippFlowService;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.AppTemplateService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.TemplateUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用模板服务测试类。
 *
 * @author 方誉州
 * @since 2025-01-16
 */
@ExtendWith(MockitoExtension.class)
public class AppTemplateServiceImplTest {
    private static final LocalDateTime TIME = LocalDateTime.of(2025, 1, 16, 9, 0);

    private AppTemplateService templateService;

    private AppBuilderAppService appService;

    @Mock
    private AippFlowService aippFlowService;

    @Mock
    private UploadedFileManageService uploadedFileManageService;

    @Mock
    private AppBuilderAppRepository appRepository;

    @Mock
    private AppTemplateRepository templateRepository;

    @Mock
    private AppBuilderFlowGraphRepository flowGraphRepository;

    @Mock
    private AppBuilderConfigRepository configRepository;

    @Mock
    private AppBuilderFormRepository formRepository;

    @Mock
    private AppBuilderConfigPropertyRepository configPropertyRepository;

    @Mock
    private AppBuilderFormPropertyRepository formPropertyRepository;

    @BeforeEach
    void setup() {
        AppBuilderAppFactory appFactory = new AppBuilderAppFactory(this.flowGraphRepository, this.configRepository,
                this.formRepository, this.configPropertyRepository, this.formPropertyRepository, this.appRepository);
        AppTemplateFactory templateFactory = new AppTemplateFactory(this.flowGraphRepository, this.configRepository,
                this.formRepository, this.configPropertyRepository, this.formPropertyRepository,
                this.templateRepository);
        this.appService = new AppBuilderAppServiceImpl(appFactory, this.aippFlowService, this.appRepository,
                templateFactory, 64, null, null, null, null, this.uploadedFileManageService, null, null, null, null,
                null, null, null, null, null, null, null, null, "", null);
        this.templateService = new AppTemplateServiceImpl(this.appService, this.templateRepository);
    }

    @Test
    @DisplayName("测试查询模板")
    void testQueryTemplate() {
        List<AppTemplate> queryResult = Collections.singletonList(AppTemplate.builder()
                .appType("default")
                .attributes(MapBuilder.<String, Object>get().put(TemplateUtils.ICON_ATTR_KEY, "/path/to/icon").build())
                .build());
        when(this.templateRepository.selectWithCondition(any())).thenReturn(queryResult);
        when(this.templateRepository.countWithCondition(any())).thenReturn(queryResult.size());

        RangedResultSet<TemplateInfoDto> res = this.templateService.query(TemplateQueryCondition.builder().build(),
                null);
        assertThat(res.getResults()).hasSize(1)
                .element(0)
                .extracting(TemplateInfoDto::getAppType, TemplateInfoDto::getId, TemplateInfoDto::getIcon,
                        TemplateInfoDto::getDescription)
                .containsExactly("default", null, "/path/to/icon", null);
    }

    @Test
    @DisplayName("测试将应用发布为应用模板")
    void testPublishAppTemplateFromApp() throws IOException {
        AppBuilderApp app = this.mockApp();
        when(this.appRepository.selectWithId(anyString())).thenReturn(app);
        when(this.flowGraphRepository.selectWithId(any())).thenReturn(app.getFlowGraph());
        when(this.configRepository.selectWithId(any())).thenReturn(app.getConfig());
        when(this.formPropertyRepository.selectWithAppId(any())).thenReturn(app.getFormProperties());

        File mockFile = File.createTempFile("old_test_icon", ".png");
        TemplateAppCreateDto mockDto = this.mockCreateDto();
        String oldIcon = mockDto.getIcon();
        oldIcon = oldIcon.replace(AippFileUtils.getFileNameFromIcon(oldIcon), mockFile.getCanonicalPath());
        mockDto.setIcon(oldIcon);
        app.getAttributes().put("icon", oldIcon);
        TemplateInfoDto published = this.templateService.publish(mockDto, this.mockOperationContext());
        File icon = new File(AippFileUtils.getFileNameFromIcon(published.getIcon()));
        mockFile.delete();
        icon.delete();

        verify(this.uploadedFileManageService, times(1)).addFileRecord(any(), eq("demo_account"), any(), any());
        verify(this.uploadedFileManageService, times(1)).updateRecord(any(), any(), eq(0));

        assertThat(published).extracting(TemplateInfoDto::getName, TemplateInfoDto::getDescription,
                        TemplateInfoDto::getAppType, TemplateInfoDto::getCreator)
                .containsExactly("test_name", "test_description", "finance", "operator");

        String templateId = published.getId();
        assertThat(app.getConfig()).extracting(AppBuilderConfig::getAppId).isEqualTo(templateId);
        assertThat(app.getFormProperties()).element(0)
                .extracting(AppBuilderFormProperty::getAppId)
                .isEqualTo(templateId);
    }

    @Test
    @DisplayName("测试根据应用模板创建应用")
    void testCreateAppByTemplate() {
        AppTemplate template = this.mockTemplate();
        when(this.templateRepository.selectWithId(anyString())).thenReturn(template);
        when(this.flowGraphRepository.selectWithId(any())).thenReturn(template.getFlowGraph());
        when(this.configRepository.selectWithId(any())).thenReturn(template.getConfig());
        when(this.formPropertyRepository.selectWithAppId(any())).thenReturn(template.getFormProperties());
        when(this.aippFlowService.previewAipp(any(), any(), any())).thenReturn(
                AippCreateDto.builder().aippId("123456").build());

        AppBuilderAppDto dto = this.templateService.createAppByTemplate(this.mockCreateDto(),
                this.mockOperationContext());
        verify(this.uploadedFileManageService, times(0)).addFileRecord(any(), any(), any(), any());
        verify(this.uploadedFileManageService, times(1)).updateRecord(any(), any(), eq(0));
        verify(this.templateRepository, times(1)).increaseUsage(eq("123456"));

        assertThat(dto).extracting(AppBuilderAppDto::getName, AppBuilderAppDto::getVersion,
                        AppBuilderAppDto::getAppType, AppBuilderAppDto::getCreateBy, AppBuilderAppDto::getType,
                        AppBuilderAppDto::getState, AppBuilderAppDto::getAippId)
                .containsExactly("test_name", "1.0.0", "finance", "operator", "app", "inactive", "123456");
        String appId = dto.getId();
        assertThat(template.getConfig()).extracting(AppBuilderConfig::getAppId).isEqualTo(appId);
        assertThat(template.getFormProperties()).element(0)
                .extracting(AppBuilderFormProperty::getAppId)
                .isEqualTo(appId);
        Map<String, Object> attributes = dto.getAttributes();
        assertThat(attributes).containsEntry("description", "test_description")
                .containsEntry("icon", "/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/tmp/test_old"
                        + ".jpg&fileName=test_old.jpg");
    }

    @Test
    @DisplayName("测试删除应用模板")
    void testDeleteAppTemplate() {
        AppTemplate template = this.mockTemplate();
        when(this.templateRepository.selectWithId(anyString())).thenReturn(template);
        this.templateService.delete(template.getId(), this.mockOperationContext());

        verify(this.configRepository, times(1)).delete(
                argThat(arg -> arg.size() == 1 && arg.get(0).equals(template.getConfigId())));
        verify(this.flowGraphRepository, times(1)).delete(
                argThat(arg -> arg.size() == 1 && arg.get(0).equals(template.getFlowGraphId())));
        verify(this.templateRepository, times(1)).deleteOne(eq(template.getId()));
        verify(this.formPropertyRepository, times(1)).deleteByAppIds(
                argThat(arg -> arg.size() == 1 && arg.get(0).equals(template.getId())));
        verify(this.uploadedFileManageService, times(1)).cleanAippFiles(
                argThat(arg -> arg.size() == 1 && arg.get(0).equals(template.getId())));
    }

    private OperationContext mockOperationContext() {
        return new OperationContext("tenant_id", "operator", "global_user_id", "demo_account", "employ_number", "name",
                "0.0.0.0", "unit test", "zh_CN");
    }

    private TemplateAppCreateDto mockCreateDto() {
        return TemplateAppCreateDto.builder()
                .id("123456")
                .name("test_name")
                .description("test_description")
                .icon("/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/tmp/test_old"
                        + ".jpg&fileName=test_old.jpg")
                .appType("finance")
                .build();
    }

    private AppTemplate mockTemplate() {
        AppTemplate template = new AppTemplate(flowGraphRepository, configRepository, formRepository,
                configPropertyRepository, formPropertyRepository);
        template.setBuiltType("basic");
        template.setCategory("chatbot");
        template.setName("Unit Test Template");
        template.setId("45698235b3d24209aefd59eb7d1c3322");
        template.setAttributes(new HashMap<>());
        template.getAttributes().put("icon", "");
        template.getAttributes().put("app_type", "编程开发");
        template.getAttributes().put("greeting", "1");
        template.getAttributes().put("description", "1");
        template.setCreateBy("yaojiang");
        template.setUpdateBy("yaojiang");
        template.setUpdateAt(TIME);
        template.setCreateAt(TIME);
        template.setVersion("1.1.1");
        template.setConfig(this.mockConfig());
        template.setConfigId(template.getConfig().getId());
        template.setFlowGraph(this.mockGraph());
        template.setFlowGraphId(template.getFlowGraph().getId());
        template.setFormProperties(this.mockFormProperties());
        return template;
    }

    private AppBuilderApp mockApp() {
        AppBuilderApp appBuilderApp = new AppBuilderApp(flowGraphRepository, configRepository, formRepository,
                configPropertyRepository, formPropertyRepository);
        appBuilderApp.setType("template");
        appBuilderApp.setAppBuiltType("basic");
        appBuilderApp.setAppCategory("chatbot");
        appBuilderApp.setName("Unit Test App");
        appBuilderApp.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");
        appBuilderApp.setId("45698235b3d24209aefd59eb7d1c3322");
        appBuilderApp.setAttributes(new HashMap<>());
        appBuilderApp.getAttributes()
                .put("icon", "/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/tmp/test_old"
                        + ".jpg&fileName=test_old.jpg");
        appBuilderApp.getAttributes().put("app_type", "编程开发");
        appBuilderApp.getAttributes().put("greeting", "1");
        appBuilderApp.getAttributes().put("description", "1");
        appBuilderApp.setState("inactive");
        appBuilderApp.setCreateBy("yaojiang");
        appBuilderApp.setUpdateBy("yaojiang");
        appBuilderApp.setUpdateAt(TIME);
        appBuilderApp.setCreateAt(TIME);
        appBuilderApp.setVersion("1.0.0");
        appBuilderApp.setPath("YGHmQFJE5ZaFW4wl");
        appBuilderApp.setConfig(this.mockConfig());
        appBuilderApp.getConfig().setApp(appBuilderApp);
        appBuilderApp.setConfigId(appBuilderApp.getConfig().getId());
        appBuilderApp.setFlowGraph(this.mockGraph());
        appBuilderApp.setFlowGraphId(appBuilderApp.getFlowGraph().getId());
        appBuilderApp.setFormProperties(mockFormProperties());
        return appBuilderApp;
    }

    private AppBuilderConfig mockConfig() {
        AppBuilderConfig config = new AppBuilderConfig(this.formRepository, this.formPropertyRepository,
                this.configPropertyRepository, this.appRepository);

        config.setAppId("45698235b3d24209aefd59eb7d1c3322");
        config.setId("24581235b3d24209aefd59eb7d1c3322");

        config.setUpdateAt(TIME);
        config.setCreateAt(TIME);
        config.setCreateBy("yaojiang");
        config.setUpdateBy("yaojiang");
        config.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");

        config.setForm(mockForm());
        config.setFormId(config.getForm().getId());
        config.setConfigProperties(mockConfigProperties());
        List<AppBuilderFormProperty> formProperties = mockFormProperties();
        for (int i = 0; i < 8; i++) {
            AppBuilderConfigProperty configProperty = config.getConfigProperties().get(i);
            configProperty.setConfig(config);
            configProperty.setFormProperty(formProperties.get(i));
        }
        return config;
    }

    private AppBuilderForm mockForm() {
        AppBuilderForm form = new AppBuilderForm(this.formPropertyRepository);
        form.setId("cc65e235b3d24209aefd59eb7d1a5499");
        form.setName("表单");
        form.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");
        form.setUpdateAt(TIME);
        form.setCreateAt(TIME);
        form.setCreateBy("yaojiang");
        form.setUpdateBy("yaojiang");
        form.setType("component");
        form.setAppearance(null);
        return form;
    }

    private List<AppBuilderFormProperty> mockFormProperties() {
        List<Object> values = Arrays.asList("null", "null", Collections.singletonList("jadewdnjbq"),
                Arrays.asList(Arrays.asList("jadewdnjbq", "tools"), Arrays.asList("jadewdnjbq", "workflows")),
                Arrays.asList("jade0pg2ag", "knowledge"), "null", Arrays.asList("jade6qm5eg", "memory"),
                JSONObject.parseObject(
                        "{\"category\":[{\"title\":\"root\",\"id\":\"root\",\"children\":[]}],\"inspirations\":[]}"),
                JSONObject.parseObject("{\"showRecommend\":false, \"list\":[]}"),
                "i18n_appBuilder_{form_property_opening_content}");
        List<String> names = Arrays.asList("basic", "ability", "model", "tools", "knowledge", "chat", "memory",
                "inspiration", "recommend", "opening");
        List<String> dataTypes = Arrays.asList("String", "String", "List<String>", "List<List<String>>", "List<String>",
                "String", "List<String>", "object", "object", "String");
        List<String> from = Arrays.asList("none", "none", "graph", "graph", "graph", "none", "graph", "input", "input",
                "input");
        List<String> group = Arrays.asList("null", "basic", "ability", "ability", "ability", "basic", "chat", "chat",
                "chat", "chat");
        List<String> description = Arrays.asList("i18n_appBuilder_{form_property_basic}",
                "i18n_appBuilder_{form_property_ability}", "i18n_appBuilder_{form_property_model}",
                "i18n_appBuilder_{form_property_tools}", "i18n_appBuilder_{form_property_knowledge}",
                "i18n_appBuilder_{form_property_chat}", "i18n_appBuilder_{form_property_memory}",
                "i18n_appBuilder_{form_property_inspiration}", "i18n_appBuilder_{form_property_recommend}",
                "i18n_appBuilder_{form_property_opening}");
        List<AppBuilderFormProperty> formProperties = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AppBuilderFormProperty formProperty = new AppBuilderFormProperty();
            formProperty.setFormId("cc65e235b3d24209aefd59eb7d1a5499");
            formProperty.setName(names.get(i));
            formProperty.setId(i + "c65e235b3d24209aefd59eb7d1a549" + i);
            formProperty.setDataType(dataTypes.get(i));
            formProperty.setDefaultValue(values.get(i));
            formProperty.setFrom(from.get(i));
            formProperty.setGroup(group.get(i));
            formProperty.setDescription(description.get(i));
            formProperty.setFormRepository(this.formRepository);
            formProperties.add(formProperty);
        }
        return formProperties;
    }

    private List<AppBuilderConfigProperty> mockConfigProperties() {
        String[] nodeIds = new String[] {"4agtId", "4agtId", "4agtId", "4agtId", "sciinj", null, null, "4agtId"};

        List<AppBuilderConfigProperty> configProperties = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            AppBuilderConfigProperty configProperty = new AppBuilderConfigProperty(this.configRepository,
                    this.formRepository, this.formPropertyRepository);
            configProperty.setConfigId(i + "275e235b3d24209aefd59eb8541a549" + i);
            configProperty.setFormPropertyId(i + "c65e235b3d24209aefd59eb7d1a549" + i);
            configProperty.setNodeId(nodeIds[i]);
            configProperty.setConfigId("24581235b3d24209aefd59eb7d1c3322");
            configProperties.add(configProperty);
        }
        return configProperties;
    }

    private AppBuilderFlowGraph mockGraph() {
        String appearance
                = "{\"id\": \"69e9dec999384b1791e24a3032010e77\", \"type\": \"jadeFlowGraph\", \"pages\": []}";
        AppBuilderFlowGraph graph = new AppBuilderFlowGraph("69e9dec999384b1791e24a3032010e77", "graph", appearance);
        graph.setUpdateAt(TIME);
        graph.setCreateAt(TIME);
        graph.setCreateBy("yaojiang");
        graph.setUpdateBy("yaojiang");
        return graph;
    }
}
