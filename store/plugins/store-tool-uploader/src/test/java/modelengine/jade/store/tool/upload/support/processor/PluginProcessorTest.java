/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support.processor;

import static modelengine.fel.tool.info.schema.PluginSchema.GROUP_ID;
import static modelengine.fel.tool.info.schema.PluginSchema.TEMP_DIR;
import static modelengine.fel.tool.info.schema.PluginSchema.TYPE;
import static modelengine.fel.tool.info.schema.PluginSchema.UNIQUENESS;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getJsonInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

import modelengine.fel.tool.info.entity.PluginJsonEntity;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.log.Logger;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.entity.transfer.PluginData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * 表示 {@link PluginProcessor} 的测试类。
 *
 * @author 李金绪
 * @since 2024-10-30
 */
@DisplayName("测试 PluginProcessor")
public class PluginProcessorTest {
    private static final String PLUGIN_JAVA_JSON = "src/test/resources/pluginJava.json";
    private static final String PLUGIN_PYTHON_JSON = "src/test/resources/pluginPython.json";
    private static final String PLUGIN_JAVA_JSON_ERROR = "src/test/resources/pluginJavaError.json";
    private static final String TEMP_DIR_PATH = "src/test/resources/temp";
    private static final Logger log = Logger.get(PluginProcessorTest.class);

    private final JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    private PluginProcessor processor;
    private PluginJsonEntity javaPlugin;
    private PluginJsonEntity pythonPlugin;

    @BeforeEach
    void setup() throws IOException {
        this.processor = new PluginProcessor(this.serializer);
        this.javaPlugin = getFileInfo(new File(PLUGIN_JAVA_JSON), this.serializer, PluginJsonEntity.class);
        this.pythonPlugin = getFileInfo(new File(PLUGIN_PYTHON_JSON), this.serializer, PluginJsonEntity.class);
        this.deleteZipFiles();
        File emptyZip = new File(TEMP_DIR_PATH, "empty.zip");
        emptyZip.createNewFile();
    }

    @AfterEach
    void teardown() {
        this.deleteZipFiles();
    }

    @Test
    @DisplayName("plugin.json - java 校验无异常")
    void shouldOkWhenJava() {
        assertDoesNotThrow(() -> {
            this.processor.validate(this.javaPlugin, null);
        });
    }

    @Test
    @DisplayName("plugin.json - python 校验无异常")
    void shouldOkWhenPython() {
        assertDoesNotThrow(() -> {
            this.processor.validate(this.pythonPlugin, null);
        });
    }

    @Test
    @DisplayName("plugin.json - java 新版校验无异常")
    void shouldOkWhenJavaNew() {
        assertDoesNotThrow(() -> {
            this.processor.validate(this.javaPlugin, null);
        });
    }

    @Test
    @DisplayName("当 checksum 为空白时，抛出异常")
    void shouldExWhenChecksumBlank() {
        assertThatThrownBy(() -> {
            this.javaPlugin.setChecksum("");
            this.processor.validate(this.javaPlugin, null);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property and cannot be blank. [file='plugin.json', "
                        + "property='checksum']");
    }

    @Test
    @DisplayName("当 description 为空时，抛出异常")
    void shouldExWhenDescriptionNull() {
        assertThatThrownBy(() -> {
            this.javaPlugin.setDescription(null);
            this.processor.validate(this.javaPlugin, null);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property. [file='plugin.json', "
                        + "property='description']");
    }

    @Test
    @DisplayName("当创建 PluginData 时，成功")
    void shouldOkWhenBuildPlugin() {
        try (MockedStatic<Processor> mockedStatic = mockStatic(Processor.class)) {
            mockedStatic.when(Processor::getUserName).thenReturn("testName");
            File tempDir = new File(TEMP_DIR_PATH);
            PluginData pluginData =
                    cast(this.processor.transform(this.javaPlugin, Collections.singletonMap(TEMP_DIR, tempDir)));
            assertThat(pluginData).isNotNull();
        }
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
    @DisplayName("测试处理 plugin.json 的唯一性信息")
    class ValidateUniqueness {
        Map<String, Object> javaPluginMap = getJsonInfo(new File(PLUGIN_JAVA_JSON), serializer);

        @Test
        @DisplayName("当 plugin.json 的 type 字段为空时，抛出异常")
        void shouldExWhenLanguageNull() {
            javaPluginMap.put(TYPE, null);
            this.mapToFile(javaPluginMap, PLUGIN_JAVA_JSON_ERROR);
            assertThatThrownBy(() -> {
                PluginJsonEntity pluginJsonEntity =
                        getFileInfo(new File(PLUGIN_JAVA_JSON_ERROR), serializer, PluginJsonEntity.class);
                processor.validate(pluginJsonEntity, null);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining(
                            "The file must contain the property and cannot be blank. [file='plugin.json', "
                                    + "property='type']");
        }

        @Test
        @DisplayName("当 plugin.json 的 type 字段为list时，抛出异常")
        void shouldExWhenLanguageInvalid() {
            javaPluginMap.put(TYPE, Collections.emptyList());
            this.mapToFile(javaPluginMap, PLUGIN_JAVA_JSON_ERROR);
            assertThatThrownBy(() -> {
                PluginJsonEntity pluginJsonEntity =
                        getFileInfo(new File(PLUGIN_JAVA_JSON_ERROR), serializer, PluginJsonEntity.class);
                processor.validate(pluginJsonEntity, null);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining("The property in the file should be of a certain type. [property='type', "
                            + "file='pluginJavaError.json', type='java.lang.String']");
        }

        @Test
        @DisplayName("当 plugin.json 的 java.groupid 字段为list时，抛出异常")
        void shouldExWhenJavaInvalid() {
            Map<String, Object> uniqueness = cast(javaPluginMap.get(UNIQUENESS));
            uniqueness.put(GROUP_ID, Collections.emptyList());
            this.mapToFile(javaPluginMap, PLUGIN_JAVA_JSON_ERROR);
            assertThatThrownBy(() -> {
                PluginJsonEntity pluginJsonEntity =
                        getFileInfo(new File(PLUGIN_JAVA_JSON_ERROR), serializer, PluginJsonEntity.class);
                processor.validate(pluginJsonEntity, null);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining(
                            "The property in the file should be of a certain type. [property='uniqueness.groupId', "
                                    + "file='pluginJavaError.json', type='java.lang.String']");
        }

        private void mapToFile(Map<String, Object> input, String path) {
            try {
                serializer.serialize(input, Files.newOutputStream(Paths.get(path)));
            } catch (IOException ex) {
                log.error("Failed to transform to file: {}", path, ex);
            }
        }
    }
}
