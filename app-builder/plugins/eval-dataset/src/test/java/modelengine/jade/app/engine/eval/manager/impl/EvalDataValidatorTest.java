/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.manager.impl;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATA_INVALID_ERROR;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.manager.EvalDataValidator;
import modelengine.jade.app.engine.eval.mapper.EvalDatasetMapper;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.schema.SchemaValidator;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataValidatorImpl} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-7-27
 */
@FitTestWithJunit(includeClasses = EvalDataValidatorImpl.class)
public class EvalDataValidatorTest {
    private static final String SCHEMA =
        "{\"title\": \"UserInfo\", \"type\": \"object\", \"properties\": {\"name\": {\"type\": \"string\"}}}}";

    private static final String VALID_CONTENT = "{\"name\": \"Jerry\"\n}";

    private static final String INVALID_CONTENT = "{\"name\": 15\n}";

    @Fit
    private EvalDataValidator validator;

    @Mock
    private EvalDatasetMapper datasetMapperMock;

    @Mock
    private SchemaValidator schemaValidatorMock;

    @Test
    @DisplayName("批量校验评估数据成功")
    void shouldOkWhenBatchValidEvalData() {
        List<String> contents = Collections.singletonList(VALID_CONTENT);
        when(this.datasetMapperMock.getSchema(anyLong())).thenReturn(SCHEMA);
        doNothing().when(this.schemaValidatorMock).validate(anyString(), anyList());
        assertDoesNotThrow(() -> validator.verify(0L, contents));
    }

    @Test
    @DisplayName("批量校验评估数据失败")
    void shouldNotOkWhenBatchValidEvalData() {
        List<String> contents = Collections.singletonList(INVALID_CONTENT);
        when(this.datasetMapperMock.getSchema(anyLong())).thenReturn(SCHEMA);
        String error = "INVALID CONTENT";
        doThrow(new JsonSchemaInvalidException(error)).when(this.schemaValidatorMock).validate(anyString(), anyList());
        assertThatThrownBy(() -> validator.verify(0L, contents)).isInstanceOf(ModelEngineException.class)
            .extracting("code")
            .isEqualTo(DATA_INVALID_ERROR.getCode());
    }
}