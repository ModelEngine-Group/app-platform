/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.log.Logger;

/**
 * AippString 操作工具类
 *
 * @author 方誉州
 * @since 2024-06-14
 */
public class AippStringUtils {
    /**
     * 文本最大字符数量
     */
    public static final int MAX_TEXT_LEN = 7168; // 7k

    /**
     * 标题数量
     */
    public static final int MAX_OUTLINE_LINE = 50;
    private static final Logger log = Logger.get(AippStringUtils.class);

    /**
     * 检查version是否为预览version
     *
     * @param version 表示待检查的version的{@link String}
     * @return 返回是否是预览的version
     */
    public static boolean isPreview(String version) {
        return version.contains("-");
    }

    /**
     * 截取限制长度内的文本
     *
     * @param text 表示带截取的文本的{@link String}
     * @param limit 表示截取长度限制的{@link Integer}
     * @return 截取后的文本
     */
    public static String textLenLimit(String text, Integer limit) {
        int limitReal = limit == null ? MAX_TEXT_LEN : limit.intValue();
        if (text.length() < limitReal) {
            return text;
        }
        return text.substring(0, limitReal);
    }

    /**
     * 截取限定行数的outline
     *
     * @param outline 表示带截取的outline的{@link String}
     * @return 返回截取后的outline
     */
    public static String outlineLenLimit(String outline) {
        int lineCount = 0;
        for (int i = 0; i < outline.length(); ++i) {
            if (outline.charAt(i) == '\n') {
                lineCount++;
                if (lineCount > MAX_OUTLINE_LINE) {
                    return outline.substring(0, i);
                }
            }
        }
        return outline;
    }

    /**
     * 字符串转换为整形
     *
     * @param str 表示待转换的字符串的{@link String}
     * @return 转换后的整形
     */
    public static Integer getIntegerFromStr(String str) {
        Integer value = null;
        try {
            value = Integer.valueOf(str);
        } catch (NumberFormatException ex) {
            log.error("invalid number string {}", str);
        }

        return value;
    }

    /**
     * 去除文本中的空白字符，并对“\”和“"”进行转义
     *
     * @param line 表示待处理的文本的{@link String}
     * @return 返回处理后的文本
     */
    public static String trimLine(String line) {
        return line.trim()
                .replace("\n", "")
                .replace("\b", "")
                .replace("\r", "")
                .replace("\f", "")
                .replace("\t", "")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
