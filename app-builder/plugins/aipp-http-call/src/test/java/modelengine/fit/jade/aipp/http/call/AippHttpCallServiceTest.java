/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.http.call.command.HttpCallCommandHandler;
import modelengine.fit.jade.aipp.http.call.command.HttpCallResult;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * {@link AippHttpCallService}测试集。
 *
 * @author 张越
 * @since 2024-12-15
 */
@DisplayName("测试http调用算子对外暴露端口")
public class AippHttpCallServiceTest {
    private HttpCallCommandHandler handler;
    private HttpCallService httpCallService;

    @BeforeEach
    void setUp() {
        this.handler = mock(HttpCallCommandHandler.class);
        this.httpCallService = new AippHttpCallService(this.handler, List.of("blacklist.com"));
    }

    @Test
    @DisplayName("运行成功")
    void shouldOk() {
        // given
        HttpCallResult httpResult = mock(HttpCallResult.class);
        when(this.handler.handle(any())).thenReturn(httpResult);
        when(httpResult.getStatus()).thenReturn(200);

        HttpRequest request = constructHttpRequest("http://examples.com");

        // when
        HttpResult result = this.httpCallService.httpCall(request);

        // then
        Assertions.assertEquals(200, result.getStatus());
    }

    @Test
    @DisplayName("黑名单网站调用失败")
    void blackListShouldBeBlocked() {
        // given
        HttpRequest request = constructHttpRequest("http://blacklist.com");

        // when
        HttpResult result = this.httpCallService.httpCall(request);

        // then
        Assertions.assertEquals(-1, result.getStatus());
        Assertions.assertEquals("Invalid request.", result.getErrorMsg());
    }

    private static HttpRequest constructHttpRequest(String url) {
        HttpRequest request = new HttpRequest();
        request.setHttpMethod("GET");
        request.setUrl(url);
        request.setTimeout(1000);
        request.setArgs(MapBuilder.<String, Object>get().put("111", "2222").build());
        return request;
    }
}
