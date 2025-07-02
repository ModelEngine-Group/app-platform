/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.utils;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildParserException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import modelengine.fel.tool.info.entity.DefinitionEntity;
import modelengine.fel.tool.info.entity.DefinitionGroupEntity;
import modelengine.fel.tool.info.entity.ToolJsonEntity;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.UuidUtils;
import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示插件的文件工具类。
 *
 * @author 李金绪
 * @since 2024-10-29
 */
public class FormatFileUtils {
    private static final Logger log = Logger.get(FormatFileUtils.class);
    private static final String TEMPORARY_TOOL_PATH = "/var/temporary/tools";
    private static final Set<String> COMPRESSED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(".zip", ".tar", ".jar"));

    /**
     * 将 {@link Object} 类型数据转化为 {@link String} 类型数据。
     *
     * @param object 表示待转换的 {@link Object} 数据。
     * @return 表示转换后的 {@link String} 数据。
     */
    public static String objToString(Object object) {
        if (object instanceof String) {
            return cast(object);
        }
        throw new IllegalStateException("Object can not cast to string.");
    }

    /**
     * 获取插件包内的所有文件。
     *
     * @param tempDir 表示插件包解压后的插件文件的 {@link File}。
     * @return 表示获取到的插件文件的 {@link File}{@code []}。
     */
    public static File[] getFiles(File tempDir) {
        File[] files = tempDir.listFiles();
        return notNull(files, () -> buildParserException("The file in the plugin cannot be null."));
    }

    /**
     * 获取插件包内的压缩文件。
     *
     * @param tempDir 表示插件包解压后的插件文件的 {@link File}。
     * @return 表示获取到的压缩文件的 {@link File}。
     */
    public static File getCompressedFile(File tempDir) {
        for (File file : getFiles(tempDir)) {
            String fileExtension = FileUtils.extension(file.getName());
            if (COMPRESSED_FILE_EXTENSIONS.contains(fileExtension)) {
                return file;
            }
        }
        throw new ModelEngineException(PluginRetCode.NO_PLUGIN_FOUND_ERROR);
    }

    /**
     * 表示获取插件包内的插件文件。
     *
     * @param tempDir 表示插件包解压后的插件文件的 {@link File}。
     * @param targetFileName 表示目标文件名的 {@link String}。
     * @return 表示获取到的目标文件的 {@link File}。
     */
    public static File getFileByName(File tempDir, String targetFileName) {
        File[] files = notNull(tempDir.listFiles(), "The file in the plugin cannot be null.");
        for (File file : files) {
            if (file.getName().equals(targetFileName)) {
                return file;
            }
        }
        throw new ModelEngineException(PluginRetCode.FILE_MISSING_ERROR, targetFileName);
    }

    /**
     * 将文件的 json 内容转换为 map 格式数据。
     *
     * @param jsonFile 表示待处理的 json 文件的 {@link File}。
     * @param serializer 表示对象序列化的序列化器的 {@link ObjectSerializer}。
     * @return 表示转换后的 map 数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> getJsonInfo(File jsonFile, ObjectSerializer serializer) {
        notNull(jsonFile, "The json file cannot be null.");
        try (InputStream in = Files.newInputStream(jsonFile.toPath())) {
            return serializer.deserialize(in, Map.class);
        } catch (IOException e) {
            throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR, e);
        }
    }

    /**
     * 解压插件文件。
     *
     * @param file 表示待解压的插件文件的 {@link FileEntity}。
     * @return 表示解压后的插件目录的 {@link File}。
     */
    public static File unzipPlugin(FileEntity file) {
        String filename = file.filename();
        if (!filename.endsWith(".zip")) {
            throw new ModelEngineException(PluginRetCode.UPLOADED_FILE_FORMAT_ERROR);
        }
        File targetTemporaryFile = Paths.get(TEMPORARY_TOOL_PATH, UuidUtils.randomUuidString()).toFile();
        storeTemporaryFile(filename, file, targetTemporaryFile);
        log.info("Save the file to the temporary file directory. [fileName='{}']", filename);
        File tempDir = new File(TEMPORARY_TOOL_PATH, "unzip");
        try {
            FileUtils.unzip(targetTemporaryFile).target(tempDir).start();
        } catch (IOException | SecurityException e) {
            FileUtils.delete(targetTemporaryFile);
            FileUtils.delete(tempDir);
            throw new ModelEngineException(PluginRetCode.UNZIP_FILE_ERROR, e, filename);
        }
        FileUtils.delete(targetTemporaryFile);
        return tempDir;
    }

    /**
     * 将上传的文件临时存储到指定的目录。
     *
     * @param fileName 表示上传文件的文件名的 {@link String}。
     * @param file 表示上传文件的 {@link FileEntity}。
     * @param targetFile 表示目标文件的 {@link File}。
     */
    private static void storeTemporaryFile(String fileName, FileEntity file, File targetFile) {
        File targetDirectory = targetFile.getParentFile();
        try {
            FileUtils.ensureDirectory(targetDirectory);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to create directories for the file. [fileName='{0}']",
                    fileName), e);
        }

        try (InputStream inStream = file.getInputStream();
             OutputStream outStream = Files.newOutputStream(targetFile.toPath())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            FileUtils.delete(targetFile.getPath());
            throw new IllegalStateException(StringUtils.format("Failed to write file. [fileName='{0}']", fileName), e);
        }
    }

    /**
     * 将文件的 json 内容转换为指定格式的对象格式数据。
     *
     * @param jsonFile 表示待处理的 json 文件的 {@link File}。
     * @param serializer 表示对象序列化的序列化器的 {@link ObjectSerializer}。
     * @param typeClass 表示目标类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @return 表述转换后的 map 数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static <T> T getFileInfo(File jsonFile, ObjectSerializer serializer, Class<T> typeClass) {
        notNull(jsonFile, "The json file cannot be null.");
        try (InputStream in = Files.newInputStream(jsonFile.toPath())) {
            return serializer.deserialize(in, typeClass);
        } catch (IOException e) {
            throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR, e);
        } catch (SerializationException e) {
            if (!(e.getCause() instanceof MismatchedInputException)) {
                throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR, e);
            }
            MismatchedInputException ex = cast(e.getCause());
            String path = buildSerializerErrorPath(ex);
            String msg = StringUtils.format(
                    "The property in the file should be of a certain type. [property='{0}', file='{1}', type='{2}']",
                    path,
                    jsonFile.getName(),
                    ex.getTargetType().getName());
            throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR, msg);
        }
    }

    private static String buildSerializerErrorPath(MismatchedInputException ex) {
        return ex.getPath().stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));
    }

    /**
     * 重命名文件。
     *
     * @param sourceFile 表示原始文件的 {@link File}。
     * @return 表示新生成的压缩文件的 {@link File}。
     */
    public static File renameFile(File sourceFile) {
        String sourceFileName = sourceFile.getName();
        File targetFile = new File(sourceFile.getParent(),
                FileUtils.ignoreExtension(sourceFileName) + "-" + System.currentTimeMillis() + FileUtils.extension(
                        sourceFileName));
        sourceFile.renameTo(targetFile);
        return targetFile;
    }

    /**
     * 将给定的对象序列化为 Map 对象。
     *
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @param object 表示待序列化的对象的 {@link Object}。
     * @return 返回序列化后的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> objToMap(ObjectSerializer serializer, Object object) {
        return cast(serializer.deserialize(serializer.serialize(object), Map.class));
    }

    /**
     * 构建定义组的 Map 对象。
     *
     * @param toolJsonEntity 表示工具的 JSON 实体的 {@link ToolJsonEntity}。
     * @return 返回构建的定义组的 Map 的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> buildDefGroupMap(ToolJsonEntity toolJsonEntity) {
        Map<String, Object> defGroupMap = new HashMap<>();
        for (DefinitionGroupEntity defGroupEntity : toolJsonEntity.getDefinitionGroups()) {
            Map<String, Object> defMap = new HashMap<>();
            for (DefinitionEntity defEntity : defGroupEntity.getDefinitions()) {
                defMap.put(defEntity.getSchema().getName(), defEntity);
            }
            defGroupMap.put(defGroupEntity.getName(), defMap);
        }
        return defGroupMap;
    }

    /**
     * 构建定义组的 Map 对象。
     *
     * @param defGroupDatas 表示定义组的 {@link List}{@code <}{@link DefinitionGroupData}{@code >}。
     * @return 返回构建的定义组的 Map 的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public static Map<String, Object> buildDefGroupMap(List<DefinitionGroupData> defGroupDatas) {
        Map<String, Object> defGroupMap = new HashMap<>();
        for (DefinitionGroupData defGroupData : defGroupDatas) {
            Map<String, Object> defMap = new HashMap<>();
            for (DefinitionData defData : defGroupData.getDefinitions()) {
                defMap.put(defData.getName(), defData);
            }
            defGroupMap.put(defGroupData.getName(), defMap);
        }
        return defGroupMap;
    }
}
