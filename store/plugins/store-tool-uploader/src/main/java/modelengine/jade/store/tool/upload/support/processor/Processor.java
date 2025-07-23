/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support.processor;

import static modelengine.fel.tool.ToolSchema.DESCRIPTION;
import static modelengine.fel.tool.ToolSchema.NAME;
import static modelengine.fel.tool.ToolSchema.PARAMETERS;
import static modelengine.fel.tool.ToolSchema.PARAMETERS_PROPERTIES;
import static modelengine.fel.tool.ToolSchema.PARAMETERS_REQUIRED;
import static modelengine.fel.tool.ToolSchema.PROPERTIES_TYPE;
import static modelengine.fel.tool.ToolSchema.RETURN_SCHEMA;
import static modelengine.fel.tool.ToolSchema.SCHEMA;
import static modelengine.fel.tool.info.schema.PluginSchema.DOT;
import static modelengine.fel.tool.info.schema.PluginSchema.TYPE;
import static modelengine.fel.tool.info.schema.ToolsSchema.ARRAY;
import static modelengine.fel.tool.info.schema.ToolsSchema.ITEMS;
import static modelengine.fel.tool.info.schema.ToolsSchema.OBJECT;
import static modelengine.fel.tool.info.schema.ToolsSchema.TOOLS_JSON;
import static modelengine.fitframework.inspection.Validation.lessThanOrEquals;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildBlankParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildNullParserException;
import static modelengine.jade.store.tool.upload.support.BasicValidator.buildParserException;

import modelengine.fel.tool.info.entity.ParameterEntity;
import modelengine.fel.tool.info.entity.SchemaEntity;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 通用信息的处理器。
 *
 * @author 李金绪
 * @since 2024-10-29
 */
public abstract class Processor {
    private static final Logger log = Logger.get(Processor.class);
    private static final int MAX_NAME_LENGTH = 256;

    /** 名称只能包含中英文、数字、中划线（-）和下划线(_)，并且不能以中划线、下划线开头。 */
    private static final String NAME_FORMAT = "^[\\u4E00-\\u9FA5A-Za-z0-9][\\u4E00-\\u9FA5A-Za-z0-9-_]*$";
    private static final Set<String> TYPE_LIST =
            new HashSet<>(Arrays.asList("string", "number", "integer", "boolean", "null", ARRAY, OBJECT));

    /**
     * 验证给定的对象象是否符合预期的格式和内容。
     *
     * @param data 表示待验证的 {@link Object}。
     * @param helper 表示帮助信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public abstract void validate(Object data, Map<String, Object> helper);

    /**
     * 将给定的 Map 对象转换为特定的对象类。
     *
     * @param data 表示待转换的 {@link Object}。
     * @param helper 表示帮助信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 返回转换后的 {@link Object}。
     */
    public abstract Object transform(Object data, Map<String, Object> helper);

    /**
     * 对给定的数据进行处理。
     *
     * @param data 表示待处理的数据的 {@link Object}。
     * @param helper 表示帮助信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 返回处理后的数据的 {@link Object}。
     */
    public Object process(Object data, Map<String, Object> helper) {
        this.validate(data, helper);
        return this.transform(data, helper);
    }

