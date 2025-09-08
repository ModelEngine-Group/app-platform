/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.file.extract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 表示{@link ExcelFileExtractor}的测试集。
 *
 * @author 黄政炫
 * @since 2025-09-06
 */
@FitTestWithJunit(includeClasses = ExcelFileExtractor.class)
@Disabled
class ExcelFileExtractorTest {
    @Fit
    ExcelFileExtractor excelFileExtractor;

    @Test
    @DisplayName("测试获取支持文件类型")
    void supportedFileType() {
        List<String> supportedTypes =
                Arrays.asList(OperatorService.FileType.EXCEL.toString(), OperatorService.FileType.CSV.toString());
        assertThat(this.excelFileExtractor.supportedFileType()).isEqualTo(supportedTypes);
    }

    @Test
    @DisplayName("测试能否捕获错误路径")
    void validPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.excelFileExtractor.extractFile("invalidPath.csv");
        });
    }

    @Test
    @DisplayName("测试 excel 文件提取成功")
    void extractFile() {
        File file = new File(this.getClass().getClassLoader().getResource("file/content.csv").getFile());
        assertThat(this.excelFileExtractor.extractFile(file.getAbsolutePath())).isEqualTo(
                "Sheet 1:\nThis is an excel test\n\n");
    }
}