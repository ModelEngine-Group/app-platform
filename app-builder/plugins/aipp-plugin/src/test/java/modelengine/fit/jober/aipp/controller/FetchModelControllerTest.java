/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 查询接入服务列表接口的测试类。
 *
 * @author 方誉州
 * @since 2024-09-14
 */
@MvcTest(classes = {FetchModelController.class})
@DisplayName("查询接入模型服务列表接口测试")
public class FetchModelControllerTest {
    @Fit
    private MockMvc mockMvc;
    @Mock
    private AippModelCenter aippModelCenter;
    @Mock
    private Authenticator authenticator;
    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("查询接入模型服务列表")
    void testFetchModelList() {
        List<ModelAccessInfo> modelList = Collections.singletonList(ModelAccessInfo.builder()
                .serviceName("testService")
                .tag("testTag")
                .build());
        ModelListDto modelListDto = ModelListDto.builder().models(modelList).total(modelList.size()).build();
        when(this.aippModelCenter.fetchModelList(any(), any(), any())).thenReturn(modelListDto);

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/api/fetch/model-list")
                .responseType(ModelListDto.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        ModelListDto rsp = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(rsp.getTotal()).isEqualTo(1);
        assertThat(rsp.getModels()).extracting(ModelAccessInfo::getServiceName).contains("testService");
    }
}
