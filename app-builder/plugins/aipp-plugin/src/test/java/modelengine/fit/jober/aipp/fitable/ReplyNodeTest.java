package modelengine.fit.jober.aipp.fitable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.template.render.TemplateService;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.service.AippLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ReplyNode}的测试用例.
 *
 * @author 孙怡菲
 * @since 2025-09-17
 */
class ReplyNodeTest {
    private AippLogService aippLogService;
    private TemplateService templateService;
    private ReplyNode replyNode;

    @BeforeEach
    void setUp() {
        this.aippLogService = mock(AippLogService.class);
        this.templateService = mock(TemplateService.class);
        this.replyNode = new ReplyNode(this.aippLogService, this.templateService);
    }

    @Test
    void shouldOkAndInsertLog() {
        Map<String, Object> businessData = new HashMap<>();
        String template = "你好，{{name}}，欢迎回来！";
        String expectedMsg = "你好，小明，欢迎回来";
        businessData.put("template", template);
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "小明");
        businessData.put("variables", variables);

        List<Map<String, Object>> flowData = new ArrayList<>();
        Map<String, Object> flowItem = new HashMap<>();
        flowItem.put("businessData", businessData);
        flowData.add(flowItem);

        when(templateService.renderTemplate(template, variables)).thenReturn(expectedMsg);

        List<Map<String, Object>> result = this.replyNode.handleTask(flowData);

        assertEquals(flowData, result);

        ArgumentCaptor<AippLogData> logCaptor = ArgumentCaptor.forClass(AippLogData.class);
        verify(this.aippLogService, times(1)).insertLogWithInterception(
                eq(AippInstLogType.MSG.name()),
                logCaptor.capture(),
                eq(businessData)
        );

        AippLogData logData = logCaptor.getValue();
        assertNotNull(logData);
        assertEquals(expectedMsg, logData.getMsg());
    }
}