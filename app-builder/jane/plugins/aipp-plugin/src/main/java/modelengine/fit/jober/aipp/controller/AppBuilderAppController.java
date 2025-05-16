/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.PublishedAppResDto;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.events.AppCreatingEvent;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.util.AppImExportUtil;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * aipp的CRUD接口。
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app")
public class AppBuilderAppController extends AbstractController {
    private static final String DEFAULT_TYPE = "app";

    private static final int ERR_CODE = -1;

    private static final String ERR_LOCALE_CODE = "90002920";

    private static final Logger log = Logger.get(AppBuilderAppController.class);

    private final List<String> excludeNames;

    private final AppBuilderAppService appService;

    private final modelengine.fit.jober.aipp.genericable.AppBuilderAppService appGenericable;

    private final LocaleService localeService;

    private final FitRuntime fitRuntime;

    private final int maxAppNum;

    /**
     * 构造函数。
     *
     * @param authenticator 表示认证器的 {@link Authenticator}。
     * @param appService 表示app服务的 {@link AppBuilderAppService}。
     * @param appGenericable 表示app通用服务的 {@link AppBuilderAppService}。
     * @param excludeNames 表示排除名称列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param localeService 表示获取国际化信息的 {@link LocaleService}。
     * @param fitRuntime 表示FIT运行时环境的 {@link FitRuntime}。
     */
    public AppBuilderAppController(Authenticator authenticator, AppBuilderAppService appService,
            modelengine.fit.jober.aipp.genericable.AppBuilderAppService appGenericable,
            @Value("${app-engine.exclude-names}") List<String> excludeNames,
            @Value("${app.max-number}") Integer maxAppNum, LocaleService localeService, FitRuntime fitRuntime) {
        super(authenticator);
        // 需要FIT框架支持exclude-names配置大括号
        this.excludeNames = replaceAsterisks(excludeNames);
        this.appService = appService;
        this.appGenericable = appGenericable;
        this.localeService = localeService;
        this.fitRuntime = fitRuntime;
        this.maxAppNum = maxAppNum != null ? maxAppNum : 200;
    }

    private static List<String> replaceAsterisks(List<String> excludeNames) {
        return excludeNames.stream().map(s -> s.replaceAll("\\*(\\w+)\\*", "{$1}")).collect(Collectors.toList());
    }

