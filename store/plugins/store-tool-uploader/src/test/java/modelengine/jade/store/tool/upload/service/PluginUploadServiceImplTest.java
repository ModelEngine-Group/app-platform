/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.service;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateSelectTools;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.info.entity.ToolJsonEntity;
import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.support.DefaultNamedEntity;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.FileUtils;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.support.DeployStatus;
import modelengine.jade.store.tool.upload.config.PluginUploadConstraintConfig;
import modelengine.jade.store.tool.upload.service.impl.PluginUploadServiceImpl;
import modelengine.jade.store.tool.upload.support.processor.DefinitionProcessor;
import modelengine.jade.store.tool.upload.support.processor.ProcessorFactory;
import modelengine.jade.store.tool.upload.support.processor.ToolProcessor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link PluginUploadServiceImpl} 的测试。
 *
 * @author 李金绪
 * @author 杭潇
 * @author 罗帅
 * @since 2024-10-31
 */
@DisplayName("测试 PluginUploadServiceImpl")
@ExtendWith(MockitoExtension.class)
public class PluginUploadServiceImplTest {
    private static final String NEW_TOOL_JSON = "src/test/resources/tools.json";

    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    @Mock
    private PluginService mockPluginService;
    @Mock
    private ProcessorFactory mockProcessorFactory;
    @Mock
    private DefinitionGroupService mockDefGroupService;
    @Mock
    private ToolGroupService mockToolGroupService;
    private PluginUploadServiceImpl pluginUploadService;
    private PluginUploadConstraintConfig pluginUploadConstraintConfig;

    private PluginData mockPluginData() {
        final PluginData pluginData = new PluginData();
        pluginData.setPluginId("pluginId");
        pluginData.setPluginName("pluginName");
        Map<String, Object> extension = new HashMap<>();
        extension.put("pluginFullName", "pluginFullName.zip");
        extension.put("type", "python");
        extension.put("name", "name");
        extension.put("pluginName", "pluginName");
        extension.put("uniqueness.name", "uniqueness.name");
        pluginData.setExtension(extension);
        pluginData.setDeployStatus(DeployStatus.UNDEPLOYED.name());
        pluginData.setPluginToolDataList(Collections.singletonList(this.mockPluginToolData()));
        pluginData.setBuiltin(false);
        return pluginData;
    }

    private PluginToolData mockPluginToolData() {
        final PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setDescription("description");
        pluginToolData.setSchema(new HashMap<>());
        Map<String, Object> runnables = new HashMap<>();
        Map<String, Object> fitables = new HashMap<>();
        fitables.put("fitableId", "multi_fid");
        fitables.put("genericableId", "multi_gid");
        runnables.put("FIT", fitables);
        pluginToolData.setRunnables(runnables);
        pluginToolData.setTags(new HashSet<>(Collections.singletonList("value")));
        pluginToolData.setName("name");
        return pluginToolData;
    }

    @BeforeEach
    void setUp() {
        this.pluginUploadConstraintConfig = new PluginUploadConstraintConfig();
        this.pluginUploadConstraintConfig.setMaxStorageRatio(0.9);
        this.pluginUploadConstraintConfig.setMaxPluginNumber(3000);
        this.pluginUploadConstraintConfig.setToolsPath("/var/");
        this.pluginUploadService = new PluginUploadServiceImpl(this.mockPluginService,
                this.serializer,
                this.pluginUploadConstraintConfig,
                this.mockProcessorFactory,
                this.mockDefGroupService,
                this.mockToolGroupService);
        Path testPath = Paths.get("/var/");
        try {
            FileUtils.ensureDirectory(testPath.toFile());
        } catch (IOException e) {
            throw new FitException(e);
        }
    }

    @AfterEach
    void teardown() {
        Path targetFile = Paths.get("src/test/resources/tool.tar");
        if (Files.exists(targetFile)) {
            FileUtils.delete(targetFile.toFile());
        }
        Path testFile = Paths.get("src/test/resources/test/");
        if (Files.exists(testFile)) {
            FileUtils.delete(testFile.toFile());
        }
    }

    @Test
    @DisplayName("删除插件成功")
    void testDeletePlugin() {
        when(this.mockPluginService.getPlugin("pluginId")).thenReturn(mockPluginData());
        final int result = this.pluginUploadService.deletePlugin("pluginId");
        assertThat(result).isEqualTo(1);
        verify(this.mockPluginService).deletePlugin("pluginId");
    }

    @Test
    @DisplayName("删除无效插件符合预期")
    void testDeleteInvalidPlugin() {
        when(this.mockPluginService.getPlugin("pluginId")).thenReturn(new PluginData());
        final int result = this.pluginUploadService.deletePlugin("pluginId");
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("上传错误后缀文件时抛出预期异常")
    void testUploadPluginsWithWrongName() throws IOException {
        HttpMessage httpMessage = mock(HttpMessage.class);
        Entity entity = FileEntity.createAttachment(httpMessage, "tool.jar", mock(InputStream.class), 100);
        try (NamedEntity namedEntity = new DefaultNamedEntity(httpMessage, "generic", entity)) {
            ModelEngineException modelEngineException = catchThrowableOfType(() -> pluginUploadService.uploadPlugins(
                    Collections.singletonList(namedEntity), Arrays.asList("toolName")), ModelEngineException.class);
            assertThat(modelEngineException.getCode()).isEqualTo(130901002);
        }
    }

    @Test
    @DisplayName("当选择部分工具上传时，成功")
    void shouldOkWhenValidateSelectTools() {
        List<String> selectTools = Arrays.asList(
                "defGroup_weather_Rain.implGroup_weather_rain_city_a.rain_today.city_a_rain_tomorrow",
                "defGroup_weather_Rain.implGroup_weather_rain_city_b.rain_tomorrow.city_b_rain_tomorrow");
        ToolProcessor toolProcessor = new ToolProcessor(this.serializer);
        DefinitionProcessor defProcessor = new DefinitionProcessor(this.serializer);
        ToolJsonEntity toolJsonEntity = getFileInfo(new File(NEW_TOOL_JSON), serializer, ToolJsonEntity.class);
        List<ToolGroupData> toolGroups = cast(toolProcessor.process(toolJsonEntity, new HashMap<>()));
        List<DefinitionGroupData> defGroups = cast(defProcessor.process(toolJsonEntity, new HashMap<>()));
        validateSelectTools(toolGroups, defGroups, selectTools);
        assertThat(toolGroups.size()).isEqualTo(1);
        assertThat(defGroups.size()).isEqualTo(1);
    }
}