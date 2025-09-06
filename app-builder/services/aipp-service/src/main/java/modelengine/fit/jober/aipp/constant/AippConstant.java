/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.constant;

/**
 * 表示 Aipp 使用的常量。
 *
 * @author 兰宇晨
 * @since 2025-01-06
 */
public class AippConstant {
    /**
     * NAS 共享目录。
     */
    public static final String NAS_SHARE_DIR = "/var/share";

    /**
     * 下载文件的 uri 路径前缀（包含租户ID占位符）。
     */
    public static final String DOWNLOAD_FILE_ORIGIN = "/v1/api/%s/file?";
}
