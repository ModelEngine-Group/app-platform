/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.genericable.AppBuilderAppService;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

/**
 * aipp的Url接口
 *
 * @author 李智超
 * @since 2024-11-21
 */
@Component
@RequestMapping(path = "/v1/api/chat")
public class AppBuilderUrlController extends AbstractController {
    private final AppBuilderAppService appGenericable;

    /**
     * AppBuilderUrlController
     *
     * @param authenticator 表示权限校验认的证器对象的 {@link Authenticator}。
     * @param appGenericable 表示app通用服务的 {@link AppBuilderAppService}。
     */
    public AppBuilderUrlController(Authenticator authenticator, AppBuilderAppService appGenericable) {
        super(authenticator);
        this.appGenericable = appGenericable;
    }

    /**
     * 查询单个app。
     *
     * @param httpRequest 请求参数。
     * @param path 表示待查询app的Path {@link String}。
     * @return 表示查询app的最新可编排版本的DTO {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{path}", description = "通过path短链查询 app ")
    public Rsp<AppBuilderAppDto> queryByPath(HttpClassicServerRequest httpRequest, @PathVariable("path") String path) {
        return Rsp.ok(this.appGenericable.queryByPath(path));
    }
}
