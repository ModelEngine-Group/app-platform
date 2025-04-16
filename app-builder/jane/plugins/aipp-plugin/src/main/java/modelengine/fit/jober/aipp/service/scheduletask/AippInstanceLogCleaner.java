/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import static modelengine.fit.jober.aipp.service.scheduletask.AppBuilderDbCleanSchedule.FILE_MAX_NUM;

import com.opencsv.CSVWriter;

import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.repository.AippInstanceLogRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * AippInstanceLog数据库表清理任务
 *
 * @author 杨祥宇
 * @since 2025-04-15
 */
@Component
public class AippInstanceLogCleaner {
    private static final Logger log = Logger.get(AippInstanceLogCleaner.class);

    private static final String AIPP_INSTANCE_LOG_FILE_PATH = "/var/share/backup/aipp-instance-log/";

    private static final String FILE_NAME = "aipp-instance-log";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AippInstanceLogRepository instanceLogRepo;

    public AippInstanceLogCleaner(AippInstanceLogRepository instanceLogRepo) {
        this.instanceLogRepo = instanceLogRepo;
    }

    public void cleanAippInstanceNormalLog(int ttl, int limit) {
        try {
            while (true) {
                List<Long> instanceLogIds =
                        instanceLogRepo.getExpireInstanceLogIds(AippTypeEnum.NORMAL.type(), ttl, limit);
                if (instanceLogIds.isEmpty()) {
                    break;
                }
                backupData(instanceLogIds);
                instanceLogRepo.forceDeleteInstanceLogs(instanceLogIds);
            }
            cleanupOldBackups(FILE_MAX_NUM);
        } catch (Exception e) {
            log.error("Error occurred while business data cleaner, exception:.", e);
        }
    }

    private void backupData(List<Long> logIds){
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path backupPath = Paths.get(AIPP_INSTANCE_LOG_FILE_PATH, FILE_NAME + currentDate + ".csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(backupPath.toFile(), true))) {
            List<AippInstLog> aippInstLogs = this.instanceLogRepo.selectByLogIds(logIds);
            if (CollectionUtils.isEmpty(aippInstLogs)) {
                return;
            }
            List<String[]> backupData = aippInstLogs.stream().map(aippInstLog -> new String[] {
                    String.valueOf(aippInstLog.getLogId()), aippInstLog.getAippId(), aippInstLog.getVersion(),
                    aippInstLog.getInstanceId(), aippInstLog.getLogData(), aippInstLog.getLogType(),
                    String.valueOf(aippInstLog.getCreateAt()), aippInstLog.getCreateUserAccount(), aippInstLog.getPath()
            }).toList();
            csvWriter.writeAll(backupData);
        } catch (IOException e) {
            log.error("Error occurred while writing aipp-instance-log.", e);
            throw new RuntimeException(e);
        }
    }

    private void cleanupOldBackups(int fileMaxNum) {
        File backupFolder = new File(AIPP_INSTANCE_LOG_FILE_PATH);
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

    public void cleanAippInstancePreviewLog(int ttl, int limit) {
        log.info("Start cleaning aipp preview instance logs");
        try {
            while (true) {
                List<Long> instanceLogIds =
                        instanceLogRepo.getExpireInstanceLogIds(AippTypeEnum.PREVIEW.type(), ttl, limit);
                if (instanceLogIds.isEmpty()) {
                    break;
                }
                instanceLogRepo.forceDeleteInstanceLogs(instanceLogIds);
            }
        } catch (Exception e) {
            log.error("clean instance logs failed, exception:", e);
        }
        log.info("Finish cleaning aipp instance logs");
    }
}
