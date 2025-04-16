/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 校验规则公共接口
 *
 * @author 高诗意
 * @since 2023/08/29
 */
public interface Rules {
    /**
     * exception
     *
     * @param paramName paramName
     * @return Supplier<WaterflowParamException>
     */
    default Supplier<WaterflowParamException> exception(String paramName) {
        return () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
    }

    /**
     * validateEmpty
     *
     * @param list list
     * @param paramName paramName
     */
    default <T> void validateEmpty(List<T> list, String paramName) {
        if (CollectionUtils.isNotEmpty(list)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }

    /**
     * validateBlank
     *
     * @param string string
     * @param paramName paramName
     */
    default void validateBlank(String string, String paramName) {
        if (StringUtils.isNotBlank(string)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }

    /**
     * validateNull
     *
     * @param object object
     * @param paramName paramName
     */
    default <T> void validateNull(T object, String paramName) {
        if (Objects.nonNull(object)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }
}
