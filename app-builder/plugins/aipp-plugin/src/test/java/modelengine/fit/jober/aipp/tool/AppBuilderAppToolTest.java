/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import static modelengine.fit.jober.aipp.constants.AippConst.DEFAULT_DATE_TIME_FORMAT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.tool.impl.AppBuilderAppToolImpl;
import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 为 {@link AppBuilderAppTool} 及其实现 {@link AppBuilderAppToolImpl} 提供单元测试
 *
 * @author 姚江
 * @since 2024-08-06
 */
@ExtendWith(MockitoExtension.class)
public class AppBuilderAppToolTest {
    private AppBuilderAppTool appBuilderAppTool;

    @Mock
    private AppBuilderAppService appService;

    @Mock
    private AppVersionService appVersionService;

    private ConverterFactory converterFactory;

    private final String appEngineUrl = "localhost";

    @BeforeEach
    void before() {
        this.converterFactory = mock(ConverterFactory.class);
        ObjectSerializer serializer =
                new AippJacksonObjectSerializer(DEFAULT_DATE_TIME_FORMAT);
        this.appBuilderAppTool = new AppBuilderAppToolImpl(appService, serializer, this.appEngineUrl,
                this.appVersionService, this.converterFactory);
    }

    @Test
    @DisplayName("创建app方法测试")
    void testCreateApp() {
        // given.
        AppVersion mockAppVersion = Mockito.mock(AppVersion.class);
        Mockito.when(this.appVersionService.create(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(mockAppVersion);
        AppBuilderAppDto appDto = AppBuilderAppDto.builder().id("id").build();
        when(this.converterFactory.convert(any(), any())).thenReturn(appDto);

        // when.
        String s = Assertions.assertDoesNotThrow(() -> this.appBuilderAppTool.createApp("defaultErrorInfo", "me"));

        // then.
        Assertions.assertTrue(s.contains(this.appEngineUrl));
        Assertions.assertTrue(s.endsWith("id"));
    }
}
