/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.app;

import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.enums.AppState.INACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.converters.impl.AppVersionToAppDtoConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domains.app.service.AppDomainService;
import modelengine.fit.jober.aipp.domains.app.service.impl.AppDomainServiceImpl;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.base.service.UserAppCollectionService;
import modelengine.jade.store.service.AppService;
import modelengine.fitframework.util.IoUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@link AppService。}
 *
 * @author 张越
 * @since 2025-02-18
 */
public class AppServiceTest {
    private AppDomainService appDomainService;
    private AppFactory appFactory;
    private AppVersionService appVersionService;
    private UploadedFileManageService uploadedFileManageService;
    private UserAppCollectionService userAppCollectionService;
    private IconConverter iconConverter;

    @BeforeEach
    public void setUp() {
        this.appFactory = mock(AppFactory.class);
        this.appVersionService = mock(AppVersionService.class);
        this.uploadedFileManageService = mock(UploadedFileManageService.class);
        this.userAppCollectionService = mock(UserAppCollectionService.class);
        this.iconConverter = mock(IconConverter.class);
        ConverterFactory converterFactory = new ConverterFactory(List.of(new AppVersionToAppDtoConverter(iconConverter)));
        this.appDomainService = new AppDomainServiceImpl(this.appFactory, this.appVersionService,
                this.uploadedFileManageService, this.userAppCollectionService, converterFactory, StringUtils.EMPTY);
    }

    @Test
    @DisplayName("测试 getVersions")
    public void testGetVersions() {
        // given.
        AppVersion appVersion = mock(AppVersion.class);
        AppBuilderAppPo data = AppBuilderAppPo.builder().appSuiteId("app_1").build();
        when(this.appVersionService.retrieval(anyString())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(data);

        App app = mock(App.class);
        when(this.appFactory.create(anyString())).thenReturn(app);
        doNothing().when(app).delete(any());

        doNothing().when(this.uploadedFileManageService).cleanAippFiles(any());
        doNothing().when(this.userAppCollectionService).deleteByAppId(anyString());

        // when.
        this.appDomainService.deleteByAppId("app_version_1", new OperationContext());

        // then.
        verify(app, times(1)).delete(any());
        verify(this.uploadedFileManageService, times(1)).cleanAippFiles(
                argThat(strings -> strings.size() == 1 && strings.get(0).equals("app_version_1")));
        verify(this.userAppCollectionService, times(1)).deleteByAppId(eq("app_version_1"));
    }

    @Test
    @DisplayName("测试 import导入")
    public void testImportApp() throws IOException {
        // given.
        String appearance = IoUtils.content(AppServiceTest.class, "/appearance.txt");

        App app = mock(App.class);
        when(this.appFactory.create(anyString())).thenReturn(app);

        AppVersion appVersion = mock(AppVersion.class);
        when(app.importData(any(), eq(""), any())).thenReturn(appVersion);
        when(appVersion.getData()).thenReturn(AppBuilderAppPo.builder()
                .id("app_version_1")
                .name("myApp")
                .type(AppTypeEnum.APP.code())
                .state(INACTIVE.getName())
                .appType(NORMAL.name())
                .version("0.0.1")
                .appCategory(AppCategory.APP.getCategory())
                .updateBy("zy")
                .createBy("zy")
                .build());

        when(appVersion.getConfig()).thenReturn(
                AppBuilderConfig.builder().form(AppBuilderForm.builder().appearance(new HashMap<>()).build()).build());
        when(appVersion.getFlowGraph()).thenReturn(AppBuilderFlowGraph.builder().appearance(appearance).build());
        when(appVersion.getFormProperties()).thenReturn(Collections.emptyList());
        when(appVersion.getAttributes()).thenReturn(new HashMap<>());

        // when.
        String appConfig = IoUtils.content(AppServiceTest.class, "/export-data.txt");
        AppBuilderAppDto dto = this.appDomainService.importApp(appConfig, new OperationContext());

        // then.
        assertEquals("app_version_1", dto.getId());
        assertEquals("myApp", dto.getName());
        assertEquals(AppTypeEnum.APP.code(), dto.getType());
        assertEquals(INACTIVE.getName(), dto.getState());
        assertEquals(NORMAL.name(), dto.getAppType());
        assertEquals("0.0.1", dto.getVersion());
        assertEquals(AppCategory.APP.getCategory(), dto.getAppCategory());
        assertEquals("zy", dto.getUpdateBy());
        assertEquals("zy", dto.getCreateBy());
    }

    @Test
    @DisplayName("测试 export导出")
    public void testExportApp() {
        // given.
        AppVersion appVersion = mock(AppVersion.class);
        when(this.appVersionService.retrieval(anyString())).thenReturn(appVersion);

        AppExportDto dto = AppExportDto.builder().build();
        when(appVersion.export(any(), any())).thenReturn(dto);

        // when.
        AppExportDto result = this.appDomainService.exportApp("app_version_1", new HashMap<>(), new OperationContext());

        // then.
        assertEquals(dto, result);
    }
}
