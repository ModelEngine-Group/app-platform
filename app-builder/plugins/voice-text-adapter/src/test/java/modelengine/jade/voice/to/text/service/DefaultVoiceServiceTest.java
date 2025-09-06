/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.voice.to.text.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import modelengine.fel.service.pipeline.HuggingFacePipelineService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * DefaultVoiceService测试类。
 *
 * @author 张粟
 * @since 2024-08-28
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultVoiceServiceTest {
    @Mock
    private HuggingFacePipelineService mockPipelineService;

    private DefaultVoiceService defaultVoiceServiceUnderTest;

    @Before
    public void setUp() {
        defaultVoiceServiceUnderTest = new DefaultVoiceService(mockPipelineService);
    }

    /**
     * 测试语音转文字
     */
    @Test
    public void testGetText() {
        // Setup
        when(mockPipelineService.call(eq("automatic-speech-recognition"), eq("openai/whisper-large-v3"),
                anyMap())).thenReturn("mockedResult!");

        // Run the test
        final String result = defaultVoiceServiceUnderTest.getText("voicePath" + "fileName");

        // Verify the results
        assertEquals("Result", result);
    }

    /**
     * 测试文字转语音
     */
    @Test
    public void testGetVoice() {
        // Setup
        when(mockPipelineService.call(eq("text-to-speech"), eq("2Noise/ChatTTS"), anyMap())).thenReturn("data2Result");

        // Run the test
        final String result = defaultVoiceServiceUnderTest.getVoice("text", 0);

        // Verify the results
        assertEquals("Result", result);
    }
}
