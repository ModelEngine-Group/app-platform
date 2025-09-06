/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 解码工具
 *
 * @since 2024/5/25
 */
public class DecodeUtil {
    /**
     * 解码str
     *
     * @param encodeStr 编码str
     * @return 解码后的str
     */
    public static String decodeStr(String encodeStr) {
        try {
            return URLDecoder.decode(encodeStr, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return encodeStr;
        }
    }
}
