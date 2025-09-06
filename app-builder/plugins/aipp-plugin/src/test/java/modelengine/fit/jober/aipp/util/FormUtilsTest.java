/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link FormUtils} 的单元测试类
 *
 * @author 姚江
 * @since 2024-07-16
 */
@ExtendWith(MockitoExtension.class)
public class FormUtilsTest {
    @Mock
    AppBuilderFormRepository formRepository;

    @Mock
    AppBuilderFormPropertyRepository formPropRepos;

    @Test
    @DisplayName("buildLogDataWithFormData方法测试")
    void testBuildLogDataWithFormData() {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("id_id", MapBuilder.get(() -> new HashMap<String, Object>()).put("hello", "1").build());
        List<AppBuilderFormProperty> formProperties = new ArrayList<>();
        AppBuilderFormProperty formProperty = AppBuilderFormProperty.builder().id("id_id").build();
        formProperties.add(formProperty);
        AippLogData aippLogData = FormUtils.buildLogDataWithFormData(formProperties, "id", "version", businessData);
        String formArgs = aippLogData.getFormArgs();
        Assertions.assertEquals("{\"id_id\":{\"hello\":\"1\"}}", formArgs);
    }

    @Test
    @DisplayName("buildFormData方法测试")
    void testBuildFormData() {
        AppBuilderForm inputForm = buildForm();
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("model", "Qianwen");
        String parentId = "parentId";
        Map<String, Object> form =
                Assertions.assertDoesNotThrow(() -> FormUtils.buildFormData(businessData, inputForm, parentId));
        Assertions.assertEquals(3, form.size());
        Assertions.assertTrue(form.containsKey(AippConst.FORM_APPEARANCE_KEY));
    }

    private AppBuilderForm buildForm() {
        String appearance = "{\"schema\":{\"parameters\":{\"type\":\"object\",\"required\":[\"P1\"],"
                + "\"properties\":{\"P1\":{\"type\":\"string\",\"default\":\"default_value\"},"
                + "\"a\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"b\":{\"type\":\"object\","
                + "\"additionalProperties\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},"
                + "\"p2\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},"
                + "\"age\":{\"type\":\"integer\"},\"b\":{\"type\":\"object\","
                + "\"properties\":{\"c\":{\"type\":\"string\"}}},\"grade\":{\"type\":\"object\","
                + "\"additionalProperties\":{\"type\":\"string\"}}}}}},\"return\":{\"type\":\"object\","
                + "\"additionalProperties\":{\"type\":\"array\",\"items\":{\"type\":\"object\","
                + "\"properties\":{\"name\":{\"type\":\"string\"},\"age\":{\"type\":\"integer\"}}}}}}}";
        return AppBuilderForm.builder()
                .formPropertyRepository(this.formPropRepos)
                .name("form")
                .id("id")
                .appearance(JsonUtils.parseObject(appearance))
                .type("component")
                .build();
    }
}