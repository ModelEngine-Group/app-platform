/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import static modelengine.fit.jober.aipp.service.scheduletask.AppBuilderDbCleanScheduler.FILE_MAX_NUM;

import com.opencsv.CSVWriter;

import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.repository.AippChatRepository;
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

/**
 * 聊天会话数据库表清理器。
 *
 * @author 杨祥宇
 * @since 2025-04-15
 */
@Component
public class ChatSessionCleaner {
    private static final Logger log = Logger.get(AippInstanceLogCleaner.class);
    private static final String CHAT_SESSION_FILE_NAME = "chat-session";
    private static final String INSTANCE_RELATIONS_FILE_NAME = "instance-relations";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String CONNECTOR = "-";

    private final AippChatRepository chatRepo;
    private final CsvWriterHelper csvWriterHelper;
    private final String chatSessionFilePath;

    /**
     * 表示用对话仓库和文件写入助手来构造 {@link ChatSessionCleaner} 的实例。
     *
     * @param chatRepo 表示对话仓库实例的 {@link AippChatRepository}。
     * @param csvWriterHelper 表示文件写入助手实例的 {@link CsvWriterHelper}。
     * @param chatSessionFilePath 表示对话文件路径的 {@link String}。
     */
    public ChatSessionCleaner(AippChatRepository chatRepo, CsvWriterHelper csvWriterHelper,
                              @Value("${chat.session.file.path}") String chatSessionFilePath) {
        this.chatRepo = chatRepo;
        this.csvWriterHelper = csvWriterHelper;
        this.chatSessionFilePath = chatSessionFilePath;
    }

    /**
     * 清理对话会话相关数据，并备份。
     *
     * @param expiredDays 表示数据最大保留天数的 {@code int}。
     * @param limit 表示批量处理数量的 {@code int}。
     */
    public void clean(int expiredDays, int limit) {
        try {
            while (true) {
                List<String> expiredChatIds = this.chatRepo.getExpiredChatIds(expiredDays, limit);
                if (expiredChatIds.isEmpty()) {
                    break;
                }
                backupChatSessionData(expiredChatIds);
                backupInstanceRelationData(expiredChatIds);
                this.chatRepo.forceDeleteChat(expiredChatIds);
            }
            cleanupOldBackups(CHAT_SESSION_FILE_NAME, FILE_MAX_NUM);
            cleanupOldBackups(INSTANCE_RELATIONS_FILE_NAME, FILE_MAX_NUM);
        } catch (Exception e) {
            log.error("Error occurred while business data cleaner, exception:.", e);
        }
    }

    private void backupChatSessionData(List<String> chatIds) {
        List<ChatInfo> chatSessionPos = this.chatRepo.selectByChatIds(chatIds);
        if (CollectionUtils.isEmpty(chatSessionPos)) {
            return;
        }
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path backupPath = Paths.get(this.chatSessionFilePath, CHAT_SESSION_FILE_NAME + CONNECTOR + currentDate + ".csv");
        try (CSVWriter csvWriter = this.csvWriterHelper.createCsvWriter(backupPath, true)) {
            List<String[]> backupData = chatSessionPos.stream().map(session -> new String[]{
                    session.getChatId(), session.getAppId(), session.getVersion(), session.getChatName(),
                    session.getAttributes(), String.valueOf(session.getCreateTime()), session.getCreator(),
                    String.valueOf(session.getUpdateTime()), session.getUpdater()
            }).toList();
            csvWriter.writeAll(backupData);
        } catch (IOException e) {
            log.error("Error occurred while backup chat session data, exception:", e);
            throw new RuntimeException(e);
        }
    }

    private void backupInstanceRelationData(List<String> chatIds) {
        List<ChatAndInstanceMap> relationPos = this.chatRepo.selectTaskInstanceRelationsByChatIds(chatIds);
        if (CollectionUtils.isEmpty(relationPos)) {
            return;
        }
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path backupPath = Paths.get(this.chatSessionFilePath, INSTANCE_RELATIONS_FILE_NAME + CONNECTOR + currentDate + ".csv");
        try (CSVWriter csvWriter = this.csvWriterHelper.createCsvWriter(backupPath, true)) {
            List<String[]> backupData = relationPos.stream().map(relationPo -> new String[]{
                    relationPo.getMsgId(), relationPo.getChatId(), relationPo.getInstanceId(),
                    String.valueOf(relationPo.getCreateTime()), relationPo.getCreator(),
                    String.valueOf(relationPo.getUpdateTime()), relationPo.getUpdater()
            }).toList();
            csvWriter.writeAll(backupData);
        } catch (IOException e) {
            log.error("Error occurred while backup instance relation data, exception:", e);
            throw new RuntimeException(e);
        }
    }

    private void cleanupOldBackups(String fileName, int fileMaxNum) {
        try {
            File backupFolder = this.csvWriterHelper.getFile(this.chatSessionFilePath);
            File[] backupFiles =
                    backupFolder.listFiles((dir, name) -> name.startsWith(fileName) && name.endsWith(".csv"));
            if (backupFiles == null) {
                return;
            }
            List<File> sortedFiles =
                    Arrays.stream(backupFiles).sorted(Comparator.comparing(File::getName).reversed()).toList();
            for (int i = fileMaxNum; i < sortedFiles.size(); i++) {
                sortedFiles.get(i).delete();
            }
        } catch (Exception e) {
            log.error("Cleanup old backups failed, filename:{}, exception:", fileName, e);
        }
    }
}
