/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks.CallbackParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks.GeneralCallbackParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks.CallbackRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks.GeneralCallbackRule;

import java.util.Arrays;
import java.util.Locale;

import static modelengine.fit.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

/**
 * 流程定义回调函数类型
 *
 * @author 李哲峰
 * @since 2023/12/11
 */
@Getter
public enum FlowCallbackType {
    GENERAL_CALLBACK("GENERAL_CALLBACK", new GeneralCallbackParser(), new GeneralCallbackRule());

    private final String code;

    private final CallbackParser callbackParser;

    private final CallbackRule callbackRule;

    FlowCallbackType(String code, CallbackParser callbackParser, CallbackRule callbackRule) {
        this.code = code;
        this.callbackParser = callbackParser;
        this.callbackRule = callbackRule;
    }

    /**
     * getCallbackType
     *
     * @param code code
     * @return FlowCallbackType
     */
    public static FlowCallbackType getCallbackType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowCallbackType", code));
    }
}
