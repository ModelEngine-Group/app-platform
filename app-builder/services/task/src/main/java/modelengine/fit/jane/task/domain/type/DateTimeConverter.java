/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain.type;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.DefaultParsingResult;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为日期时间提供数据转换器。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public class DateTimeConverter extends AbstractScalarDataConverter {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final DateTimeConverter INSTANCE = new DateTimeConverter();

    private final String fullPattern;

    private final Map<Integer, String> patterns;

    private DateTimeConverter() {
        this.fullPattern = "yyyy-MM-dd HH:mm:ss.SSS";
        this.patterns = Stream.of(this.fullPattern, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")
                .collect(Collectors.toMap(String::length, Function.identity()));
    }

    @Override
    protected Object fromExternal0(Object value) {
        if (value instanceof LocalDateTime) {
            return value;
        } else if (value instanceof Date) {
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof Number) {
            return Instant.ofEpochMilli(((Number) value).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof CharSequence) {
            String text = value.toString();
            String pattern = this.patterns.get(text.length());
            if (pattern == null) {
                throw new IllegalArgumentException(
                        StringUtils.format("The value is not a datetime. [value={0}]", value));
            }
            try {
                return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(
                        StringUtils.format("The value is not a datetime. [value={0}]", value), ex);
            }
        } else {
            throw new IllegalArgumentException(StringUtils.format("The value is not a datetime. [value={0}]", value));
        }
    }

    @Override
    protected Object toExternal0(Object value) {
        return this.toString0(value);
    }

    @Override
    protected Object fromPersistence0(Object value) {
        if (value instanceof LocalDateTime) {
            return value;
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else {
            throw new IllegalArgumentException(StringUtils.format("The value is not a datetime. [value={0}]", value));
        }
    }

    @Override
    protected Object toPersistence0(Object value) {
        return Timestamp.valueOf(ObjectUtils.<LocalDateTime>cast(value));
    }

    @Override
    protected ParsingResult<Object> parse0(String text) {
        String pattern = this.patterns.get(text.length());
        if (pattern == null) {
            return ParsingResult.failed();
        }
        try {
            return new DefaultParsingResult<>(true, LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern)));
        } catch (DateTimeParseException ex) {
            return ParsingResult.failed();
        }
    }

    @Override
    protected String toString0(Object value) {
        if (value instanceof LocalDateTime) {
            return ObjectUtils.<LocalDateTime>cast(value).format(DateTimeFormatter.ofPattern(this.fullPattern));
        }
        return null;
    }
}
