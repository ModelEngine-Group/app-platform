/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import static java.util.Locale.ROOT;
import static modelengine.fit.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.BatchSizeFilterParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.FilterParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.filters.TransBatchSizeFilterParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.filters.BatchSizeFilterRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.filters.FilterRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.filters.TransBatchSizeFilterRule;

import java.util.Arrays;

/**
 * 流程定义过滤器类型
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public enum FlowFilterType {
    MINIMUM_SIZE_FILTER("MINIMUM_SIZE_FILTER", new BatchSizeFilterParser(), new BatchSizeFilterRule()),
    BATCH_FILTER("BATCH_FILTER", new BatchSizeFilterParser(), new BatchSizeFilterRule()),
    TRANS_BATCH_FILTER("SAME_BATCH_FILTER", new TransBatchSizeFilterParser(), new TransBatchSizeFilterRule());

    private final String code;

    private final FilterParser filterParser;

    private final FilterRule filterRule;

    FlowFilterType(String code, FilterParser filterParser, FilterRule filterRule) {
        this.code = code;
        this.filterParser = filterParser;
        this.filterRule = filterRule;
    }

    /**
     * getFilterType
     *
     * @param code code
     * @return FlowFilterType
     */
    public static FlowFilterType getFilterType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(ROOT)))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowFilter", code));
    }
}
