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
import cn.idev.excel.util.StringUtils;
import lombok.NonNull;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel文件的提取器。
 *
 * @author 黄政炫
 * @since 2025-09-06
 */
@Component
public class ExcelFileExtractor implements FileExtractor {
    /**
     * 把单元格转换成格式化字符串。
     *
     * @param cell 表示单元格数据 {@link ReadCellData}。
     * @return 转换后的内容 {@link String}。
     */
    private static String getCellValueAsString(@NonNull ReadCellData<?> cell) {
        switch (cell.getType()) {
            case STRING:
                return cell.getStringValue();
            case NUMBER:
                DataFormatData fmt = cell.getDataFormatData();
                if (DateUtils.isADateFormat(fmt.getIndex(), fmt.getFormat())) {
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
     * 该文件提取器支持EXCEL和CSV类型。
     *
     * @return 支持的枚举常量类型列表 {@link List}{@code <}{@link String}{@code >}。
     */
    @Override
    @Fitable(id = "get-fileType-excel")
    public List<String> supportedFileTypes() {
        return Arrays.asList(OperatorService.FileType.EXCEL.toString(), OperatorService.FileType.CSV.toString());
    }

    /**
     * 判断文件路径是否有效
     *
     * @param fileUrl 表示文件路径 {@link String}。
     * @return 表示路径是否有效 {@code boolean}。
     */
    private boolean isValidPath(String fileUrl) {
        try {
            Path path = Paths.get(fileUrl);
            return Files.exists(path) && Files.isRegularFile(path);
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * 从指定路径的 Excel 文件中提取内容，并返回为字符串形式。
     *
     * @param fileUrl 表示文件路径的 {@link String}。
     * @return 表示文件内容的 {@link String}。
     */
    @Override
    @Fitable(id = "extract-file-excel")
    public String extractFile(String fileUrl) {
        if (!isValidPath(fileUrl)) {
            throw new IllegalArgumentException(String.format("Invalid FilePath. [fileUrl=%s]", fileUrl));
        }
        File file = Paths.get(fileUrl).toFile();
        StringBuilder excelContent = new StringBuilder();
        ExcelReadListener listener = new ExcelReadListener(excelContent);
        ExcelReader reader = null;
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            reader = FastExcel.read(is, listener)
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
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Fail to extract excel file. [exception=%s]", e.getMessage()),
                    e);
        } finally {
            if (reader != null) {
                reader.finish(); // 关闭资源
            }
        }
        return excelContent.toString();
    }

    /**
     * 读取监听器的内部类实现。
     */
    private class ExcelReadListener implements ReadListener<Map<Integer, String>> {
        private final StringBuilder excelContent;

        ExcelReadListener(StringBuilder excelContent) {
            this.excelContent = excelContent;
        }

        @Override
        public void invoke(Map<Integer, String> data, AnalysisContext context) {
            String line = data.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getValue() == null ? "" : e.getValue())
                    .collect(Collectors.joining("\t"));
            this.excelContent.append(line).append('\n');
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {}
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
            return (cellData != null) ? getCellValueAsString(cellData) : StringUtils.EMPTY;
        }
    }
}
