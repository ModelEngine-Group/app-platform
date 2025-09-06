/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.jober.aipp.service.impl.OperatorServiceImpl;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

/**
 * 表示 {@link OperatorServiceImpl} 的测试类，
 *
 * @author 兰宇晨
 * @since 2025-01-15
 */
@FitTestWithJunit(includeClasses = OperatorServiceImpl.class)
@Disabled
public class OperatorServiceImplTest {
    @Fit
    private OperatorService operatorService;

    @Mock
    private LLMService llmService;

    @Test
    @DisplayName("测试文本文件提取成功")
    void shouldOkWhenExtractTextFile() {
        assertThat(this.getContent("file/testFile/content.txt", OperatorService.FileType.TXT)).isEqualTo(
                "This is a test");
    }

    @Test
    @DisplayName("测试 Markdown 文件提取成功")
    void shouldOkWhenExtractMdFile() {
        assertThat(this.getContent("file/testFile/content.md", OperatorService.FileType.TXT)).isEqualTo(
                "This is a md test");
    }

    @Test
    @DisplayName("测试 docx 文件提取成功")
    void shouldOkWhenExtractDocxFile() {
        assertThat(this.getContent("file/testFile/content.docx", OperatorService.FileType.WORD)).isEqualTo(
                "This is a word test");
    }

    @Test
    @DisplayName("测试 excel 文件提取成功")
    void shouldOkWhenExtractExcelFile() {
        assertThat(this.getContent("file/testFile/content.xlsx", OperatorService.FileType.EXCEL)).isEqualTo(
                "Sheet 1:\nThis is an excel test\n\n");
    }

    private String getContent(String filePath, OperatorService.FileType fileType) {
        String fileUrl = "/path/mockurl.mock";
        File file = new File(this.getClass().getClassLoader().getResource(filePath).getFile());
        return this.operatorService.fileExtractor(fileUrl, Optional.of(fileType));
    }
}
