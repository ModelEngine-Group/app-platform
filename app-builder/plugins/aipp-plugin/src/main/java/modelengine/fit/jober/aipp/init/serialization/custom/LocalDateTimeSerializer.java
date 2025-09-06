/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.init.serialization.custom;

import static modelengine.fit.jober.aipp.constants.AippConst.DEFAULT_DATE_TIME_FORMAT;
import static modelengine.fitframework.util.StringUtils.blankIf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 表示 {@link LocalDateTime} 类型的自定义序列化器。
 *
 * @author 季聿阶
 * @since 2024-02-21
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    private final String dateFormat;

    public LocalDateTimeSerializer(String dateFormat) {
        this.dateFormat = blankIf(dateFormat, DEFAULT_DATE_TIME_FORMAT);
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(localDateTime.format(DateTimeFormatter.ofPattern(this.dateFormat)));
    }
}