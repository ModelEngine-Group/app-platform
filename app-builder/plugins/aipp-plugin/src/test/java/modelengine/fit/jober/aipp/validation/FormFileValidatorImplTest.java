/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.validation;

import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.validation.impl.FormFileValidatorImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * {@link FormFileValidatorImplTest} 测试类
 *
 * @author 陈潇文
 * @since 2024-12-19
 */
@ExtendWith(MockitoExtension.class)
public class FormFileValidatorImplTest {
    private static final String SCHEMA = "schema";
    private static final String PARAMETERS = "parameters";
    private static final String TYPE = "type";
    private static final String OBJECT = "object";
    private static final String REQUIRED = "required";
    private static final String PROPERTIES = "properties";
    private static final String RETURN = "return";
    private static final String ORDER = "order";
    private static final String ITEMS = "items";
    private static final String ENUM = "enum";

    private FormFileValidator formFileValidator;

    @BeforeEach
    void before() {
        this.formFileValidator = new FormFileValidatorImpl();
    }

    @Test
    @DisplayName("校验schema成功")
    void testValidateSchemaSuccess() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> returns = new HashMap<>();
        Map<String, Object> items1 = new HashMap<>();
        Map<String, Object> items2 = new HashMap<>();

        Map<String, Object> strProp = new HashMap<>();
        strProp.put(TYPE, "string");

        Map<String, Object> arrayProp1 = new HashMap<>();
        items1.put(TYPE, "string");
        arrayProp1.put(TYPE, "array");
        arrayProp1.put(ITEMS, items1);

        Map<String, Object> arrayProp2 = new HashMap<>();
        items2.put(ENUM, Arrays.asList("haha", "heihei"));
        arrayProp2.put(TYPE, "array");
        arrayProp2.put(ITEMS, Arrays.asList(items1, items2));

