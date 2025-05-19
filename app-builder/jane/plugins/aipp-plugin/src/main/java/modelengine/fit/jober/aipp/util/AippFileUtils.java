/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static modelengine.fit.jober.aipp.constant.AippConstant.NAS_SHARE_DIR;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件操作工具
 *
 * @author 刘信宏
 * @since 2024/1/22
 */
public class AippFileUtils {
    /**
     * 上传文件会上传到该共享目录。
     */
    private static final String DOWNLOAD_FILE_ORIGIN = "/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?";
    private static final Pattern PATTERN = Pattern.compile("filePath=([^&]+)");
    private static final Logger log = Logger.get(AippFileUtils.class);

    /**
     * 在本地临时目录 AippFileUtils.NAS_SHARE_DIR 创建子目录及文件。
     *
     * @param dirName 表示子目录名称的 {@link String}。
     * @param fileName 表示文件名字的 {@link String}。
     * @return 表示创建的临时文件的 {@link File}。
     * @throws IOException 创建文件出现问题时抛出该异常
     */
    public static File createFile(String dirName, String fileName) throws IOException {
        Validation.notBlank(dirName, "dirName cant be blank.");
        Validation.notBlank(fileName, "fileName cant be blank.");

        File dir = Paths.get(NAS_SHARE_DIR, dirName).toFile();
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException(dir.getCanonicalPath() + " created failed.");
            }
        }
        File docFile = Paths.get(dir.getCanonicalPath(), fileName).toFile();
        if (!docFile.exists()) {
            if (!docFile.createNewFile()) {
                log.warn(docFile.getCanonicalPath() + " already exist.");
            }
        }
        return docFile;
    }

    /**
     * 删除文件。
     *
     * @param file 文件句柄
     */
    public static void deleteFile(File file) {
        try {
            FileUtils.delete(file);
        } catch (IllegalStateException e) {
            log.warn("delete file {} failed, error = {}", file.getName(), e.getMessage());
        }
    }

    /**
     * 获得文件的可下载地址
     *
     * @param endpoint 表示app-engine启动环境地址的{@link String}
     * @param pathPrefix 表示文件url根的{@link String}
     * @param filePath 表示文件路径的{@link String}
     * @param fileName 表示下载后保存的文件名的{@link String}
     * @return 表示文件的可下载的url
     */
    public static String getFileDownloadUrl(String endpoint, String pathPrefix, String filePath, String fileName) {
        return endpoint + (StringUtils.isBlank(pathPrefix) ? StringUtils.EMPTY : pathPrefix) + DOWNLOAD_FILE_ORIGIN
                + "filePath=" + filePath + "&fileName=" + fileName;
    }

    /**
     * 获得音频文件的文件路径的url
     *
     * @param endpoint 表示app-engine启动环境地址的{@link String}
     * @param pathPrefix 表示文件url根的{@link String}
     * @param filePath 表示文件路径的{@link String}
     * @return 表示音频文件路径的url
     */
    public static String getFileDownloadFilePath(String endpoint, String pathPrefix, String filePath) {
        return endpoint + (StringUtils.isBlank(pathPrefix) ? StringUtils.EMPTY : pathPrefix) + DOWNLOAD_FILE_ORIGIN
                + "filePath=" + filePath;
    }

    /**
     * 获得app头像中的文件名称
     *
     * @param icon 表示app头像的{@link String}
     * @return 文件名称
     */
    public static String getFileNameFromIcon(String icon) {
        // 修复icon前端传入null导致后续执行异常的问题
        if (icon == null) {
            return StringUtils.EMPTY;
        }
        Matcher matcher = PATTERN.matcher(icon);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 获得文件数据。
     *
     * @param receivedFile 接收到的文件
     * @return 文件数据列表。
     */
    public static List<FileEntity> getFileEntity(PartitionedEntity receivedFile) {
        List<NamedEntity> entities = receivedFile.entities()
            .stream()
            .filter(NamedEntity::isFile)
            .toList();
        return entities.stream().map(NamedEntity::asFile).toList();
    }
}
