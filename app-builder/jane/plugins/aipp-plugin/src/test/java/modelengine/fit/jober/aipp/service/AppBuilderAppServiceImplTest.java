/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_APP_ID_KEY;
import static modelengine.fit.jober.aipp.service.impl.UploadedFileMangeServiceImpl.IRREMOVABLE;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.AppTypeDto;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.dto.export.AppExportConfigProperty;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;
import modelengine.fit.jober.aipp.dto.export.AppExportFormProperty;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.factory.CheckerFactory;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.mapper.AippUploadedFileMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderConfigMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderConfigPropertyMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderFlowGraphMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderFormMapper;
import modelengine.fit.jober.aipp.mapper.AppBuilderFormPropertyMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.repository.impl.AppBuilderAppRepositoryImpl;
import modelengine.fit.jober.aipp.repository.impl.AppBuilderConfigPropertyRepositoryImpl;
import modelengine.fit.jober.aipp.repository.impl.AppBuilderConfigRepositoryImpl;
import modelengine.fit.jober.aipp.repository.impl.AppBuilderFlowGraphRepositoryImpl;
import modelengine.fit.jober.aipp.repository.impl.AppBuilderFormPropertyRepositoryImpl;
import modelengine.fit.jober.aipp.repository.impl.AppBuilderFormRepositoryImpl;
import modelengine.fit.jober.aipp.service.impl.AppBuilderAppServiceImpl;
import modelengine.fit.jober.aipp.service.impl.RetrievalNodeChecker;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.validation.AppUpdateValidator;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.app.engine.base.service.UsrAppCollectionService;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.store.service.AppService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 姚江
 * @since 2024-04-29
 */
@ExtendWith(MockitoExtension.class)
public class AppBuilderAppServiceImplTest {
    @Mock
    private AippFlowService aippFlowService;

    @Mock
    private AppBuilderAppRepository appRepository;

    @Mock
    private AippChatMapper aippChatMapper;

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

    @Mock
    private MetaService metaService;

    @Mock
    private UsrAppCollectionService usrAppCollectionService;

    @Mock
    private UploadedFileManageService uploadedFileService;

    @Mock
    private AippModelCenter aippModelCenter;

    @Mock
    private AppTypeService appTypeService;

    @Mock
    private AippFlowDefinitionService aippFlowDefinitionService;

    @Mock
    private FlowDefinitionService flowDefinitionService;

    private AppBuilderAppServiceImpl appBuilderAppService;

    @Mock
    private KnowledgeCenterService knowledgeCenterService;

    private static final LocalDateTime TIME = LocalDateTime.of(2024, 5, 6, 15, 15, 15);

    private static final String IMPORT_CONFIG = "component/import_config.json";

    @BeforeEach
    public void before() {
        AppBuilderAppFactory factory = new AppBuilderAppFactory(flowGraphRepository, configRepository, formRepository,
                configPropertyRepository, formPropertyRepository, appRepository);
        appBuilderAppService = new AppBuilderAppServiceImpl(factory, aippFlowService, appRepository, null, 64,
                metaService, usrAppCollectionService, null, null, uploadedFileService, null, null, null, null,
                this.aippModelCenter, null, MapBuilder.<String, String>get()
                .put("version", "1.0.1")
                .put("hash-template", "123")
                .put("digest", "MD5")
                .build(), appTypeService, null, null, flowDefinitionService, aippFlowDefinitionService, "", knowledgeCenterService);
    }

