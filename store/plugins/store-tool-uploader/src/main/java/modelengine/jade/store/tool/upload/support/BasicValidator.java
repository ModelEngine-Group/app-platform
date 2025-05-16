/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support;

import static modelengine.fel.tool.info.schema.PluginSchema.CHECKSUM;
import static modelengine.fel.tool.info.schema.PluginSchema.PLUGIN_JSON;
import static modelengine.fel.tool.info.schema.ToolsSchema.DEFINITION_GROUP_NAME_IN_TOOL;
import static modelengine.fel.tool.info.schema.ToolsSchema.DEFINITION_NAME;
import static modelengine.fel.tool.info.schema.ToolsSchema.TOOLS_JSON;
import static modelengine.fel.tool.info.schema.ToolsSchema.TOOL_GROUP_NAME;
import static modelengine.fel.tool.info.schema.ToolsSchema.TOOL_NAME;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.code.PluginRetCode.JSON_PARSE_ERROR;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getCompressedFile;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileByName;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFiles;

import modelengine.fel.tool.info.entity.PluginJsonEntity;
import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.SecurityUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.tool.upload.config.PluginUploadConstraintConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基础校验：包含插件级别及最基础的校验。
 *
 * @author 李金绪
 * @since 2024-10-25
 */
public class BasicValidator {
    private static final String OR = "OR";
    private static final String DOT = ".";

