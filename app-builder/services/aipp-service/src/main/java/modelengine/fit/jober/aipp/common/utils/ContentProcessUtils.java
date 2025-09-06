/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示大模型生成内容处理的工具类。
 *
 * @author 孙怡菲
 * @since 2025-04-29
 */
public class ContentProcessUtils {
    private static final Pattern REASONING_PATTERN =
            Pattern.compile("<think>(.*?)</think>", Pattern.DOTALL);

    /**
     * 去除生成内容中的模型推理内容。
     *
     * @param rawContent 表示大模型生成内容的 {@link String}。
     * @return 表示处理后内容的 {@link String}。
     */
    public static String filterReasoningContent(String rawContent) {
        // todo 是否处理<think>标签不完整的情况
        Matcher matcher = REASONING_PATTERN.matcher(rawContent);

        String cleanedText = matcher.replaceAll("");

        return cleanedText.trim();
    }
}
