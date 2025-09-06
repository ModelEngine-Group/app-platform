/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.extractor;

public class TextExtractorFactory {
    public static TextExtractor getTextExtractor(String filePath) {
        if (filePath.endsWith(".pdf")) {
            return new PdfTextExtractor(filePath);
        } else if (filePath.endsWith(".docx")) {
            return new WordTextExtractor(filePath);
        } else {
            return null;
        }
    }
}