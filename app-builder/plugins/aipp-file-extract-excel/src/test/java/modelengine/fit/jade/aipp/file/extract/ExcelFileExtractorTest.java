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
        assertThat(this.excelFileExtractor.supportedFileTypes()).isEqualTo(supportedTypes);
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
        assertThat(this.excelFileExtractor.extractFile(file.getAbsolutePath())).isEqualTo(expected);
    }

    String expected = """
            Sheet 1:
            This is an excel test
            ID\tName\tAge\tJoinDate\tActive\tSalary\tDepartment\tNotes
            1\tJohn Doe\t25\t2023-01-15\tTRUE\t8000.50\tIT\tRegular employee
            2\tJane Smith\t30\t2022-05-20\tTRUE\t12000.00\tMarketing\tTeam leader
            3\tBob Johnson\t28\t2023-03-10\tFALSE\t7500.00\tSales\tLeft company
            4\tAlice Brown\t35\t2020-12-01\tTRUE\t15000.75\tIT\tSenior engineer
            5\tTom Wilson\t22\t2023-08-25\tTRUE\t6000.00\tHR\tIntern
            6\t\t40\t2019-06-15\tTRUE\t18000.00\tFinance\tDepartment manager
            7\tLucy Davis\t27\t2023-02-28\tFALSE\t7000.00\tOperations\tContract ended
            8\tMike Miller\t32\t2021-09-10\tTRUE\t13500.50\tIT\tProject lead
            9\tSarah Lee\t29\t2022-11-05\tTRUE\t9500.00\tMarketing\tMarketing specialist
            10\tDavid Zhang\t26\t2023-07-12\tTRUE\t8500.25\tSales\tSales representative
            
            """;
}