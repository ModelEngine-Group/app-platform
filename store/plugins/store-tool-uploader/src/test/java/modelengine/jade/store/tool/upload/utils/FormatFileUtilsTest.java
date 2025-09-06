/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.utils;

import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.buildDefGroupMap;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getJsonInfo;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.objToMap;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.unzipPlugin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.info.entity.SchemaEntity;
import modelengine.fel.tool.info.entity.ToolJsonEntity;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.util.FileUtils;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 表示 {@link FormatFileUtils} 的单元测试。
 *
 * @author 李金绪
 * @since 2024-10-29
 */
@DisplayName("测试 FormatFileUtils")
public class FormatFileUtilsTest {
    private static final String NEW_TOOL_JSON = "src/test/resources/tools.json";

    private final JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    @Test
    @DisplayName("当转换为字符串时，成功")
    void shouldOkWhenReturnString() {
        String input = "test";
        String output = FormatFileUtils.objToString(input);
        assertThat(output).isEqualTo(input);
    }

    @Test
    @DisplayName("当转换为字符串时，抛出异常")
    void shouldExWhenReturnString() {
        List<String> input = new ArrayList<>();
        assertThatThrownBy(() -> FormatFileUtils.objToString(input)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Object can not cast to string.");
    }

    @Test
    @DisplayName("当获取临时文件夹下的文件时，成功")
    void shouldOkWhenGetFiles() {
        File tempDir = mock(File.class);
        File[] files = {new File("file1"), new File("file2")};
        when(tempDir.listFiles()).thenReturn(files);
        assertThat(files.length).isEqualTo(2);
    }

    @Test
    @DisplayName("当获取指定文件时，成功")
    void shouldOkWhenGetFileByName() {
        File tempDir = mock(File.class);
        File expectedFile = mock(File.class);
        File[] files = {expectedFile};
        String targetFileName = "testFile.txt";
        when(tempDir.listFiles()).thenReturn(files);
        when(expectedFile.getName()).thenReturn(targetFileName);
        File result = FormatFileUtils.getFileByName(tempDir, targetFileName);
        assertThat(result.getName()).isEqualTo(targetFileName);
    }

    @Test
    @DisplayName("当 json 转 map 时，成功")
    void shouldOkWhenJsonFileToMap() {
        File jsonFile = new File("src/test/resources/schema.json");
        Map<String, Object> jsonInfo = getJsonInfo(jsonFile, this.serializer);
        assertThat(jsonInfo.get("name")).isEqualTo("rain_today");
    }

    @Test
    @DisplayName("当 json 转 object 时，成功")
    void shouldOkWhenJsonFileToObj() {
        File jsonFile = new File("src/test/resources/schema.json");
        SchemaEntity schema = getFileInfo(jsonFile, this.serializer, SchemaEntity.class);
        assertThat(schema.getParameters().getType()).isEqualTo("object");
    }

    @Test
    @DisplayName("当 object 转 map 时，正确")
    void shouldOkWhenObjToMap() {
        SchemaEntity schemaEntity = new SchemaEntity();
        schemaEntity.setName("testName");
        Map<String, Object> res = objToMap(this.serializer, schemaEntity);
        assertThat(res.containsKey("name")).isEqualTo(true);
    }

    @Test
    @DisplayName("当创建 DefGroupMap，正确")
    void shouldOkWhenBuildDefGroupMap() {
        ToolJsonEntity toolJsonEntity = getFileInfo(new File(NEW_TOOL_JSON), serializer, ToolJsonEntity.class);
        Map<String, Object> defGroupMap = buildDefGroupMap(toolJsonEntity);
        assertThat(defGroupMap.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("给定错误的 zip 文件，解压报错")
    @Disabled("配置目录不能写死")
    void givenErrorZipFileThenCatchException() throws IOException {
        File zipFile = new File("test-archive.zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            ZipEntry entry = new ZipEntry("../unauthorized-file.txt");
            zos.putNextEntry(entry);
            zos.write("Malicious content".getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        FileEntity fileEntity = mock(FileEntity.class);
        when(fileEntity.filename()).thenReturn("test-archive.zip");
        when(fileEntity.getInputStream()).thenReturn(Files.newInputStream(zipFile.toPath()));
        ModelEngineException modelEngineException =
                assertThrows(ModelEngineException.class, () -> unzipPlugin(fileEntity));
        assertThat(modelEngineException.getMessage()).isEqualTo("Failed to unzip plugin file. [file=test-archive.zip]");
        FileUtils.delete(zipFile);
    }
}