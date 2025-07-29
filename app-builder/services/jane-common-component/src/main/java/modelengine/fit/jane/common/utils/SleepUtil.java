/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.utils;

import modelengine.fitframework.log.Logger;

/**
 * SleepUtil 用于睡眠或延迟操作的工具类
 *
 * @author 晏钰坤
 * @since 2023/7/18
 */
public class SleepUtil {
    private static final Logger log = Logger.get(SleepUtil.class);

    /**
     * 公用的时间延迟方法
     *
     * @param millis 毫秒
     */
    public static void sleep(long millis) {
        if (millis <= 0) {
            log.error("Sleep time is invalid.");
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Sleep get InterruptedException. Cause by : {}", e);
        }
    }
}

