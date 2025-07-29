/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.dynamicform.DynamicFormService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.dummy.OperationContextDummy;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.service.impl.AippLogServiceImpl;
import modelengine.fit.jober.aipp.service.impl.AopAippLogServiceImpl;
import modelengine.fit.jober.aipp.util.SensitiveFilterTools;
import modelengine.fit.jober.common.RangedResultSet;

import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class AippLogServiceTest {
    private static final String DUMMY_ID = "someRandomId";
    private static final String DUMMY_AIPP_TYPE = "normal";
    private static final String DUMMY_VERSION = "1.0.0";
    private static final String DUMMY_LOG_MSG = "some random log message";
    private static final String DUMMY_ACCOUNT = "z00000001";
    private static final String DUMMY_META_VERSION_NEW = "1.0.1";
    private static final String DUMMY_PATH = "/123";

    @InjectMocks
    private AippLogServiceImpl aippLogService;
    @InjectMocks
    private AopAippLogServiceImpl aopAippLogService;
    @Mock
    private AippLogMapper aippLogMapperMock;
    @Mock
    private AippChatMapper aippChatMapperMock;
    @Mock
    private DynamicFormService dynamicFormServiceMock;
    @Mock
    private UploadedFileManageService uploadedFileManageServiceMock;
    @Mock
    private SensitiveFilterTools sensitiveFilterTools;
    @Mock
    private AppTaskInstanceService appTaskInstanceService;
    @Mock
    private AppTaskService appTaskService;
    @Mock
    private AippLogRepository aippLogRepository;

    private AtomicLong logId;
    private Function<AippInstLogType, AippInstLog> generateAippInstLogFunc;

    static Stream<AippLogCreateDto> invalidAippLogCreateDtoCreatorForTest() {
        return Stream.of(AippLogCreateDto.builder()
                        .aippId(null)
                        .instanceId(DUMMY_ID)
                        .logData(DUMMY_LOG_MSG)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(DUMMY_ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(null)
                        .logData(DUMMY_LOG_MSG)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(DUMMY_ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(DUMMY_ID)
                        .logData(null)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(DUMMY_ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(DUMMY_ID)
                        .logData(DUMMY_LOG_MSG)
                        .logType(null)
                        .createUserAccount(DUMMY_ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(DUMMY_ID)
                        .logData(DUMMY_LOG_MSG)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(null)
                        .build());
    }

    @BeforeEach
    void setUp() {
        logId = new AtomicLong(1L);
        generateAippInstLogFunc = (AippInstLogType logType) -> {
            AippInstLog log = new AippInstLog();
            log.setLogId(logId.get());
            logId.incrementAndGet();
            log.setLogType(logType.name());
            return log;
        };
    }

    private void mockTask() {
        AppTask task = generateTask();
        when(this.appTaskService.getTasksByAppId(any(), any(), any())).thenReturn(List.of(task));
    }

    @Test
    void shouldInsertIntoDbWhenCallInsertLog() {
        AippLogCreateDto dummyCreateDto = AippLogCreateDto.builder()
                .aippId(DUMMY_ID)
                .version(DUMMY_VERSION)
                .aippType(AippTypeEnum.NORMAL.name())
                .instanceId(DUMMY_ID)
                .logData(DUMMY_LOG_MSG)
                .logType(AippInstLogType.MSG.name())
                .createUserAccount(DUMMY_ACCOUNT)
                .path(DUMMY_PATH)
                .build();
        String returnedLogId = this.aopAippLogService.insertLog(dummyCreateDto);
        Assertions.assertEquals(returnedLogId, dummyCreateDto.getLogId());
        verify(aippLogMapperMock, times(1)).insertOne(eq(dummyCreateDto));
    }

    @Test
    void shouldDeleteWhenCallDeleteLogWithLastNotRunning() {
        final String dummyAippInstanceStatus = MetaInstStatusEnum.ARCHIVED.name();
        final String dummyInstId = DUMMY_LOG_MSG;
        this.mockTask();
        when(this.aippLogMapperMock.selectNormalInstanceIdOrderByTimeDesc(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type()),
                eq(OperationContextDummy.DUMMY_ACCOUNT))).thenReturn(Collections.singletonList(dummyInstId));
        when(this.appTaskInstanceService.getTaskId(any())).thenReturn(DUMMY_ID);
        when(this.appTaskInstanceService.getInstance(any(), any(), any())).thenReturn(Optional.of(
                AppTaskInstance.asEntity().setInstanceId(dummyInstId).setStatus(dummyAippInstanceStatus).build()));
        aippLogService.deleteAippInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        verify(aippLogMapperMock, times(1)).selectNormalInstanceIdOrderByTimeDesc(any(), any(), any());
        verify(appTaskInstanceService, times(1)).getInstance(any(), any(), any());
        verify(uploadedFileManageServiceMock, times(1)).cleanAippFiles(any());
        verify(aippLogMapperMock, times(1)).delete(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(AippTypeEnum.NORMAL.name()).type()),
                eq(OperationContextDummy.DUMMY_ACCOUNT),
                isNull());
    }

    AppTask generateTask() {
        return AppTask.asEntity()
                .setName("testName")
                .setAppSuiteId(DUMMY_ID)
                .setVersion(DUMMY_META_VERSION_NEW)
                .setCreator("testUser")
                .setCreationTime(LocalDateTime.now())
                .setLastModificationTime(LocalDateTime.now())
                .setAippType(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type())
                .setAppId(DUMMY_ID)
                .build();
    }

    @Test
    void shouldNotDeleteWhenCallDeleteLogWithLastRunning() {
        final String dummyAippInstanceStatus = MetaInstStatusEnum.RUNNING.name();
        final String dummyInstId = DUMMY_LOG_MSG;
        this.mockTask();
        when(this.aippLogMapperMock.selectNormalInstanceIdOrderByTimeDesc(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type()),
                eq(OperationContextDummy.DUMMY_ACCOUNT))).thenReturn(Collections.singletonList(dummyInstId));
        when(this.appTaskInstanceService.getTaskId(any())).thenReturn(DUMMY_ID);
        when(this.appTaskInstanceService.getInstance(any(), any(), any())).thenReturn(Optional.of(
                AppTaskInstance.asEntity().setInstanceId(dummyInstId).setStatus(dummyAippInstanceStatus).build()));
        aippLogService.deleteAippInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        verify(aippLogMapperMock, times(1)).selectNormalInstanceIdOrderByTimeDesc(any(), any(), any());
        verify(appTaskInstanceService, times(1)).getInstance(any(), any(), any());
        verify(aippLogMapperMock, times(1)).delete(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.NORMAL.name()),
                eq(OperationContextDummy.DUMMY_ACCOUNT),
                eq(dummyInstId));
        verify(uploadedFileManageServiceMock, never()).cleanAippFiles(any());
    }

    @Test
    void shouldDoNothingWhenCallDeleteLogWithNoInstId() {
        this.mockTask();
        when(aippLogMapperMock.selectNormalInstanceIdOrderByTimeDesc(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type()),
                eq(OperationContextDummy.DUMMY_ACCOUNT))).thenReturn(Collections.emptyList());
        aippLogService.deleteAippInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        verify(aippLogMapperMock, times(1)).selectNormalInstanceIdOrderByTimeDesc(any(), any(), any());
        verify(appTaskInstanceService, never()).getInstance(any(), any(), any());
        verify(aippLogMapperMock, never()).delete(any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("invalidAippLogCreateDtoCreatorForTest")
    void shouldThrowWhenCallInsertLogWithInvalidDto(AippLogCreateDto invalidDto) {
        Assertions.assertThrows(AippParamException.class, () -> this.aopAippLogService.insertLog(invalidDto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid datetime string", "2000/01/01 00:00:00"})
    void shouldReturnAllWhenCallQueryInstanceLogSinceWithInvalidTimeString(String datetimeString) {
        when(aippLogMapperMock.selectWithCondition(argThat(cond -> cond.getInstanceId().equals(DUMMY_ID)
                && Objects.isNull(cond.getAfterAt())))).thenReturn(Stream.of(AippInstLogType.MSG,
                AippInstLogType.FORM,
                AippInstLogType.ERROR,
                AippInstLogType.MSG,
                AippInstLogType.QUESTION,
                AippInstLogType.HIDDEN_QUESTION,
                AippInstLogType.HIDDEN_MSG,
                AippInstLogType.HIDDEN_FORM,
                AippInstLogType.FILE).map(generateAippInstLogFunc).collect(Collectors.toList()));
        String timeString = datetimeString.isEmpty() ? null : datetimeString;
        List<AippInstLog> result = aippLogService.queryInstanceLogSince(DUMMY_ID, timeString);
        Assertions.assertEquals(5, result.size());
        List<Long> logIdSequence = result.stream().map(AippInstLog::getLogId).collect(Collectors.toList());
        Assertions.assertIterableEquals(logIdSequence.stream().sorted().collect(Collectors.toList()),
                logIdSequence,
                "logId乱序");
        Assertions.assertEquals(AippInstLogType.MSG.name(), result.get(0).getLogType());
        Assertions.assertEquals(AippInstLogType.ERROR.name(), result.get(1).getLogType());
        Assertions.assertEquals(AippInstLogType.MSG.name(), result.get(2).getLogType());
        Assertions.assertEquals(AippInstLogType.QUESTION.name(), result.get(3).getLogType());
        Assertions.assertEquals(AippInstLogType.FILE.name(), result.get(4).getLogType());
    }

    @Test
    void shouldQueryDbWithTimeWhenCallQueryInstanceLogSinceWithTimeString() {
        String afterTimeString = "2020-01-01 00:00:01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        LocalDateTime afterTimestamp = LocalDateTime.parse(afterTimeString, formatter);
        // 截止时间后的记录
        List<AippInstLog> dummyAfterAippInstLog =
                Stream.of(AippInstLogType.MSG, AippInstLogType.FORM, AippInstLogType.ERROR)
                        .map(generateAippInstLogFunc)
                        .peek(logData -> logData.setCreateAt(afterTimestamp))
                        .collect(Collectors.toList());
        when(aippLogMapperMock.selectWithCondition(argThat(cond -> cond.getInstanceId().equals(DUMMY_ID)
                && cond.getAfterAt().equals(afterTimestamp)))).thenReturn(dummyAfterAippInstLog);

        List<AippInstLog> result = aippLogService.queryInstanceLogSince(DUMMY_ID, afterTimeString);
        Assertions.assertEquals(2, result.size());
        List<Long> logIdSequence = result.stream().map(AippInstLog::getLogId).collect(Collectors.toList());
        Assertions.assertIterableEquals(logIdSequence.stream().sorted().collect(Collectors.toList()),
                logIdSequence,
                "logId乱序");
        Assertions.assertEquals(AippInstLogType.MSG.name(), result.get(0).getLogType());
        Assertions.assertEquals(AippInstLogType.ERROR.name(), result.get(1).getLogType());
    }

    @Test
    void shouldReturnEmptyListWhenQueryAippRecentInstLogWithDbReturnNull() {
        this.mockTask();
        Assertions.assertTrue(aippLogService.queryAippRecentInstLog(DUMMY_ID,
                DUMMY_AIPP_TYPE,
                OperationContextDummy.getDummy()).isEmpty());
        verify(dynamicFormServiceMock, never()).queryFormDetailByPrimaryKey(any(), any(), any());
        verify(this.appTaskInstanceService, never()).getInstance(any(), any(), any());
    }

    @Test
    void shouldOkWhenCallDeleteLog() {
        List<Long> list = new ArrayList<>();
        list.add(123L);
        this.aippLogService.deleteLogs(list);
        verify(aippLogMapperMock, times(1)).deleteInstanceLogs(list);
    }

    @Test
    void shouldReturnWhenCallDeleteLogWithNullParam() {
        List<Long> list = new ArrayList<>();
        this.aippLogService.deleteLogs(list);
        verify(aippLogMapperMock, times(0)).deleteInstanceLogs(list);
    }

    @Test
    void shouldOkWhenCallQueryChatRecentChatLog() {
        List<String> instanceIds = new ArrayList<>(Arrays.asList("1", "2", "3"));
        when(this.aippChatMapperMock.selectInstanceByChat(any(), any())).thenReturn(instanceIds);
        when(this.appTaskInstanceService.getTaskId(any())).thenReturn("testMetaId");
        AppTaskInstance appTaskInstance1 = spy(AppTaskInstance.asEntity().setInstanceId("1").build());
        AppTaskInstance appTaskInstance2 = spy(AppTaskInstance.asEntity().setInstanceId("2").build());
        AppTaskInstance appTaskInstance3 = spy(AppTaskInstance.asEntity().setInstanceId("3").build());
        AppTask appTask = AppTask.asEntity().setTaskId("version1").setAppId("app1").build();
        when(this.appTaskService.getTasksByAppId(any(), any())).thenReturn(List.of(appTask));
        when(this.appTaskInstanceService.getInstance(anyString(), eq("1"), any(OperationContext.class))).thenReturn(
            Optional.of(appTaskInstance1));
        when(this.appTaskInstanceService.getInstance(anyString(), eq("2"), any(OperationContext.class))).thenReturn(
            Optional.of(appTaskInstance2));
        when(this.appTaskInstanceService.getInstance(anyString(), eq("3"), any(OperationContext.class))).thenReturn(
            Optional.of(appTaskInstance3));
        when(this.appTaskService.getTasks(any(), any())).thenReturn(RangedResultSet.create(List.of(appTask), 0, 1, 1));
        MockedStatic<AppTaskInstance> mockedStatic = mockStatic(AppTaskInstance.class);

        AippInstLogDataDto logDataDto1 = getMockLogDataDto("1");
        AippInstLogDataDto logDataDto2 = getMockLogDataDto("2");
        AippInstLogDataDto logDataDto3 = getMockLogDataDto("3");
        mockedStatic.when(() -> AppTaskInstance.toLogDataDto(appTaskInstance1)).thenReturn(Optional.of(logDataDto1));
        mockedStatic.when(() -> AppTaskInstance.toLogDataDto(appTaskInstance2)).thenReturn(Optional.of(logDataDto2));
        mockedStatic.when(() -> AppTaskInstance.toLogDataDto(appTaskInstance3)).thenReturn(Optional.of(logDataDto3));

        List<AippInstLogDataDto> list = this.aippLogService.queryChatRecentChatLog("1", "1", new OperationContext());
        Assertions.assertEquals(list.get(0).getInstanceId(), "1");
        Assertions.assertEquals(list.get(1).getInstanceId(), "2");
        Assertions.assertEquals(list.get(2).getInstanceId(), "3");
        mockedStatic.close();
    }

    private AippInstLogDataDto getMockLogDataDto(String instanceId) {
        AippInstLogDataDto logDataDto = mock(AippInstLogDataDto.class);
        when(logDataDto.getCreateAt()).thenReturn(LocalDateTime.now());
        when(logDataDto.getInstanceId()).thenReturn(instanceId);
        when(logDataDto.getAippId()).thenReturn("aippId");
        return logDataDto;
    }

    @Test
    void testQueryRecentLogsSinceResume() {
        List<String> instanceIds = new ArrayList<>(List.of("1"));
        AppTaskInstance appTaskInstance = AppTaskInstance.asEntity().setInstanceId("1").build();
        when(this.aippLogMapperMock.selectRecentAfterResume(any(), any(), any())).thenReturn(instanceIds);
        when(this.appTaskInstanceService.getInstance(any(), any(), any())).thenReturn(
                Optional.of(appTaskInstance));
        MockedStatic<AppTaskInstance> mockedStatic = mockStatic(AppTaskInstance.class);

        AippInstLogDataDto logDataDto = mock(AippInstLogDataDto.class);
        when(logDataDto.getInstanceId()).thenReturn("1");
        mockedStatic.when(() -> AppTaskInstance.toLogDataDto(appTaskInstance)).thenReturn(Optional.of(logDataDto));

        List<AippInstLogDataDto> list = this.aippLogService
                .queryRecentLogsSinceResume("1", "1", OperationContextDummy.getDummy());
        Assertions.assertEquals(list.get(0).getInstanceId(), "1");
        mockedStatic.close();
    }

    @Test
    void testQueryAippRecentInstLogAfterSplice() {
        List<AippInstLog> aippInstLogList = generateAippInstLogList();
        List<String> instanceIds = new ArrayList<>(Arrays.asList("1", "2", "3"));
        when(this.aippLogMapperMock.selectRecentInstanceId(any(), any(), any(), any())).thenReturn(instanceIds);
        when(this.aippLogService.queryBatchAndFilterFullLogsByLogType(instanceIds, any())).thenReturn(aippInstLogList);
        when(this.appTaskInstanceService.getInstance(any(), any(), any())).thenReturn(
                Optional.of(AppTaskInstance.asEntity().setInstanceId("1").build()));
        List<AippInstLogDataDto> list = this.aippLogService
                .queryAippRecentInstLogAfterSplice("1", "1", 3, OperationContextDummy.getDummy());
        Assertions.assertEquals(list.get(0).getInstanceId(), "1");
        Assertions.assertEquals(list.get(1).getInstanceId(), "2");
        Assertions.assertEquals(list.get(2).getInstanceId(), "3");
    }

    List<AippInstLog> generateAippInstLogList() {
        List<AippInstLog> aippInstLogList = new ArrayList<>();
        AippInstLog aippInstLog1 = AippInstLog.builder().aippId("1").logId(1L).instanceId("1")
                .logType("MSG").path("/1").createAt(LocalDateTime.now()).build();
        aippInstLogList.add(aippInstLog1);
        AippInstLog aippInstLog2 = AippInstLog.builder().aippId("1").logId(2L).instanceId("2")
                .logType("MSG").path("/2").createAt(LocalDateTime.now().plusMinutes(1)).build();
        aippInstLogList.add(aippInstLog2);
        AippInstLog aippInstLog3 = AippInstLog.builder().aippId("1").logId(3L).instanceId("3")
                .logType("MSG").path("/3").createAt(LocalDateTime.now().plusMinutes(2)).build();
        aippInstLogList.add(aippInstLog3);
        return aippInstLogList;
    }

    @Test
    void shouldReturnNullWhenCallInsertLogWithInvalidFormData() {
        AippLogData aippLogData = AippLogData.builder().formId("").build();
        Map<String, Object> businessData = MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.BS_HTTP_CONTEXT_KEY, "{\"account\":\"123\"}")
                .build();
        Assertions.assertNull(this.aippLogService.insertLogWithInterception(AippInstLogType.FORM.name(), aippLogData, businessData));
    }

    @Test
    void shouldThrowWhenCallInsertLogWithInvalidMsgData() {
        AippLogData aippLogData = AippLogData.builder().msg("你好").build();
        Map<String, Object> businessData = MapBuilder.get(() -> new HashMap<String, Object>())
                .put(AippConst.BS_HTTP_CONTEXT_KEY, "{\"account\":\"123\"}").build();
        Assertions.assertThrows(NullPointerException.class,
                () -> this.aippLogService.insertLogWithInterception(AippInstLogType.MSG.name(), aippLogData, businessData));
    }

    @Test
    void testAppChatRsp() {
        AppChatRsp appChatRsp = AppChatRsp.builder().logId("123").build();
        Assertions.assertEquals(appChatRsp.getLogId(), "123");
    }

    @Test
    void shouldThrowWhenUpdateLogWithNullParam() {
        Assertions.assertThrows(AippParamException.class,
                () -> this.aippLogService.updateLog(null, "logType", "logData"));
    }

    @Test
    void shouldOkWhenUpdateLog() {
        this.aippLogService.updateLog(123L, "logType", "logData");
        verify(this.aippLogRepository, times(1))
                .updateDataAndType(123L, "logType", "logData");
    }
}
