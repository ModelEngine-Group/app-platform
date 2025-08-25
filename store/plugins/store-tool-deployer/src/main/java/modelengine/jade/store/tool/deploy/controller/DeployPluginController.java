/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.controller;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.jade.aipp.domain.division.annotation.GetSource;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.common.Result;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.support.DeployStatus;
import modelengine.jade.store.tool.deploy.param.DeployParam;
import modelengine.jade.store.tool.deploy.service.PluginDeployService;

import java.util.List;

/**
 * 表示上传文件的控制器。
 *
 * @author 杭潇
 * @since 2024-07-11
 */
@Component
@RequestMapping("/plugins")
public class DeployPluginController {
    private final PluginDeployService pluginDeployService;

    /**
     * 通过插件服务来初始化 {@link DeployPluginController} 的新实例。
     *
     * @param pluginDeployService 表示插件部署服务的 {@link PluginDeployService}。
     */
    public DeployPluginController(PluginDeployService pluginDeployService) {
        this.pluginDeployService = notNull(pluginDeployService, "The plugin deploy service cannot be null.");
    }

    /**
     * 部署插件。
     *
     * @param deployParam 表示部署参数的 {@link DeployParam}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/deploy", description = "部署插件")
    public Result<String> deployPlugin(@RequestBody DeployParam deployParam) {
        notNull(deployParam, "The deploy param cannot be null.");
        List<String> pluginIds = deployParam.getPluginIds();
        this.pluginDeployService.deployPlugins(pluginIds);
        return Result.ok(null, pluginIds.size());
    }

    /**
     * 查询部署中的插件。
     *
     * @param status 表示插件部署状态的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link PluginData}{@code >}{@code >}。
     */
    @GetMapping(path = "/by-status/{deploy-status}", description = "查询部署中的插件")
    @GetSource
    public Result<List<PluginData>> queryDeployingCount(@PathVariable("deploy-status") String status) {
        DeployStatus deployStatus = DeployStatus.from(status);
        return Result.ok(this.pluginDeployService.queryPluginsByDeployStatus(deployStatus),
                this.pluginDeployService.queryCountByDeployStatus(deployStatus));
    }
}