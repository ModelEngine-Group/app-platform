/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.entity.ffmpeg.FfmpegMeta;

import java.io.IOException;

/**
 * FfmpegService ffmpeg服务
 *
 * @author 易文渊
 * @since 2024/1/7
 */
public interface FfmpegService {
    /**
     * stat 读取元数据
     *
     * @param inputFilePath 输入文件路径, 必须是mp4格式
     * @return FfmpegMeta {@link FfmpegMeta}
     * @throws IOException ffmpeg执行报错
     * @author 易文渊
     * @since 2024/1/7 15:43
     */
    FfmpegMeta stat(String inputFilePath) throws IOException;

    /**
     * extractAudio 提取视频中的音频
     *
     * @param inputFilePath 输入文件路径, 必须是mp4格式
     * @param outputFilePath 输出文件路径, 音频文件格式必须与视频保持一致, mp4视频一般为AAC
     * @throws IOException ffmpeg执行出错
     * @author 易文渊
     * @since 2024/1/7 15:43
     */
    void extractAudio(String inputFilePath, String outputFilePath) throws IOException;

    /**
     * splitAudio 将音频按照时间切片
     *
     * @param inputFilePath 输入文件路径
     * @param outputPatten 输出文件目录, 输出文件名如{input_prefix}_split_%03d.{input_suffix}
     * @param segmentSize 切片长度，单位为秒
     * @throws IOException ffmpeg执行出错
     * @author 易文渊
     * @since 2024/1/7 15:49
     */
    void splitAudio(String inputFilePath, String outputPatten, int segmentSize) throws IOException;
}