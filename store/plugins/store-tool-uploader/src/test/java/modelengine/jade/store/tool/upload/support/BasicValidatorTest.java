/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildBlankParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildEmptyParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildNullParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateCompleteness;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateDefAndToolRepeat;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateNecessaryFiles;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validatePluginConstraintInfo;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.info.entity.ToolJsonEntity;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.tool.upload.config.PluginUploadConstraintConfig;
import modelengine.jade.store.tool.upload.support.processor.DefinitionProcessor;
import modelengine.jade.store.tool.upload.support.processor.ToolProcessor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 表示 {@link BasicValidator} 的测试类。
 *
 * @author 李金绪
 * @author 杭潇
 * @since 2024-10-29
 */
@DisplayName("测试 BasicValidator")
public class BasicValidatorTest {
    private static final String TEMP_DIR_PATH = "src/test/resources/temp";
    private static final String NEW_TOOL_JSON = "src/test/resources/tools.json";
    private static final String MOCK_TOOLS_PATH = "/mock/store/tools/";

    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);
    private PluginService pluginService;
    private PluginUploadConstraintConfig config;

    @BeforeEach
    void setUp() throws IOException {
        this.pluginService = mock(PluginService.class);
        this.config = new PluginUploadConstraintConfig();
        this.deleteZipFiles();
        File emptyZip = new File(TEMP_DIR_PATH, "empty.zip");
        emptyZip.createNewFile();
    }

    @AfterEach
    void teardown() {
        this.deleteZipFiles();
    }

    @Nested
    @DisplayName("测试校验容器中物理内存以及插件数量是否超出配置")
    class PluginConstraintInfoTest {
        FileStore fileStore = mock(FileStore.class);

        @BeforeEach
        void setup() throws IOException {
            config.setMaxStorageRatio(0.8);
            config.setMaxPluginNumber(3800);
            config.setToolsPath(MOCK_TOOLS_PATH);
            ListResult<PluginData> pluginDataListResult =
                    new ListResult<>(Collections.singletonList(new PluginData()), 3000);
            when(pluginService.getPlugins(new PluginQuery())).thenReturn(pluginDataListResult);
            when(fileStore.getTotalSpace()).thenReturn(100L);
            when(fileStore.getUsableSpace()).thenReturn(0L);
        }

        @Test
        @DisplayName("当给定较低的存储占用量，抛出异常")
        void shouldExWhenGivenLowStorageRatio() throws IOException {
            when(fileStore.getUsableSpace()).thenReturn(10L);
            assertThatThrownBy(() -> {
                try (MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
                    filesMockedStatic.when(() -> Files.getFileStore(Paths.get(MOCK_TOOLS_PATH))).thenReturn(fileStore);
                    validatePluginConstraintInfo(config, pluginService);
                }
            }).isInstanceOf(ModelEngineException.class).hasMessageContaining("The plugin upload exceeds the limit.");
        }

        @Test
        @DisplayName("当插件数量超出限制时，抛出异常")
        void shouldExWhenReachMaxPluginNumber() {
            assertThatThrownBy(() -> {
                try (MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
                    filesMockedStatic.when(() -> Files.getFileStore(Paths.get(MOCK_TOOLS_PATH))).thenReturn(fileStore);
                    validatePluginConstraintInfo(config, pluginService);
                }
            }).isInstanceOf(ModelEngineException.class).hasMessageContaining("The plugin upload exceeds the limit.");
        }
    }

    @Test
    @DisplayName("当缺少指定文件时，抛出异常")
    void shouldExWhenMissFile() {
        assertThatThrownBy(() -> {
            File tempDir = new File(TEMP_DIR_PATH);
            validateNecessaryFiles(tempDir);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file to upload does not contain the configuration file.");
    }

    @Test
    @DisplayName("当完整性校验不一致时，抛出异常")
    void testValidateCompletenessWithValidChecksum() throws Exception {
        assertThatThrownBy(() -> {
            File tempDir = new File(TEMP_DIR_PATH);
            validateCompleteness(tempDir, this.serializer);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("Integrity check failed because the plugin has been modified.");
    }

    @Test
    @DisplayName("正确构造 null 异常")
    void shouldExWhenNull() {
        ModelEngineException ex = buildNullParserException("file", "property");
        assertThat(ex.getMessage()).contains("The file must contain the property. [file='file', property='property']");
    }

    @Test
    @DisplayName("正确构造 empty 异常")
    void shouldExWhenEmpty() {
        ModelEngineException ex = buildEmptyParserException("file", "property");
        assertThat(ex.getMessage()).contains(
                "The file must contain the property and cannot be empty. [file='file', property='property']");
    }

    @Test
    @DisplayName("正确构造 blank 异常")
    void shouldExWhenBlank() {
        ModelEngineException ex = buildBlankParserException("file", "property");
        assertThat(ex.getMessage()).contains(
                "The file must contain the property and cannot be blank. [file='file', property='property']");
    }

    @Test
    @DisplayName("正确构造异常")
    void shouldExWhenBuild() {
        ModelEngineException ex = buildParserException("msg");
        assertThat(ex.getMessage()).contains("msg");
    }

    private void deleteZipFiles() {
        File dir = new File(TEMP_DIR_PATH);
        File[] zipFiles = dir.listFiles((d, name) -> name.endsWith(".zip"));
        if (zipFiles != null) {
            for (File zipFile : zipFiles) {
                zipFile.delete();
            }
        }
    }

    @Nested
    @DisplayName("测试重复的组信息")
    class ValidateGroupInfo {
        private JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);
        private ToolJsonEntity tool;
        private List<ToolGroupData> toolGroups;
        private List<DefinitionGroupData> defGroups;
        private ToolGroupService toolGroupService;
        private DefinitionGroupService defGroupService;

        @BeforeEach
        void setup() {
            this.tool = getFileInfo(new File(NEW_TOOL_JSON), this.serializer, ToolJsonEntity.class);
            this.toolGroups = cast(new ToolProcessor(this.serializer).transform(this.tool, new HashMap<>()));
            this.defGroups = cast(new DefinitionProcessor(this.serializer).transform(this.tool, new HashMap<>()));
            this.toolGroupService = mock(ToolGroupService.class);
            this.defGroupService = mock(DefinitionGroupService.class);
        }

        @Test
        @DisplayName("当定义组存在重复时，删除定义组")
        void shouldExWhenDefGroupRepeat() {
            when(this.defGroupService.findExistDefGroups(anySet())).thenReturn(Arrays.asList("defGroup_weather_Rain"));
            int size = this.defGroups.size();
            validateDefAndToolRepeat(this.defGroups, this.toolGroups, this.toolGroupService, this.defGroupService);
            assertThat(this.defGroups.size()).isEqualTo(size - 1);
        }

        @Test
        @DisplayName("当定义组下的定义存在重复时，抛出异常")
        void shouldExWhenDefNameInDefGroupRepeat() {
            when(this.defGroupService.findFirstExistDefGroup(anySet())).thenReturn(StringUtils.EMPTY);
            when(this.defGroupService.findFirstExistDefNameInDefGroup(anyString(), anySet())).thenReturn(
                    "defGroupName.defName");
            assertThatThrownBy(() -> {
                validateDefAndToolRepeat(this.defGroups, this.toolGroups, this.toolGroupService, this.defGroupService);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining("The current operation has duplicate property. [property='definitionName', "
                            + "value='defGroupName.defName']");
        }

        @Test
        @DisplayName("当实现组下的实现存在重复时，抛出异常")
        void shouldExWhenToolNameInToolGroupRepeat() {
            when(this.defGroupService.findExistDefGroups(anySet())).thenReturn(Collections.emptyList());
            when(this.defGroupService.findFirstExistDefNameInDefGroup(anyString(),
                    anySet())).thenReturn(StringUtils.EMPTY);
            when(this.toolGroupService.get(anyString(), anyList())).thenReturn(this.toolGroups);
            assertThatThrownBy(() -> {
                validateDefAndToolRepeat(this.defGroups, this.toolGroups, this.toolGroupService, this.defGroupService);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining("The current operation has duplicate property. [property='toolName', "
                            + "value='implGroup_weather_rain_city_a.city_a_rain_today']");
        }

        @Test
        @DisplayName("当定义组下的实现组存在重复时，抛出异常")
        void shouldExWhenToolGroupInDefGroupRepeat() {
            when(this.defGroupService.findFirstExistDefGroup(anySet())).thenReturn(StringUtils.EMPTY);
            when(this.defGroupService.findFirstExistDefNameInDefGroup(anyString(),
                    anySet())).thenReturn(StringUtils.EMPTY);
            when(this.toolGroupService.get(anyString(), anyList())).thenReturn(Collections.emptyList());
            when(this.toolGroupService.findFirstExistToolGroupInDefGroup(anyString(), anyString())).thenReturn(
                    "defGroup.toolGroup");
            assertThatThrownBy(() -> {
                validateDefAndToolRepeat(this.defGroups, this.toolGroups, this.toolGroupService, this.defGroupService);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining(
                            "The current operation has duplicate property. [property='toolGroupName', value='defGroup"
                                    + ".toolGroup']");
        }
    }
}
