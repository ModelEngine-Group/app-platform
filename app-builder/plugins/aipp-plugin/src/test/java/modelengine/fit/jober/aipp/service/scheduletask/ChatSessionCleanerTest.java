/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opencsv.CSVWriter;

import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.repository.AippChatRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * {@link ChatSessionCleaner} 对应测试类。
 *
 * @author 杨祥宇
 * @since 2025-04-18
 */
@ExtendWith(MockitoExtension.class)
public class ChatSessionCleanerTest {
    @Mock
    private AippChatRepository chatRepo;

    @Mock
    private CsvWriterHelper csvWriterHelper;

    private ChatSessionCleaner chatSessionCleaner;

    @BeforeEach
    void setup() {
        String chatSessionFilePath = "/var/share/backup/chat-session/";
        this.chatSessionCleaner = new ChatSessionCleaner(this.chatRepo, this.csvWriterHelper, chatSessionFilePath);
    }

    @Test
    @DisplayName("测试没有过期数据时直接返回，不会做备份和删除操作")
    void cleanNoExpiredDataShouldDoNothing() throws IOException {
        when(this.chatRepo.getExpiredChatIds(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        this.chatSessionCleaner.clean(30, 100);
        verify(this.chatRepo, never()).forceDeleteChat(anyList());
        verify(this.csvWriterHelper, never()).createCsvWriter(any(), anyBoolean());
    }

    @Test
    @DisplayName("测试有过期数据时备份并成功删除")
    void cleanWithExpiredDataShouldBackupAndDelete() throws Exception {
        List<String> expiredChatIds = List.of("chat1", "chat2");
        when(this.chatRepo.getExpiredChatIds(anyInt(), anyInt())).thenReturn(expiredChatIds)
                .thenReturn(Collections.emptyList());
        when(this.chatRepo.selectByChatIds(expiredChatIds)).thenReturn(List.of(new ChatInfo(), new ChatInfo()));
        when(this.chatRepo.selectTaskInstanceRelationsByChatIds(expiredChatIds)).thenReturn(List.of(new ChatAndInstanceMap(),
                new ChatAndInstanceMap()));
        CSVWriter csvWriter = mock(CSVWriter.class);
        when(this.csvWriterHelper.createCsvWriter(any(), anyBoolean())).thenReturn(csvWriter);
        File file = mock(File.class);
        File csvFile = mock(File.class);
        when(this.csvWriterHelper.getFile(anyString())).thenReturn(file);
        when(file.listFiles(any(FilenameFilter.class))).thenReturn(new File[] {csvFile});
        this.chatSessionCleaner.clean(30, 100);
        verify(this.chatRepo, times(1)).forceDeleteChat(expiredChatIds);
        verify(this.csvWriterHelper, atLeast(2)).createCsvWriter(any(), eq(true));
        verify(csvWriter, times(2)).writeAll(anyList());
        verify(csvFile, times(0)).delete();
    }

    @Test
    @DisplayName("测试数据备份失败时不会删除过期数据")
    void cleanShouldNotDeleteWhenBackupFailed() throws Exception {
        List<String> expiredChatIds = List.of("chat1", "chat2");
        when(this.chatRepo.getExpiredChatIds(anyInt(), anyInt())).thenReturn(expiredChatIds)
                .thenReturn(Collections.emptyList());
        when(this.chatRepo.selectByChatIds(anyList())).thenReturn(Collections.singletonList(new ChatInfo()));
        CSVWriter csvWriter = mock(CSVWriter.class);
        when(this.csvWriterHelper.createCsvWriter(any(), anyBoolean())).thenThrow(new IOException("error"));
        this.chatSessionCleaner.clean(30, 100);
        verify(this.chatRepo, times(1)).getExpiredChatIds(anyInt(), anyInt());
        verify(this.chatRepo, times(0)).forceDeleteChat(anyList());
        verify(csvWriter, times(0)).writeAll(anyList());
    }
}