    /**
     * 查询 app 列表。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param offset 偏移量。
     * @param limit 每页查询条数。
     * @param cond 查询条件。
     * @param type 查询类型。
     * @return 查询结果列表。
     */
    @GetMapping(description = "查询 app 列表")
    public Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit, @RequestBean AppQueryCondition cond,
            @RequestQuery(name = "type", defaultValue = "app") String type) {
        return this.appService.list(this.buildAppQueryCondition(cond, type), this.contextOf(httpRequest, tenantId), offset, limit);
    }

    /**
     * 查询单个app。
     *
     * @param httpRequest 请求参数。
     * @param tenantId 租户Id。
     * @param appId 表示待查询app的Id的 {@link String}。
     * @return 表示查询app的最新可编排版本的DTO {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{app_id}", description = "查询 app ")
    public Rsp<AppBuilderAppDto> query(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") String appId) {
        return Rsp.ok(this.appGenericable.query(appId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询 app 最新可编排的版本。
     *
     * @param httpRequest 请求参数。
     * @param tenantId 租户Id。
     * @param appId 表示待查询app的Id的 {@link String}。
     * @return 表示查询app的最新可编排版本的DTO {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{app_id}/latest_orchestration", description = "查询 app 最新可编排的版本")
    public Rsp<AppBuilderAppDto> queryLatestOrchestration(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return Rsp.ok(this.appGenericable.queryLatestOrchestration(appId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询 app 的历史发布版本。
     *
     * @param httpRequest 请求参数。
     * @param appId 查询app的Id。
     * @param tenantId 租户Id。
     * @param offset 偏移量。
     * @param limit 查询条数。
     * @param cond 条件。
     * @return 查询结果列表。
     */
    @GetMapping(value = "/{app_id}/recentPublished", description = "查询 app 的历史发布版本")
    public Rsp<RangedResultSet<AppBuilderAppDto>> recentPublished(HttpClassicServerRequest httpRequest,
            @PathVariable("app_id") String appId, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit, @RequestBean AppQueryCondition cond) {
        return Rsp.ok(this.appService.recentPublished(cond,
                offset,
                limit,
                appId,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 获取应用的发布详情。
     *
     * @param httpRequest 请求参数。
     * @param tenantId 租户Id。
     * @param uniqueName 唯一名称。
     * @return 查询结果。
     */
    @GetMapping(path = "/published/unique_name/{unique_name}", description = "获取应用的发布详情")
    public Rsp<PublishedAppResDto> published(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("unique_name") String uniqueName) {
        return Rsp.ok(this.appService.published(uniqueName, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 根据模板创建aipp。
     *
     * @param request 请求参数。
     * @param appId 模板的Id。
     * @param tenantId 租户Id。
     * @param dto 创建参数。
     * @return 结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.template")
    @PostMapping(value = "/{app_id}", description = "根据模板创建aipp")
    public Rsp<AppBuilderAppDto> create(HttpClassicServerRequest request, @PathVariable("app_id") String appId,
            @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated @SpanAttr("name:$.name") AppBuilderAppCreateDto dto) {
        notNull(dto.getAppBuiltType(), "App built type cannot be null.");
        this.fitRuntime.publisherOfEvents().publishEvent(new AppCreatingEvent(this));
        OperationContext context = this.contextOf(request, tenantId);
        if (this.appService.getAppCount(tenantId,
                this.buildAppQueryCondition(AppQueryCondition.builder().createBy(context.getOperator()).build(),
                        DEFAULT_TYPE)) >= this.maxAppNum) {
            return Rsp.err(ERR_CODE, this.localeService.localize(AppBuilderAppController.ERR_LOCALE_CODE));
        }
        return Rsp.ok(this.appService.create(appId, dto, context, false));
    }

    /**
     * 通过config更新aipp。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId 待更新的app的Id。
     * @param appBuilderSaveConfigDto 待更新的应用配置的Dto。
     * @return 更新结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.config")
    @PutMapping(value = "/{app_id}/config", description = "通过config更新aipp")
    public Rsp<AppBuilderAppDto> saveConfig(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated AppBuilderSaveConfigDto appBuilderSaveConfigDto) {
        return this.appService.saveConfig(appId, appBuilderSaveConfigDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 根据graph更新aipp。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId app的Id。
     * @param flowGraphDto graph的DTO。
     * @return 结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.graph")
    @PutMapping(value = "/{app_id}/graph", description = "根据graph更新aipp")
    public Rsp<AppBuilderAppDto> updateByGraph(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated AppBuilderFlowGraphDto flowGraphDto) {
        return this.appService.updateFlowGraph(appId, flowGraphDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 更新app的基本信息。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId appId。
     * @param appDto 基本信息。
     * @return 结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.update")
    @PutMapping(value = "/{app_id}", description = "更新 app")
    public Rsp<AppBuilderAppDto> update(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody @Validated @SpanAttr("name:$.name") AppBuilderAppDto appDto) {
        return this.appService.updateApp(appId, appDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 发布 app。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appDto app的信息。
     * @return 暂时使用 aipp 运行态返回值。
     */
    @CarverSpan(value = "operation.appBuilderApp.publish")
    @PostMapping(path = "/{app_id}/publish", description = "发布 app ")
    public Rsp<AippCreateDto> publish(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated @SpanAttr("name:$.name, version:$.version") AppBuilderAppDto appDto) {
        return this.appService.publish(appDto, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 调试App。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appDto app的信息。
     * @return 结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.debug")
    @PostMapping(path = "/{app_id}/debug", description = "调试 app ")
    public Rsp<AippCreateDto> debug(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody @Validated @SpanAttr("name:$.name") AppBuilderAppDto appDto) {
        return Rsp.ok(ConvertUtils.toAippCreateDto(this.appGenericable.debug(appDto,
                this.contextOf(httpRequest, tenantId))));
    }

    /**
     * 获取 app 最新发布版本信息。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId 待请求的appId。
     * @return 返回结果。
     */
    @GetMapping(path = "/{app_id}/latest_published", description = "获取 app 最新发布版本信息")
    public Rsp<AippCreateDto> latestPublished(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return Rsp.ok(ConvertUtils.toAippCreateDto(this.appGenericable.queryLatestPublished(appId,
                this.contextOf(httpRequest, tenantId))));
    }

    /**
     * 获取灵感大全的部门信息。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appDto app的DTO。
     * @return 结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.idea")
    @PostMapping(path = "/{app_id}/inspiration/department", description = "获取灵感大全的部门信息")
    public Rsp<AippCreateDto> inspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody @Validated AppBuilderAppDto appDto) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除 app。
     *
     * @param httpRequest 请求。
     * @param tenantId 租户Id。
     * @param appId 待删除的appId。
     * @return 空结果。
     */
    @CarverSpan(value = "operation.appBuilderApp.delete")
    @DeleteMapping(path = "/{app_id}", description = "删除 app")
    public Rsp<Void> delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") @SpanAttr("app_id") String appId) {
        this.appService.delete(appId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 导出应用配置。
     *
     * @param httpRequest 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param appId 表示待导出配置的应用的 id 的 {@link String}。
     * @param response 表示服务端响应的 {@link HttpClassicServerResponse}。
     * @return 表示到出应用配置的 {@link AppExportDto}。
     */
    @CarverSpan(value = "operation.appBuilderApp.export")
    @GetMapping(path = "/export/{app_id}", description = "导出应用配置")
    public FileEntity export(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") String appId, HttpClassicServerResponse response) {
        AppExportDto configDto = this.appService.export(appId, this.contextOf(httpRequest, tenantId));
        ByteArrayInputStream fileStream =
                new ByteArrayInputStream(JsonUtils.toJsonString(configDto).getBytes(StandardCharsets.UTF_8));
        return FileEntity.createAttachment(response,
                configDto.getApp().getName() + ".json",
                fileStream,
                fileStream.available());
    }

    /**
     * 根据应用配置进行可用性校验。
     *
     * @param httpRequest 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param appCheckDtos 表示待检查配置项的{@link List}{@code <}{@link AppCheckDto}{@code >}。
     * @return 表示检查结果的 {@link Rsp}{@code <}{@link List}{@code <}{@link AppCheckDto}{@code >}{@code >}。
     */
    @CarverSpan(value = "operation.appBuilderApp.availableCheck")
    @PostMapping(path = "/available-check", description = "应用可用性校验")
    public Rsp<List<CheckResult>> checkAvailable(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody List<AppCheckDto> appCheckDtos) {
        return Rsp.ok(this.appService.checkAvailable(appCheckDtos, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 根据应用配置导入应用。
     *
     * @param httpRequest 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param appConfig 表示上传的文件的 {@link PartitionedEntity}。
     * @return 表示应用创建信息的 {@link AppBuilderAppDto}。
     * @throws AippException 导入应用异常会抛出该异常
     */
    @CarverSpan(value = "operation.appBuilderApp.import")
    @PostMapping(path = "/import", description = "导入应用配置")
    public Rsp<AppBuilderAppDto> importApp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, PartitionedEntity appConfig) {
        this.fitRuntime.publisherOfEvents().publishEvent(new AppCreatingEvent(this));
        if (this.appService.getAppCount(tenantId, this.buildAppQueryCondition(new AppQueryCondition(), DEFAULT_TYPE))
                >= this.maxAppNum) {
            throw new AippException(AippErrCode.TOO_MANY_APPS);
        }
        if (appConfig.entities().isEmpty() || !appConfig.entities().get(0).isFile()) {
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }
        try (FileEntity appConfigFileEntity = appConfig.entities().get(0).asFile()) {
            if (!AppImExportUtil.isJsonFile(appConfigFileEntity.filename())) {
                throw new IOException("Uploaded config is not a '.json' file.");
            }
            String configString = new String(AppImExportUtil.readAllBytes(appConfigFileEntity.getInputStream()),
                    StandardCharsets.UTF_8);
            return Rsp.ok(this.appService.importApp(configString, this.contextOf(httpRequest, tenantId)));
        } catch (IOException e) {
            log.error("Failed to read uploaded application config file", e);
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }
    }

    /**
     * 恢复应用到指定历史版本。
     *
     * @param httpRequest 表示 http 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param appId 表示应用 id 的 {@link String}。
     * @param resetAppId 表示指定历史版本 id 的 {@link String}。
     * @return 表示恢复后应用信息的 {@link AppBuilderAppDto}。
     */
    @CarverSpan(value = "operation.appBuilderApp.resetApp")
    @PostMapping(path = "/reset/{app_id}")
    public Rsp<AppBuilderAppDto> resetApp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestBody String resetAppId) {
        return Rsp.ok(this.appService.resetApp(appId, resetAppId, contextOf(httpRequest, tenantId)));
    }

    private AppQueryCondition buildAppQueryCondition(AppQueryCondition cond, String type) {
        cond.setType(type);
        if (cond.getExcludeNames() == null) {
            cond.setExcludeNames(this.excludeNames);
        } else {
            cond.getExcludeNames().addAll(this.excludeNames);
        }
        return cond;
    }
}