    /**
     * 验证给定的名称是否符合预期的格式和长度。
     *
     * @param name 表示待验证的名称的 {@link String}。
     * @param object 表示待验证的对象的名称的 {@link String}。
     * @param field 表示待验证的字段的名称的 {@link String}。
     */
    public void validateName(String name, String object, String field) {
        notBlank(name, () -> buildBlankParserException(object, field));
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ModelEngineException(PluginRetCode.LENGTH_EXCEEDED_LIMIT_FIELD, name);
        }
        if (!name.matches(NAME_FORMAT)) {
            log.error("The 'name' format is incorrect. [name={}, path={}]", name, object);
            throw new ModelEngineException(PluginRetCode.NAME_IS_INVALID, name, object);
        }
    }

    /**
     * 对给定的 schema 进行严格校验。
     *
     * @param fileName 表示待校验的文件名的 {@link String}。
     * @param schema 表示待校验的 schema 的 {@link SchemaEntity}。
     */
    public void validateSchemaStrictly(String fileName, SchemaEntity schema) {
        notNull(schema, () -> buildNullParserException(fileName, SCHEMA));
        this.validateName(schema.getName(), TOOLS_JSON, SCHEMA + DOT + NAME);
        notBlank(schema.getDescription(), () -> buildBlankParserException(fileName, SCHEMA + DOT + DESCRIPTION));
        this.validateParams(schema.getParameters(), fileName);
        this.validateOrder(schema.getOrder(), schema.getParameters());
        this.validateProperty(schema.getRet(), SCHEMA, RETURN_SCHEMA);
        // parameterExtension 暂时不做校验。
    }

    private void validateParams(ParameterEntity param, String fileName) {
        if (param == null) {
            return;
        }
        notBlank(param.getType(), () -> buildBlankParserException(fileName, PARAMETERS + DOT + TYPE));
        if (!param.getType().equals(OBJECT)) {
            throw buildParserException("The type of the field 'parameters' in 'tools.json' must is: 'object'.");
        }
        Map<String, Object> properties = param.getProperties();
        if (MapUtils.isNotEmpty(properties)) {
            this.validateProperties(properties);
            this.validateRequired(param, fileName);
        }
    }

    private void validateProperties(Map<String, Object> properties) {
        properties.forEach((key, value) -> validateProperty(cast(value), PARAMETERS, key));
    }

    private void validateProperty(Map<String, Object> property, String fieldName, String field) {
        String fieldFormat = fieldName + DOT + field;
        notNull(property, () -> buildNullParserException(TOOLS_JSON, fieldFormat));
        if (!property.containsKey(PROPERTIES_TYPE)) {
            throw buildParserException(StringUtils.format("The field has no type defined. [field='{0}']", fieldName));
        }
        // 暂时只校验 type。
        String typeStr = this.validateBasicType(property.get(PROPERTIES_TYPE), fieldName);
        if (ARRAY.equals(typeStr)) {
            Map<String, Object> items = cast(property.get(ITEMS));
            notNull(items, () -> buildNullParserException(TOOLS_JSON, fieldFormat + DOT + ARRAY + DOT + ITEMS));
            this.validateBasicType(items.get(PROPERTIES_TYPE), fieldName + " (array items)");
            if (items.containsKey(PARAMETERS_PROPERTIES)) {
                this.validateProperty(items, PARAMETERS, PROPERTIES_TYPE);
            }
        }
        if (OBJECT.equals(typeStr) && property.get(PARAMETERS_PROPERTIES) != null) {
            this.validateProperties(cast(property.get(PARAMETERS_PROPERTIES)));
        }
    }

    private String validateBasicType(Object type, String fieldName) {
        if (!(type instanceof String)) {
            throw buildParserException(StringUtils.format(
                    "The type of field value in schema must be String. [field='{0}']",
                    fieldName));
        }
        String typeStr = cast(type);
        if (!TYPE_LIST.contains(typeStr)) {
            throw buildParserException(StringUtils.format(
                    "The parameter type must comply with the JSON schema parameter type format. [field='{0}', "
                            + "type='{1}']",
                    fieldName,
                    typeStr));
        }
        return typeStr;
    }

    private void validateOrder(List<String> order, ParameterEntity param) {
        Set<String> paramKeys = param == null ? Collections.emptySet() : param.getProperties().keySet();
        if (order != null && !CollectionUtils.equals(order, paramKeys)) {
            throw buildParserException(
                    "The values in 'order' must exactly match those in 'parameters', with the same quantity, "
                            + "differing only in order.");
        }
    }

    private void validateRequired(ParameterEntity param, String fileName) {
        notNull(param.getRequired(), () -> buildNullParserException(fileName, SCHEMA + DOT + PARAMETERS_REQUIRED));
        // 只有在 param 及 param.properties 不为空时，才会校验 required，所以无需判空。
        Set<String> paramKeys = param.getProperties().keySet();
        for (String requiredKey : param.getRequired()) {
            if (!paramKeys.contains(requiredKey)) {
                throw buildParserException(StringUtils.format(
                        "The key in 'required' must already exist in the 'parameters'. [key='{0}']",
                        requiredKey));
            }
        }
        lessThanOrEquals(param.getRequired().size(),
                param.getProperties().size(),
                () -> buildParserException(
                        "The size of 'required' in 'tools.json' cannot be larger than 'properties' size."));
    }

    /**
     * 获取当前用户的名称。
     *
     * @return 返回当前用户的名称的 {@link String}。
     */
    public static String getUserName() {
        return Optional.ofNullable(UserContextHolder.get())
                .map(UserContext::getName)
                .orElseThrow(() -> new IllegalArgumentException("The user name cannot be null."));
    }
}
