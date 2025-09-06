/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jade.aipp.file.extract.AbstractFileExtractor;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理文件提取器的容器
 *
 * @author jsbjfkbsjk
 * @since 2025-9-6
 */
@Component
public class FileExtractorContainer {
    private final Map<OperatorService.FileType, AbstractFileExtractor> map;

    /**
     * 初始化用框架注入提取器
     *
     * @param extractors 文件提取器 {@link AbstractFileExtractor}
     */
    public FileExtractorContainer(List<AbstractFileExtractor> extractors) {
        map = new EnumMap<>(OperatorService.FileType.class);
        for (AbstractFileExtractor fileExtractor : extractors) {
            map.put(fileExtractor.supportedFileType(), fileExtractor);
        }
    }

    /**
     * 根据文件类型找到支持文件类型的提取器
     *
     * @param fileUrl 文件路径 {@link String}
     * @param fileType 文件枚举类型 {@link OperatorService.FileType}
     * @return 提取的字符串 {@link Optional<String>}
     */
    public Optional<String> extract(String fileUrl, OperatorService.FileType fileType) {
        return Optional.ofNullable(map.get(fileType))
                .map(extractor -> extractor.extractFile(fileUrl));
    }
}
