/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.dto.PluginToolDto;
import modelengine.fit.jober.aipp.dto.StoreNodeInfoDto;
import modelengine.fit.jober.aipp.service.StoreService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link StoreController} 的测试类。
 *
 * @author 鲁为
 * @since 2024-08-02
 */
@MvcTest(classes = {StoreController.class})
@DisplayName("测试 StoreController")
public class StoreControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private Authenticator authenticator;

    @Mock
    private StoreService storeService;

    private StoreController storeController;

    private HttpClassicClientResponse<?> response;

    private MockedStatic<UserContextHolder> opContextHolderMock;

    @BeforeEach
    void setUp() {
        opContextHolderMock = mockStatic(UserContextHolder.class);
        this.opContextHolderMock.when(UserContextHolder::get).thenReturn(new UserContext("Jane", "127.0.0.1", "en"));
        this.storeController = new StoreController(this.authenticator, this.storeService);
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
        this.opContextHolderMock.close();
    }

    @Test
    @DisplayName("当根据参数查询工具时，返回正确结果。")
    void shouldReturnCorrectPluginsWhenGetPlugins() {
        PluginToolDto pluginToolDto = new PluginToolDto();
        Mockito.when(this.storeService.getPlugins(any(), any())).thenReturn(pluginToolDto);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                        "/v1/api/31f20efc7e0848deab6a6bc10fc3021e/store/plugins")
                .param("excludeTags", "APP")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(Rsp.class, new Type[] {PluginToolDto.class}));
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当 type 为 basic 或者 evaluation 时，成功返回节点列表。")
    void shouldReturnBasicNodeListWhenTypeIsBasic() {
        List<StoreNodeInfoDto> storeNodeInfoDto = new ArrayList<>();
        Rsp<List<StoreNodeInfoDto>> rsp1 = this.storeController.getNodesList("basic");
        Rsp<List<StoreNodeInfoDto>> rsp2 = this.storeController.getNodesList("evaluation");
        Mockito.when(this.storeService.getNode(anyString())).thenReturn(storeNodeInfoDto);
        assertThat(rsp1.getCode()).isEqualTo(0);
        assertThat(rsp2.getCode()).isEqualTo(0);
    }

    @Test
    @DisplayName("当 type 不是 evaluation 或者 basic 时，获取列表失败，返回相关信息。")
    void shouldReturnErrWhenTypeIsOthers() {
        Rsp<List<StoreNodeInfoDto>> rsp = this.storeController.getNodesList("tool");
        assertThat(rsp.getCode()).isEqualTo(AippErrCode.INPUT_PARAM_IS_INVALID.getErrorCode());
    }
}
