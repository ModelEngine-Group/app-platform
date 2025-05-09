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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opencsv.CSVWriter;

import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.repository.AippInstanceLogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link AippInstanceLogCleaner}对应测试类。
 *
 * @author 杨祥宇
 * @since 2025-04-18
 */
@ExtendWith(MockitoExtension.class)
public class AippInstanceLogCleanerTest {
    @Mock
    private AippInstanceLogRepository instanceLogRepo;

    @Mock
    private CsvWriterHelper csvWriterHelper;

    private AippInstanceLogCleaner logCleaner;

    @BeforeEach
    void setup() {
        logCleaner = new AippInstanceLogCleaner(instanceLogRepo, csvWriterHelper);
    }

    @Test
    @DisplayName("测试清理Normal信息并备份")
    void cleanAippInstanceNormalLog_ShouldBackupAndDelete() throws Exception {
        List<Long> mockLogIds = List.of(1L, 2L);
        when(instanceLogRepo.getExpireInstanceLogIds(AippTypeEnum.NORMAL.type(), 30, 100)).thenReturn(mockLogIds)
                .thenReturn(Collections.emptyList());

        AippInstLog mockLog = new AippInstLog();
        when(instanceLogRepo.selectByLogIds(anyList())).thenReturn(List.of(mockLog));

        CSVWriter csvWriter = mock(CSVWriter.class);
        when(csvWriterHelper.createCsvWriter(any(), anyBoolean())).thenReturn(csvWriter);

        File file = mock(File.class);
        File csvFile = mock(File.class);
        when(csvWriterHelper.getFile(anyString())).thenReturn(file);
        when(file.listFiles(any(FilenameFilter.class))).thenReturn(new File[] {csvFile});

        logCleaner.cleanAippInstanceNormalLog(30, 100);

        verify(instanceLogRepo, times(2)).getExpireInstanceLogIds(anyString(), anyInt(), anyInt());
        verify(instanceLogRepo, times(1)).forceDeleteInstanceLogs(anyList());
        verify(csvWriter, times(1)).writeAll(anyList());
        verify(csvFile, times(0)).delete();
    }

    @Test
    @DisplayName("测试Normal信息备份失败时不清理数据")
    void cleanAippInstanceNormalLog_ShouldNotDeleteWhenBackupFailed() throws Exception {
        List<Long> mockLogIds = List.of(1L, 2L);
        when(instanceLogRepo.getExpireInstanceLogIds(AippTypeEnum.NORMAL.type(), 30, 100)).thenReturn(mockLogIds)
                .thenReturn(Collections.emptyList());
        when(instanceLogRepo.selectByLogIds(anyList())).thenReturn(Collections.singletonList(new AippInstLog()));

        CSVWriter csvWriter = mock(CSVWriter.class);
        when(csvWriterHelper.createCsvWriter(any(), anyBoolean())).thenThrow(new IOException("error"));

        logCleaner.cleanAippInstanceNormalLog(30, 100);

        verify(instanceLogRepo, times(1)).getExpireInstanceLogIds(anyString(), anyInt(), anyInt());
        verify(instanceLogRepo, times(0)).forceDeleteInstanceLogs(anyList());
        verify(csvWriter, times(0)).writeAll(anyList());
    }

    @Test
    public void cleanAippInstancePreviewLog_MultipleBatches_DeletesAll() {
        Mockito.when(instanceLogRepo.getExpireInstanceLogIds(Mockito.eq(AippTypeEnum.PREVIEW.type()),
                Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(Arrays.asList(1L, 2L)).thenReturn(Collections.emptyList());

        logCleaner.cleanAippInstancePreviewLog(30, 100);

        Mockito.verify(instanceLogRepo, Mockito.times(2)).getExpireInstanceLogIds(AippTypeEnum.PREVIEW.type(), 30, 100);
        Mockito.verify(instanceLogRepo).forceDeleteInstanceLogs(Arrays.asList(1L, 2L));
    }
}