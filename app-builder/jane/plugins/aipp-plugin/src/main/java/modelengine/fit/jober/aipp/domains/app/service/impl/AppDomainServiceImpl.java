/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.app.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domains.app.App;
import modelengine.fit.jober.aipp.domains.app.AppFactory;
import modelengine.fit.jober.aipp.domains.app.service.AppDomainService;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fitframework.annotation.Value;
import modelengine.jade.app.engine.base.service.UsrAppCollectionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;

import java.util.Collections;
import java.util.Map;

/**
 * 应用服务实现类.
 *
 * @author 张越
 * @since 2025-01-14
 */
@Component
public class AppDomainServiceImpl implements AppDomainService {
    private static final Logger log = Logger.get(AppDomainServiceImpl.class);

    private final AppFactory appFactory;
    private final AppVersionService appVersionService;
    private final UploadedFileManageService uploadedFileManageService;
    private final UsrAppCollectionService usrAppCollectionService;
    private final ConverterFactory converterFactory;
    private final String contextRoot;

    public AppDomainServiceImpl(AppFactory appFactory, AppVersionService appVersionService,
            UploadedFileManageService uploadedFileManageService, UsrAppCollectionService usrAppCollectionService,
            ConverterFactory converterFactory, @Value("${app-engine.contextRoot}") String contextRoot) {
        this.appFactory = appFactory;
        this.appVersionService = appVersionService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.usrAppCollectionService = usrAppCollectionService;
        this.converterFactory = converterFactory;
        this.contextRoot = contextRoot;
    }

    @Override
    @Transactional
    public void deleteByAppId(String appId, OperationContext context) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        String appSuiteId = appVersion.getData().getAppSuiteId();
        App app = this.appFactory.create(appSuiteId);
        app.delete(context);
        this.uploadedFileManageService.cleanAippFiles(Collections.singletonList(appId));
        this.usrAppCollectionService.deleteByAppId(appId);
    }

    @Override
    @Transactional
    public AppBuilderAppDto importApp(String appConfig, OperationContext context) {
        try {
            AppExportDto appExportDto = new ObjectMapper().readValue(appConfig, AppExportDto.class);
            String suiteId = Entities.generateId();
            App app = this.appFactory.create(suiteId);
            AppVersion appVersion = app.importData(appExportDto, this.contextRoot, context);
            return this.converterFactory.convert(appVersion, AppBuilderAppDto.class);
        } catch (JsonProcessingException e) {
            log.error("Imported config file is not json", e);
            throw new AippException(AippErrCode.IMPORT_CONFIG_NOT_JSON, e.getLocation().getLineNr(),
                    e.getLocation().getColumnNr());
        }
    }

    @Override
    public AppExportDto exportApp(String appId, Map<String, String> exportMeta, OperationContext context) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        return appVersion.export(context, exportMeta);
    }
}
