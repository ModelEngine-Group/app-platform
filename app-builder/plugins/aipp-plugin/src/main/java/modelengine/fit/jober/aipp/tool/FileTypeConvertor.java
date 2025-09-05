/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jade.aipp.file.extract.FileTypeConstant;
import modelengine.fit.jober.aipp.service.OperatorService;

/**
 * 文件类型转换器
 */
public class FileTypeConvertor {
    public static FileTypeConstant.FileType convert(OperatorService.FileType fileType) {
        return switch (fileType) {
            case PDF -> FileTypeConstant.FileType.PDF;
            case WORD -> FileTypeConstant.FileType.WORD;
            case EXCEL -> FileTypeConstant.FileType.EXCEL;
            case IMAGE -> FileTypeConstant.FileType.IMAGE;
            case AUDIO -> FileTypeConstant.FileType.AUDIO;
            case TXT -> FileTypeConstant.FileType.TXT;
            case HTML -> FileTypeConstant.FileType.HTML;
            case MARKDOWN -> FileTypeConstant.FileType.MARKDOWN;
            case CSV -> FileTypeConstant.FileType.CSV;
        };
    }
}
