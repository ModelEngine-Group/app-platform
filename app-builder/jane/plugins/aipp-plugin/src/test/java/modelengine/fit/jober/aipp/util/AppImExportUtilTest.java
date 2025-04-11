/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.dto.export.AppExportConfig;
import modelengine.fit.jober.aipp.dto.export.AppExportConfigProperty;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;
import modelengine.fit.jober.aipp.dto.export.AppExportFormProperty;
import modelengine.fit.jober.aipp.service.StoreServiceImplTest;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 应用导入导出工具类单元测试。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
public class AppImExportUtilTest {
    private static final String IMPORT_CONFIG = "component/import_config.json";

    @Test
    @DisplayName("测试将 AppBuilderApp 转换为 AppExportApp")
    void testConvertToAppExportApp() {
        AppBuilderApp mockApp = AppBuilderApp.builder()
                .name("testApp")
                .tenantId("tenant123")
                .type("testType")
                .version("1.0.0")
                .attributes(new HashMap<>())
                .build();

        AppExportApp exportApp = AppImExportUtil.convertToAppExportApp(mockApp);
        assertThat(exportApp).extracting(AppExportApp::getName, AppExportApp::getTenantId, AppExportApp::getType,
                AppExportApp::getVersion).containsExactly("testApp", "tenant123", "testType", "1.0.0");
    }

    @Test
    @DisplayName("测试将 AppBuilderConfig 转换为 AppExportConfig")
    void testConvertToAppExportConfig() {
        List<AppBuilderConfigProperty> mockConfigProperties = Collections.singletonList(
                AppBuilderConfigProperty.builder().id("123").nodeId("456").formPropertyId("789").build());
        List<AppBuilderFormProperty> mockFormProperties = Collections.singletonList(AppBuilderFormProperty.builder()
                .id("789")
                .formId("369")
                .name("test")
                .dataType("String")
                .defaultValue("test")
                .build());
        AppBuilderForm mockForm = mock(AppBuilderForm.class);
        AppBuilderApp mockApp = mock(AppBuilderApp.class);
        AppBuilderConfig mockConfig = AppBuilderConfig.builder()
                .id("258")
                .form(mockForm)
                .configProperties(mockConfigProperties)
                .app(mockApp)
                .build();
        when(mockApp.getFormProperties()).thenReturn(mockFormProperties);
        AppExportConfig appExportConfig = AppImExportUtil.convertToAppExportConfig(mockConfig);
        assertThat(appExportConfig.getConfigProperties()).hasSize(1)
                .map(AppExportConfigProperty::getFormProperty)
                .element(0)
                .extracting(AppExportFormProperty::getName, AppExportFormProperty::getDataType,
                        AppExportFormProperty::getDefaultValue)
                .containsExactly("test", "String", "\"test\"");
    }

    @Test
    @DisplayName("测试将 AppBuilderFlowGraph 转换为 AppExportFlowGraph")
    void testConvertToAppExportFlowGraph() {
        AppBuilderFlowGraph mockFlowGraph = AppBuilderFlowGraph.builder()
                .id("123")
                .name("testFlowGraph")
                .appearance("testAppearance")
                .build();

        AppExportFlowGraph exportFlowGraph = AppImExportUtil.convertToAppExportFlowGraph(mockFlowGraph);
        assertThat(exportFlowGraph).extracting(AppExportFlowGraph::getName, AppExportFlowGraph::getAppearance)
                .containsExactly("testFlowGraph", "testAppearance");
    }

    @Test
    @DisplayName("测试获取头像文件后缀")
    void testExtractIconExtension() {
        String iconFileName = "test.jpeg";
        assertThat(AppImExportUtil.extractIconExtension(iconFileName)).isEqualTo("jpeg");

        iconFileName = "test.png.bmp";
        assertThat(AppImExportUtil.extractIconExtension(iconFileName)).isEqualTo("bmp");

        iconFileName = "test";
        assertThat(AppImExportUtil.extractIconExtension(iconFileName)).isEqualTo("");
    }

    @Test
    @DisplayName("测试校验应用导入配置文件")
    void testCheckImportConfig() throws IOException {
        ClassLoader classLoader = AppImExportUtil.class.getClassLoader();
        String config = IoUtils.content(classLoader, IMPORT_CONFIG);
        AppExportDto configDto = JsonUtils.parseObject(config, AppExportDto.class);
        AppImExportUtil.checkAppExportDto(configDto);
    }

    @Test
    @DisplayName("测试将应用导出配置 AppExportDto 转换为 AppBuilderApp")
    void testConvertToAppBuilderApp() throws IOException {
        ClassLoader classLoader = StoreServiceImplTest.class.getClassLoader();
        String config = IoUtils.content(classLoader, IMPORT_CONFIG);
        AppExportDto configDto = JsonUtils.parseObject(config, AppExportDto.class);
        configDto.getApp().getAttributes().put("name", configDto.getApp().getName());
        OperationContext context = new OperationContext("123", "admin", null, "123456", null, "admin", "127.0.0.1",
                "windows", "cn_zh");

        AppBuilderApp app = AppImExportUtil.convertToAppBuilderApp(configDto, context);
        assertThat(app).extracting(AppBuilderApp::getType, AppBuilderApp::getState, AppBuilderApp::getVersion,
                AppBuilderApp::getTenantId).containsExactly("app", "importing", "1.0.2", "123");
        assertThat(app.getAttributes()).containsEntry("icon", "");
        assertThat(app.getConfig().getConfigProperties()).hasSize(10);
        assertThat(app.getConfig().getTenantId()).isEqualTo("123");
        assertThat(app.getConfig().getForm().getTenantId()).isEqualTo("123");
    }

    @Test
    @DisplayName("测试读取所有 bytes")
    void testReadAllBytes() throws IOException {
        String testStr = "test";
        try (InputStream inputStream = new ByteArrayInputStream(testStr.getBytes(StandardCharsets.UTF_8))) {
            byte[] result = AppImExportUtil.readAllBytes(inputStream);
            assertThat(result).isEqualTo(testStr.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    @DisplayName("测试保存头像文件")
    @Disabled("无法跑通，需要仔细审视")
    void testSaveIconFile() throws IOException {
        String iconContent = new String(
                Base64.getEncoder().encode("This is an icon png.".getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        String iconUrl = AppImExportUtil.saveIconFile(iconContent, "png", "123", "/api/jober");
        assertThat(iconUrl).startsWith("/api/jober/v1/api/123");

        String iconPath = AippFileUtils.getFileNameFromIcon(iconUrl);
        File iconFile = Paths.get(iconPath).toFile();
        assertThat(iconFile.exists()).isEqualTo(true);
        String content = new String(Base64.getDecoder().decode(iconContent.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        assertThat(content).isEqualTo("This is an icon png.");
        iconFile.delete();
    }

    @Test
    @DisplayName("测试拒绝非法的图像保存")
    void testIllegalIconSave() {
        String iconContent = new String(
                Base64.getEncoder().encode("This is an icon png.".getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        String iconUrl = AppImExportUtil.saveIconFile(iconContent, "txt", "123", "/api/jober");
        assertThat(iconUrl).isEqualTo(StringUtils.EMPTY);

        iconUrl = AppImExportUtil.saveIconFile(iconContent, "../../../.jpg", "123", "/api/jober");
        assertThat(iconUrl).isEqualTo(StringUtils.EMPTY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.yml", "test.json.txt", "test"})
    @DisplayName("测试检测文件是否为json文件")
    void testJsonFile(String fileName) {
        assertThat(AppImExportUtil.isJsonFile(fileName)).isEqualTo(false);
    }
}
