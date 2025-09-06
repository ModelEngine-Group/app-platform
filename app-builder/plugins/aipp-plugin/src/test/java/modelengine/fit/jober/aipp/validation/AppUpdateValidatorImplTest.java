/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.validation;

import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.validation.impl.AppUpdateValidatorImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link AppUpdateValidatorImpl} 的测试类
 *
 * @author 姚江
 * @since 2024-07-17
 */
@ExtendWith(MockitoExtension.class)
public class AppUpdateValidatorImplTest {
    @Mock
    AppBuilderAppFactory factory;

    AppUpdateValidator appUpdateValidator;

    @BeforeEach
    void before() {
        appUpdateValidator = new AppUpdateValidatorImpl(factory);
    }

    @Test
    @DisplayName("测试validate方法")
    void testValidate() {
        Mockito.when(factory.create(Mockito.eq("id1"))).thenReturn(AppBuilderApp.builder().state("inactive").build());
        Assertions.assertDoesNotThrow(() -> appUpdateValidator.validate("id1"));
    }

    @Test
    @DisplayName("测试validate方法：app已发布")
    void testValidateWithPublished() {
        Mockito.when(factory.create(Mockito.eq("id1"))).thenReturn(AppBuilderApp.builder().state("active").build());
        AippException ex = Assertions.assertThrows(AippException.class, () -> appUpdateValidator.validate("id1"));
        Assertions.assertEquals(90002910, ex.getCode());
    }
}
