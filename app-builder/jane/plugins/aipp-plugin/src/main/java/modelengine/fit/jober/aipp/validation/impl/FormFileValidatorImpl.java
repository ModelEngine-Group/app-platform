/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.validation.impl;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.validation.FormFileValidator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表单文件校验器Impl
 *
 * @author 陈潇文
 * @since 2024/11/19
 */
@Component
public class FormFileValidatorImpl implements FormFileValidator {
    private static final long IMAGE_MAX_SIZE = 1024 * 1024;
    private static final String PARAMETERS = "parameters";
    private static final String SCHEMA = "schema";
    private static final String TYPE = "type";
    private static final String OBJECT = "object";
    private static final String REQUIRED = "required";
    private static final String PROPERTIES = "properties";
    private static final List<String> SCHEMA_LIST =
            Arrays.asList("string", "number", "integer", "array", "boolean", "null", "object");
    private static final String ARRAY = "array";
    private static final String ITEMS = "items";
    private static final String ENUM = "enum";
    private static final String RETURN = "return";
    private static final String ORDER = "order";
    private static final List<String> BUILD_FILE_LIST = Arrays.asList("index.html", "index.js");
    private static final List<String> DISALLOWED_FILE_TYPE =
            Arrays.asList(".php", ".exe", ".bat", ".sh", ".dll", ".bin", ".zip", ".tar", ".gz", ".rar",
                    ".7z", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx");
    private static final Logger log = Logger.get(FormFileValidatorImpl.class);

    @Override
    public void validateSchema(Map<String, Object> config) {
        if (!config.containsKey(SCHEMA)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_MISSING_KEY, SCHEMA);
        }
        Map<String, Object> schema = cast(config.get(SCHEMA));
        if (!schema.containsKey(PARAMETERS)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_MISSING_KEY, PARAMETERS);
        }
        Map<String, Object> parameters = cast(schema.get(PARAMETERS));
        if (!parameters.containsKey(TYPE) || !Objects.equals(OBJECT, this.getStringTypeValue(parameters, TYPE))) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PARAMETERS_TYPE_ERROR);
        }
        if (!parameters.containsKey(REQUIRED)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PARAMETERS_MISSING_KEY, REQUIRED);
        }
        if (!parameters.containsKey(PROPERTIES)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PARAMETERS_MISSING_KEY, PROPERTIES);
        }
        Map<String, Object> properties = cast(parameters.get(PROPERTIES));
        this.validateProperties(properties);
        this.validateSchemaConstraints(parameters, properties, REQUIRED);
        if (!schema.containsKey(RETURN)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_MISSING_KEY, RETURN);
        }
        Map<String, Object> returns = cast(schema.get(RETURN));
        this.validateProperty(RETURN, returns);
        this.validateOrder(schema, properties);
    }

    private void validateSchemaConstraints(Map<String, Object> parameters, Map<String, Object> properties,
            String constraint) {
        List<String> required = cast(parameters.get(constraint));
        if (required.size() > properties.size()) {
            throw new AippException(AippErrCode.FORM_SCHEMA_FIELD_SIZE_ERROR, constraint);
        }
        required.forEach(field -> {
            if (!properties.containsKey(field)) {
                throw new AippException(AippErrCode.FORM_SCHEMA_FIELD_NOT_IN_PROPERTIES, constraint);
            }
        });
    }

    private void validateOrder(Map<String, Object> schema, Map<String, Object> properties) {
        if (!schema.containsKey(ORDER)) {
            return;
        }
        this.validateSchemaConstraints(schema, properties, ORDER);
    }

    private String getStringTypeValue(Map<String, Object> stringObjectMap, String key) {
        return ObjectUtils.cast(stringObjectMap.get(key));
    }

    private void validateProperties(Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            Map<String, Object> fieldValue = cast(entry.getValue());
            this.validateProperty(fieldName, fieldValue);
        }
    }

    private void validateProperty(String fieldName, Map<String, Object> fieldValue) {
        if (!fieldValue.containsKey(TYPE)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PROPERTY_MISSING_KEY, fieldName, TYPE);
        }
        Object fieldType = fieldValue.get(TYPE);
        if (!(fieldType instanceof String)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PROPERTY_TYPE_NOT_STRING_ERROR, fieldName);
        }
        String filedTypeString = cast(fieldType);
        this.validateType(filedTypeString, fieldName);
        if (ARRAY.equals(filedTypeString)) {
            this.validateArrayTypeProperty(fieldName, fieldValue);
        }
        if (OBJECT.equals(filedTypeString) && fieldValue.get(PROPERTIES) != null) {
            this.validateObjectItem(fieldValue);
        }
    }

    private void validateType(String filedTypeString, String fieldName) {
        if (!SCHEMA_LIST.contains(filedTypeString)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PROPERTY_TYPE_ERROR, fieldName);
        }
    }

    private void validateArrayTypeProperty(String fieldName, Map<String, Object> fieldValue) {
        Object itemsObject = fieldValue.get(ITEMS);
        if (itemsObject instanceof List) {
            List<Map<String, Object>> items = cast(itemsObject);
            items.forEach(item -> this.validateArrayItem(fieldName, item));
            return;
        }
        Map<String, Object> items = cast(itemsObject);
        if (items != null) {
            if (!items.containsKey(TYPE)) {
                throw new AippException(AippErrCode.FORM_SCHEMA_PROPERTY_MISSING_KEY,
                        fieldName + " (array items)",
                        TYPE);
            }
            Object type = items.get(TYPE);
            if (!(type instanceof String)) {
                throw new AippException(AippErrCode.FORM_SCHEMA_LIST_PROPERTY_TYPE_NOT_STRING_ERROR, fieldName);
            }
            this.validateType(cast(type), fieldName + " (array items)");
        }
    }

    private void validateArrayItem(String fieldName, Map<String, Object> item) {
        if (!item.containsKey(TYPE) && !item.containsKey(ENUM)) {
            throw new AippException(AippErrCode.FORM_SCHEMA_PROPERTY_MISSING_KEY,
                    fieldName + " (array items)",
                    TYPE + "/" + ENUM);
        }
        if (item.containsKey(TYPE)) {
            this.validateType(cast(item.get(TYPE)), fieldName + " (array items)");
            return;
        }
        if (item.containsKey(ENUM)) {
            if (!(item.get(ENUM) instanceof List)) {
                throw new AippException(AippErrCode.FORM_SCHEMA_LIST_PROPERTY_ENUM_NOT_LIST_ERROR, fieldName);
            }
        }
    }

    private void validateObjectItem(Map<String, Object> fieldValue) {
        Map<String, Object> properties = cast(fieldValue.get(PROPERTIES));
        this.validateProperties(properties);
    }

    @Override
    public void validateImg(File file) {
        long fileSize = file.length();
        if (fileSize > IMAGE_MAX_SIZE) {
            throw new AippException(AippErrCode.FORM_IMG_FILE_MAX_SIZE_EXCEED);
        }
    }

    @Override
    public void validateComponent(File directory) {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            throw new AippException(AippErrCode.FORM_BUILD_EMPTY_ERROR);
        }
        List<String> fileNames = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        BUILD_FILE_LIST.forEach(name -> {
            if (!fileNames.contains(name)) {
                throw new AippException(AippErrCode.FORM_BUILD_MISSING_FILE, name);
            }
        });
        this.validateFileType(files);
    }

    private void validateFileType(File[] files) {
        Arrays.stream(files).forEach(file -> {
            if (file.isDirectory()) {
                this.validateFileType(file.listFiles());
                return;
            }
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            if (DISALLOWED_FILE_TYPE.contains(extension)) {
                throw new AippException(AippErrCode.CONTAIN_DISALLOWED_FILE, fileName);
            }
        });
    }
}
