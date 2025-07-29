/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;

import modelengine.fitframework.util.StringUtils;

/**
 * 版本号的工具类
 *
 * @author 姚江
 * @since 2024-07-02
 */
public class VersionUtils {
    /**
     * 预览版本号中UUID的长度
     */
    public static final int PREVIEW_UUID_LEN = 6;

    /**
     * 预览版本号的版本号与uuid之间的连接符号
     */
    public static final String CONNECTION_SIGN = "-";

    /**
     * 校验一个版本号是否为正确的版本号格式
     *
     * @param version 版本号
     * @return 版本号是否合法
     */
    public static boolean isValidVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        for (String part : parts) {
            if (!isNumeric(part)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建一个预览版本号
     *
     * @param version 版本号
     * @return 预览版本号
     */
    public static String buildPreviewVersion(String version) {
        String uuid = UUIDUtil.uuid();
        String subUuid = (uuid.length() > PREVIEW_UUID_LEN) ? uuid.substring(0, PREVIEW_UUID_LEN) : uuid;
        return version + CONNECTION_SIGN + subUuid;
    }

    /**
     * 比较两个版本.
     *
     * @param v1 版本1.
     * @param v2 版本2.
     * @return 若v1比v2版本大，则返回1；若v1比v2小，则返回-1；否则返回0.
     */
    public static int compare(String v1, String v2) {
        if (!VersionUtils.isValidVersion(v1) || !VersionUtils.isValidVersion(v2)) {
            throw new AippException(AippErrCode.INVALID_VERSION_NAME);
        }
        if (StringUtils.equals(v1, v2)) {
            // 在这里，与旧版本号相同的版本号被认为是最新的
            return 0;
        }
        String[] oldPart = v1.split("\\.");
        String[] newPart = v2.split("\\.");
        for (int i = 0; i < oldPart.length; i++) {
            int oldV = Integer.parseInt(oldPart[i]);
            int newV = Integer.parseInt(newPart[i]);
            if (oldV > newV) {
                return 1;
            }
            if (newV > oldV) {
                return -1;
            }
        }
        return 0;
    }
}
