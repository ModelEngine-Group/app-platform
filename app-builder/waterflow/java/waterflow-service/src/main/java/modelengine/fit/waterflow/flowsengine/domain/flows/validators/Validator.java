/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;

/**
 * 流程定义校验接口
 *
 * @author 高诗意
 * @since 2023/08/29
 */
public interface Validator {
    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    void validate(FlowDefinition flowDefinition);

    /**
     * 校验分页参数的合法性
     *
     * @param offset 分页参数：偏移
     * @param limit 分页参数：条数
     */
    void validatePagination(int offset, int limit);
}