    private AppBuilderApp mockApp(String appId) {
        AppBuilderApp appBuilderApp = new AppBuilderApp(flowGraphRepository, configRepository, formRepository,
                configPropertyRepository, formPropertyRepository);
        appBuilderApp.setType("template");
        appBuilderApp.setAppBuiltType("basic");
        appBuilderApp.setAppCategory("chatbot");
        appBuilderApp.setName("Unit Test App");
        appBuilderApp.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");
        appBuilderApp.setId(appId);
        appBuilderApp.setAttributes(new HashMap<>());
        appBuilderApp.getAttributes()
                .put("icon", "/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/var/share/test_old"
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
        appBuilderApp.setConfig(this.mockConfig(appId));
        appBuilderApp.getConfig().setApp(appBuilderApp);
        appBuilderApp.setConfigId(appBuilderApp.getConfig().getId());
        appBuilderApp.setFlowGraph(this.mockGraph());
        appBuilderApp.setFlowGraphId(appBuilderApp.getFlowGraph().getId());
        appBuilderApp.setFormProperties(mockFormProperties(appId));
        return appBuilderApp;
    }

    private AppBuilderConfig mockConfig(String appId) {
        AppBuilderConfig config = new AppBuilderConfig(this.formRepository, this.formPropertyRepository,
                this.configPropertyRepository, this.appRepository);

        config.setAppId(appId);
        config.setId("24581235b3d24209aefd59eb7d1c3322");

        config.setUpdateAt(TIME);
        config.setCreateAt(TIME);
        config.setCreateBy("yaojiang");
        config.setUpdateBy("yaojiang");
        config.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");

        config.setForm(mockForm());
        config.setFormId(config.getForm().getId());
        config.setConfigProperties(mockConfigProperties());
        List<AppBuilderFormProperty> formProperties = mockFormProperties(appId);
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

    private List<AppBuilderFormProperty> mockFormProperties(String appId) {
        Object[] values = new Object[] {
                "null", "null", Collections.singletonList("jadewdnjbq"),
                Arrays.asList(Arrays.asList("jadewdnjbq", "tools"), Arrays.asList("jadewdnjbq", "workflows")),
                Arrays.asList("jade0pg2ag", "knowledge"), "null", Arrays.asList("jade6qm5eg", "memory"),
                JSONObject.parseObject(
                        "{\"category\":[{\"title\":\"root\",\"id\":\"root\",\"children\":[]}],\"inspirations\":[]}"),
                JSONObject.parseObject("{\"showRecommend\":false, \"list\":[]}"),
                "i18n_appBuilder_{form_property_opening_content}"
        };
        String[] names = new String[] {
                "basic", "ability", "model", "tools", "knowledge", "chat", "memory", "inspiration", "recommend",
                "opening"
        };
        String[] dataTypes = new String[] {
                "String", "String", "List<String>", "List<List<String>>", "List<String>", "String", "List<String>",
                "object", "object", "String"
        };
        String[] from = new String[] {
                "none", "none", "graph", "graph", "graph", "none", "graph", "input", "input", "input"
        };
        String[] group = new String[] {
                "null", "basic", "ability", "ability", "ability", "basic", "chat", "chat", "chat", "chat"
        };
        String[] description = new String[] {
                "i18n_appBuilder_{form_property_basic}", "i18n_appBuilder_{form_property_ability}",
                "i18n_appBuilder_{form_property_model}", "i18n_appBuilder_{form_property_tools}",
                "i18n_appBuilder_{form_property_knowledge}", "i18n_appBuilder_{form_property_chat}",
                "i18n_appBuilder_{form_property_memory}", "i18n_appBuilder_{form_property_inspiration}",
                "i18n_appBuilder_{form_property_recommend}", "i18n_appBuilder_{form_property_opening}"
        };
        List<AppBuilderFormProperty> formProperties = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AppBuilderFormProperty formProperty = new AppBuilderFormProperty();
            formProperty.setFormId("cc65e235b3d24209aefd59eb7d1a5499");
            formProperty.setName(names[i]);
            formProperty.setId(i + "c65e235b3d24209aefd59eb7d1a549" + i);
            formProperty.setDataType(dataTypes[i]);
            formProperty.setDefaultValue(values[i]);
            formProperty.setFrom(from[i]);
            formProperty.setGroup(group[i]);
            formProperty.setDescription(description[i]);
            formProperty.setFormRepository(this.formRepository);
            formProperty.setAppId(appId);
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
        String appearance =
                "{\"id\": \"69e9dec999384b1791e24a3032010e77\", \"type\": \"jadeFlowGraph\", \"pages\": [{\"x\": 0, "
                        + "\"y\": 0, \"id\": \"elsa-page:t1qrig\", \"bold\": false, \"mode\": \"configuration\", "
                        + "\"text\": \"newFlowPage\", \"type\": \"jadeFlowPage\", \"dirty\": false, \"index\": 0, "
                        + "\"width\": 2000, \"hAlign\": \"left\", \"height\": 1600, \"isPage\": true, \"italic\": "
                        + "false, \"shapes\": [{\"x\": 194, \"y\": 134, \"id\": \"a6lgso\", \"pad\": 6, \"bold\": "
                        + "false, \"name\": \"被监听者\", \"text\": \"\", \"type\": \"jadeInputNode\", \"dirty\": true, "
                        + "\"index\": 100, \"jober\": {\"name\": \"\", \"type\": \"general_jober\", \"fitables\": [],"
                        + " \"converter\": {\"type\": \"mapping_converter\", \"entity\": []}}, \"width\": 500, "
                        + "\"height\": 180, \"italic\": false, \"shadow\": \"0 2px 4px 0 rgba(0,0,0,.1)\", "
                        + "\"hideText\": true, \"backColor\": \"white\", \"container\": \"elsa-page:t1qrig\", "
                        + "\"dashWidth\": 0, \"namespace\": \"flowable\", \"autoHeight\": true, \"rotateAble\": "
                        + "false, \"borderColor\": \"rgba(28,31,35,.08)\", \"borderWidth\": 1, \"focusShadow\": \"0 0"
                        + " 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)\", \"runningTask\": 0, \"triggerMode\": "
                        + "\"auto\", \"warningTask\": 0, \"completedTask\": 0, \"componentName\": "
                        + "\"jadeInputComponent\", \"focusBackColor\": \"white\", \"enableAnimation\": true, "
                        + "\"focusBorderColor\": \"#4d53e8\", \"focusBorderWidth\": 2, \"mouseInBorderColor\": "
                        + "\"#4d53e8\"}, {\"x\": 120, \"y\": 80, \"id\": \"8ka4p9\", \"pad\": 6, \"bold\": false, "
                        + "\"name\": \"大模型\", \"text\": \"\", \"type\": \"llmNode\", \"dirty\": true, \"index\": 101,"
                        + " \"jober\": {\"name\": \"\", \"type\": \"general_jober\", \"fitables\": [], \"converter\":"
                        + " {\"type\": \"mapping_converter\", \"entity\": [{\"id\": "
                        + "\"c4aa80b5-70ef-4fb8-91f6-c9a832d5d6ec\", \"from\": \"value\", \"name\": \"model\", "
                        + "\"type\": \"String\", \"value\": \"Qwen\"}, {\"id\": "
                        + "\"96282fba-d75e-464c-b021-f390beccc942\", \"from\": \"value\", \"name\": \"temperature\", "
                        + "\"type\": \"Number\", \"value\": \"0.3\"}, {\"id\": "
                        + "\"b6440db6-f8c7-4775-870e-ba32623b8945\", \"from\": \"value\", \"name\": \"prompt\", "
                        + "\"type\": \"Object\", \"value\": [{\"id\": \"70f63499-8a4a-48c4-a5e0-e616d9ac2c08\", "
                        + "\"from\": \"value\", \"name\": \"template\", \"type\": \"String\", \"value\": \"\"}, "
                        + "{\"id\": \"15402f32-0ca9-46db-868c-69be7df334c0\", \"from\": \"value\", \"name\": "
                        + "\"variables\", \"type\": \"Object\", \"value\": [{\"id\": "
                        + "\"17832beb-c237-459e-8432-f0457a858413\", \"from\": \"reference\", \"name\": \"\", "
                        + "\"type\": \"String\", \"value\": \"\", \"referenceId\": \"\", \"referenceKey\": \"\", "
                        + "\"referenceNode\": \"\"}]}]}, {\"id\": \"5ec6d065-c6b9-4685-ad1b-ea5459d03069\", \"from\":"
                        + " \"value\", \"name\": \"tools\", \"type\": \"array\", \"value\": []}, {\"id\": "
                        + "\"bf1721b7-a88a-43e8-9ee1-8e6938c589b6\", \"from\": \"value\", \"name\": \"workflows\", "
                        + "\"type\": \"array\", \"value\": []}, {\"id\": \"b0215ced-f97f-4f87-883d-a76adb31abd7\", "
                        + "\"from\": \"value\", \"name\": \"output\", \"type\": \"Object\", \"value\": [{\"id\": "
                        + "\"24e24707-19ff-424c-a007-6c2d4a5abf05\", \"from\": \"value\", \"name\": \"llmOutput\", "
                        + "\"type\": \"string\", \"value\": \"\"}]}]}}, \"width\": 500, \"height\": 836, \"italic\": "
                        + "false, \"shadow\": \"0 2px 4px 0 rgba(0,0,0,.1)\", \"hideText\": true, \"backColor\": "
                        + "\"white\", \"container\": \"elsa-page:t1qrig\", \"dashWidth\": 0, \"namespace\": "
                        + "\"flowable\", \"autoHeight\": true, \"rotateAble\": false, \"borderColor\": \"rgba(28,31,"
                        + "35,.08)\", \"borderWidth\": 1, \"focusShadow\": \"0 0 1px rgba(0,0,0,.3),0 4px 14px rgba"
                        + "(0,0,0,.1)\", \"runningTask\": 0, \"triggerMode\": \"auto\", \"warningTask\": 0, "
                        + "\"completedTask\": 0, \"componentName\": \"llmComponent\", \"focusBackColor\": \"white\", "
                        + "\"enableAnimation\": true, \"focusBorderColor\": \"#4d53e8\", \"focusBorderWidth\": 2, "
                        + "\"mouseInBorderColor\": \"#4d53e8\"}], \"vAlign\": \"top\", \"itemPad\": [0, 0, 0, 0], "
                        + "\"division\": -1, \"dockMode\": \"none\", \"fontFace\": \"arial\", \"fontSize\": 18, "
                        + "\"hideText\": true, \"moveable\": true, \"shapesAs\": {}, \"backColor\": \"#f2f3f5\", "
                        + "\"container\": \"elsa-page:t1qrig\", \"dockAlign\": \"top\", \"fontColor\": \"#ECD0A7\", "
                        + "\"fontStyle\": \"normal\", \"itemSpace\": 5, \"namespace\": \"jadeFlow\", \"fontWeight\": "
                        + "\"bold\", \"itemScroll\": {\"x\": 0, \"y\": 0}, \"borderColor\": \"white\", "
                        + "\"focusBackColor\": \"#f2f3f5\"}], \"title\": \"69e9dec999384b1791e24a3032010e77\", \"source\": \"elsa\", "
                        + "\"tenant\": \"default\", \"setting\": {\"pad\": 10, \"tag\": {}, \"code\": \"\", "
                        + "\"pDock\": \"none\", \"hAlign\": \"center\", \"margin\": 25, \"shadow\": \"\", \"shared\":"
                        + " false, \"vAlign\": \"top\", \"itemPad\": [5, 5, 5, 5], \"visible\": true, \"autoText\": "
                        + "false, \"dockMode\": \"none\", \"dragable\": true, \"editable\": true, \"fontFace\": "
                        + "\"arial\", \"fontSize\": 12, \"infoType\": {\"name\": \"none\", \"next\": "
                        + "\"INFORMATION\"}, \"moveable\": true, \"priority\": 0, \"allowLink\": true, \"autoWidth\":"
                        + " false, \"backAlpha\": 0.15, \"backColor\": \"whitesmoke\", \"dashWidth\": 0, "
                        + "\"deletable\": true, \"fontColor\": \"steelblue\", \"fontStyle\": \"normal\", "
                        + "\"headColor\": \"steelblue\", \"lineWidth\": 2, \"underline\": false, \"autoHeight\": "
                        + "false, \"emphasized\": false, \"fontWeight\": \"lighter\", \"itemScroll\": {\"x\": 0, "
                        + "\"y\": 0}, \"lineHeight\": 1.5, \"resizeable\": true, \"rotateAble\": true, "
                        + "\"scrollLock\": {\"x\": false, \"y\": false}, \"selectable\": true, \"shadowData\": \"2px "
                        + "2px 4px\", \"borderColor\": \"steelblue\", \"borderWidth\": 1, \"bulletSpeed\": 1, "
                        + "\"focusMargin\": 0, \"focusShadow\": \"\", \"globalAlpha\": 1, \"outstanding\": false, "
                        + "\"bulletedList\": false, \"cornerRadius\": 4, \"enableSocial\": true, \"mouseInColor\": "
                        + "\"orange\", \"numberedList\": false, \"rotateDegree\": 0, \"captionhAlign\": \"center\", "
                        + "\"strikethrough\": false, \"focusBackColor\": \"whitesmoke\", \"focusFontColor\": "
                        + "\"darkorange\", \"progressStatus\": {\"name\": \"NONE\", \"next\": \"UNKNOWN\", \"color\":"
                        + " \"gray\"}, \"showedProgress\": false, \"captionfontFace\": \"arial black\", "
                        + "\"captionfontSize\": 14, \"enableAnimation\": false, \"progressPercent\": 0.65, "
                        + "\"captionfontColor\": \"whitesmoke\", \"captionfontStyle\": \"normal\", "
                        + "\"focusBorderColor\": \"darkorange\", \"focusBorderWidth\": 1, \"mouseInBackColor\": "
                        + "\"whitesmoke\", \"mouseInFontColor\": \"orange\", \"captionfontWeight\": \"lighter\", "
                        + "\"captionlineHeight\": 1, \"mouseInBorderColor\": \"orange\"}}";
        AppBuilderFlowGraph graph = new AppBuilderFlowGraph("69e9dec999384b1791e24a3032010e77", "graph", appearance);
        graph.setUpdateAt(TIME);
        graph.setCreateAt(TIME);
        graph.setCreateBy("yaojiang");
        graph.setUpdateBy("yaojiang");
        return graph;
    }

    /**
     * 为 {@link AppBuilderAppServiceImpl#create(String, AppBuilderAppCreateDto, OperationContext, boolean)} 提供测试
     */
    @Nested
    @DisplayName("创建测试")
    class TestCreate {
        @Test
        @DisplayName("测试创建基础编排")
        public void testBuildBasicApp() {
            OperationContext context = new OperationContext();
            AppBuilderAppCreateDto appCreateDto = new AppBuilderAppCreateDto();
            appCreateDto.setAppType("app");
            appCreateDto.setAppBuiltType("basic");
            String appName = "appName";
            String appId = "appId1";
            appCreateDto.setName(appName);
            appCreateDto.setAppCategory("chatbot");
            appCreateDto.setDescription("");
            AppBuilderApp appTemplate = mockApp(appId);
            AippCreateDto aippCreateDto = new AippCreateDto();
            aippCreateDto.setAippId("aippId1");
            List<KnowledgeDto> knowledgeDtos = new ArrayList<>();
            knowledgeDtos.add(KnowledgeDto.builder().groupId("default").build());
            when(appRepository.selectWithId(appId)).thenReturn(appTemplate);
            when(aippFlowService.previewAipp(anyString(), any(), any())).thenReturn(aippCreateDto);
            when(aippModelCenter.fetchModelList(any(), any(), any())).thenReturn(null);
            when(knowledgeCenterService.getSupportKnowledges(any())).thenReturn(knowledgeDtos);
            AppBuilderAppDto appBuilderAppDto = appBuilderAppService.create(appId, appCreateDto, context, false);
            assertThat(appBuilderAppDto.getName()).isEqualTo(appName);
            assertThat(appBuilderAppDto.getAppCategory()).isEqualTo("chatbot");
        }
    }

    /**
     * 为 {@link AppBuilderAppServiceImpl#updateFlow(String, OperationContext)} 提供测试
     */
    @Nested
    @DisplayName("测试根据是否更新debug")
    class TestUpdateFlow {
        @Test
        @DisplayName("测试需要更新")
        public void testTrue() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(AippConst.ATTR_APP_IS_UPDATE, true);
            Mockito.when(appRepository.selectWithId("hello"))
                    .thenReturn(AppBuilderApp.builder()
                            .id("hello")
                            .appBuiltType("basic")
                            .attributes(attributes)
                            .flowGraph(AppBuilderFlowGraph.builder().appearance("{}").build())
                            .config(AppBuilderConfig.builder()
                                    .form(AppBuilderForm.builder().build())
                                    .configProperties(new ArrayList<>())
                                    .build())
                            .build());
            Assertions.assertDoesNotThrow(() -> appBuilderAppService.updateFlow("hello", new OperationContext()));
        }
    }

    /**
     * 为 {@link AppBuilderAppServiceImpl#delete(String, OperationContext)} 提供测试
     */
    @Nested
    class TestDelete extends DatabaseBaseTest {
        private final AppBuilderAppMapper appBuilderAppMapper = sqlSessionManager.openSession(true)
                .getMapper(AppBuilderAppMapper.class);

        private final AppBuilderConfigMapper appBuilderConfigMapper = sqlSessionManager.openSession(true)
                .getMapper(AppBuilderConfigMapper.class);

        private final AppBuilderConfigPropertyMapper appBuilderConfigPropertyMapper = sqlSessionManager.openSession(
                true).getMapper(AppBuilderConfigPropertyMapper.class);

        private final AppBuilderFlowGraphMapper appBuilderFlowGraphMapper = sqlSessionManager.openSession(true)
                .getMapper(AppBuilderFlowGraphMapper.class);

        private final AppBuilderFormMapper appBuilderFormMapper = sqlSessionManager.openSession(true)
                .getMapper(AppBuilderFormMapper.class);

        private final AppBuilderFormPropertyMapper appBuilderFormPropertyMapper = sqlSessionManager.openSession(true)
                .getMapper(AppBuilderFormPropertyMapper.class);

        private final AppBuilderFormPropertyRepository formPropertyRepository
                = new AppBuilderFormPropertyRepositoryImpl(this.appBuilderFormPropertyMapper);

        private final AppBuilderFormRepository formRepository = new AppBuilderFormRepositoryImpl(
                this.appBuilderFormMapper, this.formPropertyRepository);

        private final AppBuilderConfigPropertyRepository configPropertyRepository
                = new AppBuilderConfigPropertyRepositoryImpl(this.appBuilderConfigPropertyMapper);

        private final AppBuilderConfigRepository configRepository = new AppBuilderConfigRepositoryImpl(
                this.appBuilderConfigMapper, this.configPropertyRepository);

        private final AppBuilderAppRepository appRepository = new AppBuilderAppRepositoryImpl(this.appBuilderAppMapper);

        private final AppBuilderFlowGraphRepository flowGraphRepository = new AppBuilderFlowGraphRepositoryImpl(
                this.appBuilderFlowGraphMapper);

        private AippFlowService aippFlowService;

        private final MetaService metaService = mock(MetaService.class);

        private final UsrAppCollectionService usrAppCollectionService = mock(UsrAppCollectionService.class);

        private final MetaInstanceService metaInstanceService = mock(MetaInstanceService.class);

        private final UploadedFileManageService uploadedFileManageService = Mockito.mock(
                UploadedFileManageService.class);

        private final AippUploadedFileMapper aippUploadedFileMapper = mock(AippUploadedFileMapper.class);

        private final AippLogMapper aippLogMapper = mock(AippLogMapper.class);

        private final FlowsService flowsService = mock(FlowsService.class);

        private final AppService appService = mock(AppService.class);

        private final AippChatService aippChatService = mock(AippChatService.class);

        private final AippChatMapper aippChatMapper = Mockito.mock(AippChatMapper.class);

        private final AippModelCenter aippModelCenter = mock(AippModelCenter.class);

        private final AppUpdateValidator appUpdateValidator = mock(AppUpdateValidator.class);

        private final KnowledgeCenterService knowledgeCenterService = mock(KnowledgeCenterService.class);

        private final AppBuilderAppFactory factory = new AppBuilderAppFactory(this.flowGraphRepository,
                this.configRepository, this.formRepository, this.configPropertyRepository, this.formPropertyRepository,
                this.appRepository);

        private final AppBuilderAppService appBuilderAppService = new AppBuilderAppServiceImpl(this.factory,
                this.aippFlowService, this.appRepository, null, 64, this.metaService, this.usrAppCollectionService,
                this.appUpdateValidator, this.metaInstanceService, this.uploadedFileManageService, this.aippLogMapper,
                this.flowsService, this.appService, this.aippChatService, this.aippModelCenter, this.aippChatMapper,
                null, null, null, null, null, null,
                "", knowledgeCenterService);

        @Test
        @DisplayName("更新 config")
        void testUpdateConfig() {
            AppBuilderAppPo appPo = this.appBuilderAppMapper.selectWithId("3a617d8aeb1d41a9ad7453f2f0f70d61");
            doNothing().when(appUpdateValidator).validate(anyString());
            String configId = appPo.getConfigId();
            AppBuilderConfig config = this.configRepository.selectWithId(configId);
            String formId = config.getFormId();
            AppBuilderForm appBuilderForm = this.formRepository.selectWithId(formId);
            List<AppBuilderFormProperty> appBuilderFormProperties = this.formPropertyRepository.selectWithFormId(
                    formId);
            AppBuilderConfigFormDto newFormDto = AppBuilderConfigFormDto.builder()
                    .appearance(appBuilderForm.getAppearance())
                    .id(appBuilderForm.getId())
                    .name(appBuilderForm.getName())
                    .build();
            AppBuilderFormProperty newFormProperty = AppBuilderFormProperty.builder()
                    .id("789")
                    .formId("369")
                    .name("test")
                    .dataType("String")
                    .defaultValue("test")
                    .build();
            appBuilderFormProperties.add(newFormProperty);
            AppBuilderConfigDto newConfigDto = AppBuilderConfigDto.builder().form(newFormDto).build();
            List<AppBuilderConfigFormPropertyDto> formPropertyDtos = appBuilderFormProperties.stream()
                    .map(AppBuilderFormProperty::toAppBuilderConfigFormPropertyDto)
                    .peek(dto -> dto.setDefaultValue("newValue"))
                    .collect(Collectors.toList());
            OperationContext context = new OperationContext();
            context.setOperator("test");
            Rsp<AppBuilderAppDto> newAppDto = this.appBuilderAppService.updateConfig("3a617d8aeb1d41a9ad7453f2f0f70d61",
                    newConfigDto, formPropertyDtos, context);
            assertEquals(newAppDto.getData().getConfigFormProperties().get(0).getDefaultValue(), "newValue");
        }

        @Test
        @DisplayName("测试删除 app 所有信息")
        void testDeleteAppBasicData() {
            Meta meta = this.buildMeta();
            RangedResultSet<Meta> metas = RangedResultSet.create(Collections.singletonList(meta), 0, 1, 1);
            Mockito.when(this.metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any()))
                    .thenReturn(metas);
            RangedResultSet<Instance> instances = this.buildInstances();
            Mockito.when(this.metaInstanceService.list(eq("vid1"), any(), anyLong(), anyInt(), any()))
                    .thenReturn(instances);
            Mockito.when(this.metaInstanceService.list(eq("vid1"), anyLong(), anyInt(), any()))
                    .thenReturn(instances);
            Mockito.when(this.aippChatMapper.deleteAppByAippId(anyString())).thenReturn(1);
            AppBuilderAppPo appPo = this.appBuilderAppMapper.selectWithId("df87073b9bc85a48a9b01eccc9afccc4");
            String configId = appPo.getConfigId();
            AppBuilderConfig config = this.configRepository.selectWithId(configId);
            List<AppBuilderConfigProperty> configProperties = this.configPropertyRepository.selectWithConfigId(
                    configId);
            String formId = config.getFormId();
            assertEquals(appPo.getId(), "df87073b9bc85a48a9b01eccc9afccc4");
            AppBuilderForm appBuilderForm = this.formRepository.selectWithId(formId);
            assertEquals(configProperties.size(), 8);
            assertEquals(appBuilderForm.getId(), "b8986770a6ffef44bbf2a9f26d6fc1bc");
            List<AppBuilderFormProperty> appBuilderFormProperties = this.formPropertyRepository.selectWithFormId(
                    formId);
            assertEquals(appBuilderFormProperties.size(), 10);

            this.appBuilderAppService.delete("df87073b9bc85a48a9b01eccc9afccc4", new OperationContext());

            AppBuilderAppPo newAppPo = this.appBuilderAppMapper.selectWithId("df87073b9bc85a48a9b01eccc9afccc4");
            assertNull(newAppPo);
            AppBuilderForm newAppBuilderForm = this.formRepository.selectWithId(formId);
            List<AppBuilderConfigProperty> newConfigProperties = this.configPropertyRepository.selectWithConfigId(
                    configId);
            assertEquals(newConfigProperties.size(), 0);
            List<AppBuilderFormProperty> newFormProperties = this.formPropertyRepository.selectWithFormId(formId);
            assertEquals(newFormProperties.size(), 0);
        }

        private RangedResultSet<Instance> buildInstances() {
            Instance instance1 = new Instance();
            instance1.setId("instanceId1");
            Instance instance2 = new Instance();
            instance2.setId("instanceId2");
            return RangedResultSet.create(Arrays.asList(instance1, instance2), 0, 2, 2);
        }

        private Meta buildMeta() {
            Meta meta = new Meta();
            meta.setId("mid1");
            meta.setVersionId("vid1");
            meta.setVersion("v1");
            Map<String, Object> attr = new HashMap<>();
            attr.put(ATTR_APP_ID_KEY, "df87073b9bc85a48a9b01eccc9afccc4");
            meta.setAttributes(attr);
            return meta;
        }
    }

