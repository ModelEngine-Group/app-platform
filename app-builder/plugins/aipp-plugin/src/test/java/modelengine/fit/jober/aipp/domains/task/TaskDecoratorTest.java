/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_INST_ID_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.common.globalization.LocaleService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;

/**
 * {@link TaskDecorator} 的测试类。
 *
 * @author 张越
 * @since 2025-01-13
 */
public class TaskDecoratorTest {
    private AippLogService aippLogService;
    private AppTaskInstanceService appTaskInstanceService;
    private LocaleService localeService;

    @BeforeEach
    public void setUp() {
        this.aippLogService = mock(AippLogService.class);
        this.appTaskInstanceService = mock(AppTaskInstanceService.class);
        this.localeService = mock(LocaleService.class);
    }

    @Test
    @DisplayName("异常测试")
    public void testException() {
        // given.
        AppTask appTask = Mockito.mock(AppTask.class);
        doThrow(new IllegalStateException()).when(appTask).run(any());
        when(appTask.getEntity()).thenReturn(AppTask.asEntity().setTaskId("task_1"));
        Map<String, Object> businessData = MapBuilder.<String, Object>get()
                .put(BS_AIPP_INST_ID_KEY, "instance_1")
                .build();

        doNothing().when(this.appTaskInstanceService).update(any(), any());
        when(this.localeService.localize(any())).thenReturn("xxxxxxx");
        when(this.aippLogService.insertLogWithInterception(any(), any(), any())).thenReturn("xxxxxx");

        // when.
        RunContext runContext = new RunContext(businessData, new OperationContext());
        TaskDecorator.create(appTask, this.aippLogService, this.appTaskInstanceService, this.localeService)
                .exceptionLog()
                .run(runContext);

        // then.
        ArgumentCaptor<AppTaskInstance> captor = ArgumentCaptor.forClass(AppTaskInstance.class);
        verify(this.appTaskInstanceService, times(1)).update(captor.capture(), any());
        AppTaskInstance instance = captor.getValue();
        Assertions.assertEquals("instance_1", instance.getId());
        Assertions.assertEquals("task_1", instance.getTaskId());
        Assertions.assertTrue(instance.getEntity().getStatus().isPresent());
        Assertions.assertEquals(MetaInstStatusEnum.ERROR.name(), instance.getEntity().getStatus().get());

        verify(this.localeService, times(1)).localize(eq("aipp.service.impl.AippRunTimeServiceImpl"));
        verify(this.aippLogService, times(1)).insertLogWithInterception(eq(AippInstLogType.ERROR.name()), any(), any());
    }
}
