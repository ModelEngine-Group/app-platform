/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.service.impl;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.carver.tool.ToolSchema.NAME;
import static modelengine.jade.carver.tool.ToolSchema.PROPERTIES_TYPE;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.ARTIFACT_ID;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.DOT;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.GROUP_ID;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.HTTP;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.JAR;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.JAVA;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PLUGINS;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PLUGIN_FULL_NAME;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PLUGIN_JSON;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.PYTHON;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.TEMP_DIR;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.TYPE;
import static modelengine.jade.carver.tool.info.schema.PluginSchema.UNIQUENESS;
import static modelengine.jade.carver.tool.info.schema.ToolsSchema.DEFINITIONS;
import static modelengine.jade.carver.tool.info.schema.ToolsSchema.TOOLS;
import static modelengine.jade.carver.tool.info.schema.ToolsSchema.TOOLS_JSON;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateCompleteness;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateDefAndToolRepeat;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateNecessaryFiles;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validatePluginConstraintInfo;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateSelectTools;
import static modelengine.jade.store.tool.upload.support.BasicValidator.validateToolHasDef;
import static modelengine.jade.store.tool.upload.support.processor.PluginProcessor.buildHttpPluginData;
import static modelengine.jade.store.tool.upload.support.processor.PluginProcessor.buildPluginToolDatas;
import static modelengine.jade.store.tool.upload.support.processor.ToolProcessor.enhanceSchema;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.buildDefGroupMap;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getCompressedFile;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileByName;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.objToString;

import modelengine.fit.http.entity.NamedEntity;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.info.entity.HttpJsonEntity;
import modelengine.jade.carver.tool.info.entity.PluginJsonEntity;
import modelengine.jade.carver.tool.info.entity.ToolJsonEntity;
import modelengine.jade.carver.tool.model.transfer.DefinitionGroupData;
import modelengine.jade.carver.tool.model.transfer.ToolGroupData;
import modelengine.jade.carver.tool.service.DefinitionGroupService;
import modelengine.jade.carver.tool.service.ToolGroupService;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.support.DeployStatus;
import modelengine.jade.store.tool.upload.config.PluginUploadConstraintConfig;
import modelengine.jade.store.tool.upload.service.PluginUploadService;
import modelengine.jade.store.tool.upload.support.processor.ProcessorFactory;
import modelengine.jade.store.tool.upload.utils.FormatFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 插件上传服务。
 *
 * @author 李金绪
 * @since 2024-10-29
 */
@Component
public class PluginUploadServiceImpl implements PluginUploadService {
    private static final Logger log = Logger.get(PluginUploadServiceImpl.class);
    private static final String TOOL_PATH = "/var/store/tools/";
    private static final String PERSISTENT_PATH = "/opt/fit/tools";

    private final PluginService pluginService;
    private final DefinitionGroupService defGroupService;
    private final ToolGroupService toolGroupService;
    private final ObjectSerializer serializer;
    private final PluginUploadConstraintConfig pluginUploadConstraintConfig;
    private final ProcessorFactory processorFactory;

