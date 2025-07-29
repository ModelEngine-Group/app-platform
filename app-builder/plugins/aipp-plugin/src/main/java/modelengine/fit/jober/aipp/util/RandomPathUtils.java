/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import java.util.Random;

/**
 * RandomIDUtils
 *
 * @author 李智超
 * @since 2024-11-22
 */
public class RandomPathUtils {
    private static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 验证路径是否合法。
     *
     * @param path 待验证的路径
     * @param length 期望的路径长度
     * @return 如果路径合法返回true，否则返回false
     */
    public static boolean validatePath(String path, int length) {
        if (path == null || path.length() != length) {
            return false;
        }

        for (int i = 0; i < path.length(); i++) {
            if (VALID_CHARACTERS.indexOf(path.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 随机生成Path。
     *
     * @param length 生成Path的长度
     * @return 随机生成的Path {@link String}。
     */
    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(VALID_CHARACTERS.charAt(random.nextInt(VALID_CHARACTERS.length())));
        }
        return result.toString();
    }
}
