/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.common.RangedResultSet;

import modelengine.fitframework.flowable.Choir;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 应用版本服务.
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface AppVersionService {
    /**
     * 通过appId获取 {@link AppVersion}.
     *
     * @param appId app多版本中的唯一标识.
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 对象.
     */
    Optional<AppVersion> getByAppId(String appId);

    /**
     * 通过path获取 {@link AppVersion}.
     *
     * @param path 路径.
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 对象.
     */
    Optional<AppVersion> getByPath(String path);

    /**
     * 强制获取一个应用，若获取不到则抛出应用版本不存在的异常.
     *
     * @param appId 应用版本id.
     * @return {@link AppVersion} 对象
     * @throws AippException 异常.
     */
    AppVersion retrieval(String appId);

    /**
     * 通过appSuiteId获取所有的 {@link AppVersion} 列表.
     *
     * @param appSuiteId 应用的唯一id.
     * @return {@link List}{@code <}{@link AppVersion}{@code >} 集合.
     */
    List<AppVersion> getByAppSuiteId(String appSuiteId);

    /**
     * 运行 App 的某个版本.
     *
     * @param request 运行请求.
     * @param context 操作人上下文信息.
     * @return {@link Choir}{@code <}{@link Object}{@code >} SSE对象.
     */
    Choir<Object> run(CreateAppChatRequest request, OperationContext context);

    /**
     * 调试 App 的某个版本.
     *
     * @param request 运行请求.
     * @param context 操作人上下文信息.
     * @return {@link Choir}{@code <}{@link Object}{@code >} SSE对象.
     */
    Choir<Object> debug(CreateAppChatRequest request, OperationContext context);

    /**
     * 重新启动任务实例.
     *
     * @param instanceId 任务实例id.
     * @param params 重启参数.
     * @param context 操作人上下文.
     * @return {@link Choir}{@code <}{@link Object}{@code >} SSE对象.
     */
    Choir<Object> restart(String instanceId, Map<String, Object> params, OperationContext context);

    /**
     * 创建一个 {@link AppVersion} 对象.
     *
     * @param templateId 模板id.
     * @param dto 创建参数.
     * @param context 操作人上下文信息.
     * @return {@link AppVersion} 对象.
     */
    AppVersion create(String templateId, AppBuilderAppCreateDto dto, OperationContext context);

    /**
     * 通过模板对象创建app.
     *
     * @param template 模板对象.
     * @param context 操作人上下文.
     * @return {@link AppVersion} 应用版本.
     */
    AppVersion createByTemplate(AppTemplate template, OperationContext context);

    /**
     * 升级并创建一个新版本.
     *
     * @param appId 待升级的appid.
     * @param dto 升级参数.
     * @param context 操作人上下文信息.
     * @return {@link AppVersion} 对象.
     */
    AppVersion upgrade(String appId, AppBuilderAppCreateDto dto, OperationContext context);

    /**
     * 校验app名称是否符合规范.
     *
     * @param name 名称.
     * @param context 操作人上下文信息.
     * @throws AippException 业务异常.
     */
    void validateAppName(String name, OperationContext context) throws AippException;

    /**
     * 通过应用id获取最新创建的应用版本.
     *
     * @param appSuiteId 应用id.
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 对象.
     */
    Optional<AppVersion> getLatestCreatedByAppSuiteId(String appSuiteId);

    /**
     * 通过应用id获取最先创建的应用版本.
     *
     * @param appSuiteId 应用id.
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 对象.
     */
    Optional<AppVersion> getFirstCreatedByAppSuiteId(String appSuiteId);

    /**
     * 通过tenantId以及查询条件分页查询.
     *
     * @param cond 查询条件.
     * @param tenantId 租户id.
     * @param offset 偏移量.
     * @param limit 条数限制.
     * @return {@link AppVersion} 分页集合.
     */
    RangedResultSet<AppVersion> pageListByTenantId(AppQueryCondition cond, String tenantId, long offset, int limit);

    /**
     * 根据条件以及tenantId统计app数量.
     *
     * @param cond 查询条件.
     * @param tenantId 租户id.
     * @return 数量.
     */
    long countByTenantId(AppQueryCondition cond, String tenantId);

    /**
     * 根据传入的 {@link AppBuilderAppDto} 数据进行修改.
     *
     * @param appId 版本id.
     * @param appDto 待修改数据.
     * @param context 操作人上下文信息.
     * @return {@link AppVersion} 对象.
     */
    AppVersion update(String appId, AppBuilderAppDto appDto, OperationContext context);

    /**
     * 根据传入的 {@link AppBuilderFlowGraphDto} 数据进行修改.
     *
     * @param appId 版本id.
     * @param graphDto 待修改数据.
     * @param context 操作人上下文信息.
     * @return {@link AppVersion} 对象.
     */
    AppVersion update(String appId, AppBuilderFlowGraphDto graphDto, OperationContext context);

    /**
     * 根据传入的 {@link AppBuilderSaveConfigDto} 数据进行修改.
     *
     * @param appId 版本id.
     * @param appBuilderSaveConfigDto 待修改数据.
     * @param context 操作人上下文信息.
     * @return {@link AppVersion} 对象.
     */
    AppVersion update(String appId, AppBuilderSaveConfigDto appBuilderSaveConfigDto, OperationContext context);

    /**
     * 根据传入的 {@link AppVersion} 进行修改.
     *
     * @param appVersion {@link AppVersion} 对象.
     */
    void update(AppVersion appVersion);

    /**
     * 通过id批量删除.
     *
     * @param appIds 版本id集合.
     */
    void deleteByIds(List<String> appIds);

    /**
     * 判断应用名称是否已经存在.
     *
     * @param appName 应用名称.
     * @param context 操作人上下文.
     * @return true/false.
     */
    boolean isNameExists(String appName, OperationContext context);

    /**
     * 保存.
     *
     * @param appVersion {@link AppVersion} 对象.
     */
    void save(AppVersion appVersion);
}
