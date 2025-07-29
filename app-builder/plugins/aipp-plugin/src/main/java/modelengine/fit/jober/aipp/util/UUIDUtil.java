/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import java.util.UUID;

/**
 * Uuid的Utils类。
 *
 * @author 孙怡菲
 * @since 2023-06-15
 */
public class UUIDUtil {
    /**
     * 随机生成uuid。
     *
     * @return 随机生成的uuid的 {@link String}。
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
