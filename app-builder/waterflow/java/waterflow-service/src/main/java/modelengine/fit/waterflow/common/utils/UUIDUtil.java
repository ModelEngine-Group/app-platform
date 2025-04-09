/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.common.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Uuid的Utils类。
 *
 * @author 孙怡菲
 * @since 2025-04-09
 */
public class UUIDUtil {
    /**
     * 随机生成uuid。
     *
     * @return 表示随机生成的uuid的 {@link String}。
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 使用线程本地随机数生成UUID。
     * 生成的 UUID 在唯一性和不可预测性上可能不如 UUID.randomUUID()，但在性能上有显著提升
     *
     * @return 表示随机生成UUID的 {@link String}。
     */
    public static String fastUuid() {
        long mostSigBits = ThreadLocalRandom.current().nextLong();
        long leastSigBits = ThreadLocalRandom.current().nextLong();

        // 设置版本4和变体IETF
        mostSigBits &= 0xffffffffffff0fffL;
        mostSigBits |= 0x0000000000004000L;
        leastSigBits &= 0x3fffffffffffffffL;
        leastSigBits |= 0x8000000000000000L;

        return new UUID(mostSigBits, leastSigBits).toString().replace("-", "");
    }
}
