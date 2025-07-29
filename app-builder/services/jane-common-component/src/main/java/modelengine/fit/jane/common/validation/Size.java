/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.validation;

import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The Size
 *
 * @author 刘信宏
 * @since 2023-12-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(SizeValidator.class)
@Validated
public @interface Size {
    String message() default "{javax.validation.constraints.Size.message}";

    Class<?>[] groups() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;
}
