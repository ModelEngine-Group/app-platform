/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.controller;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.info.entity.HttpJsonEntity;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.jade.aipp.domain.division.annotation.CreateSource;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.common.Result;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.aop.PluginValidation;
import modelengine.jade.store.tool.upload.service.PluginUploadService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示上传文件的控制器。
 *
 * @author 杭潇
 * @since 2024-07-11
 */
@Component
@RequestMapping("/plugins")
public class UploadPluginController {
    private final PluginUploadService pluginUploadService;

    /**
     * 通过插件服务来初始化 {@link UploadPluginController} 的新实例。
     *
     * @param pluginUploadService 表示插件上传服务的 {@link PluginUploadService}。
     */
    public UploadPluginController(PluginUploadService pluginUploadService) {
        this.pluginUploadService = notNull(pluginUploadService, "The plugin deploy service cannot be null.");
    }

    /**
     * 表示保存上传工具文件的请求。
     *
     * @param receivedFiles 表示分块的消息体数据的 {@link PartitionedEntity}。
     * @param toolNames 表示工具名的列表的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @CarverSpan("operation.store.plugin.upload")
    @PostMapping(path = "/save/plugins", description = "保存上传工具文件")
    @CreateSource
    public Result<String> saveUploadFile(PartitionedEntity receivedFiles,
            @RequestParam("toolNames") @SpanAttr("toolNames") List<String> toolNames) {
        notNull(receivedFiles, "The file to be uploaded cannot be null.");
        notNull(toolNames, "The tools name cannot be null.");
        List<NamedEntity> entityList =
                receivedFiles.entities().stream().filter(NamedEntity::isFile).collect(Collectors.toList());
        if (entityList.isEmpty()) {
            throw new ModelEngineException(PluginRetCode.NO_FILE_UPLOADED_ERROR);
        }
        this.pluginUploadService.uploadPlugins(entityList, toolNames);
        return Result.ok(null, 1);
    }

    /**
     * 删除插件的请求。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @CarverSpan("operation.store.plugin.delete")
    @DeleteMapping(value = "/delete/{pluginId}", description = "删除插件")
    @PluginValidation
    public Result<String> deletePlugin(@PathVariable("pluginId") @SpanAttr("pluginId") String pluginId) {
        notBlank(pluginId, "The plugin id cannot be blank.");
        int deleteNum = this.pluginUploadService.deletePlugin(pluginId);
        return Result.ok(null, deleteNum);
    }

    /**
     * 表示保存上传工具文件的请求。
     *
     * @param httpEntity 表示 Http 工具的消息体 {@link HttpJsonEntity}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @CarverSpan("operation.store.plugin.http")
    @PostMapping(path = "/save/http", description = "保存上传 http 工具")
    @CreateSource
    public Result<String> saveUploadHttp(@RequestBody @SpanAttr("name:$.name") HttpJsonEntity httpEntity) {
        notNull(httpEntity, "The http plugin cannot be null.");
        this.pluginUploadService.uploadHttp(httpEntity);
        return Result.ok(null, 1);
    }
}