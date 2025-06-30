/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.common.Constant;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;

/**
 * 运算符相关信息
 *
 * @author 陈镕希
 * @since 2024-05-15
 */
@Getter
@AllArgsConstructor
public enum OperatorInfo {
    NOT_EQUAL(OperatorInfo.BINARY_TYPE, " != ", false, false),
    EQUAL(OperatorInfo.BINARY_TYPE, " == ", false, false),
    IS_TRUE(OperatorInfo.UNARY_TYPE, "", false, false),
    IS_FALSE(OperatorInfo.UNARY_TYPE, " == false", false, false),
    IS_EMPTY(OperatorInfo.UNARY_TYPE, ".isEmpty()", false, false),
    IS_NOT_EMPTY(OperatorInfo.UNARY_TYPE, ".isEmpty()", true, false),
    GREATER_THAN(OperatorInfo.BINARY_TYPE, " > ", false, false),
    GREATER_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, " >= ", false, false),
    LESS_THAN(OperatorInfo.BINARY_TYPE, " < ", false, false),
    LESS_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, " <= ", false, false),
    AND(OperatorInfo.BINARY_TYPE, " && ", false, false),
    OR(OperatorInfo.BINARY_TYPE, " || ", false, false),
    TRUE(OperatorInfo.NULLARY_TYPE, "true", false, false),
    FALSE(OperatorInfo.NULLARY_TYPE, "false", false, false),
    IS_NULL(OperatorInfo.UNARY_TYPE, " == null", false, false),
    IS_NOT_NULL(OperatorInfo.UNARY_TYPE, " != null", false, false),
    IS_EMPTY_STRING(OperatorInfo.UNARY_TYPE, ".trim().is_empty()", false, false),
    IS_NOT_EMPTY_STRING(OperatorInfo.UNARY_TYPE, ".trim().is_empty()", true, false),
    LONGER_THAN(OperatorInfo.BINARY_TYPE, ".len() > ", false, false),
    LONGER_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, ".len() >= ", false, false),
    SHORTER_THAN(OperatorInfo.BINARY_TYPE, ".len() < ", false, false),
    SHORTER_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, ".len() <= ", false, false),
    CONTAINS(OperatorInfo.BINARY_TYPE, ".contains", false, true),
    DOES_NOT_CONTAIN(OperatorInfo.BINARY_TYPE, ".contains", true, true),
    STARTS_WITH(OperatorInfo.BINARY_TYPE, ".starts_with", false, true),
    ENDS_WITH(OperatorInfo.BINARY_TYPE, ".ends_with", false, true),
    ;

    /**
     * 表示零目运算符的类型常量
     */
    public static final String NULLARY_TYPE = "nullary";

    /**
     * 表示一目运算符的类型常量
     */
    public static final String UNARY_TYPE = "unary";

    /**
     * 表示二目运算符的类型常量
     */
    public static final String BINARY_TYPE = "binary";

    private static final String VALUE_KEY = "value";

    private static final String FROM_KEY = "from";

    private static final String REFERENCE_FROM_TYPE = "Reference";

    private static final String INPUT_FROM_TYPE = "Input";

    private static final String UNDEFINED_EXPRESSION = "undefined";

    private static final String INPUT_TYPE_KEY = "type";

    private static final String VALUE_REFERENCE_NODE_KEY = "referenceNode";

    private static final String VALUE_VALUE_ARRAY_KEY = "value";

    private static final String BUSINESS_DATA_PREFIX = "businessData.";

    private final String type;

    private final String operator;

    private final boolean isInvert;

    private final boolean isParenthesized;

    /**
     * 根据code获得对应运算符枚举类的 {@link OperatorInfo}。
     *
     * @param code 枚举类名称对应code的 {@link String}。
     * @return code对应运算符枚举类的 {@link OperatorInfo}。
     */
    public static OperatorInfo getByCode(String code) {
        for (OperatorInfo op : values()) {
            if (op.name().equalsIgnoreCase(code.replace(" ", "_"))) {
                return op;
            }
        }
        throw new WaterflowException(INPUT_PARAM_IS_INVALID, "OperatorInfo code");
    }

    /**
     * 根据枚举类中isInvert的值返回不同的运算符结果。
     *
     * @return 根据枚举类中isInvert的值返回不同的运算符结果的 {@link String}。
     */
    private String getInvert() {
        return isInvert ? "!" : "";
    }

    /**
     * 根据条件转换出对应的表达式。
     *
     * @param condition 表示条件对象的 {@link JSONObject}。
     * @return 表示根据条件转换出的表达式的 {@link String}。
     */
    public String buildConditionExpression(JSONObject condition) {
        JSONArray values = condition.getJSONArray(VALUE_KEY);
        String leftExpression = generateLeftExpression(values, this);
        String rightExpression = this.getParenthesized(generateRightExpression(values, this));
        return "(" + this.getInvert() + leftExpression + this.getOperator() + rightExpression + ")";
    }

    private String getParenthesized(String rightExpression) {
        return isParenthesized ? "(" + rightExpression + ")" : rightExpression;
    }

    private static String generateLeftExpression(JSONArray values, OperatorInfo operator) {
        if (OperatorInfo.UNARY_TYPE.equalsIgnoreCase(operator.getType()) || OperatorInfo.BINARY_TYPE.equalsIgnoreCase(
                operator.getType())) {
            JSONObject left = values.getJSONObject(0);
            return generateExpression(left, operator.getType());
        }
        return ""; // Unary operators do not require a right expression
    }

    private static String generateRightExpression(JSONArray values, OperatorInfo operator) {
        if (OperatorInfo.BINARY_TYPE.equalsIgnoreCase(operator.getType())) {
            JSONObject right = values.getJSONObject(1);
            return generateExpression(right, operator.getType());
        }
        return ""; // Unary operators do not require a right expression
    }

    private static String generateExpression(JSONObject valueObj, String operatorType) {
        String from = valueObj.getString(FROM_KEY);
        if (REFERENCE_FROM_TYPE.equals(from)) {
            return buildReferenceExpression(valueObj, operatorType);
        } else if (INPUT_FROM_TYPE.equals(from)) {
            return formatInputValue(valueObj);
        } else {
            return UNDEFINED_EXPRESSION;
        }
    }

    /**
     * 用于给路径外部添加统一的get包裹，以便于在json中取值。
     *
     * @param path 需要包裹的路径的 {@link String}。
     * @return 添加完get包裹的路径的 {@link String}。
     */
    private static String pathGetWrapper(String path) {
        return "get(\"" + path + "\")";
    }

    private static String buildReferenceExpression(JSONObject valueObj, String operatorType) {
        String referenceNode = valueObj.getString(VALUE_REFERENCE_NODE_KEY);
        JSONArray valueArray = valueObj.getJSONArray(VALUE_VALUE_ARRAY_KEY);
        StringBuilder valueBuilder = new StringBuilder(
                BUSINESS_DATA_PREFIX + pathGetWrapper(Constant.BUSINESS_DATA_INTERNAL_KEY) + "." + pathGetWrapper(
                        Constant.INTERNAL_OUTPUT_SCOPE_KEY) + ".").append(pathGetWrapper(referenceNode));
        for (Object val : valueArray) {
            valueBuilder.append(".").append(pathGetWrapper(val.toString()));
        }
        if ("String".equals(valueObj.getString(INPUT_TYPE_KEY)) && OperatorInfo.BINARY_TYPE.equals(operatorType)) {
            valueBuilder.append(".trim()");
        }
        return valueBuilder.toString();
    }

    private static String formatInputValue(JSONObject inputObj) {
        String inputType = inputObj.getString(INPUT_TYPE_KEY);
        switch (inputType) {
            case "Boolean":
            case "Number":
            case "Integer":
                return inputObj.getString("value");
            case "String":
                return "\"" + escapeString(inputObj.getString("value")) + "\"";
            default:
                return "undefined";
        }
    }

    private static String escapeString(String originString) {
        return originString.replace("\\", "\\\\")
                .replace("\n", "\\\n")
                .replace("\b", "\\\b")
                .replace("\r", "\\\r")
                .replace("\f", "\\\f")
                .replace("\t", "\\\t")
                .replace("\"", "\\\"");
    }
}