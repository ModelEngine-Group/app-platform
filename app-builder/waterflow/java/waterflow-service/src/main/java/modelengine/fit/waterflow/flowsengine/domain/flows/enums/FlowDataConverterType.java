/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter.FlowDataConverterParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter.MappingFlowDataConverterParser;

import java.util.Arrays;
import java.util.Locale;

import static modelengine.fit.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

/**
 * 流程数据转换器的类型
 *
 * @author 宋永坦
 * @since 2024/4/17
 */
@Getter
public enum FlowDataConverterType {
    MAPPING_CONVERTER("MAPPING_CONVERTER", new MappingFlowDataConverterParser());

    private final String code;

    private final FlowDataConverterParser parser;

    FlowDataConverterType(String code, FlowDataConverterParser parser) {
        this.code = code;
        this.parser = parser;
    }

    /**
     * getJoberType
     *
     * @param code code
     * @return FlowJoberType
     */
    public static FlowDataConverterType getType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowDataConverterType", code));
    }
}
