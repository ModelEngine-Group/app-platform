/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.log;

import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * {@link AppLog} 的测试类。
 *
 * @author 孙怡菲
 * @since 2025-02-20
 */
public class AppLogTest {
    private final AppLogFactory factory = new AppLogFactory();

    @Test
    @DisplayName("测试判断日志是否为question类型")
    public void TestIsQuestion() {
        AppLog questionLog = this.factory.create(mockLog(AippInstLogType.QUESTION.name(), "{\"msg\": \"你好\","
            + "\"form_id\": null, \"formData\": null, \"form_args\": null, \"form_version\": null,"
            + " \"formAppearance\": null}"));

        AppLog msgLog = this.factory.create(mockLog(AippInstLogType.MSG.name(), "{\"msg\": \"你好\", "
            + "\"form_id\": null, \"formData\": null, \"form_args\": null, \"form_version\": null,"
            + " \"formAppearance\": null}"));

        Assertions.assertTrue(questionLog.isQuestionType());
        Assertions.assertFalse(msgLog.isQuestionType());
    }

    @Test
    @DisplayName("测试判断日志是否为某些类型")
    public void TestIsType() {
        AppLog questionLog = this.factory.create(mockLog(AippInstLogType.QUESTION.name(), "{\"msg\": \"你好\", "
            + "\"form_id\": null, \"formData\": null, \"form_args\": null, \"form_version\": null,"
            + " \"formAppearance\": null}"));

        Assertions.assertTrue(questionLog.is(AippInstLogType.QUESTION));
        Assertions.assertFalse(questionLog.is(AippInstLogType.MSG, AippInstLogType.FORM));
    }

    @Test
    @DisplayName("测试获输入信息")
    public void TestGetInput() {
        AppLog questionLog = this.factory.create(mockLog(AippInstLogType.QUESTION.name(), "{\"msg\": \"44+44\","
            + " \"infos\": {\"input\": {\"bool\": false, \"number\": null, \"Question\": \"44+44\"}}, \"form_id\": "
            + "null, \"formData\": null, \"form_args\": null, \"form_version\": null, \"formAppearance\": null}"));

        Optional<Map<String, Object>> inputs = questionLog.getInput();

        Assertions.assertEquals(3, inputs.get().size());
    }

    @Test
    @DisplayName("测试")
    public void TestToBody() {
        String logData = "{\"msg\": \"你好\", \"form_id\": null, \"formData\": null, \"form_args\": null, "
            + "\"form_version\": null,\"formAppearance\": null}";
        AppLog log = this.factory.create(mockLog(AippInstLogType.QUESTION.name(), logData));

        AippInstLogDataDto.AippInstanceLogBody logBody = log.toBody();

        Assertions.assertEquals(AippInstLogType.QUESTION.name(), logBody.getLogType());
        Assertions.assertEquals(logData, logBody.getLogData());
    }

    private AippInstLog mockLog(String type, String logData) {
        return AippInstLog.builder()
            .createAt(LocalDateTime.now())
            .createUserAccount("t00123456")
            .path("/id2/id1")
            .logId(1L)
            .aippId("aippId")
            .version("1.0.0")
            .aippType(AippTypeEnum.NORMAL.name())
            .instanceId("id1")
            .logData(logData)
            .logType(type)
            .build();
    }
}
