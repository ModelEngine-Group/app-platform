/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.PublishedAppResDto;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Optional;

/**
 * 应用创建服务接口
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderAppService {
    /**
     * 创建应用。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param dto 表示应用信息的 {@link AppBuilderAppCreateDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param isUpgrade 表示是否为升级操作的 {@link boolean}。
     * @return 创建后的应用信息的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.create")
    AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context, boolean isUpgrade);

    /**
     * 查询已创建的应用数量。
     *
     * @param tenantId 表示租户标识的 {@link String}。
     * @param cond 表示应用查询条件的 {@link AppQueryCondition}。
     * @return 表示已创建的应用数量的 {@code long}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.getAppCount")
    long getAppCount(String tenantId, AppQueryCondition cond);

    /**
     * 更新应用信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param appDto 表示应用信息的 {@link AppBuilderAppDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.update")
    Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context);

    /**
     * 更新应用配置信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param configDto 表示应用配置信息的 {@link AppBuilderConfigDto}。
     * @param properties 表示需要更新的属性的 {@link List}{@code <}{@link AppBuilderConfigFormPropertyDto}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用配置信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.config.update")
    Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto,
            List<AppBuilderConfigFormPropertyDto> properties, OperationContext context);

    /**
     * 保存应用配置信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param appBuilderSaveConfigDto 表示应用配置信息的 {@link AppBuilderSaveConfigDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用配置信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.config.save.config")
    Rsp<AppBuilderAppDto> saveConfig(String appId, AppBuilderSaveConfigDto appBuilderSaveConfigDto,
            OperationContext context);

    /**
     * 更新应用流程图信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param graphDto 表示应用流程图信息的 {@link AppBuilderFlowGraphDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用流程图信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.flow.graph.update")
    Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto, OperationContext context);

    /**
     * 发布应用。
     *
     * @param appDto 表示应用信息的 {@link AppBuilderAppDto}。
     * @param contextOf 表示操作上下文的 {@link OperationContext}。
     * @return 发布后的应用信息的 {@link Rsp}{@code <}{@link AippCreateDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.publish")
    Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf);

    /**
     * 根据名称获取应用配置信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param name 表示应用配置信息的名称的 {@link String}。
     * @return 获取到的应用配置信息的
     * {@link Optional}{@code <}{@link AppBuilderConfigFormPropertyDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.get.property.by.name")
    Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name);

    /**
     * 获取应用的列表信息。
     *
     * @param cond 表示获取条件的 {@link AppQueryCondition}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示获取数据的最大个数的 {@code int}。
     * @return 获取到的列表信息集合
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.list")
    Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(AppQueryCondition cond, OperationContext context, long offset,
            int limit);

    /**
     * 删除应用。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.delete")
    void delete(String appId, OperationContext context);

    /**
     * 获取应用的历史版本信息。
     *
     * @param cond 表示获取条件的 {@link AppQueryCondition}。
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示获取数据的最大个数的 {@code int}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 获取到的历史版本信息集合的 {@link RangedResultSet}{@code <}{@link PublishedAppResDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.recent.published")
    RangedResultSet<AppBuilderAppDto> recentPublished(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context);

    /**
     * 获取应用的发布详情。
     *
     * @param uniqueName 表示应用发布的唯一名称
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 获取到的发布详情的 {@link PublishedAppResDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.published")
    PublishedAppResDto published(String uniqueName, OperationContext context);

    /**
     * 导出应用配置。
     *
     * @param appId 表示应用的唯一表示的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 导出的应用配置信息的 {@link AppExportDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.export")
    AppExportDto export(String appId, OperationContext context);

    /**
     * 根据应用配置进行可用性校验。
     *
     * @param appCheckDtos 表示待检查配置项的{@link List}{@code <}{@link AppCheckDto}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示检查结果的 {@link List}{@code <}{@link AppCheckDto}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.check")
    List<CheckResult> checkAvailable(List<AppCheckDto> appCheckDtos, OperationContext context);

    /**
     * 导入应用。
     *
     * @param appConfig 表示上传的应用配置文件的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示创建的应用的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.import")
    AppBuilderAppDto importApp(String appConfig, OperationContext context);

    /**
     * 将应用发布为应用模板。
     *
     * @param createDto 表示发布应用模板的基础信息的 {@link TemplateAppCreateDto}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     * @return 表示发布后的应用模板信息的 {@link TemplateInfoDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.publishTemplate")
    TemplateInfoDto publishTemplateFromApp(TemplateAppCreateDto createDto, OperationContext context);

    /**
     * 跟据应用模板创建应用。
     *
     * @param createDto 表示根据模板创建应用的基础信息的 {@link TemplateAppCreateDto}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     * @return 表示应用创建完成后应用详情的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.createByTemplate")
    AppBuilderAppDto createAppByTemplate(TemplateAppCreateDto createDto, OperationContext context);

    /**
     * 删除指定的应用模板。
     *
     * @param templateId 表示待删除模板的唯一的 id 的 {@link String}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.deleteTemplate")
    void deleteTemplate(String templateId, OperationContext context);

    /**
     * 恢复应用到指定历史版本。
     *
     * @param appId 表示应用 id 的 {@link String}。
     * @param resetId 表示指定历史版本 id 的 {@link String}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     * @return 表示恢复应用完成后应用详情的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.reset")
    AppBuilderAppDto resetApp(String appId, String resetId, OperationContext context);
}
