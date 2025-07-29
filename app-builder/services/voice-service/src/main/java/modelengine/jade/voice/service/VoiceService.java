/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.voice.service;

import modelengine.fitframework.annotation.Genericable;

/**
 * 提供语音的通用服务。
 *
 * @author 张粟
 * @since 2024-06-18
 */
public interface VoiceService {
    /**
     * 基于语音生成文本。
     *
     * @param voiceUrl 输入语音 {@link String}。
     * @return 输出文本 {@link String}。
     */
    @Genericable(id = "modelengine.jade.voice.getText")
    String getText(String voiceUrl);

    /**
     * 基于文本生成语音。
     *
     * @param text 输入文本 {@link String}。
     * @param tone 输入音色 {@link int}。
     * @return 输出语音 {@link String}。
     */
    @Genericable(id = "modelengine.jade.voice.getVoice")
    String getVoice(String text, int tone);
}