    /**
     * 表示校验容器中物理内存以及插件数量是否超出配置值。
     *
     * @param pluginUploadConstraintConfig 表示插件上传的约束性配置参数的 {@link PluginUploadConstraintConfig}。
     * @param pluginService 表示插件服务的 {@link PluginService}。
     */
    public static void validatePluginConstraintInfo(PluginUploadConstraintConfig pluginUploadConstraintConfig,
            PluginService pluginService) {
        // 校验容器物理存储是否超过限制
        try {
            FileStore fileStore = Files.getFileStore(Paths.get(pluginUploadConstraintConfig.getToolsPath()));
            long totalSpace = fileStore.getTotalSpace();
            long usedSpace = totalSpace - fileStore.getUsableSpace();
            if ((double) usedSpace / totalSpace > pluginUploadConstraintConfig.getMaxStorageRatio()) {
                throw new ModelEngineException(PluginRetCode.UPLOAD_EXCEEDED_LIMIT_FIELD,
                        StringUtils.format("The current storage usage has reached {0}%",
                                pluginUploadConstraintConfig.getMaxStorageRatio() * 100));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to obtain physical storage information.", e);
        }

        // 校验插件数量是否超出限制
        ListResult<PluginData> plugins = pluginService.getPlugins(new PluginQuery.Builder().toolName(null)
                .includeTags(new HashSet<>())
                .excludeTags(new HashSet<>())
                .mode(OR)
                .offset(0)
                .limit(Integer.MAX_VALUE)
                .build());
        if (plugins == null) {
            return;
        }
        if (plugins.getCount() >= pluginUploadConstraintConfig.getMaxPluginNumber()) {
            throw new ModelEngineException(PluginRetCode.UPLOAD_EXCEEDED_LIMIT_FIELD,
                    StringUtils.format("The maximum number of plugins that can be uploaded is {0}",
                            pluginUploadConstraintConfig.getMaxPluginNumber()));
        }
    }

    /**
     * 验证必要的文件是否存在。
     *
     * @param tempDir 表示临时目录的 {@link File}。
     */
    public static void validateNecessaryFiles(File tempDir) {
        List<String> files = Arrays.stream(getFiles(tempDir)).map(File::getName).collect(Collectors.toList());
        if (!files.contains(PLUGIN_JSON)) {
            throw new ModelEngineException(PluginRetCode.FILE_MISSING_ERROR, PLUGIN_JSON);
        }
        if (!files.contains(TOOLS_JSON)) {
            throw new ModelEngineException(PluginRetCode.FILE_MISSING_ERROR, TOOLS_JSON);
        }
    }

    /**
     * 校验插件的完整性。
     *
     * @param tempDir 表示临时目录的 {@link File}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public static void validateCompleteness(File tempDir, ObjectSerializer serializer) {
        File compressedFile = getCompressedFile(tempDir);
        String expectValidationValue = SecurityUtils.signatureOf(compressedFile, "sha-256", 1024);
        PluginJsonEntity pluginJsonEntity =
                getFileInfo(getFileByName(tempDir, PLUGIN_JSON), serializer, PluginJsonEntity.class);
        notBlank(pluginJsonEntity.getChecksum(), () -> buildBlankParserException(PLUGIN_JSON, CHECKSUM));
        String actualValidationValue = pluginJsonEntity.getChecksum();
        if (!expectValidationValue.equalsIgnoreCase(actualValidationValue)) {
            throw new ModelEngineException(PluginRetCode.PLUGIN_COMPLETENESS_CHECK_ERROR);
        }
    }

    /**
     * 校验定义组和工具组的唯一性。
     *
     * @param defGroupDatas 表示定义组数据的 {@link List<DefinitionGroupData>}。
     * @param toolGroupDatas 表示工具组数据的 {@link List<ToolGroupData>}。
     * @param toolGroupService 表示工具组服务的 {@link ToolGroupService}。
     * @param defGroupService 表示定义组服务的 {@link DefinitionGroupService}。
     */
    public static void validateDefAndToolRepeat(List<DefinitionGroupData> defGroupDatas,
            List<ToolGroupData> toolGroupDatas, ToolGroupService toolGroupService,
            DefinitionGroupService defGroupService) {
        // 定义组名不重复。
        List<String> jsonDefGroupNames =
                defGroupDatas.stream().map(DefinitionGroupData::getName).collect(Collectors.toList());
        List<String> existDefGroups = defGroupService.findExistDefGroups(new HashSet<>(jsonDefGroupNames));
        validateRepeatInfos(jsonDefGroupNames, StringUtils.EMPTY, DEFINITION_GROUP_NAME_IN_TOOL);
        // 定义组下的定义名不重复。
        for (DefinitionGroupData defGroupData : defGroupDatas) {
            List<String> jsonDefNames =
                    defGroupData.getDefinitions().stream().map(DefinitionData::getName).collect(Collectors.toList());
            String firstExistAnyDefNameInDefGroup = existDefGroups.contains(defGroupData.getName())
                    ? StringUtils.EMPTY
                    : defGroupService.findFirstExistDefNameInDefGroup(defGroupData.getName(),
                            new HashSet<>(jsonDefNames));
            validateRepeatInfos(jsonDefNames, firstExistAnyDefNameInDefGroup, DEFINITION_NAME);
        }
        Map<String, Set<String>> defToolGroupMap = new HashMap<>();
        for (ToolGroupData toolGroupData : toolGroupDatas) {
            // 实现组下的实现名不重复。
            List<String> jsonToolNames =
                    toolGroupData.getTools().stream().map(ToolData::getName).collect(Collectors.toList());
            String firstExistToolInToolGroup =
                    findFirstExistToolInToolGroup(toolGroupService, toolGroupData, new HashSet<>(jsonToolNames));
            validateRepeatInfos(jsonToolNames, firstExistToolInToolGroup, TOOL_NAME);
            // 定义组下的实现组名不重复。
            String defGroupName = toolGroupData.getDefGroupName();
            String toolGroupName = toolGroupData.getName();
            Set<String> toolGroupsInDefGroup = defToolGroupMap.computeIfAbsent(defGroupName, k -> new HashSet<>());
            String firstExistToolGroupInDefGroup =
                    toolGroupService.findFirstExistToolGroupInDefGroup(toolGroupName, defGroupName);
            validateRepeatInfos(Collections.emptyList(), firstExistToolGroupInDefGroup, TOOL_GROUP_NAME);
            if (toolGroupsInDefGroup.contains(toolGroupName)) {
                throw buildDuplicatePropertyEx(toolGroupName, TOOL_GROUP_NAME);
            }
            toolGroupsInDefGroup.add(toolGroupName);
        }
        distinctDefGroups(defGroupDatas, existDefGroups);
    }

    private static void distinctDefGroups(List<DefinitionGroupData> defGroupDatas, List<String> existDefGroups) {
        if (CollectionUtils.isEmpty(defGroupDatas) || CollectionUtils.isEmpty(existDefGroups)) {
            return;
        }
        defGroupDatas.removeIf(data -> existDefGroups.contains(data.getName()));
    }

    private static String findFirstExistToolInToolGroup(ToolGroupService toolGroupService, ToolGroupData toolGroupData,
            Set<String> toolNames) {
        List<ToolGroupData> toolGroupDatas =
                toolGroupService.get(toolGroupData.getDefGroupName(), Arrays.asList(toolGroupData.getName()));
        if (CollectionUtils.isEmpty(toolGroupDatas)) {
            return StringUtils.EMPTY;
        }
        // 指定了 1 个实现组的名字，所以这里如果非空，只能有 1 个组。
        return toolGroupDatas.get(0)
                .getTools()
                .stream()
                .filter(toolData -> toolNames.contains(toolData.getName()))
                .map(toolData -> toolData.getGroupName() + DOT + toolData.getName())
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

    private static void validateRepeatInfos(List<String> values, String repeatValue, String key) {
        validateRepeatValues(values, key);
        if (StringUtils.isNotBlank(repeatValue)) {
            throw buildDuplicatePropertyEx(key, repeatValue);
        }
    }

    private static void validateRepeatValues(List<String> values, String key) {
        List<String> duplicates = values.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(duplicates)) {
            String repeatValues = String.join(", ", duplicates);
            throw buildDuplicatePropertyEx(key, repeatValues);
        }
    }

    private static ModelEngineException buildDuplicatePropertyEx(String key, String repeatValue) {
        return buildParserException(StringUtils.format(
                "The current operation has duplicate property. [property='{0}', value='{1}']",
                key,
                repeatValue));
    }

    /**
     * 从工具组数据中筛选指定的工具。
     *
     * @param toolGroupDatas 表示工具组数据的 {@link List<ToolGroupData>}。
     * @param defGroupDatas 表示定义组数据的 {@link List<DefinitionGroupData>}。
     * @param toolNames 表示工具名的 {@link List<String>}。
     */
    public static void validateSelectTools(List<ToolGroupData> toolGroupDatas, List<DefinitionGroupData> defGroupDatas,
            List<String> toolNames) {
        filterAndProcess(toolGroupDatas,
                toolGroupData -> toolGroupData.getTools()
                        .removeIf(toolData -> !toolNames.contains(getUniqueIdentifier(toolData))),
                ToolGroupData::getTools);
        List<String> toolDefs = toolNames.stream().map(BasicValidator::getDefIdentifier).collect(Collectors.toList());
        filterAndProcess(defGroupDatas,
                defGroupData -> defGroupData.getDefinitions()
                        .removeIf(defData -> !toolDefs.contains(defData.getGroupName() + DOT + defData.getName())),
                DefinitionGroupData::getDefinitions);
        if (toolGroupDatas.isEmpty() || defGroupDatas.isEmpty()) {
            throw buildParserException("The tools or defs is empty in the json.");
        }
    }

    private static <T> void filterAndProcess(List<T> groupDataList, Consumer<T> filterFunction,
            Function<T, List<?>> emptyCheckFunction) {
        Iterator<T> iterator = groupDataList.iterator();
        while (iterator.hasNext()) {
            T groupData = iterator.next();
            filterFunction.accept(groupData);
            if (emptyCheckFunction.apply(groupData).isEmpty()) {
                iterator.remove();
            }
        }
    }

    private static String getUniqueIdentifier(ToolData toolData) {
        return toolData.getDefGroupName() + DOT + toolData.getGroupName() + DOT + toolData.getDefName() + DOT
                + toolData.getName();
    }

    private static String getDefIdentifier(String uniqueIdentifier) {
        String[] splits = uniqueIdentifier.split("\\.");
        return splits[0] + DOT + splits[2];
    }

    /**
     * 校验工具是否有对应的定义。
     *
     * @param toolGroupDatas 表示工具组数据的 {@link List<ToolGroupData>}。
     * @param defGroupMap 表示定义组映射的
     * {@link Map}{@code <}{@link String}{@code , }{@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >>}。
     */
    public static void validateToolHasDef(List<ToolGroupData> toolGroupDatas,
            Map<String, Map<String, Object>> defGroupMap) {
        toolGroupDatas.stream().flatMap(toolGroupData -> toolGroupData.getTools().stream()).forEach(toolData -> {
            String defGroupName = toolData.getDefGroupName();
            String defName = toolData.getDefName();
            if (!defGroupMap.containsKey(defGroupName) || !defGroupMap.get(defGroupName).containsKey(defName)) {
                throw buildParserException(StringUtils.format(
                        "The tool must have definition. [tool={0}, definition={1}]",
                        toolData.getName(),
                        defGroupName + DOT + defName));
            }
        });
    }

    /**
     * 校验对象是否为非空，并转化为指定类型。
     *
     * @param obj 表示对象的 {@link Object}。
     * @param typeClass 表示期望类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param field 表示该字段名称的 {@link String}。
     * @param source 表示来源的 {@link String}。
     * @param <T> 表示泛型的 {@code <}{@link T}{@code >}。
     * @return 返回校验转换后的 {@link T}。
     */
    public static <T> T validateObj(Object obj, Class<T> typeClass, String field, String source) {
        if (obj == null) {
            throw buildParserException(StringUtils.format(
                    "The property in the object cannot be null. [object={0}, field={1}]",
                    source,
                    field));
        }
        if (!typeClass.isInstance(obj)) {
            throw buildParserException(StringUtils.format(
                    "The property in object can only be the certain type. [object={0}, field={1}, type={2}]",
                    field,
                    source,
                    typeClass));
        }
        notNull(obj, () -> buildNullParserException(source, field));
        return cast(obj);
    }

    /**
     * 校验对象是否为非空，并转化为字符串。
     *
     * @param obj 表示对象的 {@link Object}。
     * @param field 表示该字段名称的 {@link String}。
     * @param source 表示来源的 {@link String}。
     * @return 返回校验转换后的 {@link String}。
     */
    public static String validateStr(Object obj, String field, String source) {
        String res = validateObj(obj, String.class, field, source);
        return notBlank(res, () -> buildEmptyParserException(source, field));
    }

    /**
     * 构建空字段的解析异常。
     *
     * @param file 表示所校验的文件的 {@link String}。
     * @param property 表示所校验的字段的 {@link String}。
     * @return 表示解析异常的 {@link ModelEngineException}。
     */
    public static ModelEngineException buildNullParserException(String file, String property) {
        String message =
                StringUtils.format("The file must contain the property. [file='{0}', property='{1}']", file, property);
        return new ModelEngineException(JSON_PARSE_ERROR, message);
    }

    /**
     * 构建空集合的解析异常。
     *
     * @param file 表示所校验的文件的 {@link String}。
     * @param property 表示所校验的字段的 {@link String}。
     * @return 表示解析异常的 {@link ModelEngineException}。
     */
    public static ModelEngineException buildEmptyParserException(String file, String property) {
        String message = StringUtils.format(
                "The file must contain the property and cannot be empty. [file='{0}', property='{1}']",
                file,
                property);
        return new ModelEngineException(JSON_PARSE_ERROR, message);
    }

    /**
     * 构建空白字段的解析异常。
     *
     * @param file 表示所校验的文件的 {@link String}。
     * @param property 表示所校验的字段的 {@link String}。
     * @return 表示解析异常的 {@link ModelEngineException}。
     */
    public static ModelEngineException buildBlankParserException(String file, String property) {
        String message = StringUtils.format(
                "The file must contain the property and cannot be blank. [file='{0}', property='{1}']",
                file,
                property);
        return new ModelEngineException(JSON_PARSE_ERROR, message);
    }

    /**
     * 构建解析异常。
     *
     * @param message 表示异常信息的 {@link String}。
     * @return 表示解析异常的 {@link ModelEngineException}。
     */
    public static ModelEngineException buildParserException(String message) {
        return new ModelEngineException(JSON_PARSE_ERROR, message);
    }
}
