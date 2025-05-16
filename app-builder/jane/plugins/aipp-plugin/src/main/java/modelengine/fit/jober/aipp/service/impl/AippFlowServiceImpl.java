/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.dynamicform.entity.FormMetaItem;
import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jade.waterflow.entity.FlowNodeFormInfo;
import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.WaterFlowService;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AippQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.convertor.FormMetaConvertor;
import modelengine.fit.jober.aipp.convertor.MetaConvertor;
import modelengine.fit.jober.aipp.convertor.TaskPropertyConvertor;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDetailDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippNodeForms;
import modelengine.fit.jober.aipp.dto.AippOverviewDto;
import modelengine.fit.jober.aipp.dto.AippOverviewRspDto;
import modelengine.fit.jober.aipp.dto.AippVersionDto;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AippFlowService;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.aipp.util.AippStringUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fit.jober.aipp.util.VersionUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.ConflictException;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.entity.transfer.AppData;
import modelengine.jade.store.entity.transfer.AppPublishData;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.ToolService;
import modelengine.jade.store.service.support.DeployStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
public class AippFlowServiceImpl implements AippFlowService {
    /**
     * 预览aipp version uuid后缀长度
     */
    public static final int PREVIEW_UUID_LEN = 6;

    private static final Logger log = Logger.get(AippFlowServiceImpl.class);

    private static final int RETRY_PREVIEW_TIMES = 5;

    private static final String DEFAULT_VERSION = "1.0.0";

    private static final String OLD_VERSION_ID = "00000000000000000000000000000000";

    private static final String APP_TYPE = "app";

    private static final String WATERFLOW_TYPE = "waterflow";

    private static final String DEPLOYED = "DEPLOYED";

    private static final String APP_TYPE_TAG_PREFIX = "APP_TYPE_";

    private final AppBuilderFormRepository formRepository;

    private final FlowsService flowsService;

    private final MetaService metaService;

    private final AippFlowDefinitionService flowDefinitionService;

    private final AippRunTimeService aippRunTimeService;

    private final AppBuilderAppFactory factory;

    private final AppBuilderAppMapper appBuilderAppMapper;

    private final AppService appService;

    private final PluginService pluginService;

    private final ToolService toolService;

