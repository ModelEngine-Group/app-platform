/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.ACTIVE;
import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.INACTIVE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;

import lombok.AllArgsConstructor;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AippQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.TaskDomainEntity;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDetailDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippOverviewDto;
import modelengine.fit.jober.aipp.dto.AippOverviewRspDto;
import modelengine.fit.jober.aipp.dto.AippVersionDto;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.aipp.service.AippFlowService;
import modelengine.fit.jober.aipp.util.AippStringUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.ConflictException;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.service.ToolService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * aipp编排服务层接口实现
 *
 * @author 刘信宏
 * @since 2023-12-12
 */
@Component
@AllArgsConstructor
public class AippFlowServiceImpl implements AippFlowService {
    private static final Logger log = Logger.get(AippFlowServiceImpl.class);
    private static final String DEFAULT_VERSION = "1.0.0";

    private final FlowsService flowsService;
    private final AppTaskService appTaskService;

    /**
     * 查询aipp详情
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @return aipp 详情
     */
    @Override
    public Rsp<AippDetailDto> queryAippDetail(String aippId, String version, OperationContext context) {
        AppTask task = this.appTaskService.getLatest(aippId, version, context).orElseThrow(() -> {
            log.error("The app task is not found. [appSuiteId={}, version={}]", aippId, version);
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });
        String flowConfigId = task.getEntity().getFlowConfigId();
        try {
            FlowInfo rsp = this.flowsService.getFlows(flowConfigId, version, context);  // 是否要改？
            AippDetailDto detail = new AippDetailDto();
            detail.setCreatedAt(task.getEntity().getCreationTime());
            detail.setUpdatedAt(task.getEntity().getLastModificationTime());
            detail.setUpdater(task.getEntity().getLastModifier());
            detail.setAippId(task.getEntity().getAppSuiteId());
            detail.setFlowViewData(JsonUtils.parseObject(rsp.getConfigData()));
            detail.setVersion(version);
            detail.setStatus(task.getEntity().getStatus());
            detail.setIcon(task.getEntity().getIcon());
            Optional.ofNullable(task.getEntity().getDescription()).ifPresent(detail::setDescription);
            Optional.ofNullable(task.getEntity().getPublishTime())
                    .map(LocalDateTime::parse)
                    .ifPresent(detail::setPublishAt);
            return Rsp.ok(detail);
        } catch (JobberException e) {
            log.error("queryAippDetail failed, task {}", task.getEntity().toString());
            throw new AippException(context, AippErrCode.OBTAIN_APP_ORCHESTRATION_INFO_FAILED);
        }
    }

    /**
     * 查询aipp详情
     *
     * @param cond 过滤条件
     * @param page 分页
     * @param context 操作上下文
     * @return aipp 概况
     */
    @Override
    public PageResponse<AippOverviewRspDto> listAipp(AippQueryCondition cond, PaginationCondition page,
            OperationContext context) {
        log.info("listAipp cond{} page{}", cond, page);
        RangedResultSet<AppTask> resultSet = this.appTaskService.getTasks(
                AppTask.asQueryEntity(page.getOffset(), page.getPageSize())
                        .addName(cond.getName())
                        .addCreator(cond.getCreator())
                        .addCategory(JaneCategory.AIPP.name())
                        .putQueryAttribute(AippConst.ATTR_AIPP_TYPE_KEY, NORMAL.name())
                        .addOrderBy(cond.getSort(), cond.getOrder())
                        .build(), context);

        List<AippOverviewRspDto> overviewDtoList = resultSet.getResults().stream().map(task -> {
            AippOverviewRspDto dto = new AippOverviewRspDto();
            dto.setCreatedAt(task.getEntity().getCreationTime());
            dto.setUpdatedAt(task.getEntity().getLastModificationTime());
            dto.setUpdater(task.getEntity().getLastModifier());
            dto.setAippId(task.getEntity().getAppSuiteId());
            dto.setStatus(task.getEntity().getStatus());
            dto.setVersion(task.getEntity().getVersion()); // 兼容没有基线版本的1.0.0版本草稿
            if (task.isDraft()) {
                dto.setVersion(task.getEntity().getBaseLineVersion());
                dto.setDraftVersion(task.getEntity().getVersion());
            }
            String publishTime = task.getEntity().getPublishTime();
            Optional.ofNullable(publishTime).map(LocalDateTime::parse).ifPresent(dto::setPublishAt);
            return dto;
        }).sorted(Comparator.comparing(AippOverviewDto::getUpdatedAt).reversed()).collect(Collectors.toList());

        return new PageResponse<>(resultSet.getRange().getTotal(), null, overviewDtoList);
    }

