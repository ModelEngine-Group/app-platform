/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.aop;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.service.DatabaseFieldLocaleService;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 表示 {@link LocaleAspect} 的单元测试。
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@DisplayName("测试LocaleAspect")
@MockitoSettings(strictness = Strictness.LENIENT)
public class LocaleAspectTest {
    private DatabaseFieldLocaleService localeService;
    private ProceedingJoinPoint pjp;
    private MockedStatic<UserContextHolder> opContextHolderMock;

    @BeforeEach
    void setUp() {
        this.localeService = mock(DatabaseFieldLocaleService.class);
        this.pjp = mock(ProceedingJoinPoint.class);
        this.opContextHolderMock = mockStatic(UserContextHolder.class);
        opContextHolderMock.when(UserContextHolder::get)
                .thenReturn(new UserContext("Jane", "127.0.0.1", "en"));
    }

    @AfterEach
    void teardown() {
        this.opContextHolderMock.close();
    }

    @Test
    @DisplayName("测试LocaleAspect可以对变量的String内容进行国际化替换")
    void shouldSuccessWhenLocalize() throws Throwable {
        AppBuilderAppPo appBuilderAppPo = new AppBuilderAppPo();
        appBuilderAppPo.setName("i18n_appBuilder_{name}");
        when(pjp.proceed()).thenReturn(appBuilderAppPo);
        when(localeService.getLocaleMessage("name", Locale.ENGLISH)).thenReturn("zhangSan");
        LocaleAspect localeAspect = new LocaleAspect(localeService);
        Object object = localeAspect.localize(pjp);
        AppBuilderAppPo resultPo;
        if (object instanceof AppBuilderAppPo) {
            resultPo = (AppBuilderAppPo) object;
            assertThat(resultPo.getName()).isEqualTo("zhangSan");
        }
    }

    @Test
    @DisplayName("测试LocaleAspect可以对变量的JsonString内容进行国际化替换")
    void shouldSuccessWhenLocalizeJsonString() throws Throwable {
        AppBuilderAppPo appBuilderAppPo = new AppBuilderAppPo();
        appBuilderAppPo.setAttributes("{\"description:\":\"this is llm description\", \"icon\": \"http://ab\", "
                + "\"greeting\": \"hello\", \"app_type\": \"i18n_appBuilder_{interview_assistant}\"}");
        when(pjp.proceed()).thenReturn(appBuilderAppPo);
        when(localeService.getLocaleMessage("interview_assistant", Locale.ENGLISH)).thenReturn("interview_assistant");
        LocaleAspect localeAspect = new LocaleAspect(localeService);
        Object object = localeAspect.localize(pjp);
        AppBuilderAppPo resultPo;
        if (object instanceof AppBuilderAppPo) {
            resultPo = (AppBuilderAppPo) object;
            assertThat(resultPo.getAttributes())
                    .isEqualTo("{\"description:\":\"this is llm description\", \"icon\": \"http://ab\", \"greeting\": "
                            + "\"hello\", \"app_type\": \"interview_assistant\"}");
        }
    }

    @Test
    @DisplayName("测试LocaleAspect可以对列表类型变量的内容进行国际化替换")
    void shouldSuccessWhenLocalizeList() throws Throwable {
        List<AppBuilderAppPo> list = new ArrayList<>();
        AppBuilderAppPo appBuilderAppPo1 = new AppBuilderAppPo();
        appBuilderAppPo1.setName("i18n_appBuilder_{name}");
        AppBuilderAppPo appBuilderAppPo2 = new AppBuilderAppPo();
        appBuilderAppPo2.setAttributes("{\"description:\":\"this is llm description\", \"icon\": \"http://ab\", "
                + "\"greeting\": \"hello\", \"app_type\": \"i18n_appBuilder_{interview_assistant}\"}");
        list.add(appBuilderAppPo1);
        list.add(appBuilderAppPo2);
        when(pjp.proceed()).thenReturn(list);
        when(localeService.getLocaleMessage("name", Locale.ENGLISH)).thenReturn("zhangSan");
        when(localeService.getLocaleMessage("interview_assistant", Locale.ENGLISH)).thenReturn("interview_assistant");
        LocaleAspect localeAspect = new LocaleAspect(localeService);
        Object result = localeAspect.localize(pjp);
        assertThat(result).isInstanceOf(List.class);
        List<Object> resultList = (List<Object>) result;
        assertThat(resultList.get(0)).isInstanceOf(AppBuilderAppPo.class);
        AppBuilderAppPo resultPo1;
        AppBuilderAppPo resultPo2;
        if (resultList.get(0) instanceof AppBuilderAppPo) {
            resultPo1 = (AppBuilderAppPo) resultList.get(0);
            assertThat(resultPo1.getName()).isEqualTo("zhangSan");
        }
        if (resultList.get(1) instanceof AppBuilderAppPo) {
            resultPo2 = (AppBuilderAppPo) resultList.get(1);
            assertThat(resultPo2.getAttributes())
                    .isEqualTo("{\"description:\":\"this is llm description\", \"icon\": \"http://ab\", \"greeting\": "
                            + "\"hello\", \"app_type\": \"interview_assistant\"}");
        }
    }
}
