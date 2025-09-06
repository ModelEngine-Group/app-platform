/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service.impl;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATA_INVALID_ERROR;
import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.FILE_INVALID_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.entity.JsonEntity;
import modelengine.jade.app.engine.eval.service.EvalFileService;
import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fit.http.entity.support.DefaultReadableBinaryEntity;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.schema.SchemaGenerator;
import modelengine.jade.schema.SchemaValidator;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link EvalFileServiceImpl} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-08-09
 */

@FitTestWithJunit(includeClasses = EvalFileServiceImpl.class)
public class EvalFileServiceImplTest {
    @Fit
    private EvalFileService evalFileService;

    @Mock
    private SchemaValidator schemaValidatorMock;

    @Mock
    private SchemaGenerator schemaGeneratorMock;

    @Test
    @DisplayName("Json 文件成功解析")
    void shouldOkWhenParseJsonFile() throws FileNotFoundException {
        List<String> expectedContents = Arrays.asList(
                "{\"name\":\"Alice Smith\",\"age\":30,\"email\":\"alice.smith@example.com\","
                        + "\"preferences\":{\"language\":\"en\"}}",
                "{\"name\":\"Bob Johnson\",\"age\":45,\"email\":\"bob.johnson@example.com\","
                        + "\"preferences\":{\"language\":\"es\"}}");
        String expectedSchema =
                "{\"title\": \"User Information\", \"type\": \"object\", \"properties\": {\"name\": {\"type\": "
                        + "\"string\"}, \"age\": {\"type\": \"integer\", \"minimum\": 0}, \"email\": {\"type\": "
                        + "\"string\", \"format\": \"email\"}}, \"required\": [\"name\", \"age\", \"email\"]}";

        doNothing().when(this.schemaValidatorMock).validate(anyString(), anyList());
        when(this.schemaGeneratorMock.generateSchema(any())).thenReturn(expectedSchema);

        File file = new File(this.getClass().getClassLoader().getResource("test/valid_json.json").getFile());
        ReadableBinaryEntity readableBinaryEntity =
                new DefaultReadableBinaryEntity(mock(HttpMessage.class), new FileInputStream(file));
        JsonEntity res = this.evalFileService.parseJsonFileToEvalData(readableBinaryEntity);

        assertThat(res).extracting(JsonEntity::getContents, JsonEntity::getSchema)
                .containsExactly(expectedContents, expectedSchema);
    }

    @Test
    @DisplayName("Json 文件格式错误导致失败解析")
    void shouldNotOkWhenParseInvalidFormatJsonFile() throws FileNotFoundException {
        doNothing().when(this.schemaValidatorMock).validate(anyString(), anyList());
        File file = new File(this.getClass().getClassLoader().getResource("test/invalid_format_json.json").getFile());
        ReadableBinaryEntity readableBinaryEntity =
                new DefaultReadableBinaryEntity(mock(HttpMessage.class), new FileInputStream(file));
        ModelEngineException ex = assertThrows(ModelEngineException.class,
                () -> this.evalFileService.parseJsonFileToEvalData(readableBinaryEntity));
        assertThat(ex.getCode()).isEqualTo(FILE_INVALID_ERROR.getCode());
    }

    @Test
    @DisplayName("Json 文件不符合数据约束导致失败解析")
    void shouldNotOkWhenParseInvalidDataJsonFile() throws FileNotFoundException {
        doThrow(new JsonSchemaInvalidException("")).when(this.schemaValidatorMock).validate(anyString(), any());
        when(this.schemaGeneratorMock.generateSchema(any())).thenReturn("");
        File file = new File(this.getClass().getClassLoader().getResource("test/invalid_data_json.json").getFile());
        ReadableBinaryEntity readableBinaryEntity =
                new DefaultReadableBinaryEntity(mock(HttpMessage.class), new FileInputStream(file));
        ModelEngineException ex = assertThrows(ModelEngineException.class,
                () -> this.evalFileService.parseJsonFileToEvalData(readableBinaryEntity));
        assertThat(ex.getCode()).isEqualTo(DATA_INVALID_ERROR.getCode());
    }
}