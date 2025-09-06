/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.utils;

import lombok.Getter;

/**
 * 采样级别枚举类。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
public enum SampleLevel {
    /** 表示一小时内的采样级别。 */
    LEVEL_1(1, 60),

    /** 表示一天内的采样级别。 */
    LEVEL_2(30, 1440),

    /** 表示一周内的采样级别。 */
    LEVEL_3(240, 10080),

    /** 表示一个月内的采样级别。 */
    LEVEL_4(1440, 43200),

    /** 表示六个月内的采样级别。 */
    LEVEL_5(10080, 259200),

    /** 表示一年内及以上的采样级别。 */
    LEVEL_6(21600, 518400);

    @Getter
    private final long interval;
    private final long threshold;

    SampleLevel(int interval, int threshold) {
        this.interval = interval;
        this.threshold = threshold;
    }

    /**
     * 计算当前时间间隔对应的采样级别。
     *
     * @param between 表示时间间隔（单位为毫秒）的 {@link Long}。
     * @return 表示采样级别的 {@link SampleLevel}。
     */
    public static SampleLevel calLevel(Long between) {
        for (SampleLevel level : SampleLevel.values()) {
            if (level.threshold > between / (1000 * 60)) {
                return level;
            }
        }
        return LEVEL_6;
    }
}