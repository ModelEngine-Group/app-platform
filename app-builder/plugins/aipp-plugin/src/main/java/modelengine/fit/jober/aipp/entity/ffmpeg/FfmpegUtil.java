/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity.ffmpeg;

/**
 * FfmpegUtil
 *
 * @author 易文渊
 * @since 2024/1/9
 */
public class FfmpegUtil {
    /**
     * formatTimestamps 格式化输出
     *
     * @param time 输入时间，单位s
     * @return String 格式化字符串 HH::mm::ss
     * @author 易文渊
     * @since 2024/1/10 9:51
     */
    public static String formatTimestamps(int time) {
        int seconds = time;
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * parseDuration 解析格式化字符串
     *
     * @param duration 格式化字符串 HH::mm::ss
     * @return int 输出秒数
     * @author 易文渊
     * @since 2024/1/10 9:51
     */
    public static int parseDuration(String duration) {
        String[] times = duration.split(":");
        int seconds = 0;
        seconds += Integer.parseInt(times[0]) * 60 * 60;
        seconds += Integer.parseInt(times[1]) * 60;
        seconds += (int) Double.parseDouble(times[2]);
        return seconds;
    }
}