/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.impl;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.DataFormatData;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.util.DateUtils;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fit.jober.aipp.tool.FileExtractor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Group(name = "defGroup-aipp-file-extract-tool-excel")
public class ExcelExtractor implements FileExtractor {
    @Override
    public OperatorService.FileType supportedType() {
        return OperatorService.FileType.EXCEL;
    }

    /**
     * 从指定路径的 Excel 文件中提取内容，并返回为字符串形式。
     * 实现方式：
     * 基于 fast-excel 包，使用流式读取（ReadListener）逐行解析，避免一次性加载整表造成的内存开销。
     * 每行数据会被转换为以制表符（\t）分隔的文本，并在行末追加换行符。
     * 支持多 sheet 解析，会依次读取工作簿中的每一个 sheet。
     *
     * @param fileUrl 表示文件路径的 {@link String}.
     * @return 表示文件内容的 {@link String}。
     * @throws RuntimeException 当文件读取或解析失败时抛出
     */
    @Override
    @ToolMethod(name = "excel_file_extract", description = "提取 Excel 文件内容")
    @Fitable("file_extract_excel")
    @Property(description = "Excel 提取结果")
    public String extractFile(String fileUrl) {
        File file = Paths.get(fileUrl).toFile();
        StringBuilder excelContent = new StringBuilder();
        ReadListener<Map<Integer, String>> listener = new ReadListener<>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                String line = data.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> e.getValue() == null ? "" : e.getValue())
                        .collect(Collectors.joining("\t"));
                excelContent.append(line).append('\n');
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        };
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            ExcelReader reader = FastExcel.read(is, listener)
                    .registerConverter(new CustomCellStringConverter())
                    .headRowNumber(0)
                    .build();

            List<ReadSheet> sheets = reader.excelExecutor().sheetList();
            for (ReadSheet meta : sheets) {
                excelContent.append("Sheet ").append(meta.getSheetNo() + 1).append(':').append('\n');
                ReadSheet readSheet = FastExcel.readSheet(meta.getSheetNo()).headRowNumber(0).build();
                reader.read(readSheet);
            }
            excelContent.append('\n');
            reader.finish(); // 关闭资源
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return excelContent.toString();
    }

    private static String getCellValueAsString(ReadCellData<?> cell) {
        switch (cell.getType()) {
            case STRING:
                return cell.getStringValue();
            case NUMBER:
                DataFormatData fmt = cell.getDataFormatData();
                short formatIndex = fmt.getIndex();
                String formatString = fmt.getFormat();
                if (DateUtils.isADateFormat(formatIndex, formatString)) {
                    double value = cell.getNumberValue().doubleValue();
                    Date date = DateUtils.getJavaDate(value, true);
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                } else {
                    BigDecimal num = cell.getNumberValue();
                    return num.stripTrailingZeros().toPlainString();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanValue());
            default:
                return "";
        }
    }

    /**
     * 自定义单元格数据转换器。
     * 将 Excel 单元格数据统一转换为字符串，避免数值/日期等类型在读取时格式不一致的问题。
     * 缺点：由于采用fast excel包,没有 FORMULA类,会将公式单元格自动计算为值
     */
    public static class CustomCellStringConverter implements Converter<String> {
        @Override
        public Class<String> supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }

        @Override
        public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                GlobalConfiguration globalConfiguration) {
            return getCellValueAsString(cellData);
        }
    }
}
