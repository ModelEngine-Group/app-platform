/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatInfo;
import modelengine.fit.jober.aipp.dto.chat.ChatQueryParams;
import modelengine.fit.jober.aipp.dto.chat.MessageInfo;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.beans.BeanUtils;
import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link AippChatServiceAdapterImpl} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 AippChatServiceAdapterImpl")
public class AippChatServiceAdapterImplTest {
    private final AippChatService aippChatService = mock(AippChatService.class);
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);
    private final AippChatServiceAdapterImpl aippChatServiceAdapterImpl =
            new AippChatServiceAdapterImpl(aippChatService, serializer);

    @Test
    @DisplayName("当查询会话列表时，返回结果正确。")
    void shouldReturnOkWhenQueryChatList() {
        ChatQueryParams body = new ChatQueryParams();
        OperationContext operationContext = new OperationContext();
        QueryChatRequest queryChatRequest = new QueryChatRequest();
        BeanUtils.copyProperties(body, queryChatRequest);
        QueryChatRspDto queryChatRspDto1 = new QueryChatRspDto();
        queryChatRspDto1.setAppId("value1");
        queryChatRspDto1.setChatId("value2");
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setAppName("appName");
        queryChatRspDto1.setMassageList(Collections.singletonList(messageInfo));
        QueryChatRspDto queryChatRspDto2 = new QueryChatRspDto();
        queryChatRspDto2.setAppId("value3");
        queryChatRspDto2.setChatId("value4");
        queryChatRspDto2.setCurrentTime(456);

        List<QueryChatRspDto> queryChatRspDtoList = Arrays.asList(queryChatRspDto1, queryChatRspDto2);
        RangedResultSet<QueryChatRspDto> rangedResultSet = RangedResultSet.create(queryChatRspDtoList,
                new RangeResult(1, 2, 3));
        when(aippChatService.queryChatList(queryChatRequest, operationContext)).thenReturn(rangedResultSet);
        RangedResultSet<ChatInfo> result = aippChatServiceAdapterImpl.queryChatList(body, operationContext);
        assertThat(result.getResults()).hasSize(2);
        ChatInfo chatInfo1 = result.getResults().get(0);
        QueryChatRspDto queryChatRspDtoOriginal1 = queryChatRspDtoList.get(0);
        assertThat(chatInfo1.getAppId()).isEqualTo(queryChatRspDtoOriginal1.getAppId());
        assertThat(chatInfo1.getChatId()).isEqualTo(queryChatRspDtoOriginal1.getChatId());
        assertThat(chatInfo1.getMessageList().get(0).getAppName()).isEqualTo(
                queryChatRspDtoOriginal1.getMassageList().get(0).getAppName());
        ChatInfo chatInfo2 = result.getResults().get(1);
        QueryChatRspDto queryChatRspDtoOriginal2 = queryChatRspDtoList.get(1);
        assertThat(chatInfo2.getChatId()).isEqualTo(queryChatRspDtoOriginal2.getChatId());
        assertThat(chatInfo2.getAppId()).isEqualTo(queryChatRspDtoOriginal2.getAppId());
        assertThat(chatInfo2.getCurrentTime()).isEqualTo(queryChatRspDtoOriginal2.getCurrentTime());
    }

    @Test
    @DisplayName("测试删除会话接口。")
    void shouldReturnOkWhenDeleteChat() {
        String chatId = "123";
        String appId = "456";
        OperationContext operationContext = new OperationContext();
        aippChatServiceAdapterImpl.deleteChat(chatId, appId, operationContext);
        verify(aippChatService, times(1)).deleteChat(chatId, appId, operationContext);
    }
}