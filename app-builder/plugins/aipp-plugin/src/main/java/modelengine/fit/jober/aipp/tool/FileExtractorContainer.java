/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jade.aipp.file.extract.FileExtractor;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理文件提取器的容器。
 *
 * @author 黄政炫
 * @since 2025-09-06
 */
@Component
public class FileExtractorContainer {
    private static final Logger log = Logger.get(FileExtractorContainer.class);

    /**
     * 一种文件类型对应一个提取器集合。
     */
    private final Map<String, List<FileExtractor>> fileExtractorMap;

    /**
     * 初始化用框架注入提取器。
     *
     * @param extractors 文件提取器 {@link FileExtractor}。
     */
    public FileExtractorContainer(List<FileExtractor> extractors) {
        this.fileExtractorMap = new HashMap<>();
        for (FileExtractor fileExtractor : extractors) {
            for (String supportedFileType : fileExtractor.supportedFileTypes()) {
                this.fileExtractorMap.computeIfAbsent(supportedFileType, k -> new ArrayList<>()).add(fileExtractor);
            }
        }
    }

    /**
     * 根据文件类型找到支持文件类型的提取器。
     *
     * @param fileUrl 文件路径 {@link String}。
     * @param fileType 文件枚举类型 {@link OperatorService.FileType}。
     * @return 提取的字符串 {@link Optional}{@code <}{@link String}{@code >}。
     */
    public Optional<String> extract(String fileUrl, OperatorService.FileType fileType) {
        if (fileType == null) {
            log.warn("File type cannot be null.");
            return Optional.empty();
        }
        List<FileExtractor> extractors = this.fileExtractorMap.get(fileType.toString());
        if (CollectionUtils.isEmpty(extractors)) {
            return Optional.empty();
        }
        if (extractors.size() > 1) {
            log.warn("Multiple extractors found, using the first one instead. [name={}]",
                    extractors.get(0).getClass().getSimpleName());
        }
        return Optional.ofNullable(extractors.get(0)).map(extractor -> extractor.extractFile(fileUrl));
    }
}
