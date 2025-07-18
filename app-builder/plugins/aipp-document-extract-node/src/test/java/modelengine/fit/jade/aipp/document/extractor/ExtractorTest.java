/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.extractor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.store.service.ToolService;
import modelengine.jade.voice.service.VoiceService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link BaseExtractor} 及其实现的测试类。
 *
 * @author 兰宇晨
 * @since 2025-01-16
 */
@FitTestWithJunit(includeClasses = {AudioExtractor.class, ImageExtractor.class, TextExtractor.class})
public class ExtractorTest {
    private BaseExtractor baseExtractor;

    @Mock
    private VoiceService voiceService;

    @Mock
    private ChatModel chatModel;

    @Mock
    private ToolService toolService;

    @Mock
    private ToolExecuteService toolExecuteService;

    @Test
    @DisplayName("测试音频提取成功")
    void shouldOkWhenExtractAudio() {
        String mockResult = "Test Result";
        when(this.voiceService.getText(anyString())).thenReturn(mockResult);
        this.baseExtractor = new AudioExtractor(this.voiceService);
        assertThat(this.baseExtractor.extract("http://mockurl.mp3", new HashMap<>())).isEqualTo(mockResult);
    }

    @Test
    @DisplayName("测试图片提取成功")
    void shouldOkWhenExtractImage() {
        String mockResult = "Test Result";
        Choir<ChatMessage> mockChatMessage = Choir.just(new AiMessage(mockResult));
        when(this.chatModel.generate(any(), any())).thenReturn((mockChatMessage));
        this.baseExtractor = new ImageExtractor(this.chatModel);
        Map<String, Object> mockMap = new HashMap<>();
        mockMap.put("prompt", "mockprompt");
        assertThat(this.baseExtractor.extract("http://mockurl.png", mockMap)).isEqualTo(mockResult);
    }

    @Test
    @DisplayName("测试文本提取成功")
    void shouldOkWhenExtractText() {
        String mockResult = "Test Result";
        String mockUniqueName = "1234-5678";
        ToolData mockTool = new ToolData();
        mockTool.setName("impl-aipp-file-tool-extract");
        mockTool.setUniqueName(mockUniqueName);
        when(this.toolService.getTools(anyString())).thenReturn(Collections.singletonList(mockTool));
        when(this.toolExecuteService.execute(eq(mockUniqueName), anyMap())).thenReturn(mockResult);
        this.baseExtractor = new TextExtractor(this.toolService, this.toolExecuteService);
        assertThat(this.baseExtractor.extract("http://mockurl.png", new HashMap<>())).isEqualTo(mockResult);
    }
}
