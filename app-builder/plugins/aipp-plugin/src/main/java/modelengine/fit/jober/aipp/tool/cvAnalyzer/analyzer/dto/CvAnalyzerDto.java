/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI简历解析插件输出类。
 *
 * @author 杨璨宇
 * @since 2024/09/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CvAnalyzerDto {
    private boolean isFileHandled;
    private String cvAnalyzerPrompt;
    private String errorMessage;
}