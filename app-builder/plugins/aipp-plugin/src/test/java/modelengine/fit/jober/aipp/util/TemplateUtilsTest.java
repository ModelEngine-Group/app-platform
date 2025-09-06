/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 应用模板的工具类的测试类。
 *
 * @author 方誉州
 * @since 2025-01-16
 */
public class TemplateUtilsTest {
    @Test
    @DisplayName("测试 AppBuilderApp 类转换为 AppTemplate")
    void testAppBuilderAppConvertToAppTemplate() {
        AppBuilderApp testApp = AppBuilderApp.builder().id("123456789").updateBy("jade").version("1.1.1").build();
        AppTemplate template = TemplateUtils.convertToAppTemplate(testApp);
        assertThat(template).extracting(AppTemplate::getId,
                AppTemplate::getLike,
                AppTemplate::getUpdateBy,
                AppTemplate::getVersion).containsExactly("123456789", 0L, null, "1.0.0");
    }

    @Test
    @DisplayName("测试 AppTemplate 转换为 TemplateInfoDto")
    void testAppTemplateConvertToTemplateInfoDto() {
        AppTemplate testTemplate = AppTemplate.builder()
                .id("123456789")
                .attributes(MapBuilder.<String, Object>get().put(TemplateUtils.ICON_ATTR_KEY, "/path/to/icon").build())
                .build();
        TemplateInfoDto info = TemplateUtils.convertToTemplateDto(testTemplate);
        assertThat(info).extracting(TemplateInfoDto::getId, TemplateInfoDto::getIcon)
                .containsExactly("123456789", "/path/to/icon");
    }

    @Test
    @DisplayName("测试 AppTemplate 转换为 AppBuilderApp")
    void testAppTemplateConvertToAppBuilderApp() {
        AppTemplate testTemplate = AppTemplate.builder().id("123456789").version("1.1.1").build();
        AppBuilderApp app = TemplateUtils.convertToAppBuilderApp(testTemplate);
        assertThat(app).extracting(AppBuilderApp::getId,
                AppBuilderApp::getVersion,
                AppBuilderApp::getState,
                AppBuilderApp::getType).containsExactly("123456789", "1.0.0", "inactive", "app");
    }
}
