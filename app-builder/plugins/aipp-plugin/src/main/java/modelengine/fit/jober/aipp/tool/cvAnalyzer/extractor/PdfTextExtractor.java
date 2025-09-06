/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.extractor;

import lombok.AllArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

@AllArgsConstructor
public class PdfTextExtractor implements TextExtractor {
    private String filePath;

    @Override
    public String extractText() throws IOException {
        File pdfFile = new File(filePath);
        try (PDDocument doc = PDDocument.load(pdfFile)) {
            int pages = doc.getNumberOfPages();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pages; i++) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(doc);
                sb.append(text);
                if (i != pages - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }
}
