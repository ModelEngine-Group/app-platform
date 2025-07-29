/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.validation;

import modelengine.fitframework.validation.ConstraintValidator;

/**
 * The PatternValidator
 *
 * @author 刘信宏
 * @since 2023-12-14
 */
public class PatternValidator implements ConstraintValidator<Pattern, String> {
    private String regexp;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Pattern constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value) {
        return value != null && value.matches(regexp);
    }

    @Override
    public Object[] args() {
        return new Object[] {this.regexp};
    }
}
