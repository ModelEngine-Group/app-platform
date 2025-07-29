/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.AgentCreateInfoDto;
import modelengine.fit.jober.aipp.service.AgentInfoGenerateService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自动生成智能体信息接口测试类。
 *
 * @author 兰宇晨
 * @since 2024-12-09
 */
@MvcTest(classes = {AgentController.class})
@DisplayName("测试 AgentController")
public class AgentControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private Authenticator authenticator;

    @Mock
    private AgentInfoGenerateService agentInfoGenerateService;

    private HttpClassicClientResponse<?> response;

    @Test
    @DisplayName("测试根据描述生成智能体信息")
    void shouldReturnOkWhenGenerateAgentInfo() {
        when(this.agentInfoGenerateService.generateName(anyString(), any())).thenReturn("NAME");
        when(this.agentInfoGenerateService.generateGreeting(anyString(), any())).thenReturn("GREETING");
        when(this.agentInfoGenerateService.generatePrompt(anyString(), any())).thenReturn("PROMPT");
        List<String> tools = new ArrayList<String>() {{
            add("UNIQUENAME");
        }};
        when(this.agentInfoGenerateService.selectTools(anyString(), any(), any())).thenReturn(tools);
        AgentCreateInfoDto agentCreateInfoDto = new AgentCreateInfoDto();
        agentCreateInfoDto.setDescription("DESC");
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/api/tid/agent")
                .jsonEntity(agentCreateInfoDto)
                .responseType(Rsp.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        Rsp<Map<String, Object>> entityRsp = ObjectUtils.cast(response.objectEntity().get().object());
        Map<String, Object> entity = entityRsp.getData();
        assertThat(entity).contains(entry("name", "NAME"))
                .contains(entry("greeting", "GREETING"))
                .contains(entry("prompt", "PROMPT"))
                .contains(entry("tools", tools));
    }
}
