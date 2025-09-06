/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.port.fit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.extract.ContentExtractParam;
import modelengine.fit.jade.aipp.extract.ExtractResult;
import modelengine.fit.jade.aipp.extract.code.ContentExtractRetCode;
import modelengine.fit.jade.aipp.extract.command.ContentExtractCommand;
import modelengine.fit.jade.aipp.extract.command.ExtractCommandHandler;
import modelengine.fit.jade.aipp.extract.ports.fit.AippInfoExtractService;
import modelengine.fit.jade.aipp.extract.util.TestUtils;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.enums.ModelType;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.common.exception.ModelEngineException;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * 表示 {@link AippInfoExtractService} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-11-06
 */
@FitTestWithJunit(includeClasses = AippInfoExtractService.class)
public class AippInfoExtractServiceTest {
    @Fit
    private AippInfoExtractService infoExtractService;

    @Mock
    private ExtractCommandHandler commandHandler;

    @AfterEach
    void teardown() {
        reset(this.commandHandler);
    }

    @Test
    @DisplayName("fit 调用成功")
    void shouldOkWhenExtractInfo() {
        Map<String, String> info = MapBuilder.<String, String>get().put("one", "1").put("two", "2").build();
        when(this.commandHandler.handle(any())).thenReturn(info);
        ContentExtractCommand command = TestUtils.getExtractCommand();
        ContentExtractParam extractParam = constructContentExtractParam(command);
        ExtractResult result =
                infoExtractService.extract(extractParam, command.getMemoryConfig(), command.getHistories());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getExtractedParams()).isEqualTo(info);
    }

    @Test
    @DisplayName("fit 调用失败")
    void shouldFailWhenExtractInfo() {
        ModelEngineException exception = new ModelEngineException(ContentExtractRetCode.TOOLCALL_SIZE_ERROR, 2, 1);
        when(this.commandHandler.handle(any())).thenThrow(exception);
        ContentExtractCommand command = TestUtils.getExtractCommand();
        ContentExtractParam extractParam = constructContentExtractParam(command);
        ExtractResult result =
                infoExtractService.extract(extractParam, command.getMemoryConfig(), command.getHistories());
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getExtractedParams()).isEqualTo(exception.getMessage());
    }

    @NotNull
    private static ContentExtractParam constructContentExtractParam(ContentExtractCommand command) {
        ModelAccessInfo modelInfo = new ModelAccessInfo(command.getModel(), command.getModelTag(),
                null, null, ModelType.CHAT_COMPLETIONS.value());
        ContentExtractParam extractParam = new ContentExtractParam();
        extractParam.setText(command.getText());
        extractParam.setDesc(command.getDesc());
        extractParam.setOutputSchema(command.getOutputSchema());
        extractParam.setAccessInfo(modelInfo);
        extractParam.setTemperature(command.getTemperature());
        return extractParam;
    }
}