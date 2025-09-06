/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.common.exception.AippJsonDecodeException;
import modelengine.fit.jober.aipp.common.exception.AippJsonEncodeException;
import modelengine.fit.jober.aipp.init.serialization.custom.LocalDateTimeDeserializer;
import modelengine.fit.jober.aipp.init.serialization.custom.LocalDateTimeSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * json工具类
 *
 * @author 孙怡菲
 * @since 2025/5/10
 */
public interface JsonUtils {
    /**
     * 将json字符串解析为map
     *
     * @param jsonString json字符串
     * @return map
     */
    static Map<String, Object> parseObject(String jsonString) {
        try {
            return new ObjectMapper().readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    /**
     * 将json字符串解析为对象
     *
     * @param jsonString json字符串
     * @param clazz 类型
     * @param <T> 对象类型
     * @return 对象
     */
    static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    /**
     * 将json字符串解析为list
     *
     * @param jsonString json字符串
     * @param clazz 类型
     * @param <T> 元素类型
     * @return list
     */
    static <T> List<T> parseArray(String jsonString, Class<T[]> clazz) {
        try {
            return Arrays.asList(new ObjectMapper().readValue(jsonString, clazz));
        } catch (JsonProcessingException e) {
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    /**
     * 把对象转换为json字符串
     *
     * @param object 对象
     * @return json字符串
     */
    static String toJsonString(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(StringUtils.EMPTY));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(StringUtils.EMPTY));
            mapper.registerModule(module);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new AippJsonEncodeException(e.getMessage());
        }
    }

    /**
     * 校验是否为合格的json
     *
     * @param jsonLikeString 类json字符串
     * @return 是否是json格式
     */
    static boolean isValidJson(String jsonLikeString) {
        boolean isValid;
        try {
            TreeNode node =
                    new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).readTree(jsonLikeString);
            isValid = Objects.nonNull(node);
        } catch (JsonProcessingException e) {
            isValid = false;
        }
        return isValid;
    }
}
