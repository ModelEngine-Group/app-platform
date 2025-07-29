/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.voice.to.text.controller;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.common.Result;
import modelengine.jade.voice.service.VoiceService;

/**
 * 处理 HTTP 请求的控制器。
 *
 * @author 张粟
 * @since 2024/6/18
 */
@Component
@RequestMapping("/voice")
public class VoiceController {
    private final VoiceService voiceService;

    /**
     * 初始化 {@link VoiceController} 的新实例。
     *
     * @param toolService 表示语音服务的 {@link VoiceService}。
     */
    public VoiceController(VoiceService toolService) {
        this.voiceService = notNull(toolService, "The voice service cannot be null.");
    }

    /**
     * 语音转文字
     *
     * @param voicePath 输入语音文件路径 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link String}。
     */
    @GetMapping("/toText")
    public Result<String> getText(@RequestQuery(value = "voicePath", required = false) String voicePath) {
        return Result.ok(voiceService.getText(voicePath), "success", 1);
    }

    /**
     * 文字转语音
     *
     * @param text 输入文本 {@link String}。
     * @param tone 输入音色 {@link int}。
     * @return 表示格式化之后的返回消息的 {@link String}{@code <}{@link String}{@code >}。
     */
    @GetMapping("/toVoice")
    public Result<String> getVoice(@RequestQuery(value = "text", required = false) String text,
            @RequestQuery(value = "tone", required = false) int tone) {
        return Result.ok(voiceService.getVoice(text, tone), "success", 1);
    }
}