        properties.put("a", strProp);
        properties.put("b", arrayProp1);
        properties.put("c", arrayProp2);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        returns.put(TYPE, OBJECT);
        returns.put(PROPERTIES, properties);
        schema.put(RETURN, returns);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        Assertions.assertDoesNotThrow(() -> this.formFileValidator.validateSchema(config));
    }

    @Test
    @DisplayName("校验schema失败，缺少schema字段")
    void testValidateSchemaFailedWithoutSchemaKey() {
        Map<String, Object> config = new HashMap<String, Object>();

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002109, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，缺少parameters字段")
    void testValidateSchemaFailedWithoutParametersKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        Map<String, Object> schema = new HashMap<String, Object>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        schema.put(PARAMETERS, parameters);
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002110, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，parameters缺少最外层type字段")
    void testValidateSchemaFailedWithParametersWithoutTypeKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        Map<String, Object> schema = new HashMap<String, Object>();
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002109, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，缺少required字段")
    void testValidateSchemaFailedWithoutRequiredKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        Map<String, Object> schema = new HashMap<String, Object>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(TYPE, OBJECT);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002111, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，缺少properties字段")
    void testValidateSchemaFailedWithoutPropertiesKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        Map<String, Object> schema = new HashMap<String, Object>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002111, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，缺少returns字段")
    void testValidateSchemaFailedWithoutReturnsKey() {
        Map<String, Object> config = new HashMap<String, Object>();
        Map<String, Object> schema = new HashMap<String, Object>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        Map<String, Object> properties = new HashMap<String, Object>();
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002109, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，参数缺少type")
    void testValidateSchemaFailedWhenpropertyWithoutTypeKey() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> strProp = new HashMap<>();

        properties.put("a", strProp);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002112, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，参数type类型不是string")
    void testValidateSchemaFailedWhenpropertyTypeKeyIsNotString() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> strProp = new HashMap<>();
        strProp.put(TYPE, 123);

        properties.put("a", strProp);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002113, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，参数type类型不合规")
    void testValidateSchemaFailedWhenpropertyTypeKeyIllegal() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> strProp = new HashMap<>();
        strProp.put(TYPE, "enum");

        properties.put("a", strProp);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002114, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，array类型缺少type")
    void testValidateSchemaFailedWhenArrayParameterWithoutTypeKey() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> items1 = new HashMap<>();

        Map<String, Object> arrayProp1 = new HashMap<>();
        arrayProp1.put(TYPE, "array");
        arrayProp1.put(ITEMS, items1);

        properties.put("b", arrayProp1);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002112, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，array类型type不是string")
    void testValidateSchemaFailedWhenArrayParameterTypeKeyIsNotString() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> items1 = new HashMap<>();

        Map<String, Object> arrayProp1 = new HashMap<>();
        items1.put(TYPE, 123);
        arrayProp1.put(TYPE, "array");
        arrayProp1.put(ITEMS, items1);

        properties.put("b", arrayProp1);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002115, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，array tuple类型参数配置有误")
    void testValidateSchemaWhenArrayTupleError() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> items1 = new HashMap<>();
        Map<String, Object> items2 = new HashMap<>();

        Map<String, Object> arrayProp2 = new HashMap<>();
        arrayProp2.put(TYPE, "array");
        arrayProp2.put(ITEMS, Arrays.asList(items1, items2));

        properties.put("c", arrayProp2);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002112, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，array tuple类型参数enum配置有误")
    void testValidateSchemaWhenArrayTupleWithEnumError() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> items2 = new HashMap<>();

        Map<String, Object> arrayProp2 = new HashMap<>();
        items2.put(ENUM, 123);
        arrayProp2.put(TYPE, "array");
        arrayProp2.put(ITEMS, Arrays.asList(items2, items2));

        properties.put("c", arrayProp2);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, new ArrayList<>());
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002116, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，required包含参数个数多于入参个数")
    void testValidateSchemaFailedWhenRequiredMoreParam() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> strProp = new HashMap<>();
        strProp.put(TYPE, "string");

        properties.put("a", strProp);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, Arrays.asList("a", "b"));
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002117, ex.getCode());
    }

    @Test
    @DisplayName("校验schema失败，required包含参数不同于入参的参数")
    void testValidateSchemaFailedWhenRequiredExtraParam() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        Map<String, Object> strProp = new HashMap<>();
        strProp.put(TYPE, "string");

        properties.put("a", strProp);
        parameters.put(TYPE, OBJECT);
        parameters.put(REQUIRED, Arrays.asList("b"));
        parameters.put(PROPERTIES, properties);
        schema.put(PARAMETERS, parameters);
        schema.put(ORDER, new ArrayList<>());
        config.put(SCHEMA, schema);

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateSchema(config));
        Assertions.assertEquals(90002118, ex.getCode());
    }

    @Test
    @DisplayName("校验预览图成功")
    void testValidateImgSuccess() throws IOException {
        File file = new File(Files.createTempFile("temp", ".jpg").toString());
        file.deleteOnExit();

        BufferedImage image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "jpg", file);

        Assertions.assertDoesNotThrow(() -> this.formFileValidator.validateImg(file));
    }

    @Test
    @DisplayName("校验组件文件成功")
    void testValidateComponentSuccess() throws IOException {
        File directory = Files.createTempDirectory("temp").toFile();
        directory.deleteOnExit();

        File htmlFile = new File(directory, "index.html");
        File jsFile = new File(directory, "index.js");
        File assets = Files.createDirectory(Paths.get(directory.getCanonicalPath(), "assets")).toFile();

        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write("<html><body>Test HTML</body></html>");
        }

        try (FileWriter writer = new FileWriter(jsFile)) {
            writer.write("<html><body>Test JS</body></html>");
        }

        Assertions.assertDoesNotThrow(() -> this.formFileValidator.validateComponent(directory));
    }

    @Test
    @DisplayName("校验组件文件失败，build文件夹为空")
    void testValidateComponentFailedWithEmptyBuild() throws IOException {
        File directory = Files.createTempDirectory("temp").toFile();
        directory.deleteOnExit();

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateComponent(directory));
        Assertions.assertEquals(90002119, ex.getCode());
    }

    @Test
    @DisplayName("校验组件文件失败，缺少index.html文件")
    void testValidateComponentFailedWithoutIndexFile() throws IOException {
        File directory = Files.createTempDirectory("temp").toFile();
        directory.deleteOnExit();

        File jsFile = new File(directory, "index.js");

        try (FileWriter writer = new FileWriter(jsFile)) {
            writer.write("<html><body>Test JS</body></html>");
        }

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateComponent(directory));
        Assertions.assertEquals(90002120, ex.getCode());
    }

    @Test
    @DisplayName("校验组件文件失败，包含不允许存在的文件")
    void testValidateComponentFailedWithIllegalFile() throws IOException {
        File directory = Files.createTempDirectory("temp").toFile();
        directory.deleteOnExit();

        File htmlFile = new File(directory, "index.html");
        File jsFile = new File(directory, "index.js");
        File shFile = new File(directory, "test.sh");

        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write("<html><body>Test HTML</body></html>");
        }

        try (FileWriter writer = new FileWriter(jsFile)) {
            writer.write("<html><body>Test JS</body></html>");
        }

        try (FileWriter writer = new FileWriter(shFile)) {
            writer.write("test");
        }

        AippException ex =
                Assertions.assertThrows(AippException.class, () -> this.formFileValidator.validateComponent(directory));
        Assertions.assertEquals(90002124, ex.getCode());
    }
}