    /**
     * 查询指定aipp的版本列表
     *
     * @param aippId aippId
     * @param ctx 操作上下文
     * @return aipp 版本概况
     */
    @Override
    public List<AippVersionDto> listAippVersions(String aippId, OperationContext ctx) {
        List<AppTask> tasks = this.appTaskService.getTaskList(aippId, NORMAL.name(), ACTIVE.getCode(), ctx);
        return tasks.stream()
                .map(t -> new AippVersionDto(t.getEntity().getVersion(), t.getEntity().getStatus(),
                        t.getEntity().getCreator(), t.getEntity().getCreationTime()))
                .collect(Collectors.toList());
    }

    /**
     * 删除aipp
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @throws AippForbiddenException 禁止删除aipp异常
     */
    @Override
    public void deleteAipp(String aippId, String version, OperationContext context) throws AippForbiddenException {
        log.info("deleting aipp {} version {}", aippId, version);
        AppTask task = this.appTaskService.getLatest(aippId, version, context).orElseThrow(() -> {
            log.error("The app task is not found. [appSuiteId={}, version={}]", aippId, version);
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });
        if (task.isActive()) {
            log.error("not allow to delete an active aipp, aippId {} version {} status {}", aippId, version,
                    task.getEntity().getStatus());
            throw new AippForbiddenException(context, AippErrCode.DELETE_AIPP_FORBIDDEN);
        }
        try {
            int ret = this.flowsService.deleteFlows(task.getEntity().getFlowConfigId(), task.getEntity().getVersion(),
                    context);
            if (ret != 0) {
                log.error("delete aipp {} version {} failed, ret {}", aippId, version, ret);
            }
        } catch (JobberException e) {
            log.error("delete aipp failed, aipp {} version {}", aippId, version);
            throw new AippException(context, AippErrCode.APP_DELETE_FAILED);
        }
        this.appTaskService.deleteTaskById(task.getEntity().getTaskId(), context);
    }

    /**
     * 创建aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id和版本信息
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    @Override
    public AippCreateDto create(AippDto aippDto, OperationContext context) throws AippException {
        return this.createAippHandle(aippDto, context);
    }

    /**
     * 创建aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id和版本信息
     */
    private AippCreateDto createAippHandle(AippDto aippDto, OperationContext context) {
        Tuple flowCreateArgs = Tuple.duet(JsonUtils.toJsonString(aippDto.getFlowViewData()), context);
        return this.saveAipp(aippDto,
                null,
                context,
                (tuple -> this.buildFlowCreateFunc(flowCreateArgs)),
                flowCreateArgs);
    }

