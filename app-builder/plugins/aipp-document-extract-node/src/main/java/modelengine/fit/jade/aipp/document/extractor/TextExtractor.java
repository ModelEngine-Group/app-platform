/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.extractor;

import static modelengine.fit.jade.aipp.document.code.DocumentExtractRetCode.MULTI_MODAL_FILE_EXTRACT_TOOL_NOT_FOUND;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jober.aipp.service.OperatorService.FileType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.service.ToolService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文档提取工具。
 *
 * @author 马朝阳
 * @since 2024-12-12
 */
@Component
public class TextExtractor implements BaseExtractor {
    private final ToolService toolService;
    private final ToolExecuteService toolExecuteService;

    public TextExtractor(ToolService toolService, ToolExecuteService toolExecuteService) {
        this.toolService = toolService;
        this.toolExecuteService = toolExecuteService;
    }

    /**
     * 表示文件内容提取接口。
     *
     * @param fileUrl 文件链接。
     * @param context 文件提取额外参数。
     * @return 表示文件内容的 {@link String}。
     */
    @Override
    public String extract(String fileUrl, Map<String, Object> context) {
        List<ToolData> tools = this.toolService.getTools("defGroup-aipp-file-tool");
        List<String> uniqueNameList = tools.stream()
                .filter(tool -> tool.getName().equals("impl-aipp-file-tool-extract"))
                .map(ToolData::getUniqueName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(uniqueNameList)) {
            throw new ModelEngineException(MULTI_MODAL_FILE_EXTRACT_TOOL_NOT_FOUND);
        }
        Map<String, Object> jsonArg = MapBuilder.<String, Object>get().put("filePath", fileUrl).build();
        return toolExecuteService.execute(uniqueNameList.get(0), jsonArg);
    }

    @Override
    public FileType type() {
        return FileType.TXT;
    }
}
