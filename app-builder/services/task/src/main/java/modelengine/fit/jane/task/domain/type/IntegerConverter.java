/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain.type;

import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.DefaultParsingResult;

/**
 * 为整数提供数据转换器。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public class IntegerConverter extends AbstractScalarDataConverter {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final IntegerConverter INSTANCE = new IntegerConverter();

    private IntegerConverter() {
    }

    @Override
    protected Object fromExternal0(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = value.toString();
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(StringUtils.format("The value is not an integer. [value={0}]", value));
        }
    }

    @Override
    protected Object toExternal0(Object value) {
        return value;
    }

    @Override
    protected Object fromPersistence0(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    @Override
    protected Object toPersistence0(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    @Override
    protected ParsingResult<Object> parse0(String text) {
        try {
            return new DefaultParsingResult<>(true, Long.parseLong(text));
        } catch (NumberFormatException ignored) {
            return ParsingResult.failed();
        }
    }

    @Override
    protected String toString0(Object value) {
        return value.toString();
    }
}
