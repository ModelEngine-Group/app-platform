/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain.type;

import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.DefaultParsingResult;

import java.util.List;
import java.util.Map;

/**
 * 为文本提供数据转换器。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public class TextConverter extends AbstractScalarDataConverter {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final TextConverter INSTANCE = new TextConverter();

    private TextConverter() {
    }

    @Override
    protected Object fromExternal0(Object value) {
        if (value instanceof List) {
            throw new IllegalArgumentException(
                    StringUtils.format("The value to be converted to a text cannot be a list. [value={0}]", value));
        } else if (value instanceof Map) {
            throw new IllegalArgumentException(
                    StringUtils.format("The value to be converted to a text cannot be a map. [value={0}]", value));
        } else {
            return value.toString();
        }
    }

    @Override
    protected Object toExternal0(Object value) {
        return value;
    }

    @Override
    protected Object fromPersistence0(Object value) {
        return value;
    }

    @Override
    protected Object toPersistence0(Object value) {
        return value;
    }

    @Override
    protected ParsingResult<Object> parse0(String text) {
        return new DefaultParsingResult<>(true, text);
    }

    @Override
    protected String toString0(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
}
