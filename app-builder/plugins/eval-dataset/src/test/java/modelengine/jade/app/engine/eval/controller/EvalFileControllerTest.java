/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.entity.JsonEntity;
import modelengine.jade.app.engine.eval.service.EvalFileService;
import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.ReadableBinaryEntity;
import modelengine.fit.http.entity.support.DefaultReadableBinaryEntity;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

/**
 * 表示 {@link EvalFileController} 的测试集。
 *
 * @author 兰宇晨
 * @since 2024-08-09
 */
@MvcTest(classes = {EvalFileController.class})
@DisplayName("测试 EvalFileController")
public class EvalFileControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalFileService evalFileService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("解析 Json 文件接口成功")
    void shouldOkWhenParseJsonFile() throws FileNotFoundException {
        when(this.evalFileService.parseJsonFileToEvalData(any())).thenReturn(new JsonEntity(Collections.EMPTY_LIST,
                StringUtils.EMPTY));

        File file = new File(this.getClass().getClassLoader().getResource("test/valid_json.json").getFile());
        ReadableBinaryEntity readableBinaryEntity =
                new DefaultReadableBinaryEntity(mock(HttpMessage.class), new FileInputStream(file));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/eval/file").entity(readableBinaryEntity);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}
