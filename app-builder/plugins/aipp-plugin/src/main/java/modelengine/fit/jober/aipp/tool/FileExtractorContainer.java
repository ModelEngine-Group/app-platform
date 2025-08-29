/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FileExtractorContainer {
    private Map<OperatorService.FileType, FileExtractor> map;

    public FileExtractorContainer(List<FileExtractor> extractors) {
        map = new EnumMap<>(OperatorService.FileType.class);
        for (FileExtractor fileExtractor : extractors) {
            map.put(fileExtractor.supportedType(), fileExtractor);
        }
    }

    public String extract(String fileUrl, OperatorService.FileType fileType) {
        if (map.containsKey(fileType)) {
            return map.get(fileType).extractFile(fileUrl);
        } else {
            return "";//先返回空字符串
        }
    }

}

