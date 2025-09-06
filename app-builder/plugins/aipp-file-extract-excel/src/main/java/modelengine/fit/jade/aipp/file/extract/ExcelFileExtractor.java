/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.file.extract;

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
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

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

/**
 * Excel文件的提取器。
 *
 * @author jsbjfkbsjk
 * @since 2025-9-6
 */
@Component
public class ExcelFileExtractor implements AbstractFileExtractor {
    /**
     * 把单元格转换成格式化字符串
     *
     * @param cell 表示单元格数据 {@link ReadCellData}
     * @return 转换后的内容 {@link String}
     */
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
     * 该文件提取器支持excel类型
     *
     * @return 枚举常量类型 {@link OperatorService.FileType}
     */
    @Override
    @Fitable(id = "get-fileType-excel")
    public OperatorService.FileType supportedFileType() {
        return OperatorService.FileType.EXCEL;
    }

    /**
     * 从指定路径的 Excel 文件中提取内容，并返回为字符串形式。
     *
     * @param fileUrl 表示文件路径的 {@link String}.
     * @return 表示文件内容的 {@link String}。
     */
    @Override
    @Fitable(id = "extract-file-excel")
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

    /**
     * 自定义单元格数据转换器。
     * 该转换器实现了能够处理单元格数据并将其转换为字符串形式。
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