    private FlowInfo buildFlowCreateFunc(Tuple flowCreateArgs) {
        if (flowCreateArgs.capacity() < 2) {
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID,
                    StringUtils.format("Flow create size is {0}", flowCreateArgs.capacity()));
        }
        String configData = ObjectUtils.cast(flowCreateArgs.get(0).orElse(null));
        OperationContext operationContext = ObjectUtils.cast(flowCreateArgs.get(1).orElse(null));
        return this.flowsService.createFlows(configData, operationContext);
    }

    private AippCreateDto upgradeAippHandle(AippDto aippDto, AippCreateDto baselineInfo, OperationContext context,
            String flowId, String newFlowVersion) {
        Tuple flowUpgradeArgs =
                Tuple.quartet(flowId, newFlowVersion, JsonUtils.toJsonString(aippDto.getFlowViewData()), context);
        return this.saveAipp(aippDto,
                baselineInfo,
                context,
                (tuple -> this.buildFlowUpgradeFunc(flowUpgradeArgs)),
                flowUpgradeArgs);
    }

    private FlowInfo buildFlowUpgradeFunc(Tuple flowUpgradeArgs) {
        if (flowUpgradeArgs.capacity() < 4) {
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID,
                    StringUtils.format("flow upgrade size is {0}", flowUpgradeArgs.capacity()));
        }
        String flowId = ObjectUtils.cast(flowUpgradeArgs.get(0).orElse(null));
        String newFlowVersion = ObjectUtils.cast(flowUpgradeArgs.get(1).orElse(null));
        String configData = ObjectUtils.cast(flowUpgradeArgs.get(2).orElse(null));
        OperationContext operationContext = ObjectUtils.cast(flowUpgradeArgs.get(3).orElse(null));
        return this.flowsService.upgradeFlows(flowId, newFlowVersion, configData, operationContext);
    }

    /**
     * 保存 aipp 相关信息。
     *
     * @param aippDto aipp 定义
     * @param baselineInfo aipp基线版本信息, 非升级场景为null
     * @param context 操作上下文
     * @param flowSaveFunc 流保存函数
     * @param flowSaveArgs 流保存参数
     * @return aipp id和版本信息
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    private AippCreateDto saveAipp(AippDto aippDto, AippCreateDto baselineInfo, OperationContext context,
            Function<Tuple, FlowInfo> flowSaveFunc, Tuple flowSaveArgs) {
        log.info("create aipp, name {}", aippDto.getName());
        if (StringUtils.isBlank(aippDto.getName())) {
            log.error("aipp name cant be blank, create flow failed, tenantId {}", context.getTenantId());
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        }
        FlowInfo flowInfo;
        try {
            flowInfo = flowSaveFunc.apply(flowSaveArgs);
        } catch (JobberException e) {
            log.error("create flow failed, tenantId {} aipp {}, error {}",
                    context.getTenantId(),
                    aippDto.getName(),
                    e.getMessage());
            throw new AippException(context, AippErrCode.APP_PUBLISH_FAILED);
        }
        try {
            AppTask createArgs = AppTask.asCreateEntity()
                    .fetch(aippDto)
                    .fetch(baselineInfo)
                    .setFlowConfigId(flowInfo.getFlowId())
                    .setAippType(NORMAL.name())
                    .build();
            log.debug("create aipp, task info {}", createArgs.getEntity().toString());
            AppTask appTask = this.appTaskService.createTask(createArgs, context);
            return AippCreateDto.builder()
                    .aippId(appTask.getEntity().getAppSuiteId())
                    .version(appTask.getEntity().getVersion())
                    .build();
        } catch (ConflictException e) {
            log.error("create aipp failed, error: {}", e.getMessage());
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
    }

    private void updateMetaDeclaration(String metaVersionId, String version, AippDto aippDto, OperationContext ctx) {
        TaskDomainEntity updateEntity = AppTask.asUpdateEntity(metaVersionId)
                .setName(aippDto.getName())
                .setDescription(aippDto.getDescription())
                .setIcon(aippDto.getIcon())
                .setVersion(version)
                .fetch(aippDto.getFlowViewData());
        log.debug("patch meta, update entity {}", updateEntity);
        this.appTaskService.updateTask(updateEntity.build(), ctx);
    }

    private void validateUpdate(String aippId, AppTask task, String name, OperationContext context) {
        if (task.isActive()) {
            log.error("not allow to update an active aipp, aippId {} status {}", aippId, task.getEntity().getStatus());
            throw new AippForbiddenException(context, AippErrCode.UPDATE_AIPP_FORBIDDEN);
        }
        if (StringUtils.isBlank(name)) {
            log.error("aipp name cant be blank, create flow failed, aippId {}", aippId);
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        }
    }

    /**
     * 更新aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     * @throws AippForbiddenException 禁止更新aipp异常
     * @throws AippParamException 入参异常
     */
    @Override
    public AippCreateDto update(AippDto aippDto, OperationContext context)
            throws AippForbiddenException, AippParamException {
        String aippId = aippDto.getId();
        String version = aippDto.getVersion();
        log.info("update aipp {} name {}", aippId, aippDto.getName());
        Optional<AppTask> taskOp = this.appTaskService.getLatest(aippId, version, context);
        if (taskOp.isEmpty()) {
            return this.updateNewVersionAipp(aippDto, context, aippId, version);
        }
        AppTask task = taskOp.get();
        validateUpdate(aippId, task, aippDto.getName(), context);
        this.updateMetaDeclaration(task.getEntity().getTaskId(), task.getEntity().getVersion(), aippDto, context);
        // 更新流程
        if (aippDto.getFlowViewData() == null || aippDto.getFlowViewData().isEmpty()) {
            return AippCreateDto.builder().aippId(aippId).version(task.getEntity().getVersion()).build();
        }
        try {
            this.flowsService.updateFlows(task.getEntity().getFlowConfigId(),
                    task.getEntity().getAttributeVersion(),
                    JsonUtils.toJsonString(aippDto.getFlowViewData()),
                    context);
        } catch (JobberException e) {
            log.error("update aipp failed, aipp {} name {}", aippId, aippDto.getName());
            throw new AippException(context, AippErrCode.APP_UPDATE_FAILED);
        }
        return AippCreateDto.builder().aippId(aippId).version(task.getEntity().getVersion()).build();
    }

    private AippCreateDto updateNewVersionAipp(AippDto aippDto, OperationContext context, String aippId,
            String version) {
        AppTask task = this.appTaskService.getLatestCreate(aippId, NORMAL.name(), INACTIVE.getCode(), context)
                .orElseThrow(() -> {
                    log.error("The app task is not found. [appSuiteId={}, aippType={}, status={}]",
                            aippId,
                            NORMAL.name(),
                            INACTIVE.getCode());
                    return new AippException(AippErrCode.APP_NOT_FOUND);
                });

        String flowId = Optional.ofNullable(task.getEntity().getFlowConfigId()).orElse(StringUtils.EMPTY);
        this.upgradeAippHandle(aippDto, AippCreateDto.builder().aippId(aippId).build(), context, flowId, version);
        return this.update(aippDto, context);
    }

    /**
     * 退出预览aipp的清理
     *
     * @param previewAippId 预览版本的aippId
     * @param previewVersion 预览版本号
     * @param context 操作上下文
     */
    @Override
    public void cleanPreviewAipp(String previewAippId, String previewVersion, OperationContext context) {
        // 过滤非预览版本
        if (!AippStringUtils.isPreview(previewVersion)) {
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version is not preview");
        }
        CompletableFuture.runAsync(() -> {
            AppTask previewTask =
                    this.appTaskService.getLatest(previewAippId, previewVersion, context).orElseThrow(() -> {
                        log.error("The app task is not found. [appSuiteId={}, version={}]",
                                previewAippId,
                                previewVersion);
                        return new AippException(AippErrCode.APP_NOT_FOUND);
                    });
            previewTask.cleanResource(context);
        });
    }

    private boolean isValidUpgradeVersion(String oldVersion, String newVersion) {
        final String delimiter = "\\.";
        if (StringUtils.isBlank(oldVersion) || StringUtils.isBlank(newVersion)) {
            return false;
        }
        String[] oldVersionArray = oldVersion.split(delimiter);
        String[] newVersionArray = newVersion.split(delimiter);
        if (oldVersionArray.length != newVersionArray.length) {
            return false;
        }
        try {
            for (int i = 0; i < oldVersionArray.length; ++i) {
                int oldVersionInt = Integer.parseInt(oldVersionArray[i]);
                int newVersionInt = Integer.parseInt(newVersionArray[i]);
                if (newVersionInt > oldVersionInt) {
                    return true;
                } else if (newVersionInt < oldVersionInt) {
                    return false;
                } else {
                    continue;
                }
            }
        } catch (NumberFormatException e) {
            return false;  // 解析不成int
        }
        return true;  // 全相等
    }

    /**
     * 升级aipp
     *
     * @param baselineVersion 基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return 应用升级信息
     * @throws AippParamException 入参异常
     */
    @Override
    public AippCreateDto upgrade(String baselineVersion, AippDto aippDto, OperationContext context) {
        String aippId = aippDto.getId();
        log.info("upgrade aipp {} name {}", aippId, aippDto.getName());
        String newAippVersion = aippDto.getVersion();

        // 校验版本号是否为递增
        if (!this.isValidUpgradeVersion(baselineVersion, newAippVersion)) {
            throw new AippParamException(context,
                    AippErrCode.INPUT_PARAM_IS_INVALID,
                    AippConst.FLOW_CONFIG_VERSION_KEY);
        }
        AppTask task = this.appTaskService.getLatestCreate(aippId, NORMAL.name(), context).orElseThrow(() -> {
            log.error("The app task is not found. [appSuiteId={}, aippType={}]", aippId, NORMAL.name());
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });

        String flowId = Optional.ofNullable(task.getEntity().getFlowConfigId()).orElse(StringUtils.EMPTY);
        Validation.notBlank(flowId, () -> {
            throw new AippParamException(context,
                    AippErrCode.INPUT_PARAM_IS_INVALID,
                    AippConst.ATTR_FLOW_CONFIG_ID_KEY);
        });
        String newFlowVersion =
                aippDto.getFlowViewData().getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, DEFAULT_VERSION).toString();
        if (task.isUpgrade(newAippVersion)) {
            this.upgradeAippHandle(aippDto,
                    AippCreateDto.builder().aippId(aippId).version(baselineVersion).build(),
                    context,
                    flowId,
                    newFlowVersion);
        }
        return this.update(aippDto, context);
    }
}
