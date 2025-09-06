/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatInfo;
import modelengine.fit.jober.aipp.dto.chat.ChatQueryParams;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.genericable.adapter.AippChatServiceAdapter;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.beans.BeanUtils;
import modelengine.fitframework.serialization.ObjectSerializer;

import java.util.stream.Collectors;

/**
 * {@link AippChatService} 的适配器类的实现类。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Component
public class AippChatServiceAdapterImpl implements AippChatServiceAdapter {
    private final AippChatService aippChatService;

    private final ObjectSerializer serializer;

    public AippChatServiceAdapterImpl(AippChatService aippChatService,
            @Fit(alias = "json") ObjectSerializer serializer) {
        this.aippChatService = notNull(aippChatService, "The aipp chat service cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    public RangedResultSet<ChatInfo> queryChatList(ChatQueryParams params, OperationContext operationContext) {
        QueryChatRequest queryChatRequest = BeanUtils.copyProperties(params, QueryChatRequest.class);
        RangedResultSet<QueryChatRspDto> rangedResultSet = this.aippChatService.queryChatList(queryChatRequest,
                operationContext);
        return this.queryChatRspDtoConvertToAdapter(rangedResultSet);
    }

    @Override
    public void deleteChat(String chatId, String appId, OperationContext operationContext) {
        this.aippChatService.deleteChat(chatId, appId, operationContext);
    }

    RangedResultSet<ChatInfo> queryChatRspDtoConvertToAdapter(RangedResultSet<QueryChatRspDto> dtos) {
        return RangedResultSet.create(dtos.getResults()
                .stream()
                .map(queryChatRspDto -> this.serializer.<ChatInfo>deserialize(
                        this.serializer.serialize(queryChatRspDto), ChatInfo.class))
                .collect(Collectors.toList()), dtos.getRange());
    }
}
