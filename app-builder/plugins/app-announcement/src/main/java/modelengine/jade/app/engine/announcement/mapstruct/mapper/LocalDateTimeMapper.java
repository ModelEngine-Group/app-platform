/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.announcement.mapstruct.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 公告信息日期转换类。
 *
 * @author 张圆
 * @since 2024-06-18
 */
public class LocalDateTimeMapper {
    /**
     * 将日期时间对象转换为格式化的字符串。
     *
     * @param date 需要格式化的日期时间对象 {@link LocalDateTime}
     * @return 表示格式化的日期时间字符串 {@link String}
     * @throws IllegalArgumentException 如果输入的日期时间对象为null。
     */
    public String asString(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将格式化的日期时间字符串转换为日期时间对象。
     *
     * @param date 需要转换的日期时间字符串 {@link String}
     * @return 转换后的日期时间对象。{@link LocalDateTime}
     * @throws IllegalArgumentException 如果输入的日期时间字符串为null。
     */
    public LocalDateTime asDate(String date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}


