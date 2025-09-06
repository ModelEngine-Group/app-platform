/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import com.opencsv.CSVWriter;

import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.repository.AippInstanceLogRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static modelengine.fit.jober.aipp.service.scheduletask.AppBuilderDbCleanScheduler.FILE_MAX_NUM;

/**
 * 应用实例日志清理器。
 *
 * @author 杨祥宇
 * @since 2025-04-15
 */
@Component
public class AippInstanceLogCleaner {
    private static final Logger log = Logger.get(AippInstanceLogCleaner.class);
    private static final String FILE_NAME = "aipp-instance-log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String CONNECTOR = "-";

    private final AippInstanceLogRepository instanceLogRepo;
    private final CsvWriterHelper csvWriterHelper;
    private final String aippInstanceLogFilePath;

    /**
     * 表示用应用日志仓库实例构造 {@link AippInstanceLogCleaner} 的实例。
     *
     * @param instanceLogRepo 表示应用日志仓库实例的 {@link AippInstanceLogRepository}。
     * @param csvWriterHelper 表示文件写入助手实例的 {@link CsvWriterHelper}。
     * @param aippInstanceLogFilePath 表示日志文件备份路径的 {@link String}。
     */
    public AippInstanceLogCleaner(AippInstanceLogRepository instanceLogRepo, CsvWriterHelper csvWriterHelper,
            @Value("${aipp.instance.log.file.path}") String aippInstanceLogFilePath) {
        this.instanceLogRepo = instanceLogRepo;
        this.csvWriterHelper = csvWriterHelper;
        this.aippInstanceLogFilePath = aippInstanceLogFilePath;
    }

    /**
     * 清理已发布的应用对话历史记录表数据，并备份。
     *
     * @param expiredDays 表示数据最大保留时长的 {@code int}。
     * @param limit 表示批量处理数量的 {@code int}。
     */
    public void cleanAippInstanceNormalLog(int expiredDays, int limit) {
        try {
            while (true) {
                List<Long> instanceLogIds =
                        this.instanceLogRepo.getExpireInstanceLogIds(AippTypeEnum.NORMAL.type(), expiredDays, limit);
                if (instanceLogIds.isEmpty()) {
                    break;
                }
                backupData(instanceLogIds);
                this.instanceLogRepo.forceDeleteInstanceLogs(instanceLogIds);
            }
            cleanupOldBackups(FILE_MAX_NUM);
        } catch (Exception e) {
            log.error("Error occurred while business data cleaner, exception:.", e);
        }
    }

    private void backupData(List<Long> logIds) {
        List<AippInstLog> aippInstLogs = this.instanceLogRepo.selectByLogIds(logIds);
        if (CollectionUtils.isEmpty(aippInstLogs)) {
            return;
        }
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path backupPath = Paths.get(this.aippInstanceLogFilePath, FILE_NAME + CONNECTOR + currentDate + ".csv");
        try (CSVWriter csvWriter = this.csvWriterHelper.createCsvWriter(backupPath, true)) {
            List<String[]> backupData = aippInstLogs.stream().map(aippInstLog -> new String[] {
                    String.valueOf(aippInstLog.getLogId()), aippInstLog.getAippId(), aippInstLog.getVersion(),
                    aippInstLog.getInstanceId(), aippInstLog.getLogData(), aippInstLog.getLogType(),
                    String.valueOf(aippInstLog.getCreateAt()), aippInstLog.getCreateUserAccount(), aippInstLog.getPath()
            }).toList();
            csvWriter.writeAll(backupData);
        } catch (IOException e) {
            log.error("Error occurred while writing aipp-instance-log.", e);
            throw new IllegalStateException(e);
        }
    }

    private void cleanupOldBackups(int fileMaxNum) {
        File backupFolder = this.csvWriterHelper.getFile(this.aippInstanceLogFilePath);
        File[] backupFiles = backupFolder.listFiles((dir, name) -> name.startsWith(FILE_NAME) && name.endsWith(".csv"));
        if (backupFiles == null) {
            return;
        }
        List<File> sortedFiles =
                Arrays.stream(backupFiles).sorted(Comparator.comparing(File::getName).reversed()).toList();
        for (int i = fileMaxNum; i < sortedFiles.size(); i++) {
            sortedFiles.get(i).delete();
        }
    }

    /**
     * 清理调试应用对话历史记录表数据。
     *
     * @param expiredDays 表示数据最大保留时长的 {@code int}。
     * @param limit 表示批量处理数量的 {@code int}。
     */
    public void cleanAippInstancePreviewLog(int expiredDays, int limit) {
        log.info("Start cleaning aipp preview instance logs");
        try {
            while (true) {
                List<Long> instanceLogIds =
                        this.instanceLogRepo.getExpireInstanceLogIds(AippTypeEnum.PREVIEW.type(), expiredDays, limit);
                if (instanceLogIds.isEmpty()) {
                    break;
                }
                this.instanceLogRepo.forceDeleteInstanceLogs(instanceLogIds);
            }
        } catch (Exception e) {
            log.error("clean instance logs failed, exception:", e);
        }
        log.info("Finish cleaning aipp instance logs");
    }
}
