/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jade.aipp.file.extract.AbstractFileExtractor;
import modelengine.fit.jade.aipp.file.extract.FileTypeConstant;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理文件提取器的容器
 */
@Component
public class FileExtractorContainer {
    private final Map<FileTypeConstant.FileType, AbstractFileExtractor> map;

    public FileExtractorContainer(List<AbstractFileExtractor> extractors) {
        map = new EnumMap<>(FileTypeConstant.FileType.class);
        for (AbstractFileExtractor fileExtractor : extractors) {
            map.put(fileExtractor.supportedFileType(), fileExtractor);
        }
    }

    public Optional<String> extract(String fileUrl, OperatorService.FileType fileType) {
        FileTypeConstant.FileType fileType_transform = FileTypeConvertor.convert(fileType);
        return Optional.ofNullable(map.get(fileType_transform))
                .map(extractor -> extractor.extractFile(fileUrl));
    }

}
