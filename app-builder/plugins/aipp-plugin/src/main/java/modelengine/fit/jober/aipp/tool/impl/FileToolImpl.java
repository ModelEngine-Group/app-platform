/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.impl;

import modelengine.fit.jober.aipp.entity.FileExtensionEnum;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fit.jober.aipp.tool.FileTool;
import modelengine.fit.jober.aipp.util.AippStringUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fel.tool.annotation.Attribute;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;

/**
 * 文件工具类实现。
 *
 * @author 孙怡菲
 * @since 2024-05-30
 */
@Component
@Group(name = "implGroup-aipp-file-tool")
public class FileToolImpl implements FileTool {
    private final OperatorService operatorService;

    public FileToolImpl(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Override
    @Fitable("extract.multi.type")
    @ToolMethod(name = "impl-aipp-file-tool-extract", description = "解析文件内容", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "FILETOOL")
    })
    public String extractFile(String filePath) {
        Integer defaultToken = 20000;
        String fileContent = this.operatorService.fileExtractor(filePath, FileExtensionEnum.findType(filePath));
        return AippStringUtils.textLenLimit(fileContent, defaultToken);
    }
}
