/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.extractor;

import lombok.AllArgsConstructor;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@AllArgsConstructor
public class WordTextExtractor implements TextExtractor {
    private String filePath;

    @Override
    public String extractText() throws IOException {
        StringBuilder extractedText = new StringBuilder();

        File docxFile = new File(filePath);
        try (FileInputStream fis = new FileInputStream(docxFile); XWPFDocument document = new XWPFDocument(fis)) {
            for (IBodyElement element : document.getBodyElements()) {
                if (element.getElementType() == BodyElementType.PARAGRAPH) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    extractedText.append(paragraph.getText()).append("\n");
                } else if (element.getElementType() == BodyElementType.TABLE) {
                    XWPFTable table = (XWPFTable) element;
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            extractedText.append(cell.getText()).append("\t");
                        }
                        extractedText.append("\n");
                    }
                }
            }
            return extractedText.toString();
        }
    }
}
