/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用于过滤字符串中的敏感信息
 *
 * @author 姚江
 * @since 2024-09-10
 */
@Component
public class SensitiveFilterTools {
    private final List<SensitiveReplaceEntity> sensitiveReplaceEntities;

    public SensitiveFilterTools(@Value("${sensitive.replace}") List<SensitiveReplaceEntity> sensitiveReplaceEntities) {
        this.sensitiveReplaceEntities = sensitiveReplaceEntities.stream()
                .peek(entity -> entity.compiledPattern = Pattern.compile(entity.pattern))
                .collect(Collectors.toList());
    }

    /**
     * 将输入的字符串经过预定正则过滤
     *
     * @param sensitive 输入的可能带有敏感信息的 {@link String}
     * @return 过滤后的 {@link String}
     */
    public String filterString(String sensitive) {
        String result = sensitive;
        for (SensitiveReplaceEntity filter : sensitiveReplaceEntities) {
            result = filter.compiledPattern.matcher(result).replaceAll(filter.to);
        }
        return result;
    }

    /**
     * 字符串敏感信息过滤实体类
     *
     * @author 姚江
     * @since 2024-09-10
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensitiveReplaceEntity {
        private String pattern;
        private String to;
        private Pattern compiledPattern;
    }
}
