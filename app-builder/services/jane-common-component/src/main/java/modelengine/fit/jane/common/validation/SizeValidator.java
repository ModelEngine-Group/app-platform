/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.validation;

import modelengine.fitframework.validation.ConstraintValidator;

/**
 * The SizeValidator
 *
 * @author 刘信宏
 * @since 2023-12-14
 */
public class SizeValidator implements ConstraintValidator<Size, String> {
    private int min;

    private int max;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Size constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value) {
        return value != null && value.length() >= min && value.length() <= max;
    }

    @Override
    public Object[] args() {
        return new Object[] {this.min, this.max};
    }
}
