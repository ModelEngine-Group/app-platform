/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.GET_FORM_CONFIG_ERROR;

import modelengine.fit.dynamicform.entity.FormMetaInfo;
import modelengine.fit.dynamicform.entity.FormMetaItem;
import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.entity.AippLogData;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AppBuilder Form 操作工具类
 *
 * @author 方誉州
 * @since 2024/6/14
 */
public class FormUtils {
    private static final Logger log = Logger.get(FormUtils.class);
    private static final String SCHEMA_KEY = "schema";
    private static final String PARAMETERS_KEY = "parameters";
    private static final String PROPERTIES_KEY = "properties";

    /**
     * 构建表单数据。
     *
     * @param businessData 表示业务数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param appBuilderForm 表示表单领域对象的 {@link AppBuilderForm}。
     * @param parentInstanceId 表示父实例 Id 的 {@link String}。
     * @return 表示构建后的表单数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}。
     */
    public static Map<String, Object> buildFormData(Map<String, Object> businessData, AppBuilderForm appBuilderForm,
            String parentInstanceId) {
        Map<String, Object> form = new HashMap<>();
        form.put(AippConst.FORM_APPEARANCE_KEY, appBuilderForm.getAppearance());
        Map<String, Object> formDataMap = buildFormData(businessData, appBuilderForm.getAppearance());
        form.put(AippConst.FORM_DATA_KEY, formDataMap);
        form.put(AippConst.PARENT_INSTANCE_ID, parentInstanceId);
        return form;
    }

    private static Map<String, Object> buildFormData(Map<String, Object> businessData,
            Map<String, Object> appearance) {
        Map<String, Object> formDataMap = new HashMap<>();
        if (appearance == null) {
            return formDataMap;
        }
        Set<String> propertiesKeys = getPropertiesKeys(appearance);
        propertiesKeys.forEach(key -> formDataMap.put(key, businessData.getOrDefault(key, StringUtils.EMPTY)));
        return formDataMap;
    }

    private static Set<String> getPropertiesKeys(Map<String, Object> appearance) {
        if (!appearance.containsKey(SCHEMA_KEY)) {
            log.error("Failed to build form data: appearance not contain schema key. [appearance={}]", appearance);
            throw new AippException(GET_FORM_CONFIG_ERROR);
        }
        Map<String, Object> schemaNode = ObjectUtils.cast(appearance.get(SCHEMA_KEY));
        if (!schemaNode.containsKey(PARAMETERS_KEY)) {
            log.error("Failed to build form data: appearance not contain parameters key. [schemaNode={}]", schemaNode);
            throw new AippException(GET_FORM_CONFIG_ERROR);
        }
        Map<String, Object> parameters = ObjectUtils.cast(schemaNode.get(PARAMETERS_KEY));
        if (!parameters.containsKey(PROPERTIES_KEY)) {
            log.error("Failed to build form data: appearance not contain properties key. [properties={}]", parameters);
            throw new AippException(GET_FORM_CONFIG_ERROR);
        }
        return ObjectUtils.<Map<String, Object>>cast(parameters.get(PROPERTIES_KEY)).keySet();
    }

    /**
     * 使用表单数据构造Aipp日志{@link AippLogData}
     *
     * @param formProperties 表示表单配置项集合
     * @param formId form id
     * @param formVersion form版本号
     * @param businessData business data
     * @return 构造得到的Aipp日志
     */
    public static AippLogData buildLogDataWithFormData(List<AppBuilderFormProperty> formProperties, String formId,
            String formVersion, Map<String, Object> businessData) {
        List<FormMetaQueryParameter> parameter =
                Collections.singletonList(new FormMetaQueryParameter(formId, formVersion));

        Map<String, Object> formArgs = buildFormMetaInfos(parameter, formProperties).stream()
                .flatMap(item -> item.getFormMetaItems().stream().map(FormMetaItem::getKey))
                .filter(businessData::containsKey)
                .collect(Collectors.toMap(Function.identity(), businessData::get));

        return AippLogData.builder()
                .formId(formId)
                .formVersion(formVersion)
                .formArgs(JsonUtils.toJsonString(formArgs))
                .build();
    }

    /**
     * 构造form元数据信息{@link FormMetaInfo}列表
     *
     * @param parameters form元数据查询参数列表
     * @param formProperties 表示表单数据配置项
     * @return 返回form元数据信息列表
     */
    public static List<FormMetaInfo> buildFormMetaInfos(List<FormMetaQueryParameter> parameters,
            List<AppBuilderFormProperty> formProperties) {
        return parameters.stream()
                .map(parameter -> buildFormMetaInfo(parameter, formProperties))
                .collect(Collectors.toList());
    }

    private static FormMetaInfo buildFormMetaInfo(FormMetaQueryParameter parameter,
            List<AppBuilderFormProperty> formProperties) {
        FormMetaInfo formMetaInfo = new FormMetaInfo(parameter.getFormId(), parameter.getVersion());
        formMetaInfo.setFormMetaItems(buildFormMetaItems(formProperties));
        return formMetaInfo;
    }

    private static List<FormMetaItem> buildFormMetaItems(List<AppBuilderFormProperty> formProperties) {
        return formProperties.stream().map(FormUtils::buildFormMetaItem).collect(Collectors.toList());
    }

    private static FormMetaItem buildFormMetaItem(AppBuilderFormProperty formProperty) {
        return FormMetaItem.builder()
                .key(formProperty.getId())
                .name(formProperty.getName())
                .type(formProperty.getDataType())
                .defaultValue(formProperty.getDefaultValue())
                .build();
    }
}