    public AippFlowServiceImpl(@Fit FlowsService flowsService, @Fit MetaService metaService,
            @Fit AippFlowDefinitionService flowDefinitionService, @Fit AippRunTimeService aippRunTimeService,
            @Fit AppBuilderFormRepository formRepository, AppBuilderAppMapper appBuilderAppMapper,
            AppBuilderAppFactory factory, @Fit AppService appService, @Fit PluginService pluginService,
            @Fit ToolService toolService) {
        this.flowsService = flowsService;
        this.metaService = metaService;
        this.flowDefinitionService = flowDefinitionService;
        this.aippRunTimeService = aippRunTimeService;
        this.formRepository = formRepository;
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.factory = factory;
        this.appService = appService;
        this.pluginService = pluginService;
        this.toolService = toolService;
    }

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
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        log.info("queryAippDetail aipp {} version {}, meta attr {}", aippId, version, meta.getAttributes());
        String flowConfigId = ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY));

        FlowInfo rsp;
        try {
            rsp = this.flowsService.getFlows(flowConfigId, version, context);  // 是否要改？
        } catch (JobberException e) {
            log.error("queryAippDetail failed, aipp {} version {}, meta attr {}",
                    aippId,
                    version,
                    meta.getAttributes());
            throw new AippException(context, AippErrCode.OBTAIN_APP_ORCHESTRATION_INFO_FAILED);
        }
        AippDetailDto detail = MetaConvertor.INSTANCE.toAippDetailDto(meta);
        detail.setFlowViewData(JsonUtils.parseObject(rsp.getConfigData()));
        detail.setVersion(version);
        detail.setStatus(ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_META_STATUS_KEY)));
        detail.setIcon(ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_META_ICON_KEY)));
        if (meta.getAttributes().containsKey(AippConst.ATTR_DESCRIPTION_KEY)) {
            detail.setDescription(ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_DESCRIPTION_KEY)));
        }
        if (meta.getAttributes().containsKey(AippConst.ATTR_PUBLISH_TIME_KEY)) {
            detail.setPublishAt(LocalDateTime.parse(ObjectUtils.<String>cast(meta.getAttributes()
                    .get(AippConst.ATTR_PUBLISH_TIME_KEY))));
        }

        return Rsp.ok(detail);
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
        MetaFilter metaFilter = new MetaFilter();
        if (StringUtils.isNotBlank(cond.getName())) {
            metaFilter.setNames(Collections.singletonList(cond.getName()));
        }
        if (StringUtils.isNotBlank(cond.getCreator())) {
            metaFilter.setCreators(Collections.singletonList(cond.getCreator()));
        }
        metaFilter.setCategories(Collections.singletonList(JaneCategory.AIPP.name()));
        metaFilter.setAttributes(Collections.singletonMap(AippConst.ATTR_AIPP_TYPE_KEY,
                Collections.singletonList(AippTypeEnum.NORMAL.name())));
        String sortEncode = MetaUtils.formatSorter(cond.getSort(), cond.getOrder());
        metaFilter.setOrderBys(Collections.singletonList(sortEncode));
        // 容老数据，当前把没有 aipp_type 的数据也进行获取。等数据库刷完数据后，更改逻辑
        RangedResultSet<Meta> metaRes =
                this.metaService.list(metaFilter, true, page.getOffset(), page.getPageSize(), context);
        List<AippOverviewRspDto> overviewDtoList = metaRes.getResults().stream().map(item -> {
            this.handleOldData(item);
            AippOverviewRspDto dto = MetaConvertor.INSTANCE.toAippOverviewRspDto(item);
            String status = ObjectUtils.<String>cast(item.getAttributes().get(AippConst.ATTR_META_STATUS_KEY));

            dto.setStatus(status);

            dto.setVersion(item.getVersion());  // 兼容没有基线版本的1.0.0版本草稿
            if (this.isDraft(item, status)) {
                dto.setVersion(ObjectUtils.<String>cast(item.getAttributes().get(AippConst.ATTR_BASELINE_VERSION_KEY)));

                dto.setDraftVersion(item.getVersion());
            }

            if (item.getAttributes().containsKey(AippConst.ATTR_PUBLISH_TIME_KEY)) {
                dto.setPublishAt(LocalDateTime.parse(ObjectUtils.<String>cast(item.getAttributes()
                        .get(AippConst.ATTR_PUBLISH_TIME_KEY))));
            }
            return dto;
        }).sorted(Comparator.comparing(AippOverviewDto::getUpdatedAt).reversed()).collect(Collectors.toList());

        return new PageResponse<>(metaRes.getRange().getTotal(), null, overviewDtoList);
    }

    private void handleOldData(Meta item) {
        if (Objects.equals(item.getId(), OLD_VERSION_ID)) {
            item.setId(item.getVersionId());
        }
    }

    private MetaFilter buildOldDataMetaFilter(MetaFilter metaFilter) {
        MetaFilter oldFilter = new MetaFilter(metaFilter.getMetaIds(),
                metaFilter.getVersionIds(),
                metaFilter.getNames(),
                metaFilter.getCategories(),
                metaFilter.getCreators(),
                metaFilter.getOrderBys(),
                metaFilter.getVersions(),
                metaFilter.getAttributes());
        oldFilter.setMetaIds(Collections.singletonList(OLD_VERSION_ID));
        oldFilter.setAttributes(Collections.emptyMap());
        return oldFilter;
    }

    private boolean isDraft(Meta item, String status) {
        return item.getAttributes().get(AippConst.ATTR_BASELINE_VERSION_KEY) != null && !Objects.equals(
                AippMetaStatusEnum.getAippMetaStatus(status),
                AippMetaStatusEnum.ACTIVE);
    }

    /**
     * 查询指定aipp的版本列表
     *
     * @param aippId aippId
     * @param context 操作上下文
     * @return aipp 版本概况
     */
    @Override
    public List<AippVersionDto> listAippVersions(String aippId, OperationContext context) {
        return MetaUtils.getAllPublishedMeta(this.metaService, aippId, context)
                .stream()
                .map(meta -> new AippVersionDto(meta.getVersion(),
                        ObjectUtils.cast(meta.getAttributes().get(AippConst.ATTR_META_STATUS_KEY)),
                        meta.getCreator(),
                        meta.getCreationTime()))
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
        Meta meta = MetaUtils.getAnyMeta(metaService, aippId, version, context);
        Map<String, Object> attr = meta.getAttributes();
        if (!AippMetaStatusEnum.INACTIVE.getCode().equals(attr.get(AippConst.ATTR_META_STATUS_KEY))) {
            log.error("not allow to delete an active aipp, aippId {} version {} status {}",
                    aippId,
                    version,
                    attr.getOrDefault(AippConst.ATTR_META_STATUS_KEY, "null"));
            throw new AippForbiddenException(context, AippErrCode.DELETE_AIPP_FORBIDDEN);
        }

        try {
            int ret =
                    this.flowsService.deleteFlows(ObjectUtils.<String>cast(attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)),
                            meta.getVersion(),
                            context);
            if (ret != 0) {
                log.error("delete aipp {} version {} failed, ret {}", aippId, version, ret);
            }
        } catch (JobberException e) {
            log.error("delete aipp failed, aipp {} version {}", aippId, version);
            throw new AippException(context, AippErrCode.APP_DELETE_FAILED);
        }
        this.metaService.delete(meta.getVersionId(), context);
    }

    private MetaDeclarationInfo buildInitialMetaDeclaration(AippDto aippDto, AippCreateDto baselineInfo,
            FlowInfo flowInfo, String aippType) {
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();
        declaration.setCategory(Undefinable.defined(JaneCategory.AIPP.name()));
        declaration.setName(Undefinable.defined(aippDto.getName()));
        declaration.setVersion(Undefinable.defined(aippDto.getVersion()));

        String description = aippDto.getDescription();
        declaration.putAttribute(AippConst.ATTR_FLOW_CONFIG_ID_KEY, flowInfo.getFlowId());
        declaration.putAttribute(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode());
        declaration.putAttribute(AippConst.ATTR_DESCRIPTION_KEY, description == null ? "aipp 编排应用" : description);
        declaration.putAttribute(AippConst.ATTR_META_ICON_KEY, aippDto.getIcon());
        declaration.putAttribute(AippConst.ATTR_AIPP_TYPE_KEY, aippType);
        declaration.putAttribute(AippConst.ATTR_APP_ID_KEY, aippDto.getAppId());
        if (baselineInfo != null) {
            declaration.putAttribute(AippConst.ATTR_BASELINE_VERSION_KEY, baselineInfo.getVersion());
            declaration.setBasicMetaTemplateId(Undefinable.defined(baselineInfo.getAippId()));
        }

        List<MetaPropertyDeclarationInfo> props = AippConst.STATIC_META_ITEMS.stream()
                .map(FormMetaConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .collect(Collectors.toList());
        declaration.setProperties(Undefinable.defined(props));

        return declaration;
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
        MetaDeclarationInfo declarationInfo =
                this.buildInitialMetaDeclaration(aippDto, baselineInfo, flowInfo, AippTypeEnum.NORMAL.name());
        log.debug("create aipp, declaration attr info {}", declarationInfo.getAttributes().getValue());
        try {
            Meta meta = this.metaService.create(declarationInfo, context);
            return AippCreateDto.builder().aippId(meta.getId()).version(meta.getVersion()).build();
        } catch (ConflictException e) {
            log.error("create aipp failed, error: {}", e.getMessage());
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
    }

    private void putAttrIfNotBlank(Map<String, Object> attr, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            attr.put(key, value);
        }
    }

    private void updateAttribute(Map<String, Object> attr, AippDto aippDto) {
        putAttrIfNotBlank(attr, AippConst.ATTR_DESCRIPTION_KEY, aippDto.getDescription());
        putAttrIfNotBlank(attr, AippConst.ATTR_META_ICON_KEY, aippDto.getIcon());

        Map<String, Object> flowView = aippDto.getFlowViewData();
        if (flowView != null && !flowView.isEmpty()) {
            attr.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY,
                    flowView.getOrDefault(AippConst.FLOW_CONFIG_ID_KEY, attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)));
            attr.put(AippConst.ATTR_VERSION_KEY,
                    flowView.getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, attr.get(AippConst.ATTR_VERSION_KEY)));
        }
    }

    private void updateMetaDeclaration(String metaVersionId, String version, AippDto aippDto, Map<String, Object> attr,
            OperationContext context) {
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();
        if (StringUtils.isNotBlank(aippDto.getName())) {
            declaration.setName(Undefinable.defined(aippDto.getName()));
            declaration.setVersion(Undefinable.defined(version));  // 底层task更新name的时候必须带上version
        }
        updateAttribute(attr, aippDto);
        declaration.setAttributes(Undefinable.defined(attr));
        log.debug("patch meta, metaVersionId {} name {} attr {}",
                metaVersionId,
                declaration.getName().getDefined() ? declaration.getName().getValue() : "undefined",
                declaration.getAttributes().getDefined() ? declaration.getAttributes().getValue() : "undefined");
        this.metaService.patch(metaVersionId, declaration, context);
    }

    private void validateUpdate(String aippId, Map<String, Object> attr, String name, OperationContext context) {
        if (!AippMetaStatusEnum.INACTIVE.getCode().equals(attr.get(AippConst.ATTR_META_STATUS_KEY))) {
            log.error("not allow to update an active aipp, aippId {} status {}",
                    aippId,
                    attr.getOrDefault(AippConst.ATTR_META_STATUS_KEY, "null"));
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
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, context);
        if (meta == null) {
            return this.updateNewVersionAipp(aippDto, context, aippId, version);
        }
        Map<String, Object> attr = meta.getAttributes();
        validateUpdate(aippId, attr, aippDto.getName(), context);
        updateMetaDeclaration(meta.getVersionId(), meta.getVersion(), aippDto, attr, context);
        // 更新流程
        if (aippDto.getFlowViewData() == null || aippDto.getFlowViewData().isEmpty()) {
            return AippCreateDto.builder().aippId(aippId).version(meta.getVersion()).build();
        }
        try {
            this.flowsService.updateFlows(ObjectUtils.<String>cast(attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY)),
                    ObjectUtils.<String>cast(attr.get(AippConst.ATTR_VERSION_KEY)),
                    JsonUtils.toJsonString(aippDto.getFlowViewData()),
                    context);
        } catch (JobberException e) {
            log.error("update aipp failed, aipp {} name {}", aippId, aippDto.getName());
            throw new AippException(context, AippErrCode.APP_UPDATE_FAILED);
        }
        return AippCreateDto.builder().aippId(aippId).version(meta.getVersion()).build();
    }

    private AippCreateDto updateNewVersionAipp(AippDto aippDto, OperationContext context, String aippId,
            String version) {
        Meta lastDraftMeta = MetaUtils.getLastDraftMeta(this.metaService, aippId, context);
        String flowId = lastDraftMeta.getAttributes()
                .getOrDefault(AippConst.ATTR_FLOW_CONFIG_ID_KEY, StringUtils.EMPTY)
                .toString();
        this.upgradeAippHandle(aippDto, AippCreateDto.builder().aippId(aippId).build(), context, flowId, version);
        return this.update(aippDto, context);
    }

    private AippCreateDto createPreviewAipp(String baselineVersion, AippDto aippDto, OperationContext context) {
        Map<String, Object> flowViewData = aippDto.getFlowViewData();
        String flowId = ObjectUtils.<String>cast(flowViewData.get(AippConst.FLOW_CONFIG_ID_KEY));
        String previewVersion = ObjectUtils.<String>cast(flowViewData.get(AippConst.FLOW_CONFIG_VERSION_KEY));
        // 创建、发布流程定义
        FlowInfo flowInfo = this.flowsService.publishFlowsWithoutElsa(flowId,
                previewVersion,
                JsonUtils.toJsonString(flowViewData),
                context);
        // 预览时，aipp 的 version 用的是 flowInfo 的 version，是否合理待确认
        aippDto.setVersion(flowInfo.getVersion());
        MetaDeclarationInfo declarationInfo = this.buildInitialMetaDeclaration(aippDto,
                AippCreateDto.builder().aippId(aippDto.getId()).version(baselineVersion).build(),
                flowInfo,
                AippTypeEnum.PREVIEW.name());
        AppBuilderApp app = this.factory.create(aippDto.getAppId());
        List<AippNodeForms> aippNodeForms = buildAippNodeForms(flowInfo, app.getFormProperties());
        // 追加attribute
        Map<String, Object> attr = declarationInfo.getAttributes().getValue();
        appendAttribute(attr, aippNodeForms, flowInfo.getFlowDefinitionId());

        log.debug("create preview aipp, declaration attr info {}", attr);
        Meta meta = metaService.create(declarationInfo, context);
        return AippCreateDto.builder().aippId(meta.getId()).version(previewVersion).build();
    }

    private void appendAttribute(Map<String, Object> attr, List<AippNodeForms> aippNodeForms, String flowDefinitionId) {
        for (AippNodeForms node : aippNodeForms) {
            if (node.getMetaInfo().isEmpty()) {
                continue;
            }
            if (NodeTypes.START.getType().equalsIgnoreCase(node.getType())) {
                attr.put(AippConst.ATTR_START_FORM_ID_KEY, node.getMetaInfo().get(0).getFormId());
                attr.put(AippConst.ATTR_START_FORM_VERSION_KEY, node.getMetaInfo().get(0).getVersion());
            }
            if (NodeTypes.END.getType().equalsIgnoreCase(node.getType())) {
                attr.put(AippConst.ATTR_END_FORM_ID_KEY, node.getMetaInfo().get(0).getFormId());
                attr.put(AippConst.ATTR_END_FORM_VERSION_KEY, node.getMetaInfo().get(0).getVersion());
            }
        }
        attr.put(AippConst.ATTR_FLOW_DEF_ID_KEY, flowDefinitionId);
        attr.put(AippConst.ATTR_PUBLISH_TIME_KEY, LocalDateTime.now().toString());
        attr.put(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode());
    }

    private MetaPropertyDeclarationInfo trimPropsName(MetaPropertyDeclarationInfo info) {
        String newName = info.getName().getValue().trim();
        info.setName(Undefinable.defined(newName));
        return info;
    }

    private List<MetaPropertyDeclarationInfo> getMetaPropertyDeclarationInfos(List<AippNodeForms> aippNodeForms) {
        List<MetaPropertyDeclarationInfo> formProps = aippNodeForms.stream()
                .flatMap(node -> node.getMetaInfo().stream())
                .flatMap(metaList -> metaList.getFormMetaItems().stream())
                .map(FormMetaConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .filter(item -> StringUtils.isNotBlank(item.getName().getValue()))
                .map(this::trimPropsName)
                .collect(Collectors.toList());

        // 检查name不能重复、且不能和初始变量重复
        validateProps(formProps);
        return formProps;
    }

    /**
     * 预览aipp
     *
     * @param baselineVersion aipp 的基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return 创建预览aipp的id和version
     * @throws AippException 预览aipp异常
     */
    @Override
    public AippCreateDto previewAipp(String baselineVersion, AippDto aippDto, OperationContext context)
            throws AippException {
        List<Meta> metaList = MetaUtils.getAllMetasByAppId(this.metaService, aippDto.getAppId(), context);
        if (!metaList.isEmpty()) {
            Meta meta = metaList.get(0);
            if (MetaUtils.isPublished(meta)) {
                return AippCreateDto.builder().aippId(meta.getId()).version(meta.getVersion()).build();
            }
        }
        FlowDefinitionResult definitionResult = this.getSameFlowDefinition(aippDto);
        if (definitionResult != null) {
            RangedResultSet<Meta> metas =
                    this.metaService.list(this.buildFlowDefinitionFilter(definitionResult), true, 0, 1, context);
            if (!metas.getResults().isEmpty()) {
                Meta meta = metas.getResults().get(0);
                return AippCreateDto.builder().aippId(meta.getId()).version(meta.getVersion()).build();
            }
        }
        // 过滤预览版本
        if (AippStringUtils.isPreview(baselineVersion)) {
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version is preview");
        }
        // 设置预览版本
        int retryTimes = RETRY_PREVIEW_TIMES;
        String previewVersion;
        String errorMsg;
        int errorCode;
        do {
            previewVersion = VersionUtils.buildPreviewVersion(baselineVersion);
            aippDto.getFlowViewData().put(AippConst.FLOW_CONFIG_VERSION_KEY, previewVersion);
            try {
                return this.createPreviewAipp(baselineVersion, aippDto, context);
            } catch (JobberException e) {
                if (e.getCode() != ErrorCodes.FLOW_ALREADY_EXIST.getErrorCode()) {
                    errorMsg = e.getMessage();
                    errorCode = e.getCode();
                    break;
                }
                errorMsg = e.getMessage();
                errorCode = e.getCode();
                log.warn("create preview aipp failed, times {} aippId {} version {}, error {}",
                        RETRY_PREVIEW_TIMES - retryTimes,
                        aippDto.getId(),
                        previewVersion,
                        e.getMessage());
            }
        } while (retryTimes-- > 0);
        log.error("Failed to preview aipp.[errorMsg={}]", errorMsg);
        throw this.handleException(context, errorCode);
    }

    private AippException handleException(OperationContext context, int code) {
        switch (ErrorCodes.getErrorCodes(code)) {
            case INVALID_FLOW_NODE_SIZE:
                return new AippException(context, AippErrCode.INVALID_FLOW_NODE_SIZE);
            case INVALID_START_NODE_EVENT_SIZE:
                return new AippException(context, AippErrCode.INVALID_START_NODE_EVENT_SIZE);
            case INVALID_EVENT_CONFIG:
            case INVALID_STATE_NODE_EVENT_SIZE:
                return new AippException(context, AippErrCode.INVALID_EVENT_CONFIG);
            default:
                return new AippException(context, AippErrCode.INVALID_FLOW_CONFIG);
        }
    }

    private MetaFilter buildFlowDefinitionFilter(FlowDefinitionResult definitionResult) {
        MetaFilter filter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("flow_definition_id", Collections.singletonList(definitionResult.getFlowDefinitionId()));
        attributes.put("flow_config_id", Collections.singletonList(definitionResult.getMetaId()));
        filter.setAttributes(attributes);
        return filter;
    }

    private FlowDefinitionResult getSameFlowDefinition(AippDto aippDto) {
        Map<String, Object> flowViewData = aippDto.getFlowViewData();
        String metaId = String.valueOf(flowViewData.getOrDefault(AippConst.FLOW_CONFIG_ID_KEY, StringUtils.EMPTY));
        String version =
                String.valueOf(flowViewData.getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, StringUtils.EMPTY));
        List<FlowDefinitionResult> flowDefinitions =
                this.flowDefinitionService.getFlowDefinitionByMetaIdAndPartVersion(metaId, version + "-", null);
        String parsedGraphData =
                this.flowDefinitionService.getParsedGraphData(JsonUtils.toJsonString(aippDto.getFlowViewData()),
                        version);
        Map<String, Object> aippFlowDefinitionMapping = this.buildFlowDefinition(parsedGraphData);
        return flowDefinitions.stream().limit(1).filter(definition -> {
            Map<String, Object> map = this.buildFlowDefinition(definition.getGraph());
            return this.compareMaps(map, aippFlowDefinitionMapping);
        }).findAny().orElse(null);
    }

    private Map<String, Object> buildFlowDefinition(String flowDefinition) {
        Map<String, Object> parsedFlowDefinitionMapping = JsonUtils.parseObject(flowDefinition);

        // 这边 name 和 version 不需要比较
        parsedFlowDefinitionMapping.remove(AippConst.FLOW_CONFIG_NAME);
        parsedFlowDefinitionMapping.remove(AippConst.FLOW_CONFIG_VERSION_KEY);
        return parsedFlowDefinitionMapping;
    }

    /**
     * 比较两个map是否相等
     *
     * @param map1 第一个map
     * @param map2 第二个map
     * @return 如果两个map相等，返回true，否则返回false
     */
    public boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = map2.get(key);
            if (!map2.containsKey(key) || !this.isSameObject(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameObject(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        if (obj1 instanceof Map && obj2 instanceof Map) {
            return compareMaps(ObjectUtils.cast(obj1), ObjectUtils.cast(obj2));
        }
        return Objects.equals(obj1, obj2);
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
            Meta previewMeta = MetaUtils.getAnyMeta(metaService, previewAippId, previewVersion, context);
            cleanResourceForPreview(previewMeta, previewAippId, previewVersion, context);
        });
    }

    private void cleanResourceForPreview(Meta previewMeta, String previewAippId, String previewVersion,
            OperationContext context) {
        if (previewMeta.getAttributes()
                .getOrDefault(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode())
                .equals(AippMetaStatusEnum.ACTIVE.getCode())) {
            this.aippRunTimeService.terminateAllPreviewInstances(previewAippId,
                    previewMeta.getVersionId(),
                    true,
                    context);
        }
        String flowId = previewMeta.getAttributes().get(AippConst.ATTR_FLOW_CONFIG_ID_KEY).toString();
        if (!StringUtils.isBlank(flowId)) {
            try {
                this.flowsService.deleteFlowsWithoutElsa(flowId, previewVersion, context);
            } catch (JobberException e) {
                log.error("delete flow failed, flowId: {} previewVersion: {}", flowId, previewVersion);
                throw new AippException(context, AippErrCode.APP_PUBLISH_FAILED);
            }
        }
        this.metaService.delete(previewMeta.getVersionId(), context);
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
        Meta latestMeta = MetaUtils.getLastNormalMeta(this.metaService, aippId, context);
        String latestMetaStatus = latestMeta.getAttributes()
                .getOrDefault(AippConst.ATTR_META_STATUS_KEY, AippMetaStatusEnum.INACTIVE.getCode())
                .toString();
        String flowId = latestMeta.getAttributes().getOrDefault(AippConst.ATTR_FLOW_CONFIG_ID_KEY, "").toString();
        Validation.notBlank(flowId, () -> {
            throw new AippParamException(context,
                    AippErrCode.INPUT_PARAM_IS_INVALID,
                    AippConst.ATTR_FLOW_CONFIG_ID_KEY);
        });
        String newFlowVersion =
                aippDto.getFlowViewData().getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, DEFAULT_VERSION).toString();
        if (this.isUpgradeVersion(newAippVersion, latestMeta, latestMetaStatus)) {
            this.upgradeAippHandle(aippDto,
                    AippCreateDto.builder().aippId(aippId).version(baselineVersion).build(),
                    context,
                    flowId,
                    newFlowVersion);
        }
        return this.update(aippDto, context);
    }

    // 如果是第一个草稿版本，或者新的草稿版本与之前版本号不一致，都需要升级版本操作。
    private boolean isUpgradeVersion(String newAippVersion, Meta latestMeta, String latestMetaStatus) {
        return latestMetaStatus.equals(AippMetaStatusEnum.ACTIVE.getCode()) || !Objects.equals(newAippVersion,
                latestMeta.getVersion());
    }

    private void validateProps(List<MetaPropertyDeclarationInfo> props) {
        Set<String> staticKeySet =
                AippConst.STATIC_META_ITEMS.stream().map(FormMetaItem::getKey).collect(Collectors.toSet());
        if (staticKeySet.size() != AippConst.STATIC_META_ITEMS.size()) {
            log.error("The initial meta item key cant be repeated.");
            throw new AippException(AippErrCode.AIPP_PROPS_KEY_DUPLICATE);
        }
        Iterator<MetaPropertyDeclarationInfo> iter = props.iterator();
        while (iter.hasNext()) {
            MetaPropertyDeclarationInfo prop = iter.next();
            if (prop.getName().getDefined() && staticKeySet.contains(prop.getName().getValue())) {
                iter.remove();
                log.warn("The form field repeat {}", prop.getName().getValue());
                continue;
            }
            staticKeySet.add(prop.getName().getValue());
        }
    }

    private List<MetaPropertyDeclarationInfo> buildPatchProps(List<MetaPropertyDeclarationInfo> props, Meta meta) {
        if (meta.getProperties() == null) {
            return props;
        }
        List<MetaPropertyDeclarationInfo> propsPatch = meta.getProperties()
                .stream()
                .map(TaskPropertyConvertor.INSTANCE::toMetaPropertyDeclarationInfo)
                .collect(Collectors.toList());
        propsPatch.addAll(props);

        return propsPatch;
    }

    private MetaDeclarationInfo buildPublishMetaDeclaration(String aippId, List<AippNodeForms> aippNodeForms,
            String flowDefinitionId, Meta meta, AippDto aippDto, String uniqueName) {
        // 解析表单属性字段
        List<MetaPropertyDeclarationInfo> props = getMetaPropertyDeclarationInfos(aippNodeForms);

        // 追加aipp meta属性字段
        MetaDeclarationInfo declaration = new MetaDeclarationInfo();

        // 追加/更新 aipp attribute字段
        Map<String, Object> attrPatch = meta.getAttributes();
        appendAttribute(attrPatch, aippNodeForms, flowDefinitionId);
        updateAttribute(attrPatch, aippDto);
        attrPatch.put(AippConst.ATTR_PUBLISH_DESCRIPTION, aippDto.getPublishedDescription());
        attrPatch.put(AippConst.ATTR_PUBLISH_UPDATE_LOG, aippDto.getPublishedUpdateLog());
        attrPatch.put(AippConst.ATTR_UNIQUE_NAME, uniqueName);
        declaration.setAttributes(Undefinable.defined(attrPatch));
        declaration.setName(Undefinable.defined(meta.getName()));
        declaration.setVersion(Undefinable.defined(meta.getVersion()));

        log.debug("patch meta, aippId {} name {} attr {}",
                aippId,
                declaration.getName().getDefined() ? declaration.getName().getValue() : "undefined",
                declaration.getAttributes().getDefined() ? declaration.getAttributes().getValue() : "undefined");
        return declaration;
    }

    private List<AippNodeForms> buildAippNodeForms(FlowInfo flowInfo, List<AppBuilderFormProperty> formProperties) {
        if (flowInfo.getFlowNodes() == null) {
            return Collections.emptyList();
        }
        return flowInfo.getFlowNodes().stream().filter(item -> item.getFlowNodeForm() != null).map(item -> {
            FlowNodeFormInfo form = item.getFlowNodeForm();
            List<FormMetaQueryParameter> parameter =
                    Collections.singletonList(new FormMetaQueryParameter(form.getFormId(), form.getVersion()));
            return AippNodeForms.builder()
                    .type(item.getType())
                    .metaInfo(FormUtils.buildFormMetaInfos(parameter, formProperties))
                    .build();
        }).collect(Collectors.toList());
    }

    private void rollbackAipp(String versionId, FlowInfo flowInfo, OperationContext context) {
        try {
            if (flowInfo != null) {
                this.flowDefinitionService.deleteFlows(flowInfo.getFlowDefinitionId(), context);
            }
            this.metaService.delete(versionId, context);
        } catch (AippException e) {
            log.error("rollbackAipp failed, versionId {}, e = {}", versionId, e);
        }
    }

    private FlowInfo publishFlow(AippDto aippDto, Map<String, Object> attr, OperationContext context) {
        String flowConfigId = ObjectUtils.<String>cast(attr.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY));
        String version = ObjectUtils.<String>cast(attr.get(AippConst.ATTR_VERSION_KEY));
        FlowInfo flowInfo;
        try {
            flowInfo = this.flowsService.publishFlows(flowConfigId,
                    version,
                    JsonUtils.toJsonString(aippDto.getFlowViewData()),
                    context);
        } catch (JobberException e) {
            AippErrCode retCode = (e.getCode() == ErrorCodes.FLOW_ALREADY_EXIST.getErrorCode())
                    ? AippErrCode.FLOW_ALREADY_EXIST
                    : AippErrCode.APP_PUBLISH_FAILED;
            throw new AippException(context, retCode);
        }
        return flowInfo;
    }

    @Override
    public Rsp<AippCreateDto> publish(AippDto aippDto, AppBuilderApp app, OperationContext context)
            throws AippException {
        String aippId = aippDto.getId();
        String version = aippDto.getVersion();
        CompletableFuture.runAsync(() -> MetaUtils.getAllPreviewMeta(metaService, aippId, context)
                .forEach(meta -> cleanResourceForPreview(meta, aippId, meta.getVersion(), context)));

        Meta meta = MetaUtils.getLastNormalMeta(metaService, aippId, context);
        Map<String, Object> attr = meta.getAttributes();
        String originalDraftVersion = meta.getVersion();
        if (!this.isValidUpgradeVersion(originalDraftVersion, version)) {
            log.error("old version {} is larger than new one {}", originalDraftVersion, version);
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version");
        }
        attr.put(AippConst.ATTR_VERSION_KEY, version);
        aippDto.getFlowViewData().put(AippConst.ATTR_VERSION_KEY, version);
        meta.setVersion(version);
        log.info("publish aipp {} name {} attr {}", aippId, aippDto.getName(), attr);

        validateUpdate(aippId, attr, aippDto.getName(), context);
        // 发布流程
        FlowInfo flowInfo = null;
        try {
            flowInfo = publishFlow(aippDto, attr, context);
            // 查询表单 元数据
            List<AippNodeForms> aippNodeForms = buildAippNodeForms(flowInfo, app.getFormProperties());

            // 往 store 发布
            String uniqueName = this.publishToStore(aippDto, context, flowInfo);

            // 发布aipp
            MetaDeclarationInfo declaration = buildPublishMetaDeclaration(aippId,
                    aippNodeForms,
                    flowInfo.getFlowDefinitionId(),
                    meta,
                    aippDto,
                    uniqueName);
            this.metaService.patch(meta.getVersionId(), declaration, context);
            return Rsp.ok(AippCreateDto.builder()
                    .aippId(aippId)
                    .version(meta.getVersion())
                    .toolUniqueName(uniqueName)
                    .build());
        } catch (AippException e) {
            log.error("publish aipp {} failed.", aippId, e);
            rollbackAipp(meta.getVersionId(), flowInfo, context);
            throw e;
        }
    }

    private String publishToStore(AippDto aippDto, OperationContext context, FlowInfo flowInfo) {
        AppPublishData appData = this.buildItemData(aippDto, context, flowInfo);
        String uniqueName = "";
        if (StringUtils.equalsIgnoreCase(aippDto.getType(), APP_TYPE)) {
            uniqueName = this.appService.publishApp(appData);
        } else if (StringUtils.equalsIgnoreCase(aippDto.getType(), WATERFLOW_TYPE)) {
            if (appData.getUniqueName() == null) {
                AppData.fillAppData(appData);
                PluginData pluginData = this.buildPluginData(appData);
                this.pluginService.addPlugin(pluginData);
                uniqueName = appData.getUniqueName();
            } else {
                // 修复store切换四层模型后未修改完全的问题
                AppData.fillAppData(appData);
                PluginData pluginData = this.buildPluginData(appData);
                uniqueName = this.toolService.upgradeTool(pluginData.getPluginToolDataList().get(0));
            }
        } else {
            throw new AippException(AippErrCode.ILLEGAL_AIPP_TYPE);
        }
        this.appBuilderAppMapper.updateAppWithStoreId(uniqueName, aippDto.getAppId(), aippDto.getVersion());
        return uniqueName;
    }

    private PluginData buildPluginData(AppData appData) {
        PluginData pluginData = new PluginData();
        pluginData.setDeployStatus(DeployStatus.RELEASED.name());
        pluginData.setCreator(appData.getCreator());
        pluginData.setModifier(appData.getModifier());
        pluginData.setPluginName(appData.getName());
        pluginData.setExtension(new HashMap<>());
        pluginData.setPluginId(Entities.generateId() + Entities.generateId());
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setCreator(appData.getCreator());
        pluginToolData.setModifier(appData.getModifier());
        pluginToolData.setName(appData.getName());
        pluginToolData.setDescription(appData.getDescription());
        pluginToolData.setSchema(appData.getSchema());
        pluginToolData.setRunnables(appData.getRunnables());
        pluginToolData.setSource(appData.getSource());
        pluginToolData.setIcon(appData.getIcon());
        pluginToolData.setTags(appData.getTags());
        pluginToolData.setVersion(appData.getVersion());
        pluginToolData.setLikeCount(appData.getLikeCount());
        pluginToolData.setDownloadCount(appData.getDownloadCount());
        pluginToolData.setPluginId(pluginData.getPluginId());
        if (appData.getUniqueName() != null) {
            pluginToolData.setUniqueName(appData.getUniqueName());
        }
        // 修复store切换四层模型后未修改完全的问题
        pluginToolData.setDefName(appData.getDefName());
        pluginToolData.setDefGroupName(appData.getDefGroupName());
        pluginToolData.setGroupName(appData.getGroupName());
        pluginData.setPluginToolDataList(Collections.singletonList(pluginToolData));
        pluginData.setDefinitionGroupDataList(Arrays.asList(AppData.toDefGroup(appData)));
        pluginData.setToolGroupDataList(Arrays.asList(AppData.toToolGroup(appData)));
        return pluginData;
    }

    private AppPublishData buildItemData(AippDto aippDto, OperationContext context, FlowInfo flowInfo) {
        AppCategory appCategory = AppCategory.findByType(aippDto.getType())
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID));
        AppPublishData itemData = new AppPublishData();
        itemData.setCreator(context.getOperator());
        itemData.setModifier(context.getOperator());
        itemData.setIcon(aippDto.getIcon());
        itemData.setName(aippDto.getName());
        itemData.setDescription(aippDto.getDescription());
        itemData.setAppCategory(aippDto.getAppCategory());
        itemData.setVersion(aippDto.getVersion());
        itemData.setUniqueName(aippDto.getUniqueName());
        itemData.setSchema(this.buildToolSchema(appCategory, context, aippDto, flowInfo));
        itemData.setSource(appCategory.getSource());
        itemData.setTags(new HashSet<String>() {
            {
                add(appCategory.getTag());
                add(buildAppTypeTag(aippDto));
            }
        });
        itemData.setRunnables(this.buildRunnables(aippDto));
        return itemData;
    }

    private Map<String, Object> buildToolSchema(AppCategory appCategory, OperationContext context, AippDto aippDto,
            FlowInfo flowInfo) {
        return MapBuilder.<String, Object>get()
                .put("name", aippDto.getName())
                .put("description", aippDto.getDescription())
                .put("parameters", this.buildParameters(aippDto, context, flowInfo, appCategory))
                .put("order", Arrays.asList("tenantId", "aippId", "version", "inputParams"))
                .put("return", buildReturn())
                .put("manualIntervention", Objects.equals(appCategory, AppCategory.WATER_FLOW))
                .build();
    }

    private Map<String, Object> buildParameters(AippDto aippDto, OperationContext context, FlowInfo flowInfo,
            AppCategory appCategory) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("type", "object");
        parameterMap.put("properties", this.buildPropertiesMap(aippDto, context, appCategory, flowInfo));
        parameterMap.put("required", Arrays.asList("tenantId", "aippId", "version", "inputParams"));
        return parameterMap;
    }

    private Map<String, Object> buildRunnables(AippDto aippDto) {
        Map<String, Object> runnablesMap = new HashMap<>();
        runnablesMap.put("FIT",
                MapBuilder.get()
                        .put("genericableId", WaterFlowService.GENERICABLE_WATER_FLOW_INVOKER)
                        .put("fitableId", "water.flow.invoke")
                        .build());
        Map<Object, Object> app = MapBuilder.get()
                .put("appId", aippDto.getAppId())
                .put("aippId", aippDto.getId())
                .put("version", aippDto.getVersion())
                .put("appCategory", aippDto.getAppCategory())
                .build();
        runnablesMap.put("APP", app);
        return runnablesMap;
    }

    private Map<String, Object> buildPropertiesMap(AippDto aippDto, OperationContext context, AppCategory appCategory,
            FlowInfo flowInfo) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("tenantId",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the tenant id of the waterFlow tool")
                        .put("default", context.getTenantId())
                        .build());
        propertiesMap.put("aippId",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the aipp id of the waterFlow tool")
                        .put("default", aippDto.getId())
                        .build());
        propertiesMap.put("version",
                MapBuilder.get()
                        .put("type", "string")
                        .put("description", "the aipp version of the waterFlow tool")
                        .put("default", aippDto.getVersion())
                        .build());
        propertiesMap.put("inputParams", this.buildInputParamsSchema(flowInfo));
        return propertiesMap;
    }

    private Map<String, Object> buildInputParamsSchema(FlowInfo flowInfo) {
        Map<String, Object> propertiesMapOfInputParam = new HashMap<>();
        List<String> required = new ArrayList<>();
        List<String> order = new ArrayList<>();
        flowInfo.getInputParamsByName("input").forEach(inputParam -> {
            String name = inputParam.getOrDefault("name", StringUtils.EMPTY).toString();
            String type = inputParam.getOrDefault("type", StringUtils.EMPTY).toString();
            String description = inputParam.getOrDefault("description", StringUtils.EMPTY).toString();
            propertiesMapOfInputParam.put(name,
                    MapBuilder.get().put("type", type).put("description", description).build());
            if (ObjectUtils.cast(inputParam.getOrDefault("isRequired", false))) {
                required.add(name);
            }
            order.add(name);
        });
        return MapBuilder.<String, Object>get()
                .put("type", "object")
                .put("properties", propertiesMapOfInputParam)
                .put("required", required)
                .put("order", order)
                .build();
    }

    private Map<String, Object> buildReturn() {
        // 返参的具体属性信息暂不填充，需要考虑多end节点的情况
        return MapBuilder.<String, Object>get().put("type", "object").put("properties", new HashMap<>()).build();
    }

    private String buildAppTypeTag(AippDto aippDto) {
        return APP_TYPE_TAG_PREFIX + StringUtils.toUpperCase(aippDto.getAppType());
    }
}
