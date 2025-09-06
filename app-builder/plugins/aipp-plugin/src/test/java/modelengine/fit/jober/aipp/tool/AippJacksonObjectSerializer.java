/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jober.aipp.init.serialization.custom.LocalDateTimeDeserializer;
import modelengine.fit.jober.aipp.init.serialization.custom.LocalDateTimeSerializer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * 自定义序列化器, 解决Jackson序列化别名问题。当前用作测试类
 *
 * @author 邬涨财
 * @since 2024-02-26
 */
public class AippJacksonObjectSerializer implements ObjectSerializer {
    private final ObjectMapper mapper;

    /**
     * 为Aipp提供兹定于序列化器
     *
     * @param dateTimeFormat 日期时间格式
     */
    public AippJacksonObjectSerializer(@Value("${jackson.datetime-format}") String dateTimeFormat) {
        SimpleModule customSerialization = new SimpleModule();

        customSerialization.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormat));
        customSerialization.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormat));

        this.mapper = JsonMapper.builder(new JsonFactoryBuilder().streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(Integer.MAX_VALUE)
                        .build()).build())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .addModule(customSerialization)
                .build();
    }

    @Override
    public <T> T deserialize(InputStream in, Charset charset, Type objectType, Map<String, Object> context)
            throws SerializationException {
        notNull(objectType, "The object type cannot be null.");
        notNull(in, "The input stream cannot be null.");
        final String contextKey = "length";
        Map<String, Object> actualContext = ObjectUtils.getIfNull(context, Collections::emptyMap);
        try {
            String read;
            if (!actualContext.containsKey(contextKey)) {
                read = IoUtils.content(in);
            } else {
                read = IoUtils.content(in, ObjectUtils.<Integer>cast(actualContext.get(contextKey)));
            }
            return this.mapper.readValue(read, this.mapper.constructType(objectType));
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize by Jackson.", e);
        }
    }

    private JsonEncoding getJsonEncoding(Charset charset) {
        for (JsonEncoding encoding : JsonEncoding.values()) {
            if (StringUtils.equalsIgnoreCase(charset.name(), encoding.getJavaName())) {
                return encoding;
            }
        }
        return JsonEncoding.UTF8;
    }

    @Override
    public <T> void serialize(T object, Charset charset, OutputStream out, Map<String, Object> context)
            throws SerializationException {
        notNull(out, "The output stream cannot be null.");
        try (JsonGenerator jsonGenerator = this.mapper.createGenerator(out, this.getJsonEncoding(charset))) {
            this.mapper.writeValue(jsonGenerator, object);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize by Jackson.", e);
        }
    }
}
