/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain.type;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jane.task.domain.DataConverter;
import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.DefaultParsingResult;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 为列表类型的数据转换器提供基类。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public abstract class AbstractListDataConverter implements DataConverter {
    private static final char ESCAPE_CHARACTER = '\\';

    private static final char SEPARATOR_CHARACTER = ',';

    private final DataConverter elementConverter;

    public AbstractListDataConverter(DataConverter elementConverter) {
        this.elementConverter = elementConverter;
    }

    private static Object convert(Object value, Function<Object, Object> mapper) {
        if (value == null) {
            return Collections.emptyList();
        } else if (value instanceof List) {
            return ((List<?>) value).stream().map(mapper).collect(Collectors.toList());
        } else if (value.getClass().isArray()) {
            return IntStream.range(0, Array.getLength(value))
                    .mapToObj(index -> Array.get(value, index))
                    .map(mapper)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException(StringUtils.format("The value is not a list. [value={0}]", value));
        }
    }

    @Override
    public Object fromExternal(Object value) {
        return convert(value, this.elementConverter::fromExternal);
    }

    @Override
    public Object toExternal(Object value) {
        return convert(value, this.elementConverter::toExternal);
    }

    @Override
    public Object fromPersistence(Object value) {
        return convert(value, this.elementConverter::fromPersistence);
    }

    @Override
    public Object toPersistence(Object value) {
        return convert(value, this.elementConverter::toPersistence);
    }

    @Override
    public ParsingResult<Object> parse(String text) {
        String actual = StringUtils.trim(text);
        if (StringUtils.isEmpty(actual)) {
            return new DefaultParsingResult<>(true, Collections.emptyList());
        }
        if (actual.length() < 2 || actual.charAt(0) != '[' || actual.charAt(actual.length() - 1) != ']') {
            return ParsingResult.failed();
        }
        actual = actual.substring(1, actual.length() - 1);
        List<Object> results = new LinkedList<>();
        StringBuilder builder = new StringBuilder(actual.length());
        for (int i = 0; i < actual.length(); i++) {
            char ch = actual.charAt(i);
            if (ch == ESCAPE_CHARACTER) {
                if (i < actual.length() - 1) {
                    builder.append(actual.charAt(++i));
                } else {
                    return ParsingResult.failed();
                }
            }
            if (ch == SEPARATOR_CHARACTER) {
                String value = builder.toString();
                ParsingResult<Object> parsingResult = this.elementConverter.parse(value);
                if (parsingResult.isParsed()) {
                    results.add(parsingResult.getResult());
                } else {
                    return ParsingResult.failed();
                }
                builder.setLength(0);
            }
        }
        ParsingResult<Object> parsingResult = this.elementConverter.parse(builder.toString());
        if (parsingResult.isParsed()) {
            results.add(parsingResult.getResult());
        } else {
            return ParsingResult.failed();
        }
        return new DefaultParsingResult<>(true, results);
    }

    @Override
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        List<Object> values = cast(value);
        StringBuilder builder = new StringBuilder();
        for (Object current : values) {
            String text = this.elementConverter.toString(current);
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (ch == ESCAPE_CHARACTER || ch == SEPARATOR_CHARACTER) {
                    builder.append(ESCAPE_CHARACTER);
                }
                builder.append(ch);
            }
            builder.append(SEPARATOR_CHARACTER);
        }
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }
}
