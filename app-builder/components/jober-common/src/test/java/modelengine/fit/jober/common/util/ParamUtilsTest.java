/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.util;

import modelengine.fit.jober.entity.File;
import modelengine.fit.jober.entity.FileDeclaration;
import modelengine.fit.jober.entity.OperationContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link ParamUtils}对应测试类。
 *
 * @author 姚江
 * @since 2023-11-01 10:21
 */
@ExtendWith(MockitoExtension.class)
public class ParamUtilsTest {
    String tenantId = "testId";

    String operator = "TEST.";

    String operatorIp = "0.0.0.0";

    String sourcePlatform = "UT";

    String name = "fileName";

    byte[] content = new byte[0];

    @Nested
    @DisplayName("测试OperationContext从entity包转换为util包")
    class EntityContextConvertUtilContext {
        @Test
        @DisplayName("测试非null的context转换")
        void testSourceNoNull() {
            // given
            modelengine.fit.jane.task.util.OperationContext expected =
                    modelengine.fit.jane.task.util.OperationContext.custom()
                    .operator(operator)
                    .tenantId(tenantId)
                    .operatorIp(operatorIp)
                    .sourcePlatform(sourcePlatform)
                    .build();

            OperationContext source = new OperationContext();
            source.setOperator(operator);
            source.setTenantId(tenantId);
            source.setOperatorIp(operatorIp);
            source.setSourcePlatform(sourcePlatform);

            // when
            modelengine.fit.jane.task.util.OperationContext actual = ParamUtils.convertOperationContext(source);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        @DisplayName("测试null的context转换")
        void testSourceNull() {
            // given
            modelengine.fit.jane.task.util.OperationContext expected =
                    modelengine.fit.jane.task.util.OperationContext.custom().build();

            // when
            OperationContext context = null;
            modelengine.fit.jane.task.util.OperationContext actual = ParamUtils.convertOperationContext(context);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("测试OperationContext从util包转换为entity包")
    class UtilContextConvertEntityContext {
        @Test
        @DisplayName("测试非null的context转换")
        void testSourceNoNull() {
            // given
            modelengine.fit.jane.task.util.OperationContext source =
                    modelengine.fit.jane.task.util.OperationContext.custom()
                    .operator(operator)
                    .tenantId(tenantId)
                    .operatorIp(operatorIp)
                    .sourcePlatform(sourcePlatform)
                    .build();

            OperationContext expected = new OperationContext();
            expected.setOperator(operator);
            expected.setTenantId(tenantId);
            expected.setOperatorIp(operatorIp);
            expected.setSourcePlatform(sourcePlatform);

            // when
            OperationContext actual = ParamUtils.convertOperationContext(source);

            // then
            Assertions.assertEquals(expected.getOperator(), actual.getOperator());
            Assertions.assertEquals(expected.getSourcePlatform(), actual.getSourcePlatform());
            Assertions.assertEquals(expected.getOperatorIp(), actual.getOperatorIp());
            Assertions.assertEquals(expected.getTenantId(), actual.getTenantId());
        }

        @Test
        @DisplayName("测试null的context转换")
        void testSourceNull() {

            // when
            modelengine.fit.jane.task.util.OperationContext context = null;
            OperationContext actual = ParamUtils.convertOperationContext(context);

            // then
            Assertions.assertNull(actual.getTenantId());
            Assertions.assertNull(actual.getOperatorIp());
            Assertions.assertNull(actual.getOperator());
            Assertions.assertNull(actual.getSourcePlatform());
        }
    }

    @Test
    @DisplayName("测试file从entity包转换为domain包")
    void testEntityFileConvertDomainFile() {
        // given
        modelengine.fit.jober.entity.File file = new File();
        file.setName(name);
        file.setContent(content);

        modelengine.fit.jane.task.domain.File expected = modelengine.fit.jane.task.domain.File.custom()
                .name(name)
                .content(content)
                .build();

        // when
        modelengine.fit.jane.task.domain.File actual = ParamUtils.convertFile(file);

        // then
        Assertions.assertEquals(expected.name(), actual.name());
        Assertions.assertEquals(expected.content(), actual.content());
    }

    @Test
    @DisplayName("测试declaration从entity包转换为domain包")
    void testEntityDeclarationConvertDomainDeclaration() {
        // given
        FileDeclaration fileDeclaration = new FileDeclaration();
        fileDeclaration.setName(name);
        fileDeclaration.setContent(content);

        modelengine.fit.jane.task.domain.File.Declaration expected =
                modelengine.fit.jane.task.domain.File.Declaration.custom().name(name).content(content).build();

        // when
        modelengine.fit.jane.task.domain.File.Declaration actual = ParamUtils.convertDeclaration(fileDeclaration);

        //then
        Assertions.assertEquals(expected.name(), actual.name());
        Assertions.assertEquals(expected.content(), actual.content());
    }

    @Test
    @DisplayName("测试declaration从domain包转换为entity包")
    void testDomainDeclarationConvertEntityDeclaration() {
        // given
        modelengine.fit.jane.task.domain.File.Declaration declaration =
                modelengine.fit.jane.task.domain.File.Declaration.custom().name(name).content(content).build();

        FileDeclaration expected = new FileDeclaration();
        expected.setName(name);
        expected.setContent(content);

        // when
        FileDeclaration actual = ParamUtils.convertDeclaration(declaration);

        // then
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getContent(), actual.getContent());
    }
}