    @Test
    @DisplayName("测试生成版本号符合预期")
    public void testGenerateVersion() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AppBuilderAppServiceImpl.class.getDeclaredMethod("buildVersion", AppBuilderApp.class,
                boolean.class);
        method.setAccessible(true);
        AppBuilderApp appBuilderApp = mockApp("id");
        appBuilderApp.setVersion("10.99.99");
        Object result = method.invoke(appBuilderAppService, appBuilderApp, true);
        assertThat(result).isInstanceOf(String.class);
        String resAfterCast = cast(result);
        assertThat(resAfterCast).isEqualTo("10.99.99");
    }

    @Test
    @DisplayName("测试查询app能得到最初创建的app的创建时间")
    public void testQuery() {
        Meta mockMeta = new Meta();
        mockMeta.setId("test");
        mockMeta.setAttributes(MapBuilder.<String, Object>get().put(ATTR_APP_ID_KEY, "test-appid").build());
        LocalDateTime mockCreateAt = LocalDateTime.of(2024, 12, 21, 12, 0, 0);
        mockMeta.setCreationTime(mockCreateAt);
        List<Meta> mockMetas = Collections.singletonList(mockMeta);
        when(appRepository.selectWithId(any())).thenReturn(mockApp("test-appid"));
        when(metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(mockMetas, 0, 1, 1));
        AppBuilderAppDto appBuilderAppDto = appBuilderAppService.query("test-appid", new OperationContext());
        assertThat(appBuilderAppDto.getBaselineCreateAt()).isEqualTo(mockCreateAt);

        appBuilderAppDto = appBuilderAppService.queryLatestOrchestration("test-appid", new OperationContext());
        assertThat(appBuilderAppDto.getBaselineCreateAt()).isEqualTo(mockCreateAt);
    }

    @Test
    @DisplayName("测试查询最新编排流程图")
    void testQueryByPathValidPath() {
        String validPath = "YGHmQFJE5ZaFW4wl";
        when(appRepository.selectWithPath(any())).thenReturn(mockApp("45698235b3d24209aefd59eb7d1c3322"));
        AppBuilderAppDto result = appBuilderAppService.queryByPath(validPath);
        assertEquals("45698235b3d24209aefd59eb7d1c3322", result.getId());
        assertEquals("Unit Test App", result.getName());
        assertEquals("/chat/YGHmQFJE5ZaFW4wl", result.getChatUrl());
    }

    @Test
    @DisplayName("测试查询无效路径")
    void testQueryByPathInvalidPath() {
        String invalidPath = "invalidPath";
        AippException exception = assertThrows(AippException.class, () -> {
            appBuilderAppService.queryByPath(invalidPath);
        });
        assertEquals("路径格式无效", exception.getMessage());
    }

    @Test
    @DisplayName("测试查询最新编排流程图")
    void testQueryLatestOrchestration() {
        OperationContext context = new OperationContext();
        Meta mockMeta = new Meta();
        mockMeta.setId("test");
        mockMeta.setAttributes(MapBuilder.<String, Object>get().put(ATTR_APP_ID_KEY, "test-appid").build());
        LocalDateTime mockCreateAt = LocalDateTime.of(2024, 12, 21, 12, 0, 0);
        mockMeta.setCreationTime(mockCreateAt);
        List<Meta> mockMetas = Collections.singletonList(mockMeta);
        when(metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(
                RangedResultSet.create(mockMetas, 0, 1, 1));
        AippCreate aippCreate = appBuilderAppService.queryLatestPublished("123", context);
        Assertions.assertEquals(aippCreate.getAippId(), "test");
    }

    @Nested
    @DisplayName("更新app测试")
    class TestUpgrade {
        @Test
        @DisplayName("测试Dto为空时更新app失败")
        void testUpdateWhenDtoIsNull() {
            AippException exception = assertThrows(AippException.class,
                    () -> appBuilderAppService.updateApp("appId", null, new OperationContext()));
            Assertions.assertEquals(AippErrCode.INVALID_OPERATION.getErrorCode(), exception.getCode());
        }

        @Test
        @DisplayName("测试icon变化时更新成功")
        void testUpdateAppWhenIconChanges() throws AippTaskNotFoundException {
            String appId = "45698235b3d24209aefd59eb7d1c3322";
            AppBuilderAppFactory appFactory = mock(AppBuilderAppFactory.class);
            UploadedFileManageService uploadedFileManageService = mock(UploadedFileManageService.class);
            AppUpdateValidator appUpdateValidator = mock(AppUpdateValidator.class);
            AppBuilderAppServiceImpl service = spy(new AppBuilderAppServiceImpl(appFactory,
                    null,
                    null,
                    null,
                    100,
                    null,
                    null,
                    appUpdateValidator,
                    null,
                    uploadedFileManageService,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "",
                    null));
            doNothing().when(service).validateUpdateApp(any(), any(), any());
            doNothing().when(appUpdateValidator).validate(anyString());
            when(appFactory.create(anyString())).thenReturn(mockApp(appId));
            doNothing().when(appFactory).update(any());
            Assertions.assertDoesNotThrow(() -> service.updateApp(appId, mockAppDto(), new OperationContext()));
        }

        private AppBuilderAppDto mockAppDto() {
            AppBuilderAppDto appDto = new AppBuilderAppDto();
            appDto.setAttributes(new HashMap<>());
            appDto.getAttributes()
                    .put("icon", "/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/var/share/test_new"
                            + ".jpg&fileName=test_new.jpg");
            appDto.setId("45698235b3d24209aefd59eb7d1c3322");
            appDto.setName("1");
            appDto.setType("app");
            return appDto;
        }
    }

    @Test
    @DisplayName("测试生成独特path方法")
    public void testGenerateUniquePathWithSuccessfulGeneration() throws Exception {
        when(appRepository.checkPathExists(anyString())).thenReturn(false);
        Method method = AppBuilderAppServiceImpl.class.getDeclaredMethod("generateUniquePath");
        method.setAccessible(true);  // 允许访问私有方法
        Object result = method.invoke(appBuilderAppService);
        if (result instanceof String) {
            String path = (String) result;  // 安全地进行类型转换
            verify(appRepository, times(1)).checkPathExists(anyString());
        }
    }

    @Test
    @DisplayName("测试生成独特path方法报错")
    void testGenerateUniquePathWithFailureAfterRetries() throws Exception {
        // 模拟 checkPathExists 总是返回 true，即路径总是存在
        when(appRepository.checkPathExists(anyString())).thenReturn(true);

        // 使用反射调用 generateUniquePath 方法
        Method method = AppBuilderAppServiceImpl.class.getDeclaredMethod("generateUniquePath");
        method.setAccessible(true);  // 允许访问私有方法

        // 捕获反射调用时抛出的异常
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(appBuilderAppService);
        });

        // 获取原始异常，即被反射方法抛出的异常
        Throwable cause = exception.getCause();

        // 验证异常类型是 AippException
        assertEquals(AippException.class, cause.getClass());
        assertEquals("系统错误，更新应用配置失败，请联系管理员。", cause.getMessage());
    }

    @Test
    @DisplayName("测试保存新app方法")
    void testSaveNewApp() throws NoSuchMethodException {
        Method saveNewAppBuilderApp = AppBuilderAppServiceImpl.class.getDeclaredMethod("saveNewAppBuilderApp",
                AppBuilderApp.class);
        saveNewAppBuilderApp.setAccessible(true);
        String appId = "45698235b3d24209aefd59eb7d1c3322";
        Assertions.assertDoesNotThrow(() -> saveNewAppBuilderApp.invoke(appBuilderAppService, mockApp(appId)));
        verify(uploadedFileService).updateRecord(eq(appId), eq("/var/share/test_old.jpg"),
                eq(IRREMOVABLE));
    }

    @Test
    @DisplayName("测试保存应用配置")
    void testSaveConfig() {
        String appId = "45698235b3d24209aefd59eb7d1c3322";
        AppBuilderSaveConfigDto appBuilderSaveConfigDto = AppBuilderSaveConfigDto.builder()
                .input(Collections.singletonList(AppBuilderConfigFormPropertyDto.builder().build()))
                .graph("{\"graph\":\"abc\"}")
                .build();
        Mockito.when(this.appRepository.selectWithId(appId)).thenReturn(mockApp(appId));
        doNothing().when(this.formPropertyRepository).updateMany(any());
        doNothing().when(this.flowGraphRepository).updateOne(any());
        Rsp<AppBuilderAppDto> res = this.appBuilderAppService.saveConfig(appId, appBuilderSaveConfigDto,
                new OperationContext());
        Assertions.assertEquals(res.getData().getId(), "45698235b3d24209aefd59eb7d1c3322");
    }

    @Test
    @DisplayName("测试应用导出")
    void testExportAppConfig() throws NoSuchMethodException {
        AppBuilderApp mockApp = AppBuilderApp.builder()
                .id("123456")
                .configId("123456")
                .flowGraphId("123456")
                .name("testApp")
                .tenantId("tenant123")
                .type("testType")
                .version("1.0.0")
                .createBy("admin")
                .attributes(new HashMap<>())
                .formPropertyRepository(this.formPropertyRepository)
                .build();
        List<AppBuilderConfigProperty> mockConfigProperties = Collections.singletonList(
                AppBuilderConfigProperty.builder().id("123").nodeId("456").formPropertyId("789").build());
        List<AppBuilderFormProperty> mockFormProperties = Collections.singletonList(AppBuilderFormProperty.builder()
                .id("789")
                .formId("369")
                .name("test")
                .dataType("String")
                .defaultValue("test")
                .group("null")
                .build());
        AppBuilderForm mockForm = mock(AppBuilderForm.class);
        AppBuilderConfig mockConfig = AppBuilderConfig.builder()
                .id("258")
                .form(mockForm)
                .configProperties(mockConfigProperties)
                .app(mockApp)
                .build();
        AppBuilderFlowGraph mockFlowGraph = AppBuilderFlowGraph.builder()
                .id("123")
                .name("testFlowGraph")
                .appearance("{\"id\": \"testGraphId\"}")
                .build();
        when(this.appRepository.selectWithId(anyString())).thenReturn(mockApp);
        when(this.configRepository.selectWithId(anyString())).thenReturn(mockConfig);
        when(this.flowGraphRepository.selectWithId(anyString())).thenReturn(mockFlowGraph);
        when(this.formPropertyRepository.selectWithAppId(anyString())).thenReturn(mockFormProperties);
        when(this.appTypeService.query(any(), any())).thenReturn(AppTypeDto.builder().name("单元测试").build());
        when(this.aippFlowDefinitionService.getParsedGraphData(any(), any())).thenReturn("testFlowDefinition");
        doNothing().when(this.flowDefinitionService).validateDefinitionData(any());

        OperationContext operationContext = new OperationContext();
        operationContext.setName("admin");
        AppExportDto exportConfig = this.appBuilderAppService.export("123", operationContext);

        assertThat(exportConfig.getApp()).extracting(AppExportApp::getName, AppExportApp::getVersion,
                AppExportApp::getAppType).containsExactly("testApp", "1.0.0", "单元测试");
        assertThat(exportConfig.getConfig().getConfigProperties()).hasSize(1)
                .map(AppExportConfigProperty::getFormProperty)
                .element(0)
                .extracting(AppExportFormProperty::getName, AppExportFormProperty::getDataType,
                        AppExportFormProperty::getDefaultValue)
                .containsExactly("test", "String", "\"test\"");
        assertThat(exportConfig.getFlowGraph()).extracting(AppExportFlowGraph::getName,
                AppExportFlowGraph::getAppearance).containsExactly("testFlowGraph", "{\"id\": \"testGraphId\"}");
    }

    @Test
    @DisplayName("校验模型节点配置")
    void testModelNode() {
        String testNode = "{\"type\":\"knowledgeRetrievalNodeState\",\"nodeInfos\":[{\"nodeId\":\"jadenthrcv\","
                + "\"nodeName\":\"知识检索\",\"configs\":[{\"id\":14,\"configName\":\"knowledgeRepos\",\"name\":\"k14\","
                + "\"description\":\"\",\"type\":\"VECTOR\",\"createdAt\":\"2024-12-02 12:41:14\",\"checked\":true},"
                + "{\"id\":2,\"configName\":\"knowledgeRepos\",\"name\":\"k2\",\"description\":\"\","
                + "\"type\":\"VECTOR\"," + "\"createdAt\":\"2024-12-02 12:37:17\",\"checked\":true}]}]}";
        AppCheckDto appCheckDto = JsonUtils.parseObject(testNode, AppCheckDto.class);
        try (MockedStatic<CheckerFactory> mockedStatic = mockStatic(CheckerFactory.class)) {
            mockedStatic.when(() -> CheckerFactory.getChecker(anyString())).thenReturn(new RetrievalNodeChecker());
            List<CheckResult> results = this.appBuilderAppService.checkAvailable(
                    Collections.singletonList(appCheckDto), null);
            Assertions.assertFalse(results.get(0).isValid());
            Assertions.assertEquals(results.get(0).getConfigChecks().size(), 2);
        }
    }

    @Test
    @DisplayName("测试应用导入")
    void testImportAppConfig() throws IOException {
        when(this.aippFlowService.previewAipp(anyString(), any(), any())).thenReturn(
                AippCreateDto.builder().aippId("123456").build());
        ClassLoader classLoader = AppBuilderAppServiceImplTest.class.getClassLoader();
        String config = IoUtils.content(classLoader, IMPORT_CONFIG);
        OperationContext context = new OperationContext();
        context.setTenantId("123");
        context.setOperator("admin");
        when(this.appTypeService.queryAll(any())).thenReturn(
                Collections.singletonList(AppTypeDto.builder().name("other").build()));
        when(this.appTypeService.add(any(), any())).thenReturn(AppTypeDto.builder().id("1234556").build());
        AppBuilderAppDto appDto = this.appBuilderAppService.importApp(config, context);

        assertThat(appDto).extracting(AppBuilderAppDto::getVersion, AppBuilderAppDto::getCreateBy,
                        AppBuilderAppDto::getState, AppBuilderAppDto::getName, AppBuilderAppDto::getAippId,
                        AppBuilderAppDto::getAppType)
                .containsExactly("1.0.0", "admin", "importing", "fyz01", "123456", "1234556");
    }

    @Test
    @DisplayName("测试根据应用种类获取应用列表")
    void testListApplication() {
        AppBuilderApp app = this.mockApp("testId");
        when(this.appRepository.selectWithLatestApp(any(), any(), anyLong(), anyInt())).thenReturn(
                Collections.singletonList(app));
        AppQueryCondition cond = new AppQueryCondition();
        cond.setAppCategory("chatbot");
        RangedResultSet<AppBuilderAppMetadataDto> metaData = this.appBuilderAppService.list(cond, new OperationContext(), 0L,
                10).getData();
        AppBuilderAppMetadataDto dto = metaData.getResults().get(0);
        assertThat(dto).extracting(AppBuilderAppMetadataDto::getAppCategory).isEqualTo("chatbot");
    }

    @Test
    @DisplayName("测试恢复应用到指定版本")
    void testResetApp() {
        String currentAppId = "currentId";
        String resetAppId = "resetId";
        String resetTenantId = "default";

        AppBuilderApp currentApp = this.mockApp(currentAppId);
        String graphString = "{\"id\": \"graphId\", \"title\":\"graphId\"}, \"tenant\":\"tenantId\"";
        String currentGraphId = "graphId";
        AppBuilderFlowGraph currentGraph =
                AppBuilderFlowGraph.builder().appearance(graphString).id(currentGraphId).name("LLM template").build();
        currentApp.setFlowGraph(currentGraph);
        currentApp.setFlowGraphId(currentGraphId);

        when(this.appRepository.selectWithId(eq(currentAppId))).thenReturn(currentApp);
        when(this.appRepository.selectWithId(eq(resetAppId))).thenReturn(this.mockApp(resetAppId));
        AppBuilderAppDto dto = this.appBuilderAppService.resetApp(currentAppId, resetAppId, new OperationContext());

        assertThat(dto).extracting(dto1 -> dto1.getFlowGraph().getId(),
                        dto1 -> dto1.getFlowGraph().getAppearance().get("id"),
                        dto1 -> dto1.getFlowGraph().getAppearance().get("title"),
                        dto1 -> dto1.getFlowGraph().getAppearance().get("tenant"))
                .containsExactly(currentGraphId, currentGraphId, currentGraphId, resetTenantId);
    }
}
