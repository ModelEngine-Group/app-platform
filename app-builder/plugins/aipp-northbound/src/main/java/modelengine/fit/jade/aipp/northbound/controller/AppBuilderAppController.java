/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.controller;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jane.task.gateway.Authenticator;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.chat.AppMetadata;
import modelengine.fit.jober.aipp.dto.chat.AppQueryParams;
import modelengine.fit.jober.aipp.genericable.AppBuilderAppService;
import modelengine.fit.jober.aipp.genericable.adapter.AppBuilderAppServiceAdapter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用信息管理北向接口。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
@Component
@RequestMapping(path = "/api/app/v1/tenants/{tenantId}/apps", group = "应用信息管理接口")
public class AppBuilderAppController extends AbstractController {
    private final List<String> excludeNames;
    private final AppBuilderAppServiceAdapter appService;
    private final AppBuilderAppService appGenericable;

    /**
     * 构造方法。
     *
     * @param authenticator 表示身份校验器的 {@link Authenticator}。
     * @param appService 表示应用服务的 {@link AppBuilderAppServiceAdapter}。
     * @param appGenericable 表示应用服务的 {@link AppBuilderAppService}。
     * @param excludeNames 表示需要排除的应用名字列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public AppBuilderAppController(Authenticator authenticator, AppBuilderAppServiceAdapter appService,
            AppBuilderAppService appGenericable, @Value("${app-engine.exclude-names}") List<String> excludeNames) {
        super(authenticator);
        this.excludeNames = notNull(replaceAsterisks(excludeNames), "The excludeNames cannot be null.");
        this.appService = notNull(appService, "The appService cannot be null.");
        this.appGenericable = notNull(appGenericable, "The appGenericable cannot be null.");
    }

    private static List<String> replaceAsterisks(List<String> excludeNames) {
        return excludeNames.stream().map(s -> s.replaceAll("\\*(\\w+)\\*", "{$1}")).collect(Collectors.toList());
    }

    /**
     * 查询指定租户的应用列表。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户的唯一标识符的 {@link String}。
     * @param cond 表示查询条件的 {@link AppQueryParams}。
     * @return 表示应用查询结果列表的
     * {@link Rsp}{@code <}{@link RangedResultSet}{@code <}{@link AppMetadata}{@code >>}。
     */
    @GetMapping(summary = "查询用户应用列表",
            description = "该接口可以使用指定条件筛选用户应用列表，如应用id、查询的应用名字和状态等.")
    public Rsp<RangedResultSet<AppMetadata>> list(HttpClassicServerRequest httpRequest,
            @PathVariable("tenantId") @Property(description = "租户的唯一标识符") String tenantId,
            @RequestBean AppQueryParams cond) {
        return Rsp.ok(this.appService.list(this.buildAppQueryCondition(cond), this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询应用配置详情。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户的唯一标识符的 {@link String}。
     * @param appId 表示待查询应用的唯一标识符的 {@link String}。
     * @return 表示应用配置详情的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(path = "/{appId}/config", summary = "查询应用配置详情",
            description = "该接口可以通过待查询应用的唯一标识符来查询指定应用的配置详情。")
    public Rsp<AppBuilderAppDto> query(HttpClassicServerRequest httpRequest,
            @PathVariable("tenantId") @Property(description = "租户的唯一标识符") String tenantId,
            @PathVariable("appId") @Property(description = "待查询 app 的唯一标识符") String appId) {
        return Rsp.ok(this.appGenericable.query(appId, this.contextOf(httpRequest, tenantId)));
    }

    private AppQueryParams buildAppQueryCondition(AppQueryParams cond) {
        if (cond.getExcludeNames() == null) {
            cond.setExcludeNames(this.excludeNames);
        } else {
            cond.getExcludeNames().addAll(this.excludeNames);
        }
        return cond;
    }
}