    /**
     * 通过插件服务来初始化 {@link PluginUploadServiceImpl} 的新实例。
     *
     * @param pluginService 表示插件服务的 {@link PluginService}。
     * @param serializer 表示对象序列化的序列化器的 {@link ObjectSerializer}。
     * @param pluginUploadConstraintConfig 表示插件上传的约束性配置参数的 {@link PluginUploadConstraintConfig}。
     * @param processorFactory 表示处理器工厂的 {@link ProcessorFactory}。
     * @param defGroupService 表示定义组服务的 {@link DefinitionGroupService}。
     * @param toolGroupService 表示工具组服务的 {@link ToolGroupService}。
     */
    public PluginUploadServiceImpl(PluginService pluginService, @Fit(alias = "json") ObjectSerializer serializer,
            PluginUploadConstraintConfig pluginUploadConstraintConfig, ProcessorFactory processorFactory,
            DefinitionGroupService defGroupService, ToolGroupService toolGroupService) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
        this.serializer = notNull(serializer, "The object serializer cannot be null.");
        this.pluginUploadConstraintConfig =
                notNull(pluginUploadConstraintConfig, "The plugin upload constraint config cannot be null.");
        this.processorFactory = notNull(processorFactory, "The processor factory cannot be null.");
        this.defGroupService = notNull(defGroupService, "The definition group service cannot be null.");
        this.toolGroupService = notNull(toolGroupService, "The tool group service cannot be null.");
    }

    @Override
    public void uploadPlugins(List<NamedEntity> namedEntities, List<String> toolNames) {
        for (NamedEntity namedEntity : namedEntities) {
            validatePluginConstraintInfo(this.pluginUploadConstraintConfig, this.pluginService);
            File tempDir = FormatFileUtils.unzipPlugin(namedEntity.asFile());
            try {
                validateNecessaryFiles(tempDir);
                validateCompleteness(tempDir, this.serializer);
                this.savePlugin(tempDir, toolNames);
            } finally {
                FileUtils.delete(tempDir);
            }
            log.info("The plugin is added successfully, and the selected tools are added successfully.");
        }
    }

    private void savePlugin(File tempDir, List<String> toolNames) {
        PluginJsonEntity pluginJsonEntity =
                getFileInfo(getFileByName(tempDir, PLUGIN_JSON), this.serializer, PluginJsonEntity.class);
        PluginData pluginData = cast(this.processorFactory.createInstance(PLUGINS)
                .process(pluginJsonEntity, Collections.singletonMap(TEMP_DIR, tempDir)));
        Object toolJsonEntity = getFileInfo(getFileByName(tempDir, TOOLS_JSON), this.serializer, ToolJsonEntity.class);
        List<DefinitionGroupData> defGroups =
                cast(this.processorFactory.createInstance(DEFINITIONS).process(toolJsonEntity, new HashMap<>()));
        List<ToolGroupData> toolGroups =
                cast(this.processorFactory.createInstance(TOOLS).process(toolJsonEntity, new HashMap<>()));
        this.save(tempDir, pluginData, defGroups, toolGroups, toolNames);
    }

    private void save(File tempDir, PluginData pluginData, List<DefinitionGroupData> defGroupDatas,
            List<ToolGroupData> toolGroupDatas, List<String> toolNames) {
        this.checkUniquePluginId(pluginData.getPluginId());
        Map<String, Map<String, Object>> defGroupMap = cast(buildDefGroupMap(defGroupDatas));
        // 保证校验的顺序。
        validateToolHasDef(toolGroupDatas, defGroupMap);
        validateSelectTools(toolGroupDatas, defGroupDatas, toolNames);
        enhanceSchema(toolGroupDatas, defGroupMap);
        validateDefAndToolRepeat(defGroupDatas, toolGroupDatas, this.toolGroupService, this.defGroupService);
        pluginData.setToolGroupDataList(toolGroupDatas);
        pluginData.setDefinitionGroupDataList(defGroupDatas);
        pluginData.setPluginToolDataList(buildPluginToolDatas(toolGroupDatas, pluginData.getPluginId()));
        this.pluginService.addPlugin(pluginData);
        this.savePluginToPersistentPath(cast(getCompressedFile(tempDir)), pluginData);
        log.info("Save metadata to data base successfully.");
    }

    @Override
    public int deletePlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        if (pluginData.getPluginId() == null) {
            // 无此插件时，返回删除数量为 0
            log.warn("No plugin found when try to delete. [pluginId={}]", pluginId);
            return 0;
        }
        this.pluginService.deletePlugin(pluginId);
        Object type = pluginData.getExtension().get(PROPERTIES_TYPE);
        if (type != null && !Objects.equals(type, HTTP)) {
            String toolName = objToString(pluginData.getExtension().get(PLUGIN_FULL_NAME));
            Path toolPath = toolName.endsWith(JAR) ? Paths.get(TOOL_PATH, JAVA) : Paths.get(TOOL_PATH, PYTHON);
            Path deployPath = Paths.get(toolPath.toString(), toolName);
            FileUtils.delete(deployPath.toFile());
            Path persistentPath = this.generatePersistentPath(pluginData);
            FileUtils.delete(persistentPath.toFile());
        }
        log.info("Succeeded to delete plugin. [pluginName={}]", pluginData.getPluginName());
        // 正常删除，返回删除数量为 1
        return 1;
    }

    private void savePluginToPersistentPath(File pluginFile, PluginData pluginData) {
        try {
            Path persistentPath = this.generatePersistentPath(pluginData);
            FileUtils.ensureDirectory(persistentPath.toFile());
            Files.copy(pluginFile.toPath(),
                    Paths.get(persistentPath.toString(), pluginFile.getName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to copy file. [toolFile='{0}']", pluginFile), e);
        }
    }

    private Path generatePersistentPath(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        if (StringUtils.equalsIgnoreCase(JAVA, objToString(extension.get(TYPE)))) {
            return Paths.get(PERSISTENT_PATH,
                    JAVA,
                    objToString(extension.get(UNIQUENESS + DOT + ARTIFACT_ID)),
                    objToString(extension.get(UNIQUENESS + DOT + GROUP_ID)));
        }
        return Paths.get(PERSISTENT_PATH, PYTHON, objToString(extension.get(UNIQUENESS + DOT + NAME)));
    }

    private void checkUniquePluginId(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        if (pluginData.getPluginId() == null) {
            return;
        }
        // 如果已有相同插件的状态是已部署或者部署中，插件唯一性校验失败，插件上传失败
        if (Objects.equals(pluginData.getDeployStatus(), DeployStatus.DEPLOYED.toString())
                || Objects.equals(pluginData.getDeployStatus(), DeployStatus.DEPLOYING.toString())) {
            throw new ModelEngineException(PluginRetCode.PLUGIN_UNIQUE_CHECK_ERROR);
        }
        // 未部署或者部署失败的相同插件可以被新插件替换
        this.deletePlugin(pluginId);
    }

    @Override
    public void uploadHttp(HttpJsonEntity httpEntity) {
        List<ToolGroupData> toolGroups =
                cast(this.processorFactory.createInstance(TOOLS).process(httpEntity, new HashMap<>()));
        List<DefinitionGroupData> defGroups =
                cast(this.processorFactory.createInstance(DEFINITIONS).process(httpEntity, new HashMap<>()));
        PluginData pluginData = buildHttpPluginData(defGroups, toolGroups, httpEntity);
        this.checkUniquePluginId(pluginData.getPluginId());
        Map<String, Map<String, Object>> defGroupMap = cast(buildDefGroupMap(defGroups));
        enhanceSchema(toolGroups, defGroupMap);
        this.pluginService.addPlugin(pluginData);
    }
}