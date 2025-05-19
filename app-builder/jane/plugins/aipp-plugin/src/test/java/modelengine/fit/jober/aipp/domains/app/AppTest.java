/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.app;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.PluginToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@link App} 的测试类。
 *
 * @author 张越
 * @since 2025-02-18
 */
public class AppTest {
    private AppFactory factory;
    private AppVersionService appVersionService;
    private AppBuilderConfigRepository appBuilderConfigRepository;
    private AppBuilderFlowGraphRepository appBuilderFlowGraphRepository;
    private AppBuilderFormPropertyRepository formPropertyRepository;
    private AippLogMapper aippLogMapper;
    private AppService appService;
    private AippChatMapper aippChatMapper;
    private AppVersionFactory appVersionFactory;
    private PluginToolService pluginToolService;
    private PluginService pluginService;

    @BeforeEach
    public void setUp() {
        this.appVersionService = mock(AppVersionService.class);
        this.appBuilderConfigRepository = mock(AppBuilderConfigRepository.class);
        this.appBuilderFlowGraphRepository = mock(AppBuilderFlowGraphRepository.class);
        this.formPropertyRepository = mock(AppBuilderFormPropertyRepository.class);
        this.aippLogMapper = mock(AippLogMapper.class);
        this.appService = mock(AppService.class);
        this.aippChatMapper = mock(AippChatMapper.class);
        AppVersionRepository appVersionRepository = mock(AppVersionRepository.class);
        this.appVersionFactory = mock(AppVersionFactory.class);
        this.pluginToolService = mock(PluginToolService.class);
        this.pluginService = mock(PluginService.class);

        this.factory = new AppFactory(this.appVersionService, this.appBuilderConfigRepository,
                this.appBuilderFlowGraphRepository, this.formPropertyRepository, this.aippLogMapper, this.appService,
                this.aippChatMapper,
                appVersionRepository,
                this.appVersionFactory,
                new HashMap<>(),
                this.pluginToolService,
                this.pluginService);
    }

    @Test
    @DisplayName("测试 getVersions")
    public void testGetVersions() {
        // given.
        App app = this.factory.create("app_1");
        when(this.appVersionService.getByAppSuiteId(anyString())).thenReturn(List.of(mock(AppVersion.class)));

        // when.
        List<AppVersion> versions = app.getVersions();

        // then.
        assertEquals(1, versions.size());
        verify(this.appVersionService, times(1)).getByAppSuiteId(eq("app_1"));
    }

    @Test
    @DisplayName("测试 export 异常")
    public void testExportException() {
        // given.
        App app = this.factory.create("app_1");
        when(this.appVersionService.getByAppSuiteId(anyString())).thenReturn(Collections.emptyList());

        // when.
        AippException exception = assertThrows(AippException.class,
                () -> app.export(new OperationContext()));

        // then.
        assertEquals(APP_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试 export")
    public void testExport() {
        // given.
        App app = this.factory.create("app_1");
        AppVersion appVersion = mock(AppVersion.class);
        AppBuilderAppPo data = AppBuilderAppPo.builder().updateAt(LocalDateTime.now()).build();
        when(appVersion.getData()).thenReturn(data);
        when(this.appVersionService.getByAppSuiteId(anyString())).thenReturn(List.of(appVersion));
        when(appVersion.export(any(), any())).thenReturn(AppExportDto.builder().version("1.0.0").build());

        // when.
        AppExportDto exportDto = app.export(new OperationContext());

        // then.
        assertEquals("1.0.0", exportDto.getVersion());
    }

    @Test
    @DisplayName("测试 import")
    public void testImport() {
        // given.
        App app = this.factory.create("app_1");
        AppVersion appVersion = mock(AppVersion.class);
        AppBuilderAppPo data = AppBuilderAppPo.builder().updateAt(LocalDateTime.now()).name("myApp").build();
        when(appVersion.getData()).thenReturn(data);
        when(this.appVersionFactory.create(any(), any())).thenReturn(appVersion);
        doNothing().when(appVersion).importData(any(), anyString(), eq(StringUtils.EMPTY), any(), any());
        doNothing().when(this.appVersionService).validateAppName(anyString(), any());
        doNothing().when(this.appVersionService).save(any());

        // when.
        AppVersion version = app.importData(AppExportDto.builder().build(), StringUtils.EMPTY, new OperationContext());

        // then.
        assertEquals(appVersion, version);
        verify(this.appVersionService, times(1)).validateAppName(eq("myApp"), any());
        verify(this.appVersionService, times(1)).save(any());
    }

    @Test
    @DisplayName("测试 delete 方法")
    public void testDelete() {
        // given.
        App app = this.factory.create("app_1");
        AppVersion appVersion = mock(AppVersion.class);
        AppBuilderAppPo data = AppBuilderAppPo.builder()
                .updateAt(LocalDateTime.now())
                .name("myApp")
                .configId("config_1")
                .flowGraphId("flow_1")
                .appId("app_version_1")
                .type("app")
                .build();
        when(appVersion.getData()).thenReturn(data);
        when(this.appVersionService.getByAppSuiteId(anyString())).thenReturn(List.of(appVersion));

        AppTask task = mock(AppTask.class);
        when(appVersion.getTasks(any())).thenReturn(List.of(task));
        doNothing().when(task).delete(any());
        when(task.getEntity()).thenReturn(AppTask.asEntity().setUniqueName("unique_name_1"));

        AppTaskInstance instance = mock(AppTaskInstance.class);
        when(task.getInstances(any())).thenReturn(List.of(instance));
        when(instance.getEntity()).thenReturn(AppTaskInstance.asEntity().setInstanceId("instance_1"));

        doNothing().when(this.appBuilderConfigRepository).delete(any());
        doNothing().when(this.appBuilderFlowGraphRepository).delete(any());
        doNothing().when(this.appVersionService).deleteByIds(any());
        doNothing().when(this.formPropertyRepository).deleteByAppIds(any());
        doNothing().when(this.aippLogMapper).deleteByInstanceIds(any());
        when(this.appService.deleteApp(anyString())).thenReturn("");
        when(this.aippChatMapper.deleteAppByAippId(anyString())).thenReturn(1);

        // when.
        app.delete(new OperationContext());

        // then.
        verify(this.appBuilderConfigRepository, times(1)).delete(any());
        verify(this.appBuilderFlowGraphRepository, times(1)).delete(any());
        verify(this.appVersionService, times(1)).deleteByIds(any());
        verify(this.formPropertyRepository, times(1)).deleteByAppIds(any());
        verify(this.aippLogMapper, times(1)).deleteByInstanceIds(any());
        verify(this.appService, times(1)).deleteApp(eq("unique_name_1"));
        verify(this.aippChatMapper, times(1)).deleteAppByAippId(eq("app_1"));
    }
}
