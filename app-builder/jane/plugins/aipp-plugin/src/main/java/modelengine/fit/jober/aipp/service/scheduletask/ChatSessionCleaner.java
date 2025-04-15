/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.scheduletask;

import com.opencsv.CSVWriter;

import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.repository.AippChatRepository;
import modelengine.fitframework.annotation.Component;
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
 * ChatSession数据库表清理任务
 *
 * @author 杨祥宇
 * @since 2025-04-15
 */
@Component
public class ChatSessionCleaner {
    private static final Logger log = Logger.get(AippInstanceLogCleaner.class);

    private static final String CHAT_SESSION_FILE_PATH = "/var/share/backup/chat-session/";

    private static final String CHAT_SESSION_FILE_NAME = "chat-session";

    private static final String INSTANCE_RELATIONS_FILE_NAME = "instance-relations";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AippChatRepository chatRepo;

    public ChatSessionCleaner(AippChatRepository chatRepo) {
        this.chatRepo = chatRepo;
    }

    public void chatSessionCleaner(int ttl, int limit) {
        try {
            while (true) {
                List<String> expiredChatIds = chatRepo.getExpiredChatIds(ttl, limit);
                if (expiredChatIds.isEmpty()) {
                    break;
                }
                backupChatSessionData(expiredChatIds);
                backupInstanceRelationData(expiredChatIds);
                chatRepo.forceDeleteChat(expiredChatIds);
            }
            cleanupOldBackups(CHAT_SESSION_FILE_NAME, ttl);
            cleanupOldBackups(INSTANCE_RELATIONS_FILE_NAME, ttl);
        } catch (Exception e) {
            log.error("Error occurred while business data cleaner, exception:.", e);
        }
    }

    private void backupChatSessionData(List<String> chatIds) {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path backupPath = Paths.get(CHAT_SESSION_FILE_PATH, CHAT_SESSION_FILE_NAME + currentDate + ".csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(backupPath.toFile(), true))) {
            List<ChatInfo> chatSessionPos = this.chatRepo.selectByChatIds(chatIds);
            if (CollectionUtils.isEmpty(chatSessionPos)) {
                return;
            }
            List<String[]> backupData = chatSessionPos.stream().map(session -> new String[] {
                    session.getChatId(), session.getAppId(), session.getVersion(), session.getChatName(),
                    session.getAttributes(), String.valueOf(session.getCreateTime()), session.getCreator(),
                    String.valueOf(session.getUpdateTime()), session.getUpdater()
            }).toList();
            csvWriter.writeAll(backupData);
        } catch (IOException e) {
            log.error("Error occurred while backup chat session data, exception:", e);
        }
    }

    private void backupInstanceRelationData(List<String> chatIds) {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);
        Path backupPath = Paths.get(CHAT_SESSION_FILE_PATH, INSTANCE_RELATIONS_FILE_NAME + currentDate + ".csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(backupPath.toFile(), true))) {
            List<ChatAndInstanceMap> relationPos = this.chatRepo.selectTaskInstanceRelationsByChatIds(chatIds);
            if (CollectionUtils.isEmpty(relationPos)) {
                return;
            }
            List<String[]> backupData = relationPos.stream().map(relationPo -> new String[] {
                    relationPo.getMsgId(), relationPo.getChatId(), relationPo.getInstanceId(),
                    String.valueOf(relationPo.getCreateTime()), relationPo.getCreator(),
                    String.valueOf(relationPo.getUpdateTime()), relationPo.getUpdater()
            }).toList();
            csvWriter.writeAll(backupData);
        } catch (IOException e) {
            log.error("Error occurred while backup instance relation data, exception:", e);
        }
    }

    private void cleanupOldBackups(String fileName, int fileMaxNum) {
        try {
            File backupFolder = new File(CHAT_SESSION_FILE_PATH);
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
