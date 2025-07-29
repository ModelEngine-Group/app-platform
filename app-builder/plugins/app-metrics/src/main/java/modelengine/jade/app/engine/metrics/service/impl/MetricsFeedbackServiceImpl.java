/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service.impl;

import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import modelengine.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import modelengine.jade.app.engine.metrics.service.MetricsFeedbackService;
import modelengine.jade.app.engine.metrics.utils.MetaUtils;
import modelengine.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import modelengine.jade.app.engine.metrics.vo.Page;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * MetricsFeedackServiceImpl类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024-05-21
 */
@Component
public class MetricsFeedbackServiceImpl implements MetricsFeedbackService {
    private static final int DATE_CELL_WIDTH = 25 * 256;

    @Fit
    private ConversationRecordMapper conversationRecordMapper;

    @Fit
    private MetaService metaService;

    /**
     * 获取以页为单位的feedback数据
     *
     * @param metricsFeedbackDTO 筛选条件
     * @return 以页为单位的feedback数据
     */
    @Override
    public Page<MetricsFeedbackVo> getMetricsFeedback(MetricsFeedbackDto metricsFeedbackDTO) {
        List<String> appIds = MetaUtils.getAllPublishedAppId(this.metaService, metricsFeedbackDTO.getAppId(), null);
        List<MetricsFeedbackVo> conversations = conversationRecordMapper.getByCondition(metricsFeedbackDTO, appIds);

        Page<MetricsFeedbackVo> page = new Page<>();
        page.setPageIndex(metricsFeedbackDTO.getPageIndex());
        page.setPageSize(metricsFeedbackDTO.getPageSize());
        page.setTotal(conversationRecordMapper.getCountByCondition(metricsFeedbackDTO, appIds));
        page.setData(conversations);
        return page;
    }

    /**
     * 导出所有符合条件的数据
     *
     * @param metricsFeedbackDTO 筛选条件
     * @return ByteArrayInputStream
     * @throws IOException 异常捕获
     */
    @Override
    public ByteArrayInputStream export(MetricsFeedbackDto metricsFeedbackDTO) throws IOException {
        List<String> appIds = MetaUtils.getAllPublishedAppId(this.metaService, metricsFeedbackDTO.getAppId(), null);
        String[] headStr = {"用户提问", "应用回答", "时间", "响应速度", "用户", "反馈", "反馈详情"};

        try (Workbook wb = new HSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet();
            int rowIndex = 0;

            // 创建并填充表头
            Row headRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < headStr.length; i++) {
                Cell cell = headRow.createCell(i, CellType.STRING);
                cell.setCellValue(headStr[i]);
            }

            long count = conversationRecordMapper.getCountByCondition(metricsFeedbackDTO, appIds);
            // 每次取500条数据处理
            int batch = 500;
            metricsFeedbackDTO.setPageSize(batch);

            CellStyle dateCellStyle = wb.createCellStyle();
            dateCellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

            for (int i = 0; i <= count / batch; i++) {
                metricsFeedbackDTO.setPageIndex(i + 1);
                Page<MetricsFeedbackVo> feedbackPage = getMetricsFeedback(metricsFeedbackDTO);
                for (MetricsFeedbackVo vo : feedbackPage.getData()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0, CellType.STRING).setCellValue(vo.getQuestion());
                    row.createCell(1, CellType.STRING).setCellValue(vo.getAnswer());
                    Cell cell = row.createCell(2);
                    cell.setCellValue(vo.getCreateTime());
                    cell.setCellStyle(dateCellStyle);
                    row.createCell(3, CellType.STRING).setCellValue(vo.getResponseTime() + "ms");
                    row.createCell(4, CellType.STRING).setCellValue(vo.getCreateUser());
                    row.createCell(5, CellType.STRING)
                            .setCellValue(vo.getUserFeedback() == null ? -1 : vo.getUserFeedback());
                    row.createCell(6, CellType.STRING)
                            .setCellValue(vo.getUserFeedbackText() == null ? "" : vo.getUserFeedbackText());
                }
            }
            sheet.setColumnWidth(2, DATE_CELL_WIDTH);

            wb.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
