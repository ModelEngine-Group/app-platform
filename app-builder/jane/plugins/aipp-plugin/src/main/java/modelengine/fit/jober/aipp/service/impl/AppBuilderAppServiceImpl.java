/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_DELETE_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_PUBLISH_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_UPDATE_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INVALID_PATH_ERROR;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.OBTAIN_APP_CONFIGURATION_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.OBTAIN_APP_ORCHESTRATION_INFO_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.QUERY_PUBLICATION_HISTORY_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.UPDATE_APP_CONFIGURATION_FAILED;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_APP_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_UNIQUE_NAME;
import static modelengine.fit.jober.aipp.service.impl.UploadedFileMangeServiceImpl.IRREMOVABLE;
import static modelengine.fit.jober.aipp.service.impl.UploadedFileMangeServiceImpl.REMOVABLE;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.opentelemetry.api.trace.Span;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.AppTypeDto;
import modelengine.fit.jober.aipp.dto.PublishedAppResDto;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.dto.export.AppExportConfig;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.enums.AippSortKeyEnum;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.factory.CheckerFactory;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.aipp.service.AippFlowService;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.AppTypeService;
import modelengine.fit.jober.aipp.service.Checker;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.AppImExportUtil;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.MetaInstanceUtils;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fit.jober.aipp.util.RandomPathUtils;
import modelengine.fit.jober.aipp.util.TemplateUtils;
import modelengine.fit.jober.aipp.util.VersionUtils;
import modelengine.fit.jober.aipp.validation.AppUpdateValidator;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.base.service.UsrAppCollectionService;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.PluginToolService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用开发接口实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderAppServiceImpl
        implements AppBuilderAppService, modelengine.fit.jober.aipp.genericable.AppBuilderAppService {
    private static final Logger log = Logger.get(AppBuilderAppServiceImpl.class);

    private static final int PATH_LENGTH = 16;

    private static final int RETRY_CREATE_TIMES = 5;

    private static final int MODEL_LIST_SERVICE_NAME = 0;

    private static final int MODEL_LIST_TAG = 1;

    private static final String APP_BUILDER_DEFAULT_MODEL_NAME = "#app_builder_default_model_name#";

    private static final String APP_BUILDER_DEFAULT_SERVICE_NAME = "#app_builder_default_service_name#";

    private static final String APP_BUILDER_DEFAULT_TAG = "#app_builder_default_tag#";

    private static final String DEFAULT_APP_VERSION = "1.0.0";

    private static final String VERSION_FORMAT = "{0}.{1}.{2}";

    private static final String PUBLISH_UPDATE_DESCRIPTION_KEY = "publishedDescription";

    private static final String PUBLISH_UPDATE_LOG_KEY = "publishedUpdateLog";

    private static final int VERSION_LENGTH = 8;

    private static final String FORM_PROPERTY_GROUP_NULL = "null";

    private static final int RETRY_PATH_GENERATION_TIMES = 3;

    private static final String[] TEMPLATE_DEFAULT_ATTRIBUTE_KEYS = {"icon", "description", "greeting", "app_type"};

    private static final String APP_ATTR_DESCRIPTION = "description";

    private static final String APP_ATTR_ICON = "icon";

    private static final String APP_ATTR_GREETING = "greeting";

    private static final String APP_ATTR_STORE_ID = "store_id";

    private static final String APP_ATTR_APP_TYPE = "app_type";

    private static final String APP_ATTR_LATEST_VERSION = "latest_version";

    private final AppBuilderAppFactory appFactory;

    private final AppTemplateFactory templateFactory;

    private final AippFlowService aippFlowService;

    private final AppBuilderAppRepository appRepository;

    private final int nameLengthMaximum;

    private final MetaService metaService;

    private final AppUpdateValidator appUpdateValidator;

    private final MetaInstanceService metaInstanceService;

    private final UsrAppCollectionService usrAppCollectionService;

    private final UploadedFileManageService uploadedFileManageService;

    private final String appNameFormat = "^[\\u4E00-\\u9FA5A-Za-z0-9][\\u4E00-\\u9FA5A-Za-z0-9-_]*$";

    private final AippLogMapper aippLogMapper;

    private final FlowsService flowsService;

    private final AppService appService;

    private final AippModelCenter aippModelCenter;

    private final AippChatMapper aippChatMapper;

    private final PluginToolService pluginToolService;

    private final PluginService pluginService;

    private final Map<String, String> exportMeta;

    private final AppTypeService appTypeService;

    private final FlowDefinitionService flowDefinitionService;

    private final AippFlowDefinitionService aippFlowDefinitionService;

    private final String contextRoot;

    public AppBuilderAppServiceImpl(AppBuilderAppFactory appFactory, AippFlowService aippFlowService,
            AppBuilderAppRepository appRepository, AppTemplateFactory templateFactory,
            @Value("${validation.task.name.length.maximum:64}") int nameLengthMaximum, MetaService metaService,
            UsrAppCollectionService usrAppCollectionService, AppUpdateValidator appUpdateValidator,
            MetaInstanceService metaInstanceService, UploadedFileManageService uploadedFileManageService,
            AippLogMapper aippLogMapper, FlowsService flowsService, AppService appService,
            AippChatService aippChatService, AippModelCenter aippModelCenter, AippChatMapper aippChatMapper,
            @Value("${export-meta}") Map<String, String> exportMeta, AppTypeService appTypeService,
            PluginToolService pluginToolService, PluginService pluginService,
            FlowDefinitionService flowDefinitionService, AippFlowDefinitionService aippFlowDefinitionService,
            @Value("${app-engine.contextRoot}") String contextRoot) {
        this.nameLengthMaximum = nameLengthMaximum;
        this.appFactory = appFactory;
        this.templateFactory = templateFactory;
        this.aippFlowService = aippFlowService;
        this.appRepository = appRepository;
        this.metaService = metaService;
        this.usrAppCollectionService = usrAppCollectionService;
        this.appUpdateValidator = appUpdateValidator;
        this.metaInstanceService = metaInstanceService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.aippLogMapper = aippLogMapper;
        this.flowsService = flowsService;
        this.appService = appService;
        this.aippModelCenter = aippModelCenter;
        this.aippChatMapper = aippChatMapper;
        this.exportMeta = exportMeta;
        this.appTypeService = appTypeService;
        this.pluginToolService = pluginToolService;
        this.pluginService = pluginService;
        this.flowDefinitionService = flowDefinitionService;
        this.aippFlowDefinitionService = aippFlowDefinitionService;
        this.contextRoot = contextRoot;
    }

    @Override
    @Fitable(id = "default")
    public AppBuilderAppDto query(String appId, OperationContext context) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        AppBuilderAppDto appBuilderAppDto = this.buildFullAppDto(appBuilderApp);
        try {
            appBuilderAppDto.setBaselineCreateAt(this.getMetaByCreatAtDirection(appId, context, DirectionEnum.ASCEND)
                    .getCreationTime());
            appBuilderAppDto.setAippId(MetaUtils.getAippIdByAppId(this.metaService, appId, context));
        } catch (AippTaskNotFoundException e) {
            throw new AippException(OBTAIN_APP_CONFIGURATION_FAILED);
        }
        return appBuilderAppDto;
    }

    @Override
    @Fitable(id = "default")
    public AppBuilderAppDto queryByPath(String path) {
        if (!RandomPathUtils.validatePath(path, PATH_LENGTH)) {
            log.error("Invalid path format path: {}.", path);
            throw new AippException(INVALID_PATH_ERROR);
        }
        AppBuilderApp appBuilderApp = this.appFactory.createByPath(path);
        return this.buildFullAppDto(appBuilderApp);
    }

    @Override
    @Fitable(id = "default")
    @Transactional
    public Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf) {
        // 要加个save appDto到数据的逻辑
        this.validateApp(appDto.getId());
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto);
        try {
            this.validateVersion(aippDto, contextOf);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(APP_PUBLISH_FAILED);
        }
        String id = appDto.getId();
        AppBuilderApp appBuilderApp = this.appFactory.create(id);
        if (AppState.getAppState(appBuilderApp.getState()) == AppState.PUBLISHED) {
            throw new AippException(AippErrCode.APP_HAS_PUBLISHED);
        }
        appBuilderApp.setState(AppState.PUBLISHED.getName());
        // 添加校验，禁止更低版本手动输入
        this.validateVersionIsLatest(appBuilderApp.getVersion(), appDto.getVersion());
        appBuilderApp.setVersion(appDto.getVersion());
        AippCreateDto aippCreateDto = this.aippFlowService.create(aippDto, contextOf);
        aippDto.setId(aippCreateDto.getAippId());
        AppBuilderSaveConfigDto saveConfigDto = AppBuilderSaveConfigDto.builder()
                .graph(JsonUtils.toJsonString(appDto.getFlowGraph().getAppearance()))
                .input(appDto.getConfigFormProperties())
                .build();
        this.saveConfig(id, saveConfigDto, contextOf);
        Map<String, Object> appBuilderAppAttr = appBuilderApp.getAttributes();
        appBuilderAppAttr.put(PUBLISH_UPDATE_DESCRIPTION_KEY, aippDto.getPublishedDescription());
        appBuilderAppAttr.put(PUBLISH_UPDATE_LOG_KEY, aippDto.getPublishedUpdateLog());
        if (StringUtils.isEmpty(appBuilderApp.getPath())) {
            String path = generateUniquePath();
            appBuilderApp.setPath(path);
        }
        this.appFactory.update(appBuilderApp);
        if (appBuilderApp.getAttributes().containsKey("store_id")) {
            aippDto.setUniqueName(appBuilderApp.getAttributes().get("store_id").toString());
        }
        return this.aippFlowService.publish(aippDto, appBuilderApp, contextOf);
    }

    /**
     * 校验版本号是否比现有号更新
     *
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    private void validateVersionIsLatest(String oldVersion, String newVersion) {
        if (!VersionUtils.isValidVersion(oldVersion) || !VersionUtils.isValidVersion(newVersion)) {
            throw new AippException(AippErrCode.INVALID_VERSION_NAME);
        }
        if (StringUtils.equals(oldVersion, newVersion)) {
            // 在这里，与旧版本号相同的版本号被认为是最新的
            return;
        }
        String[] oldPart = oldVersion.split("\\.");
        String[] newPart = newVersion.split("\\.");
        for (int i = 0; i < oldPart.length; i++) {
            int oldV = Integer.parseInt(oldPart[i]);
            int newV = Integer.parseInt(newPart[i]);
            if (oldV > newV) {
                // 如果某个号比当前号小，则认为新版本比旧版本小，抛出错误
                throw new AippParamException(AippErrCode.NEW_VERSION_IS_LOWER);
            }
            if (newV > oldV) {
                // 如果某个号比当前号大，则认为新版本比旧版本大，例如2.0.0比1.9.9大
                break;
            }
        }
    }

    private void validateVersion(AippDto aippDto, OperationContext contextOf) throws AippTaskNotFoundException {
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, aippDto.getAppId(), contextOf);
        MetaFilter metaFilter = new MetaFilter();
        metaFilter.setVersions(Collections.singletonList(aippDto.getVersion()));
        metaFilter.setMetaIds(Collections.singletonList(aippId));
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.NORMAL.type()));
        attributes.put(AippConst.ATTR_META_STATUS_KEY, Collections.singletonList(AippMetaStatusEnum.ACTIVE.getCode()));
        metaFilter.setAttributes(attributes);
        RangedResultSet<Meta> metas = this.metaService.list(metaFilter, true, 0, 1, contextOf);
        if (!metas.getResults().isEmpty()) {
            throw new AippException(AippErrCode.APP_VERSION_HAS_ALREADY);
        }
    }

    @Override
    @Fitable(id = "default")
    public AippCreate debug(AppBuilderAppDto appDto, OperationContext contextOf) {
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto);
        // Rsp 得统一整改下
        return ConvertUtils.toAippCreate(this.aippFlowService.previewAipp(appDto.getVersion(), aippDto, contextOf));
    }

    @Override
    @Fitable(id = "default")
    public void updateFlow(String appId, OperationContext contextOf) {
        AppBuilderApp app = this.appFactory.create(appId);
        if (ObjectUtils.cast(app.getAttributes().getOrDefault(AippConst.ATTR_APP_IS_UPDATE, true))) {
            AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(this.buildFullAppDto(app));
            this.aippFlowService.previewAipp(app.getVersion(), aippDto, contextOf);
            app.getAttributes().put(AippConst.ATTR_APP_IS_UPDATE, false);
            this.appFactory.update(app);
        }
    }

    private Meta getMetaByCreatAtDirection(String appId, OperationContext context, DirectionEnum direction)
            throws AippTaskNotFoundException {
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, appId, context);
        MetaFilter filter = new MetaFilter();
        filter.setMetaIds(Collections.singletonList(aippId));
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), direction.name());
        filter.setOrderBys(Collections.singletonList(sortEncode));
        List<Meta> metas = MetaUtils.getListMetaHandle(this.metaService, filter, context);
        if (metas.isEmpty()) {
            log.error("Meta list can not be empty.");
            throw new AippTaskNotFoundException(TASK_NOT_FOUND);
        }
        return metas.get(0);
    }

    @Override
    public AppBuilderAppDto queryLatestOrchestration(String appId, OperationContext context) {
        this.validateApp(appId);
        Meta latestMeta;
        try {
            latestMeta = this.getMetaByCreatAtDirection(appId, context, DirectionEnum.DESCEND);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(OBTAIN_APP_ORCHESTRATION_INFO_FAILED);
        }
        String copiedAppId = String.valueOf(latestMeta.getAttributes().get(ATTR_APP_ID_KEY));
        AppBuilderApp copiedApp = this.appFactory.create(copiedAppId);
        copiedApp.setAppBuiltType(this.appRepository.selectWithId(copiedAppId).getAppBuiltType());
        if (MetaUtils.isPublished(latestMeta)) {
            return this.create(copiedAppId, this.buildAppBuilderAppCreateDto(copiedApp), context, true);
        }
        return this.query(copiedAppId, context);
    }

    /**
     * 校验应用是否存在
     *
     * @param appId 待校验的应用 id
     */
    private AppBuilderApp validateApp(String appId) {
        return this.appFactory.create(appId);
    }

    @Override
    public AippCreate queryLatestPublished(String appId, OperationContext context) {
        List<Meta> metas;
        try {
            metas = this.getPublishedMetaList(appId, context);
        } catch (AippTaskNotFoundException e) {
            log.error("Failed to get published meta list.");
            throw new AippException(OBTAIN_APP_CONFIGURATION_FAILED);
        }
        if (metas.isEmpty()) {
            log.error("Meta list can not be empty.");
            throw new AippParamException(TASK_NOT_FOUND);
        }
        Meta latestMeta = metas.get(0);
        String latestAppId = ObjectUtils.cast(latestMeta.getAttributes().get(ATTR_APP_ID_KEY));
        return AippCreate.builder()
                .version(latestMeta.getVersion())
                .aippId(latestMeta.getId())
                .appId(latestAppId)
                .build();
    }

    private List<Meta> getPublishedMetaList(String appId, OperationContext context) throws AippTaskNotFoundException {
        MetaFilter filter = new MetaFilter();
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, appId, context);
        filter.setMetaIds(Collections.singletonList(aippId));
        String sortEncode = MetaUtils.formatSorter(AippSortKeyEnum.CREATE_AT.name(), DirectionEnum.DESCEND.name());
        filter.setOrderBys(Collections.singletonList(sortEncode));
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, Collections.singletonList(AippTypeEnum.NORMAL.name()));
        attributes.put(AippConst.ATTR_META_STATUS_KEY, Collections.singletonList(AippMetaStatusEnum.ACTIVE.getCode()));
        filter.setAttributes(attributes);
        return MetaUtils.getListMetaHandle(this.metaService, filter, context);
    }

    private AppBuilderAppCreateDto buildAppBuilderAppCreateDto(AppBuilderApp app) {
        Map<String, Object> attributes = app.getAttributes();
        String description = this.getAttribute(attributes, APP_ATTR_DESCRIPTION);
        String icon = this.getAttribute(attributes, APP_ATTR_ICON);
        String greeting = this.getAttribute(attributes, APP_ATTR_GREETING);
        String storeId = this.getAttribute(attributes, APP_ATTR_STORE_ID);
        return AppBuilderAppCreateDto.builder()
                .name(app.getName())
                .description(description)
                .icon(icon)
                .greeting(greeting)
                .appType(app.getAppType())
                .type(app.getType())
                .storeId(storeId)
                .appBuiltType(app.getAppBuiltType())
                .appCategory(app.getAppCategory())
                .build();
    }

    @Override
    @Fitable(id = "default")
    public Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        List<AppBuilderConfigFormPropertyDto> configFormPropertyDtos =
                this.buildAppBuilderConfigFormProperties(appBuilderApp.getFormProperties());
        return configFormPropertyDtos.stream().filter(prop -> prop.getName().equals(name)).findFirst();
    }

    @Override
    @Fitable(id = "default")
    public Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(AppQueryCondition cond,
            HttpClassicServerRequest httpRequest, String tenantId, long offset, int limit) {
        List<AppBuilderAppMetadataDto> result = this.appRepository.selectWithLatestApp(cond, tenantId, offset, limit)
                .stream()
                .map(this::buildAppMetaData)
                .collect(Collectors.toList());
        long total = this.appRepository.countWithLatestApp(tenantId, cond);
        return Rsp.ok(RangedResultSet.create(result, offset, limit, total));
    }

    private AppBuilderAppMetadataDto buildAppMetaData(AppBuilderApp app) {
        List<String> tags = new ArrayList<>();
        tags.add(app.getType().toUpperCase(Locale.ROOT));
        return AppBuilderAppMetadataDto.builder()
                .id(app.getId())
                .name(app.getName())
                .type(app.getType())
                .state(app.getState())
                .appType(app.getAppType())
                .attributes(app.getAttributes())
                .version(app.getVersion())
                .createBy(app.getCreateBy())
                .updateBy(app.getUpdateBy())
                .createAt(app.getCreateAt())
                .updateAt(app.getUpdateAt())
                .appCategory(app.getAppCategory())
                .tags(tags)
                .appBuiltType(app.getAppBuiltType())
                .build();
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context,
            boolean isUpgrade) {
        if (dto != null && !isUpgrade) {
            this.validateCreateApp(dto, context);
        }
        String[] firstModelInfo = this.getFirstModelInfo(context);
        AppBuilderApp templateApp = this.appFactory.create(appId);
        if (Objects.nonNull(dto)) {
            templateApp.setAppCategory(dto.getAppCategory());
            templateApp.setAppBuiltType(dto.getAppBuiltType());
        }
        AppBuilderFlowGraph flowGraph = templateApp.getFlowGraph();
        flowGraph.setAppearance(flowGraph.getAppearance()
                .replace(APP_BUILDER_DEFAULT_MODEL_NAME, firstModelInfo[MODEL_LIST_SERVICE_NAME])
                .replace(APP_BUILDER_DEFAULT_SERVICE_NAME, firstModelInfo[MODEL_LIST_SERVICE_NAME])
                .replace(APP_BUILDER_DEFAULT_TAG, firstModelInfo[MODEL_LIST_TAG]));
        return this.createAppWithTemplate(dto, templateApp, context, isUpgrade, AppTypeEnum.APP.name(), false);
    }

    private AppBuilderAppDto createAppWithTemplate(AppBuilderAppCreateDto dto, AppBuilderApp templateApp,
            OperationContext context, boolean isUpgrade, String appType, boolean isImport) {
        // 根据模板app复制app，仅需修改所有id
        // 优先copy下层内容，因为上层改变Id后，会影响下层对象的查询
        AppBuilderFlowGraph flowGraph = templateApp.getFlowGraph();
        flowGraph.setId(Entities.generateId());
        Map<String, Object> appearance;
        try {
            appearance = JSONObject.parseObject(flowGraph.getAppearance(), new TypeReference<Map<String, Object>>() {});
        } catch (JSONException e) {
            log.error("Import config failed, cause: {}", e);
            throw new AippException(AippErrCode.IMPORT_CONFIG_FIELD_ERROR, "flowGraph.appearance");
        }
        appearance.computeIfPresent("id", (key, value) -> flowGraph.getId());
        // 这里在创建应用时需要保证graph中的title+version唯一，否则在发布flow时会报错
        appearance.put("title", flowGraph.getId());
        // 动态修改graph中的model为可选model的第一个
        flowGraph.setAppearance(JSONObject.toJSONString(appearance));
        String version = this.buildVersion(templateApp, isUpgrade);
        List<AppBuilderFormProperty> formProperties = templateApp.getFormProperties();
        templateApp.setId(Entities.generateId());
        AppBuilderConfig config = resetConfig(formProperties, templateApp.getConfig());
        templateApp.setConfigId(config.getId());
        templateApp.setFlowGraphId(flowGraph.getId());
        templateApp.setType(appType);
        templateApp.setTenantId(context.getTenantId());
        if (!isImport) {
            templateApp.setState(AppState.INACTIVE.getName());
        }
        String preVersion = templateApp.getVersion();
        templateApp.setVersion(version);
        config.setAppId(templateApp.getId());
        if (Objects.nonNull(dto)) {
            templateApp.setAttributes(this.createAppAttributes(dto, isUpgrade, preVersion));
            templateApp.setName(dto.getName());
            templateApp.setType(dto.getType());
            templateApp.setAppType(dto.getAppType());
        }
        resetOperatorAndTime(templateApp, LocalDateTime.now(), context.getOperator());
        this.saveNewAppBuilderApp(templateApp);
        AppBuilderAppDto appDto = this.buildFullAppDto(templateApp);
        AippCreateDto createDto = this.saveMeta(appDto, version, context);
        appDto.setAippId(createDto.getAippId());
        return appDto;
    }

    @Override
    @Fitable(id = "default")
    public long getAppCount(String tenantId, AppQueryCondition cond) {
        return this.appRepository.countWithLatestApp(tenantId, cond);
    }

    private AippCreateDto saveMeta(AppBuilderAppDto appDto, String version, OperationContext context) {
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto);
        return this.aippFlowService.previewAipp(version, aippDto, context);
    }

    private String buildVersion(AppBuilderApp app, boolean isUpgrade) {
        // 当前只考虑升级，如果后续需要做基于应用创建新应用，则需要改动下面逻辑。
        if (!isUpgrade || !VersionUtils.isValidVersion(app.getVersion())) {
            return DEFAULT_APP_VERSION;
        }
        String[] parts = app.getVersion().split("\\.");
        parts[2] = String.valueOf(Integer.parseInt(parts[2]) + 1);
        String newVersion = StringUtils.format(VERSION_FORMAT, parts[0], parts[1], parts[2]);
        return newVersion.length() > VERSION_LENGTH ? app.getVersion() : newVersion;
    }

    private void validateCreateApp(AppBuilderAppCreateDto dto, OperationContext context) {
        String name = dto.getName();
        this.validateAppName(name, context);
        AppQueryCondition queryCondition =
                AppQueryCondition.builder().tenantId(context.getTenantId()).name(name).build();
        if (!this.appRepository.selectWithCondition(queryCondition).isEmpty()) {
            log.error("Create aipp failed, [name={}, tenantId={}]", name, context.getTenantId());
            throw new AippException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
        if (dto.getDescription() != null) {
            this.validateAppDescription(dto, context);
        }
        this.validateAppCategory(dto, context);
    }

    private void validateAppDescription(AppBuilderAppCreateDto dto, OperationContext context) {
        if (dto.getDescription().length() > 300) {
            log.error("Create aipp failed, [name={}, tenantId={}], app description is larger than 300.",
                    dto.getName(),
                    context.getTenantId());
            throw new AippException(context, AippErrCode.APP_DESCRIPTION_LENGTH_OUT_OF_BOUNDS);
        }
    }

    private void validateAppCategory(AppBuilderAppCreateDto dto, OperationContext context) {
        if (dto.getAppCategory() == null) {
            log.error("Create aipp failed, [name={}, tenantId={}], app category is null.",
                    dto.getName(),
                    context.getTenantId());
            throw new AippException(context, AippErrCode.APP_CATEGORY_IS_NULL);
        }
    }

    /**
     * 校验更新的app的合法性
     *
     * @param appId app唯一标识
     * @param name app名称
     * @param context 操作上下文
     * @throws AippTaskNotFoundException 任务不存在异常
     */
    public void validateUpdateApp(String appId, String name, OperationContext context)
            throws AippTaskNotFoundException {
        // 这个静态校验名称不能为空、不能超长
        this.validateAppName(name, context);

        // 如果该app已经发布过了，那么将不再允许修改名称
        List<Meta> metaList = this.getPublishedMetaList(appId, context);
        if (CollectionUtils.isNotEmpty(metaList) && !StringUtils.equals(metaList.get(0).getName(), name)) {
            throw new AippException(AippErrCode.APP_NAME_HAS_PUBLISHED);
        }

        // 到这里，要么是metaList是空的，要么就是没有改名
        // 如果metaList不是空的，证明没有改名，不管
        if (CollectionUtils.isNotEmpty(metaList)) {
            return;
        }
        // 如果mataList是空的，即没有发布过，那么名称可以修改为不和其它名称重复的名称
        AppQueryCondition queryCondition =
                AppQueryCondition.builder().tenantId(context.getTenantId()).name(name).build();
        List<AppBuilderApp> appBuilderApps = this.appRepository.selectWithCondition(queryCondition);
        if (appBuilderApps.isEmpty()) {
            return;
        }
        if (appBuilderApps.size() > 1 || !Objects.equals(appBuilderApps.get(0).getId(), appId)) {
            log.error("update aipp failed, [name={}, tenantId={}]", name, context.getTenantId());
            throw new AippException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
    }

    private void validateAppName(String name, OperationContext context) {
        if (!name.matches(this.appNameFormat)) {
            log.error("Create aipp failed: the name format is incorrect. [name={}]", name);
            throw new AippParamException(context, AippErrCode.APP_NAME_IS_INVALID);
        }
        String trimName = StringUtils.trim(name);
        if (StringUtils.isEmpty(trimName)) {
            log.error("Create aipp failed: name can not be empty.");
            throw new AippParamException(context, AippErrCode.AIPP_NAME_IS_EMPTY);
        } else {
            if (name.length() > this.nameLengthMaximum) {
                log.error("Create aipp failed: the length of task name is out of bounds. [name={}]", name);
                throw new AippParamException(context, AippErrCode.AIPP_NAME_LENGTH_OUT_OF_BOUNDS);
            }
        }
    }

    private Map<String, Object> createAppAttributes(AppBuilderAppCreateDto dto, boolean isUpgrade, String preVersion) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(APP_ATTR_DESCRIPTION, dto.getDescription());
        // 增加保护，前端传入null时部分场景导致出现字符串"null"
        attributes.put(APP_ATTR_ICON, dto.getIcon() == null ? StringUtils.EMPTY : dto.getIcon());
        attributes.put(APP_ATTR_GREETING, dto.getGreeting() == null ? StringUtils.EMPTY : dto.getGreeting());
        attributes.put(APP_ATTR_APP_TYPE, dto.getAppType() == null ? StringUtils.EMPTY : dto.getAppType());
        if (StringUtils.isNotBlank(dto.getStoreId())) {
            attributes.put(APP_ATTR_STORE_ID, dto.getStoreId());
        }
        if (isUpgrade) {
            attributes.put(APP_ATTR_LATEST_VERSION, preVersion);
        }
        return attributes;
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context) {
        if (appDto == null) {
            throw new AippException(AippErrCode.INVALID_OPERATION);
        }

        // 这一行都是有校验的作用，不能挪到下面
        AppBuilderApp update = this.appFactory.create(appId);
        try {
            this.validateUpdateApp(appId, appDto.getName(), context);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(APP_UPDATE_FAILED);
        }
        this.appUpdateValidator.validate(appId);
        update.setUpdateBy(context.getOperator());
        update.setUpdateAt(LocalDateTime.now());
        update.setName(appDto.getName());
        update.setType(appDto.getType());
        update.setAppType(appDto.getAppType());
        // 避免前端更新将app表的attributes覆盖了
        String oldIcon = ObjectUtils.cast(update.getAttributes().get("icon"));
        this.updateAttributes(update, appDto.getAttributes());
        this.updateAppState(update, appDto.getState());
        update.setVersion(appDto.getVersion());
        this.appFactory.update(update);
        String newIcon = ObjectUtils.cast(update.getAttributes().get("icon"));
        if (StringUtils.isNotBlank(newIcon) && !StringUtils.equals(oldIcon, newIcon)) {
            this.uploadedFileManageService.changeRemovable(AippFileUtils.getFileNameFromIcon(oldIcon), REMOVABLE);
            this.uploadedFileManageService.changeRemovable(AippFileUtils.getFileNameFromIcon(newIcon), IRREMOVABLE);
        }
        return Rsp.ok(this.buildFullAppDto(update));
    }

    private void updateAttributes(AppBuilderApp update, Map<String, Object> attributes) {
        Map<String, Object> attributesOld = update.getAttributes();
        attributesOld.putAll(attributes);
        attributesOld.put(AippConst.ATTR_APP_IS_UPDATE, true);
    }

    private void updateAppState(AppBuilderApp appToUpdated, String targetState) {
        if (StringUtils.equals(appToUpdated.getState(), AppState.IMPORTING.getName()) && StringUtils.equals(targetState,
                AppState.INACTIVE.getName())) {
            appToUpdated.setState(AppState.INACTIVE.getName());
        }
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto,
            List<AppBuilderConfigFormPropertyDto> properties, OperationContext context) {
        this.appUpdateValidator.validate(appId);
        LocalDateTime operateTime = LocalDateTime.now();
        AppBuilderApp oldApp = this.appFactory.create(appId);
        Span.current().setAttribute("name", oldApp.getName());

        AppBuilderConfig oldConfig = oldApp.getConfig();
        AppBuilderFlowGraph oldFlowGraph = oldApp.getFlowGraph();
        List<AppBuilderFormProperty> oldFormProperties = oldApp.getFormProperties();

        // 先更新config
        this.updateConfigPropertiesByAppBuilderConfigDto(appId, properties, oldConfig, oldFormProperties);
        this.updateConfigAndForm(configDto, context, oldConfig, operateTime, oldApp);
        // 然后同步更新flowGraph
        oldFlowGraph.setUpdateBy(context.getOperator());
        oldFlowGraph.setUpdateAt(operateTime);
        oldFlowGraph.setAppearance(this.updateFlowGraphAppearanceByConfigDto(oldFlowGraph.getAppearance(), properties));
        oldApp.getFlowGraphRepository().updateOne(oldFlowGraph);
        // 最后更新app主表
        oldApp.setUpdateAt(operateTime);
        oldApp.setUpdateBy(context.getOperator());
        this.updateAttributes(oldApp, new HashMap<>());
        this.appFactory.update(oldApp);
        return Rsp.ok(this.buildFullAppDto(oldApp));
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> saveConfig(String appId, AppBuilderSaveConfigDto appBuilderSaveConfigDto,
            OperationContext context) {
        List<AppBuilderFormProperty> formProperties = appBuilderSaveConfigDto.getInput()
                .stream()
                .map(formPropertyDto -> AppBuilderFormProperty.builder()
                        .id(formPropertyDto.getId())
                        .formId(FORM_PROPERTY_GROUP_NULL)
                        .name(formPropertyDto.getName())
                        .dataType(formPropertyDto.getDataType())
                        .defaultValue(formPropertyDto.getDefaultValue())
                        .from(formPropertyDto.getFrom())
                        .group(formPropertyDto.getGroup())
                        .description(formPropertyDto.getDescription())
                        .build())
                .collect(Collectors.toList());
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        this.updateAttributes(appBuilderApp, new HashMap<>());
        this.appFactory.update(appBuilderApp);
        appBuilderApp.getFormPropertyRepository().updateMany(formProperties);

        appBuilderApp.getFlowGraph().setAppearance(appBuilderSaveConfigDto.getGraph());
        appBuilderApp.getFlowGraphRepository().updateOne(appBuilderApp.getFlowGraph());
        return Rsp.ok(this.buildFullAppDto(appBuilderApp));
    }

    private void updateConfigAndForm(AppBuilderConfigDto configDto, OperationContext context,
            AppBuilderConfig oldConfig, LocalDateTime operateTime, AppBuilderApp oldApp) {
        oldConfig.setUpdateBy(context.getOperator());
        oldConfig.setUpdateAt(operateTime);
        oldApp.getConfigRepository().updateOne(oldConfig);
        oldConfig.getForm().setUpdateBy(context.getOperator());
        oldConfig.getForm().setUpdateAt(operateTime);
        oldConfig.getForm().setName(configDto.getForm().getName());
        oldConfig.getForm().setAppearance(configDto.getForm().getAppearance());
        oldApp.getFormRepository().updateOne(oldConfig.getForm());
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto,
            OperationContext context) {
        this.appUpdateValidator.validate(appId);
        LocalDateTime operateTime = LocalDateTime.now();

        AppBuilderApp oldApp = this.appFactory.create(appId);
        Span.current().setAttribute("name", oldApp.getName());
        // 优先更新graph本身
        AppBuilderFlowGraph oldAppFlowGraph = oldApp.getFlowGraph();
        oldAppFlowGraph.setUpdateAt(operateTime);
        oldAppFlowGraph.setUpdateBy(context.getOperator());
        oldAppFlowGraph.setName(graphDto.getName());
        oldAppFlowGraph.setAppearance(JSONObject.toJSONString(graphDto.getAppearance()));
        oldApp.getFlowGraphRepository().updateOne(oldAppFlowGraph);
        // 根据graph更新config
        String appearance = oldAppFlowGraph.getAppearance();
        AppBuilderConfig oldConfig = oldApp.getConfig();
        List<AppBuilderFormProperty> oldFormProperties = oldApp.getFormProperties();
        this.updateConfigByGlowGraphAppearance(appearance, oldFormProperties, oldConfig); // 这个方法是在更新properties
        oldConfig.setUpdateAt(operateTime);
        oldConfig.setUpdateBy(context.getOperator());
        oldApp.getConfigRepository().updateOne(oldConfig);
        // 最后更新app主表
        oldApp.setUpdateAt(operateTime);
        oldApp.setUpdateBy(context.getOperator());
        this.updateAttributes(oldApp, new HashMap<>());
        this.appFactory.update(oldApp);
        return Rsp.ok(this.buildFullAppDto(oldApp));
    }

    @Override
    @Fitable(id = "default")
    public void delete(String appId, OperationContext context) {
        AppBuilderApp app = this.validateApp(appId);
        MetaFilter filter = new MetaFilter();
        try {
            String metaId = MetaUtils.getAippIdByAppId(this.metaService, appId, context);
            filter.setMetaIds(Collections.singletonList(metaId));
            List<Meta> metas = MetaUtils.getListMetaHandle(this.metaService, filter, context);
            if (CollectionUtils.isEmpty(metas)) {
                return;
            }
            List<String> metaVersionIds =
                    metas.stream().map(Meta::getVersionId).distinct().collect(Collectors.toList());
            List<Tuple> versionIdInstances = this.getVersionIdInstanceIds(metaVersionIds, context);
            Set<String> instanceIds = this.getInstanceIds(versionIdInstances);
            List<String> appIds = this.getFullAppIds(metas);
            this.deleteApps(appIds);
            this.deleteMetaInstances(versionIdInstances, context);
            this.deleteMetas(metaVersionIds, context);
            this.uploadedFileManageService.cleanAippFiles(Collections.singletonList(appId));
            this.deleteLogs(instanceIds);
            this.deleteFlows(metas, context);
            this.deleteStore(metas, AppCategory.findByType(app.getType()).orElse(null));
            this.deleteChats(metaId);
            this.deleteUsrAppCollection(appId);
        } catch (AippTaskNotFoundException exception) {
            throw new AippException(APP_DELETE_FAILED);
        }
    }

    private void deleteChats(String metaId) {
        this.aippChatMapper.deleteAppByAippId(metaId);
    }

    private void deleteUsrAppCollection(String appId) {
        this.usrAppCollectionService.deleteByAppId(appId);
    }

    private void deleteStore(List<Meta> metas, AppCategory type) {
        if (type == null) {
            return;
        }
        List<String> uniqueNames = metas.stream()
                .filter(meta -> meta != null && meta.getAttributes().containsKey(ATTR_UNIQUE_NAME))
                .map(meta -> ObjectUtils.<String>cast(meta.getAttributes().get(ATTR_UNIQUE_NAME)))
                .distinct()
                .toList();
        if (type == AppCategory.WATER_FLOW) {
            List<PluginToolData> pluginTools = this.pluginToolService.getPluginTools(uniqueNames);
            pluginTools.forEach(pluginTool -> this.pluginService.deletePlugin(pluginTool.getPluginId()));
        } else {
            uniqueNames.forEach(this.appService::deleteApp);
        }
    }

    private void deleteFlows(List<Meta> metas, OperationContext context) {
        metas.forEach(meta -> this.flowsService.deleteFlowsWithoutElsa(meta.getId(), meta.getVersion(), context));
    }

    private void deleteLogs(Set<String> instanceIds) {
        if (instanceIds.isEmpty()) {
            return;
        }
        this.aippLogMapper.deleteByInstanceIds(new ArrayList<>(instanceIds));
    }

    private Set<String> getInstanceIds(List<Tuple> versionIdInstances) {
        Set<String> instanceIds = new HashSet<>();
        for (Tuple versionIdInstance : versionIdInstances) {
            List<Instance> instances = ObjectUtils.cast(versionIdInstance.get(1)
                    .orElseThrow(() -> new AippException(AippErrCode.DELETE_ERROR)));
            if (CollectionUtils.isEmpty(instances)) {
                continue;
            }
            instanceIds.addAll(instances.stream().map(Instance::getId).distinct().collect(Collectors.toList()));
        }
        return instanceIds;
    }

    private List<Tuple> getVersionIdInstanceIds(List<String> metaVersionIds, OperationContext context) {
        return metaVersionIds.stream()
                .map(versionId -> Tuple.duet(versionId,
                        MetaInstanceUtils.getOneInstance(versionId, this.metaInstanceService, context)))
                .collect(Collectors.toList());
    }

    private void deleteApps(List<String> appIds) {
        this.appFactory.delete(appIds);
    }

    private List<String> getFullAppIds(List<Meta> metas) {
        return metas.stream()
                .filter(meta -> meta != null && meta.getAttributes().containsKey(AippConst.ATTR_APP_ID_KEY))
                .map(meta -> ObjectUtils.<String>cast(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY)))
                .distinct()
                .collect(Collectors.toList());
    }

    private void deleteMetaInstances(List<Tuple> versionIdInstances, OperationContext context) {
        versionIdInstances.forEach(versionIdInstance -> this.deleteMetaInstance(context, versionIdInstance));
    }

    private void deleteMetaInstance(OperationContext context, Tuple versionIdInstance) {
        String versionId = ObjectUtils.cast(versionIdInstance.get(0)
                .orElseThrow(() -> new AippException(AippErrCode.DELETE_ERROR)));
        List<Instance> instances = ObjectUtils.cast(versionIdInstance.get(1)
                .orElseThrow(() -> new AippException(AippErrCode.DELETE_ERROR)));
        if (CollectionUtils.isEmpty(instances)) {
            return;
        }
        instances.forEach(instance -> this.metaInstanceService.deleteMetaInstance(versionId,
                instance.getId(),
                context));
    }

    private void deleteMetas(List<String> metaVersionIds, OperationContext context) {
        metaVersionIds.forEach(versionId -> this.metaService.delete(versionId, context));
    }

    @Override
    public PublishedAppResDto published(String uniqueName, OperationContext context) {
        MetaFilter filter = new MetaFilter();
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(ATTR_UNIQUE_NAME, Collections.singletonList(uniqueName));
        filter.setAttributes(attributes);
        RangedResultSet<Meta> metas = this.metaService.list(filter, true, 0, 1, context);
        if (metas.getResults().isEmpty()) {
            log.error("Meta can not be null.");
            throw new AippParamException(QUERY_PUBLICATION_HISTORY_FAILED);
        }
        Meta meta = metas.getResults().get(0);
        String appId = String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        String publishedDescription = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_DESCRIPTION));
        String publishedUpdateLog = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_UPDATE_LOG));
        return PublishedAppResDto.builder()
                .appId(appId)
                .appVersion(meta.getVersion())
                .publishedAt(meta.getCreationTime())
                .publishedBy(meta.getCreator())
                .publishedDescription(publishedDescription)
                .publishedUpdateLog(publishedUpdateLog)
                .build();
    }

    @Override
    public AppExportDto export(String appId, OperationContext context) {
        AppBuilderApp app = this.appFactory.create(appId);
        if (!StringUtils.equals(app.getCreateBy(), context.getName())) {
            throw new AippException(AippErrCode.EXPORT_CONFIG_UNAUTHED);
        }

        // 校验流程编排合法性
        try {
            AppBuilderAppDto appDto = this.buildFullAppDto(app);
            String flowDefinitionData =
                    this.aippFlowDefinitionService.getParsedGraphData(JsonUtils.toJsonString(appDto.getFlowGraph()
                            .getAppearance()), app.getVersion());
            this.flowDefinitionService.validateDefinitionData(flowDefinitionData);
        } catch (Exception e) {
            log.error("app config export failed", e);
            throw new AippException(AippErrCode.EXPORT_INVALID_FLOW_EXCEPTION);
        }

        try {
            AppExportApp exportAppInfo = AppImExportUtil.convertToAppExportApp(app);

            String appType = this.appTypeService.query(app.getAppType(), context.getTenantId()).getName();
            exportAppInfo.setAppType(appType);
            String icon = ObjectUtils.cast(app.getAttributes().get("icon"));
            if (StringUtils.isNotBlank(icon)) {
                exportAppInfo.getAttributes()
                        .put("icon", this.getIconAttributes(AippFileUtils.getFileNameFromIcon(icon)));
            }
            AppBuilderConfig appBuilderConfig = app.getConfig();
            appBuilderConfig.setApp(app);
            AppExportConfig exportAppConfig = AppImExportUtil.convertToAppExportConfig(appBuilderConfig);
            AppExportFlowGraph exportFlowGraph = AppImExportUtil.convertToAppExportFlowGraph(app.getFlowGraph());

            return AppExportDto.builder()
                    .version(this.exportMeta.get("version"))
                    .app(exportAppInfo)
                    .config(exportAppConfig)
                    .flowGraph(exportFlowGraph)
                    .build();
        } catch (DataAccessException e) {
            log.error("app config export failed", e);
            throw new AippException(AippErrCode.EXPORT_CONFIG_DB_EXCEPTION);
        }
    }

    private Map<String, String> getIconAttributes(String iconPath) {
        try {
            File iconFile = FileUtils.canonicalize(iconPath);
            Map<String, String> iconAttr = MapBuilder.<String, String>get().build();
            byte[] iconBytes = AppImExportUtil.readAllBytes(Files.newInputStream(iconFile.toPath()));
            iconAttr.put("content", Base64.getEncoder().encodeToString(iconBytes));
            iconAttr.put("type", AppImExportUtil.extractIconExtension(iconFile.getName()));
            return iconAttr;
        } catch (IllegalStateException | IOException e) {
            return MapBuilder.<String, String>get().put("content", StringUtils.EMPTY).build();
        }
    }

    @Override
    @Transactional
    public AppBuilderAppDto importApp(String appConfig, OperationContext context) {
        try {
            AppExportDto appExportDto = new ObjectMapper().readValue(appConfig, AppExportDto.class);
            if (!StringUtils.equals(appExportDto.getVersion(), this.exportMeta.get("version"))) {
                throw new AippException(AippErrCode.IMPORT_CONFIG_UNMATCHED_VERSION,
                        this.exportMeta.get("version"),
                        appExportDto.getVersion());
            }
            AppImExportUtil.checkAppExportDto(appExportDto);
            String initAppName = appExportDto.getApp().getName();
            List<String> similarNames = this.appRepository.selectWithSimilarName(initAppName);
            String newName = AppImExportUtil.generateNewAppName(similarNames, initAppName);
            this.validateAppName(newName, context);
            appExportDto.getApp().setName(newName);
            appExportDto.getApp().getAttributes().put("name", newName);
            String appTypeId = this.createAppType(appExportDto.getApp().getAppType(), context.getTenantId());
            appExportDto.getApp().setAppType(appTypeId);

            Object iconAttr = appExportDto.getApp().getAttributes().get("icon");
            AppBuilderApp templateApp = AppImExportUtil.convertToAppBuilderApp(appExportDto, context);
            this.appFactory.setRepositories(templateApp);
            AppBuilderAppDto appDto =
                    this.createAppWithTemplate(null, templateApp, context, false, templateApp.getType(), true);
            String iconContent =
                    iconAttr instanceof Map ? ObjectUtils.cast(ObjectUtils.<Map<String, Object>>cast(iconAttr)
                            .get("content")) : StringUtils.EMPTY;
            if (StringUtils.isBlank(iconContent)) {
                return appDto;
            }
            String iconExtension = ObjectUtils.cast(ObjectUtils.<Map<String, Object>>cast(iconAttr).get("type"));
            String iconPath = AppImExportUtil.saveIconFile(iconContent, iconExtension, context.getTenantId(),
                    this.contextRoot);
            if (StringUtils.isBlank(iconPath)) {
                return appDto;
            }
            AppBuilderApp update = this.appFactory.create(appDto.getId());
            update.getAttributes().put("icon", iconPath);
            this.appFactory.update(update);
            this.uploadedFileManageService.addFileRecord(update.getId(),
                    context.getAccount(),
                    AippFileUtils.getFileNameFromIcon(iconPath),
                    Entities.generateId());
            return this.buildFullAppDto(update);
        } catch (JsonProcessingException e) {
            log.error("Imported config file is not json", e);
            throw new AippException(AippErrCode.IMPORT_CONFIG_NOT_JSON,
                    e.getLocation().getLineNr(),
                    e.getLocation().getColumnNr());
        }
    }

    private String createAppType(String appTypeName, String tenantId) {
        Optional<String> createdId = this.appTypeService.queryAll(tenantId)
                .stream()
                .filter(dto -> StringUtils.equals(dto.getName(), appTypeName))
                .map(AppTypeDto::getId)
                .findAny();
        return createdId.orElseGet(() -> this.appTypeService.add(AppTypeDto.builder().name(appTypeName).build(),
                tenantId).getId());
    }

    private String copyIconFiles(String icon, String aippId, String operator) throws IOException {
        File originIcon = FileUtils.canonicalize(AippFileUtils.getFileNameFromIcon(icon));
        String originIconName = originIcon.getName();
        String copiedIconName = UUID.randomUUID() + FileUtils.extension(originIconName);
        File copiedIcon = FileUtils.canonicalize(originIcon.getCanonicalPath().replace(originIconName, copiedIconName));
        IoUtils.copy(originIcon, copiedIcon);
        this.uploadedFileManageService.addFileRecord(aippId,
                operator,
                copiedIcon.getCanonicalPath(),
                Entities.generateId());
        return icon.replace(originIconName, copiedIconName);
    }

    @Override
    @Transactional
    public TemplateInfoDto publishTemplateFromApp(TemplateAppCreateDto createDto, OperationContext context) {
        this.validateAppName(createDto.getName(), context);
        AppBuilderApp app = this.appFactory.create(createDto.getId());
        AppTemplate newTemplate = TemplateUtils.convertToAppTemplate(app);
        this.templateFactory.setRepositories(newTemplate);
        return this.createTemplateFromApp(createDto, newTemplate, context);
    }

    private TemplateInfoDto createTemplateFromApp(TemplateAppCreateDto dto, AppTemplate template,
            OperationContext context) {
        AppBuilderFlowGraph flowGraph = template.getFlowGraph();
        flowGraph.setId(Entities.generateId());
        List<AppBuilderFormProperty> formProperties = template.getFormProperties();
        AppBuilderConfig config = resetConfig(formProperties, template.getConfig());
        template.setId(Entities.generateId());
        template.setAttributes(this.resetTemplateAttributes(template.getAttributes()));
        config.setAppId(template.getId());
        template.setConfigId(config.getId());
        template.setFlowGraphId(flowGraph.getId());
        if (dto != null) {
            template.setName(dto.getName());
            template.setAppType(dto.getAppType());
            template.getAttributes().put(TemplateUtils.DESCRIPTION_ATTR_KEY, dto.getDescription());
            String icon = ObjectUtils.cast(template.getAttributes().get(TemplateUtils.ICON_ATTR_KEY));
            if (StringUtils.isNotBlank(icon) && StringUtils.equals(icon, dto.getIcon())) {
                try {
                    String copiedIcon = this.copyIconFiles(icon, template.getId(), context.getAccount());
                    template.getAttributes().put(TemplateUtils.ICON_ATTR_KEY, copiedIcon);
                } catch (IOException e) {
                    log.warn("Failed to create a copy of icon when publish.", e);
                    template.getAttributes().put(TemplateUtils.ICON_ATTR_KEY, StringUtils.EMPTY);
                }
            }
        }
        resetOperatorAndTime(template, LocalDateTime.now(), context.getOperator());
        this.templateFactory.save(template);
        String icon = ObjectUtils.cast(template.getAttributes().get(TemplateUtils.ICON_ATTR_KEY));
        if (StringUtils.isNotBlank(icon)) {
            this.uploadedFileManageService.updateRecord(template.getId(),
                    AippFileUtils.getFileNameFromIcon(icon),
                    IRREMOVABLE);
        }

        return TemplateUtils.convertToTemplateDto(template);
    }

    private Map<String, Object> resetTemplateAttributes(Map<String, Object> attributes) {
        Map<String, Object> resetedAttr = MapBuilder.<String, Object>get().build();
        Arrays.stream(TEMPLATE_DEFAULT_ATTRIBUTE_KEYS).forEach(attr -> resetedAttr.put(attr, attributes.get(attr)));
        return resetedAttr;
    }

    @Override
    public AppBuilderAppDto createAppByTemplate(TemplateAppCreateDto createDto, OperationContext context) {
        this.validateAppName(createDto.getName(), context);
        AppTemplate template = this.templateFactory.create(createDto.getId());
        AppBuilderApp appTemplate = TemplateUtils.convertToAppBuilderApp(template);
        this.appFactory.setRepositories(appTemplate);
        AppBuilderAppCreateDto dto = this.buildAppBuilderAppCreateDto(appTemplate);
        dto.setName(createDto.getName());
        dto.setType(AppTypeEnum.APP.code());
        dto.setDescription(createDto.getDescription());
        dto.setAppType(createDto.getAppType());
        String icon = ObjectUtils.cast(appTemplate.getAttributes().get(TemplateUtils.ICON_ATTR_KEY));
        if (StringUtils.isNotBlank(icon) && StringUtils.equals(icon, createDto.getIcon())) {
            try {
                String copiedIcon = this.copyIconFiles(icon, null, context.getAccount());
                dto.setIcon(copiedIcon);
            } catch (IOException e) {
                log.warn("Failed to create a copy of icon when create app.", e);
                dto.setIcon(StringUtils.EMPTY);
            }
        } else {
            dto.setIcon(createDto.getIcon());
        }
        return this.createAppWithTemplate(dto, appTemplate, context, false, AppTypeEnum.APP.code(), false);
    }

    @Override
    public void deleteTemplate(String templateId, OperationContext context) {
        this.templateFactory.delete(templateId);
        this.uploadedFileManageService.cleanAippFiles(Collections.singletonList(templateId));
    }

    @Override
    public List<PublishedAppResDto> recentPublished(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context) {
        this.validateApp(appId);
        try {
            String aippId = MetaUtils.getAippIdByAppId(this.metaService, appId, context);
            List<Meta> allPublishedMeta = MetaUtils.getAllPublishedMeta(this.metaService, aippId, context)
                    .stream()
                    .filter(meta -> !this.isAppBelong(appId, meta))
                    .collect(Collectors.toList());
            List<String> appIds = allPublishedMeta.stream()
                    .map(meta -> String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY)))
                    .collect(Collectors.toList());
            cond.setIds(appIds);
            cond.setTenantId(context.getTenantId());
            List<AppBuilderApp> allPublishedApp = this.appRepository.selectWithCondition(cond);
            Map<String, AppBuilderApp> appIdKeyAppValueMap =
                    allPublishedApp.stream().collect(Collectors.toMap(AppBuilderApp::getId, Function.identity()));
            return this.buildPublishedAppResDtos(allPublishedMeta, appIdKeyAppValueMap);
        } catch (AippTaskNotFoundException exception) {
            throw new AippException(QUERY_PUBLICATION_HISTORY_FAILED);
        }
    }

    @Override
    public List<CheckResult> checkAvailable(List<AppCheckDto> appCheckDtos, OperationContext context) {
        List<CheckResult> results = new ArrayList<>();
        appCheckDtos.forEach(dto -> {
            Checker nodeChecker = CheckerFactory.getChecker(dto.getType());
            results.addAll(nodeChecker.validate(dto, context));
        });
        return results.stream().filter(result -> !result.isValid()).collect(Collectors.toList());
    }

    private boolean isAppBelong(String appId, Meta meta) {
        return Objects.equals(String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY)), appId);
    }

    private List<PublishedAppResDto> buildPublishedAppResDtos(List<Meta> metas,
            Map<String, AppBuilderApp> appIdKeyAppValueMap) {
        return metas.stream()
                .map(meta -> this.buildPublishedAppResDto(meta, appIdKeyAppValueMap))
                .collect(Collectors.toList());
    }

    private PublishedAppResDto buildPublishedAppResDto(Meta meta, Map<String, AppBuilderApp> appIdKeyAppValueMap) {
        String appId = String.valueOf(meta.getAttributes().get(AippConst.ATTR_APP_ID_KEY));
        String publishedDescription = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_DESCRIPTION));
        String publishedUpdateLog = String.valueOf(meta.getAttributes().get(AippConst.ATTR_PUBLISH_UPDATE_LOG));
        AppBuilderApp app = appIdKeyAppValueMap.get(appId);
        return PublishedAppResDto.builder()
                .appId(appId)
                .appVersion(app.getVersion())
                .publishedAt(meta.getCreationTime())
                .publishedBy(meta.getCreator())
                .publishedDescription(publishedDescription)
                .publishedUpdateLog(publishedUpdateLog)
                .build();
    }

    private static AppBuilderConfig resetConfig(List<AppBuilderFormProperty> formProperties, AppBuilderConfig config) {
        AppBuilderForm form = config.getForm();
        // 这里先根据旧的formId查询得到formProperties
        Map<String, AppBuilderFormProperty> idToFormPropertyMap =
                formProperties.stream().collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));
        // 先根据旧的configId查询得到configProperties
        List<AppBuilderConfigProperty> configProperties = config.getConfigProperties();
        config.setId(Entities.generateId());
        configProperties.forEach(configProperty -> resetIdToConfigAndFormProperty(configProperty,
                idToFormPropertyMap,
                form.getId(),
                config.getId()));
        config.setFormId(form.getId());
        return config;
    }

    private static void resetIdToConfigAndFormProperty(AppBuilderConfigProperty configProperty,
            Map<String, AppBuilderFormProperty> idToFormPropertyMap, String formId, String configId) {
        configProperty.setId(Entities.generateId());
        configProperty.setConfigId(configId);
        AppBuilderFormProperty formProperty = idToFormPropertyMap.get(configProperty.getFormPropertyId());
        if (formProperty == null) {
            return;
        }
        formProperty.setId(Entities.generateId());
        formProperty.setFormId(formId);
        configProperty.setFormPropertyId(formProperty.getId());
    }

    private static void resetOperatorAndTime(AppBuilderApp app, LocalDateTime time, String operator) {
        app.setCreateBy(operator);
        app.setCreateAt(time);
        app.setUpdateBy(operator);
        app.setUpdateAt(time);
        resetOperatorAndTimeForConfig(app.getConfig(), time, operator);
        resetOperatorAndTimeForFlowGraph(app.getFlowGraph(), time, operator);
    }

    private static void resetOperatorAndTime(AppTemplate template, LocalDateTime time, String operator) {
        template.setCreateBy(operator);
        template.setCreateAt(time);
        template.setUpdateBy(operator);
        template.setUpdateAt(time);
        resetOperatorAndTimeForConfig(template.getConfig(), time, operator);
        resetOperatorAndTimeForFlowGraph(template.getFlowGraph(), time, operator);
    }

    private static void resetOperatorAndTimeForConfig(AppBuilderConfig config, LocalDateTime time, String operator) {
        config.setCreateBy(operator);
        config.setCreateAt(time);
        config.setUpdateBy(operator);
        config.setUpdateAt(time);
        AppBuilderForm form = config.getForm();
        form.setCreateBy(operator);
        form.setCreateAt(time);
        form.setUpdateBy(operator);
        form.setUpdateAt(time);
    }

    private static void resetOperatorAndTimeForFlowGraph(AppBuilderFlowGraph flowGraph, LocalDateTime time,
            String operator) {
        flowGraph.setCreateBy(operator);
        flowGraph.setCreateAt(time);
        flowGraph.setUpdateBy(operator);
        flowGraph.setUpdateAt(time);
    }

    private void saveNewAppBuilderApp(AppBuilderApp appBuilderApp) {
        // 保存app
        this.appFactory.save(appBuilderApp);

        String icon = ObjectUtils.cast(appBuilderApp.getAttributes().get("icon"));
        if (StringUtils.isNotBlank(icon)) {
            this.uploadedFileManageService.updateRecord(appBuilderApp.getId(),
                    AippFileUtils.getFileNameFromIcon(icon),
                    IRREMOVABLE);
        }

        appBuilderApp.getConfigRepository().insertOne(appBuilderApp.getConfig());
        appBuilderApp.getFlowGraphRepository().insertOne(appBuilderApp.getFlowGraph());
        appBuilderApp.getConfigPropertyRepository().insertMore(appBuilderApp.getConfig().getConfigProperties());
        List<AppBuilderFormProperty> formProperties = appBuilderApp.getFormProperties();
        formProperties.forEach(property -> {
            property.setAppId(appBuilderApp.getId());
        });
        appBuilderApp.getFormPropertyRepository().insertMore(formProperties);
    }

    private AppBuilderAppDto buildFullAppDto(AppBuilderApp app) {
        AppBuilderAppDto.AppBuilderAppDtoBuilder appDtoBuilder = AppBuilderAppDto.builder()
                .id(app.getId())
                .name(app.getName())
                .type(app.getType())
                .state(app.getState())
                .appType(app.getAppType())
                .attributes(app.getAttributes())
                .version(app.getVersion())
                .appCategory(app.getAppCategory())
                .createBy(app.getCreateBy())
                .updateBy(app.getUpdateBy())
                .createAt(app.getCreateAt())
                .updateAt(app.getUpdateAt())
                .config(this.buildAppBuilderConfig(app.getConfig()))
                .flowGraph(this.buildFlowGraph(app.getFlowGraph()))
                .appBuiltType(app.getAppBuiltType())
                .configFormProperties(this.buildAppBuilderConfigFormProperties(app.getFormProperties()));
        Optional.ofNullable(app.getPath())
                .filter(path -> !path.isEmpty())
                .ifPresent(path -> appDtoBuilder.chatUrl(String.format("/chat/%s", path)));
        return appDtoBuilder.build();
    }

    private AppBuilderFlowGraphDto buildFlowGraph(AppBuilderFlowGraph flowGraph) {
        return AppBuilderFlowGraphDto.builder()
                .id(flowGraph.getId())
                .name(flowGraph.getName())
                .appearance(JsonUtils.parseObject(flowGraph.getAppearance()))
                .createBy(flowGraph.getCreateBy())
                .updateBy(flowGraph.getUpdateBy())
                .createAt(flowGraph.getCreateAt())
                .updateAt(flowGraph.getUpdateAt())
                .build();
    }

    private AppBuilderConfigDto buildAppBuilderConfig(AppBuilderConfig config) {
        return AppBuilderConfigDto.builder()
                .id(config.getId())
                .tenantId(config.getTenantId())
                .createBy(config.getCreateBy())
                .updateBy(config.getUpdateBy())
                .createAt(config.getCreateAt())
                .updateAt(config.getUpdateAt())
                .form(this.buildAppBuilderConfigFormDto(config))
                .build();
    }

    private AppBuilderConfigFormDto buildAppBuilderConfigFormDto(AppBuilderConfig config) {
        Validation.notNull(config.getForm(), "Form can not be null.");
        return AppBuilderConfigFormDto.builder()
                .id(config.getFormId())
                .name(config.getForm().getName())
                .appearance(config.getForm().getAppearance())
                .build();
    }

    private List<AppBuilderConfigFormPropertyDto> buildAppBuilderConfigFormProperties(
            List<AppBuilderFormProperty> formProperties) {
        LinkedHashMap<String, AppBuilderConfigFormPropertyDto> formPropertyMapping = formProperties.stream()
                .map(AppBuilderFormProperty::toAppBuilderConfigFormPropertyDto)
                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getName,
                        Function.identity(),
                        (k1, k2) -> k1,
                        LinkedHashMap::new));
        String root = "";
        for (Map.Entry<String, AppBuilderConfigFormPropertyDto> entry : formPropertyMapping.entrySet()) {
            AppBuilderConfigFormPropertyDto dto = entry.getValue();
            String group = entry.getValue().getGroup();
            if (group.equals(FORM_PROPERTY_GROUP_NULL)) {
                root = dto.getName();
            } else {
                group = dto.getGroup();
                AppBuilderConfigFormPropertyDto parent = formPropertyMapping.get(group);
                if (parent == null) {
                    throw new AippException(AippErrCode.FORM_PROPERTY_PARENT_NOT_EXIST);
                }
                parent.addChild(dto);
            }
        }
        AppBuilderConfigFormPropertyDto rootProperty = formPropertyMapping.get(root);
        return rootProperty == null ? Collections.emptyList() : Collections.singletonList(rootProperty);
    }

    private void updateConfigPropertiesByAppBuilderConfigDto(String appId,
            List<AppBuilderConfigFormPropertyDto> newProperties, AppBuilderConfig oldConfig,
            List<AppBuilderFormProperty> oldFormProperties) {
        Map<String, AppBuilderConfigFormPropertyDto> newIdToPropertyDtoMap = newProperties.stream()
                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getId, Function.identity()));
        List<AppBuilderConfigProperty> oldConfigProperties = oldConfig.getConfigProperties(); // 这个对象里全是id，所以是不会改动的
        Set<String> oldFormPropertyIds =
                oldFormProperties.stream().map(AppBuilderFormProperty::getId).collect(Collectors.toSet());

        // 删除
        this.deleteProperties(oldConfig, oldConfigProperties, newIdToPropertyDtoMap, oldFormProperties);

        // 新增
        this.addProperties(appId, oldConfig, newProperties, oldFormPropertyIds);

        // 修改, 待修改的内容, 循环修改
        oldFormProperties.stream()
                .filter(formProperty -> newIdToPropertyDtoMap.containsKey(formProperty.getId()))
                .forEach(formProperty -> {
                    AppBuilderConfigFormPropertyDto propertyDto = newIdToPropertyDtoMap.get(formProperty.getId());
                    formProperty.setName(propertyDto.getName());
                    formProperty.setDataType(propertyDto.getDataType());
                    formProperty.setDefaultValue(propertyDto.getDefaultValue());
                    oldConfig.getForm().getFormPropertyRepository().updateOne(formProperty);
                });
    }

    private void addProperties(String appId, AppBuilderConfig config, List<AppBuilderConfigFormPropertyDto> properties,
            Set<String> formPropertyIds) {
        List<AppBuilderConfigProperty> toAddConfigProperties = properties.stream()
                .filter(pd -> StringUtils.isBlank(pd.getId()) || !formPropertyIds.contains(pd.getId()))
                .map(propertyDto -> {
                    AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder()
                            .formId(config.getFormId())
                            .name(propertyDto.getName())
                            .dataType(propertyDto.getDataType())
                            .defaultValue(propertyDto.getDefaultValue())
                            .id(Entities.generateId())
                            .appId(appId)
                            .build();
                    return AppBuilderConfigProperty.builder()
                            .id(Entities.generateId())
                            .configId(config.getId())
                            .nodeId(propertyDto.getNodeId())
                            .formPropertyId(formProperty.getId())
                            .formProperty(formProperty)
                            .build();
                })
                .collect(Collectors.toList());
        List<AppBuilderFormProperty> toAddFormProperties = toAddConfigProperties.stream()
                .map(AppBuilderConfigProperty::getFormProperty)
                .collect(Collectors.toList());

        config.getConfigPropertyRepository().insertMore(toAddConfigProperties);
        config.getForm().getFormPropertyRepository().insertMore(toAddFormProperties);
    }

    private void deleteProperties(AppBuilderConfig config, List<AppBuilderConfigProperty> configProperties,
            Map<String, AppBuilderConfigFormPropertyDto> idToPropertyDtoMap,
            List<AppBuilderFormProperty> formProperties) {
        List<String> toDeleteConfigPropertyIds = configProperties.stream()
                .filter(cp -> !idToPropertyDtoMap.containsKey(cp.getFormPropertyId()))
                .map(AppBuilderConfigProperty::getId)
                .collect(Collectors.toList());
        List<String> toDeleteFormPropertyIds = formProperties.stream()
                .map(AppBuilderFormProperty::getId)
                .filter(id -> !idToPropertyDtoMap.containsKey(id))
                .collect(Collectors.toList());
        config.getConfigPropertyRepository().deleteMore(toDeleteConfigPropertyIds);
        config.getForm().getFormPropertyRepository().deleteMore(toDeleteFormPropertyIds);
    }

    private String updateFlowGraphAppearanceByConfigDto(String oldAppearance,
            List<AppBuilderConfigFormPropertyDto> formProperties) {
        // 将dto的properties转成 {nodeId : {name:value, name:value},  ... }形式
        Map<String, Map<String, String>> nodeIdToPropertyNameValueMap = formProperties.stream()
                .filter(fp -> StringUtils.isNotBlank(fp.getNodeId()))
                .collect(Collectors.groupingBy(AppBuilderConfigFormPropertyDto::getNodeId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getName,
                                        appBuilderConfigFormPropertyDto -> JsonUtils.toJsonString(
                                                appBuilderConfigFormPropertyDto.getDefaultValue())))));
        JSONObject oldAppearanceObject = JSONObject.parseObject(oldAppearance);
        JSONObject page = ObjectUtils.<JSONObject>cast(oldAppearanceObject.getJSONArray("pages").get(0));
        JSONArray shapes = page.getJSONArray("shapes");

        for (int j = 0; j < shapes.size(); j++) {
            JSONObject node = shapes.getJSONObject(j);
            String id = node.getString("id");
            String type = node.getString("type");
            if (!StringUtils.equals(type, "startNodeStart") && !type.endsWith("NodeState")) {
                continue;
            }

            Map<String, String> nameValue = nodeIdToPropertyNameValueMap.get(id);

            String flowMetaString = node.get("flowMeta").toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode flowMeta = null;
            try {
                flowMeta = mapper.readTree(flowMetaString);
                JsonNode params = flowMeta.findPath("inputParams");
                for (int i = 0; i < params.size(); i++) {
                    JsonNode child = params.get(i);
                    processParam(child, nameValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Object tt = JSON.parse(flowMeta.toString());
            node.put("flowMeta", tt);
        }

        return JSONObject.toJSONString(oldAppearanceObject);
    }

    private void processParam(JsonNode node, Map<String, String> params) {
        List<String> singleLayerParams = new ArrayList<>(Arrays.asList("model", "temperature", "systemPrompt"));
        List<String> doubleLayerParams = new ArrayList<>(Arrays.asList("tools", "workflows"));
        if (params == null) {
            return;
        }
        for (Map.Entry<String, String> param : params.entrySet()) {
            handleParam(node, param, singleLayerParams, doubleLayerParams);
        }
    }

    private void handleParam(JsonNode node, Map.Entry<String, String> param, List<String> singleLayerParams,
            List<String> doubleLayerParams) {
        if (StringUtils.equals(node.get("name").asText(), param.getKey())) {
            if (singleLayerParams.contains(param.getKey())) {
                this.handleParamTemperature(node, param);
                return;
            }

            if (doubleLayerParams.contains(param.getKey())) {
                ArrayNode valueArrayNode = convertList(param.getValue());
                ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
                return;
            }

            if (StringUtils.equals("knowledge", param.getKey())) {
                this.handleParamKnowledge(node, param);
                return;
            }

            if (StringUtils.equals("memory", param.getKey())) {
                JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
                ArrayNode valueArrayNode = nodeFactory.arrayNode();
                Map<String, Object> res = JsonUtils.parseObject(param.getValue(), Map.class);
                if (Objects.equals(res.get("type"), "UserSelect")) {
                    this.parseUserSelect(res, valueArrayNode);
                } else {
                    this.parseOtherMemoryType(res, valueArrayNode);
                }
                ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
            }
        }
    }

    private void handleParamTemperature(JsonNode node, Map.Entry<String, String> param) {
        if (StringUtils.equals(param.getKey(), "temperature")) {
            ObjectUtils.<ObjectNode>cast(node).put("value", JsonUtils.parseObject(param.getValue(), Float.class));
        } else {
            ObjectUtils.<ObjectNode>cast(node).put("value", JsonUtils.parseObject(param.getValue(), String.class));
        }
    }

    private void handleParamKnowledge(JsonNode node, Map.Entry<String, String> param) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        List<Map<String, Object>> res =
                ObjectUtils.<List<Map<String, Object>>>cast(JsonUtils.parseObject(param.getValue(), List.class));
        res.forEach(r -> {
            ArrayNode valueArrayNode1 = nodeFactory.arrayNode();
            for (Map.Entry<String, Object> rr : r.entrySet()) {
                if (StringUtils.equals(rr.getKey(), "id")) {
                    valueArrayNode1.add(convertId(rr.getKey(), ObjectUtils.<Integer>cast(rr.getValue()).longValue()));
                } else {
                    valueArrayNode1.add(convertObject(rr.getKey(), String.valueOf(rr.getValue())));
                }
            }
            Map<String, Object> a = new HashMap<>();
            a.put("id", UUID.randomUUID().toString());
            a.put("type", "Object");
            a.put("from", "Expand");
            a.put("value", valueArrayNode1);
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, Object> entry : a.entrySet()) {
                if (StringUtils.equals(entry.getKey(), "value")) {
                    mapNode.put(entry.getKey(), ObjectUtils.<JsonNode>cast(entry.getValue()));
                } else {
                    mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
                }
            }
            valueArrayNode.add(mapNode);
        });
        ObjectUtils.<ObjectNode>cast(node).set("value", valueArrayNode);
    }

    private void parseOtherMemoryType(Map<String, Object> res, ArrayNode valueArrayNode) {
        for (Map.Entry<String, Object> resEntry : res.entrySet()) {
            if (Objects.equals(resEntry.getKey(), AippConst.MEMORY_SWITCH_KEY)) {
                this.checkEntryType(resEntry, Boolean.class);
                valueArrayNode.add(this.convertMemorySwitch(resEntry.getKey(), ObjectUtils.cast(resEntry.getValue())));
            } else {
                valueArrayNode.add(this.convertObject(resEntry.getKey(), String.valueOf(resEntry.getValue())));
            }
        }
    }

    private void parseUserSelect(Map<String, Object> res, ArrayNode valueArrayNode) {
        for (Map.Entry<String, Object> resEntry : res.entrySet()) {
            if (Objects.equals(resEntry.getKey(), AippConst.MEMORY_SWITCH_KEY)) {
                this.checkEntryType(resEntry, Boolean.class);
                valueArrayNode.add(this.convertMemorySwitch(resEntry.getKey(), ObjectUtils.cast(resEntry.getValue())));
            } else if (Objects.equals(resEntry.getKey(), "value")) {
                valueArrayNode.add(this.convertValueForUserSelect(resEntry.getKey(),
                        String.valueOf(resEntry.getValue())));
            } else {
                valueArrayNode.add(this.convertObject(resEntry.getKey(), String.valueOf(resEntry.getValue())));
            }
        }
    }

    private ArrayNode convertList(String value) {
        String[] res = JsonUtils.parseObject(value, String[].class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        List<Map<String, String>> re = Arrays.stream(res).map(this::convert).collect(Collectors.toList());

        ArrayNode valueArrayNode = nodeFactory.arrayNode();
        for (Map<String, String> rr : re) {
            ObjectNode mapNode = nodeFactory.objectNode();
            for (Map.Entry<String, String> entry : rr.entrySet()) {
                mapNode.put(entry.getKey(), entry.getValue());
            }
            valueArrayNode.add(mapNode);
        }
        return valueArrayNode;
    }

    private Map<String, String> convert(String value) {
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        return map;
    }

    private ObjectNode convertObject(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    private ObjectNode convertId(String key, Long value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", "String");
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "value")) {
                mapNode.put(entry.getKey(), ObjectUtils.<Long>cast(entry.getValue()));
            } else {
                mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
            }
        }
        return mapNode;
    }

    private ObjectNode convertMemorySwitch(String key, Boolean isOpenSwitch) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, Object> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "Input");
        map.put("type", "Boolean");
        map.put("value", isOpenSwitch);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (StringUtils.equals(entry.getKey(), "value")) {
                this.checkEntryType(entry, Boolean.class);
                mapNode.put(entry.getKey(), ObjectUtils.<Boolean>cast(entry.getValue()));
            } else {
                this.checkEntryType(entry, String.class);
                mapNode.put(entry.getKey(), ObjectUtils.<String>cast(entry.getValue()));
            }
        }
        return mapNode;
    }

    private ObjectNode convertValueForUserSelect(String key, String value) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        Map<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("name", key);
        map.put("from", "input");
        map.put("type", StringUtils.EMPTY);
        map.put("value", value);
        ObjectNode mapNode = nodeFactory.objectNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapNode.put(entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    private void checkEntryType(Map.Entry<String, Object> entry, Class<?> clazz) {
        if (!clazz.isInstance(entry.getValue())) {
            throw new AippException(UPDATE_APP_CONFIGURATION_FAILED, entry.getValue().getClass().getName());
        }
    }

    private void updateConfigByGlowGraphAppearance(String appearance, List<AppBuilderFormProperty> formProperties,
            AppBuilderConfig config) {
        // 这个map {nodeId:{name:value}}
        Map<String, Map<String, Object>> nodeIdToJadeConfigMap = this.getJadeConfigsFromAppearance(appearance);
        List<AppBuilderConfigProperty> configProperties = config.getConfigProperties();
        Map<String, AppBuilderFormProperty> idToFormPropertyMap =
                formProperties.stream().collect(Collectors.toMap(AppBuilderFormProperty::getId, Function.identity()));
        // 这样写避免循环的时候去查询数据库获取configProperty对应的formProperty
        for (AppBuilderConfigProperty cp : configProperties) {
            if (!idToFormPropertyMap.containsKey(cp.getFormPropertyId())) {
                // 2024/4/29 0029 这里可能拿到null，这里暂时不知道什么问题，先把拿不到的跳过
                continue;
            }
            cp.setFormProperty(idToFormPropertyMap.get(cp.getFormPropertyId()));
            String nodeId = cp.getNodeId();
            if (StringUtils.isBlank(nodeId)) {
                // 这里排除掉空nodeId的config
                continue;
            }
            Map<String, Object> nameValue = nodeIdToJadeConfigMap.get(nodeId);
            AppBuilderFormProperty formProperty = cp.getFormProperty();
            if (MapUtils.isEmpty(nameValue)) {
                // 2024/4/29 0029 暂时先不删除了，仅修改现存的内容
                continue;
            } else {
                if (nameValue.get(formProperty.getName()) == null) {
                    continue;
                }
            }
            if ("model".equals(formProperty.getName())) {
                if (nameValue.get("accessInfo") == null) {
                    formProperty.setDefaultValue(nameValue.get(formProperty.getName()));
                } else {
                    formProperty.setDefaultValue(ObjectUtils.<Map<String, String>>cast(nameValue.get("accessInfo"))
                            .get("serviceName"));
                }
            } else {
                formProperty.setDefaultValue(nameValue.get(formProperty.getName()));
            }
            // 更新
            config.getFormPropertyRepository().updateOne(formProperty);
        }
    }

    private Map<String, Map<String, Object>> getJadeConfigsFromAppearance(String appearance) {
        JSONArray pages = JSONObject.parseObject(appearance).getJSONArray("pages");
        // 这个map {nodeId:{name:value}}
        Map<String, Map<String, Object>> nodeIdToJadeConfigMap = new HashMap<>();
        for (int i = 0; i < pages.size(); i++) {
            JSONObject page = pages.getJSONObject(i);
            JSONArray shapes = page.getJSONArray("shapes");
            for (int j = 0; j < shapes.size(); j++) {
                JSONObject node = shapes.getJSONObject(j);
                String nodeId = node.getString("id");
                JSONArray inputParams = this.extractingInputParams(node);
                if (Objects.isNull(inputParams)) {
                    continue;
                }
                nodeIdToJadeConfigMap.put(nodeId, this.extractingExpandObject(inputParams));
            }
        }
        return nodeIdToJadeConfigMap;
    }

    private JSONArray extractingInputParams(JSONObject node) {
        String nodeType = node.getString("type");
        if (StringUtils.equalsIgnoreCase("startNodeStart", nodeType)) {
            return node.getJSONObject("flowMeta").getJSONArray("inputParams");
        } else if (StringUtils.equalsIgnoreCase("evaluationStartNodeStart", nodeType)) {
            return new JSONArray();
        } else if (StringUtils.equalsIgnoreCase("endNodeEnd", nodeType) || StringUtils.equalsIgnoreCase(
                "evaluationEndNodeEnd",
                nodeType)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("jadeEvent", nodeType)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("conditionNodeCondition", nodeType)) {
            return null;
        } else if (StringUtils.equalsIgnoreCase("manualCheckNodeState", nodeType) || StringUtils.equalsIgnoreCase(
                "intelligentFormNodeState",
                nodeType)) {
            return node.getJSONObject("flowMeta")
                    .getJSONObject("task")
                    .getJSONObject("converter")
                    .getJSONObject("entity")
                    .getJSONArray("inputParams");
        } else {
            return node.getJSONObject("flowMeta")
                    .getJSONObject("jober")
                    .getJSONObject("converter")
                    .getJSONObject("entity")
                    .getJSONArray("inputParams");
        }
    }

    // 如果type是Array，那么调用这个方法获取一个List<Object>
    private List<Object> extractingExpandArray(JSONArray value) {
        List<Object> result = new ArrayList<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (StringUtils.equalsIgnoreCase("Input", jsonObject.getString("from"))) {
                result.add(jsonObject.get("value"));
                continue;
            }
            if (StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"))) {
                this.handleExpandType(jsonObject, result);
            }
        }
        return result;
    }

    private void handleExpandType(JSONObject jsonObject, List<Object> result) {
        if (StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"))) {
            List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
            result.add(array);
            return;
        }
        if (StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"))) {
            Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
            if (MapUtils.isNotEmpty(map)) {
                result.add(map);
            }
        }
    }

    // 如果type是Object，那么调用这个方法获取一个Map<String, Object>
    private Map<String, Object> extractingExpandObject(JSONArray value) {
        Map<String, Object> result = new HashMap<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (StringUtils.equalsIgnoreCase("Input", jsonObject.getString("from"))) {
                result.put(jsonObject.getString("name"), jsonObject.get("value"));
                continue;
            }
            if (StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"))) {
                this.handleExpandType(jsonObject, result);
            }
        }
        return result;
    }

    private void handleExpandType(JSONObject jsonObject, Map<String, Object> result) {
        if (StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"))) {
            List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
            result.put(jsonObject.getString("name"), array);
            return;
        }
        if (StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"))) {
            Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
            if (MapUtils.isNotEmpty(map)) {
                result.put(jsonObject.getString("name"), map);
            }
        }
    }

    private String[] getFirstModelInfo(OperationContext context) {
        // TODO: 缩小异常捕获的范围。
        try {
            ModelListDto modelList = this.aippModelCenter.fetchModelList(AippConst.CHAT_MODEL_TYPE, null, context);
            if (modelList != null && modelList.getModels() != null && !modelList.getModels().isEmpty()) {
                ModelAccessInfo firstModel = modelList.getModels().get(0);
                return new String[]{firstModel.getServiceName(), firstModel.getTag()};
            } else {
                return new String[]{StringUtils.EMPTY, StringUtils.EMPTY};
            }
        } catch (Exception e) {
            log.error("Failed to get first model information.", e);
            return new String[]{StringUtils.EMPTY, StringUtils.EMPTY};
        }
    }

    private String generateUniquePath() {
        String path;
        int retryTimes = RETRY_PATH_GENERATION_TIMES;
        do {
            path = RandomPathUtils.generateRandomString(PATH_LENGTH);
            if (!this.appRepository.checkPathExists(path)) {
                return path;
            }
            log.warn("Path already exists, retrying... {} times left", retryTimes - 1);
        } while (retryTimes-- > 0);

        log.error("Failed to generate a unique path for app after {} retries.", RETRY_PATH_GENERATION_TIMES);
        throw new AippException(UPDATE_APP_CONFIGURATION_FAILED);
    }

    private String getAttribute(Map<String, Object> attributes, String name) {
        // 增加保护，之前创建的应用部分前端传入了null, 如果再新建版本则导致新版本出现字符串"null"
        Object value = attributes.get(name);
        return value == null ? StringUtils.EMPTY : String.valueOf(value);
    }
}
