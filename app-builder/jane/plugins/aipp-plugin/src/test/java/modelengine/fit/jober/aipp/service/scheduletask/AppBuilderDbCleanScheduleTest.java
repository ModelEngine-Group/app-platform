/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.repository.AippInstanceLogRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link AppBuilderDbCleanSchedule}对应测试类。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
class AppBuilderDbCleanScheduleTest {
    @Mock
    private AippInstanceLogRepository instanceLogRepository;

    @Mock
    private AppBuilderRuntimeInfoRepository runtimeInfoRepository;

    private AppBuilderDbCleanSchedule dbCleanSchedule;

    @BeforeEach
    void setUp() {
        instanceLogRepository = Mockito.mock(AippInstanceLogRepository.class);
        runtimeInfoRepository = Mockito.mock(AppBuilderRuntimeInfoRepository.class);
        dbCleanSchedule = new AppBuilderDbCleanSchedule(1, instanceLogRepository, runtimeInfoRepository);
    }

    @Test
    void testAppBuilderDbCleanScheduleSuccess() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(instanceLogRepository.getExpirePreviewInstanceLogs(anyInt(), anyInt()))
                .thenReturn(ids)
                .thenReturn(Collections.emptyList());
        when(runtimeInfoRepository.getExpiredRuntimeInfos(anyInt(), anyInt()))
                .thenReturn(ids)
                .thenReturn(Collections.emptyList());

        dbCleanSchedule.appBuilderDbCleanSchedule();

        verify(instanceLogRepository, times(2)).getExpirePreviewInstanceLogs(anyInt(), anyInt());
        verify(instanceLogRepository, times(1)).forceDeleteInstanceLogs(anyList());
        verify(runtimeInfoRepository, times(2)).getExpiredRuntimeInfos(anyInt(), anyInt());
        verify(runtimeInfoRepository, times(1)).deleteRuntimeInfos(anyList());
    }
}