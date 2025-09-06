/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import static modelengine.jade.common.Result.calculateOffset;

import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.jade.store.entity.query.PluginToolQuery;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import modelengine.fit.jober.aipp.dto.ModelDto;
import modelengine.fit.jober.aipp.dto.PluginToolDto;
import modelengine.fit.jober.aipp.dto.StoreNodeConfigResDto;
import modelengine.fit.jober.aipp.dto.StoreNodeInfoDto;
import modelengine.fit.jober.aipp.enums.NodeTypeEnum;
import modelengine.fit.jober.aipp.service.StoreService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;

/**
 * Store 相关接口
 *
 * @author 邬涨财
 * @since 2024-05-13
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/store")
public class StoreController extends AbstractController {
    private final StoreService storeService;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param storeService 存储服务
     */
    public StoreController(Authenticator authenticator, StoreService storeService) {
        super(authenticator);
        this.storeService = storeService;
    }

    /**
     * 获取所有工具和基础节点配置
     *
     * @param httpRequest 请求
     * @param mode tags的拼接方式
     * @param pageNum 页数
     * @param pageSize 分页大小
     * @param tag 标签
     * @param version 版本
     * @return 查询结果
     * @deprecated
     */
    @Deprecated
    @GetMapping(path = "/nodes", description = "获取所有工具和基础节点配置")
    public Rsp<StoreNodeConfigResDto> getBasicNodesAndTools(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "tag") String tag, @RequestParam(value = "version") String version) {
        return Rsp.ok(this.storeService.getBasicNodesAndTools(tag, mode, pageNum, pageSize, version));
    }

    /**
     * 获取任务的模型列表。
     *
     * @param httpRequest 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param taskName 表示任务名的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Rsp}{@code <}{@link ModelDto}{@code >}。
     */
    @GetMapping(path = "/models", description = "获取任务的模型列表")
    public Rsp<ModelDto> getModels(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "taskName", defaultValue = "") String taskName) {
        return Rsp.ok(this.storeService.getModels(taskName, pageNum, pageSize));
    }

    /**
     * 获取已发布的所有指定类型的插件配置。
     *
     * @param httpRequest 表示请求的 {@link HttpClassicServerRequest}。
     * @param name 表示工具名的关键词的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param tenantId 表示租户标识的 {@link String}。
     * @param mode 表示标签拼接方式 {@link String}。
     * @param pageNum 表示页数的 {@code int}。
     * @param pageSize 表示分页大小的 {@code int}。
     * @param isDeployed 表示插件是否部署的 {@link Boolean}。
     * @return 表示插件工具传输类返回消息结构体的 {@link Rsp}{@code <}{@link PluginToolDto}{@code >}。
     */
    @GetMapping(path = "/plugins", description = "获取已发布的所有指定类型的插件配置")
    public Rsp<PluginToolDto> getPlugins(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "includeTags", defaultValue = "", required = false) List<String> includeTags,
            @RequestParam(value = "excludeTags", defaultValue = "", required = false) List<String> excludeTags,
            @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "isDeployed", defaultValue = "false", required = false) Boolean isDeployed) {
        PluginToolQuery pluginToolQuery = new PluginToolQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(mode)
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version("")
                .isDeployed(isDeployed)
                .build();
        return Rsp.ok(this.storeService.getPlugins(pluginToolQuery, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 根据 type {@link String} 获取相应节点配置。
     *
     * @param type 表示节点类型的 {@link String}。
     * @return 表示 HispRsp 构造的对象的 {@link Rsp}{@code <}{@link StoreNodeInfoDto}{@code >}。
     */
    @GetMapping(path = "/nodes/list", description = "获取节点配置")
    public Rsp<List<StoreNodeInfoDto>> getNodesList(@RequestParam(value = "type") String type) {
        if (StringUtils.equals(type, NodeTypeEnum.BASIC.type()) || StringUtils.equals(type,
                NodeTypeEnum.EVALUATION.type())) {
            return Rsp.ok(this.storeService.getNode(type));
        }
        return Rsp.err(AippErrCode.INPUT_PARAM_IS_INVALID.getErrorCode(),
                StringUtils.format(AippErrCode.INPUT_PARAM_IS_INVALID.getMessage(), "[type=" + type + "]"));
    }

    /**
     * 获取所有工具流
     *
     * @param httpRequest 请求
     * @param mode tags的拼接方式
     * @param pageNum 页数
     * @param pageSize 分页大小
     * @param version 版本
     * @return 查询结果
     */
    @GetMapping(path = "/waterflow", description = "获取所有工具流")
    public Rsp<List<AppBuilderWaterFlowInfoDto>> getWaterFlowInfos(HttpClassicServerRequest httpRequest,
            @RequestParam(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "version") String version) {
        return Rsp.ok(this.storeService.getWaterFlowInfos(mode, pageNum, pageSize, version));
    }
}
