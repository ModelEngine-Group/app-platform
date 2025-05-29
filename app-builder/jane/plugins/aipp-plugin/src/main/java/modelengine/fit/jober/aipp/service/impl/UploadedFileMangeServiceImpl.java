/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.dto.aipplog.AippUploadedFileInfoDto;
import modelengine.fit.jober.aipp.mapper.AippUploadedFileMapper;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.AippFileUtils;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.IoUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * aipp上传的文件管理服务实现, 主要实现
 * 1. 用户主动清理文件
 * 2. 30天超期文件自动清理
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class UploadedFileMangeServiceImpl implements UploadedFileManageService {
    /**
     * 文件记录可删除标志
     */
    public static final int REMOVABLE = 1;

    /**
     * 文件记录不可删除标志
     */
    public static final int IRREMOVABLE = 0;

    private static final Logger log = Logger.get(UploadedFileMangeServiceImpl.class);

    private final AippUploadedFileMapper aippUploadedFileMapper;

    public UploadedFileMangeServiceImpl(AippUploadedFileMapper aippUploadedFileMapper) {
        this.aippUploadedFileMapper = aippUploadedFileMapper;
    }

    /**
     * 清除储存时长超过30天（在sql中限制）的文件, 按CRON表达式每天凌晨4点启动一次清理
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 4 * * ? ")
    public void cleanExpiredFiles() {
        try {
            List<String> fileNames = aippUploadedFileMapper.queryExpiredFiles();
            List<String> filesDeleted = batchDeleteFiles(fileNames);
            log.info("clean expired files, expect {}, cleaned {}.", fileNames.size(), filesDeleted.size());
            if (!filesDeleted.isEmpty()) {
                aippUploadedFileMapper.deleteFileRecords(null, filesDeleted);
            }
        } catch (AippException e) {
            log.error("delete files failed on exception. reason: {}", e.getMessage());
        }
    }

    /**
     * 根据aipp和用户删除相关的文件
     *
     * @param aippIds aipp id
     */
    @Override
    public void cleanAippFiles(List<String> aippIds) {
        log.debug("clean files for aipp: {}.", aippIds);
        if (CollectionUtils.isEmpty(aippIds)) {
            return;
        }
        aippIds.forEach(this::delete);
    }

    private void delete(String aippId) {
        List<String> filesToDelete = aippUploadedFileMapper.queryFilesByUserAipp(aippId);
        List<String> filesDeleted = batchDeleteFiles(filesToDelete);
        if (!filesDeleted.isEmpty()) {
            aippUploadedFileMapper.deleteFileRecords(aippId, filesDeleted);
        }
    }

    private List<String> batchDeleteFiles(List<String> fileNames) {
        List<String> fileDeleted = new ArrayList<>();
        for (String filename : fileNames) {
            File fileToDelete = Paths.get(filename).toFile();
            if (!fileToDelete.exists()) {
                fileDeleted.add(filename);
                continue;
            }
            try {
                FileUtils.delete(fileToDelete);
                if (!fileToDelete.exists()) {
                    fileDeleted.add(filename);
                }
            } catch (IllegalStateException e) {
                log.error("delete file {} failed. reason: {}", filename, e.getMessage());
            }
        }
        return fileDeleted;
    }

    /**
     * 添加文件记录
     *
     * @param aipp 文件关联aipp Id
     * @param createUserAccount 文件关联创建人工号
     * @param filename 文件名或者文件夹名
     * @param fileUuid 文件uuid
     */
    @Override
    public void addFileRecord(String aipp, String createUserAccount, String filename, String fileUuid) {
        if (Stream.of(createUserAccount, filename).allMatch(s -> s != null && !s.isEmpty())) {
            aippUploadedFileMapper.insertFileRecord(new AippUploadedFileInfoDto(aipp,
                    createUserAccount,
                    filename,
                    fileUuid));
        }
    }

    /**
     * 更改文件可清理标志
     *
     * @param fileName 文件名称
     * @param status 文件是否可以清理的标识
     */
    @Override
    public void changeRemovable(String fileName, Integer status) {
        aippUploadedFileMapper.updateRecord(null, fileName, status);
    }

    @Override
    public void changeRemovableWithFileUuid(String fileUuid, Integer status) {
        aippUploadedFileMapper.updateRecordWithFileUuid(fileUuid, status);
    }

    @Override
    public void updateRecord(String appId, String fileName, Integer status) {
        aippUploadedFileMapper.updateRecord(appId, fileName, status);
    }

    @Override
    public String copyIconFiles(String icon, String aippId, String operator) throws IOException {
        File originIcon = FileUtils.canonicalize(AippFileUtils.getFileNameFromIcon(icon));
        String originIconName = originIcon.getName();
        String copiedIconName = UUID.randomUUID() + FileUtils.extension(originIconName);
        File copiedIcon = FileUtils.canonicalize(originIcon.getCanonicalPath().replace(originIconName, copiedIconName));
        IoUtils.copy(originIcon, copiedIcon);
        this.addFileRecord(
                aippId,
                operator,
                copiedIcon.getCanonicalPath(),
                Entities.generateId()
        );
        return icon.replace(originIconName, copiedIconName);
    }
}
