/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.dto.chat.ChatInfoRspDto;
import modelengine.fit.jober.aipp.dto.chat.CreateChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatInfoRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import java.util.List;
import java.util.Map;

/**
 * 历史对话接口
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/chat", group = "aipp对话管理接口")
public class AippChatController extends AbstractController {
    private final AippChatService aippChatService;

    /**
     * AippChatController
     *
     * @param authenticator authenticator
     * @param aippChatService aippChatService
     */
    public AippChatController(Authenticator authenticator, AippChatService aippChatService) {
        super(authenticator);
        this.aippChatService = aippChatService;
    }

    /**
     * createChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param body body
     * @return Rsp<QueryChatRsp>
     */
    @CarverSpan(value = "operation.aippChat.create")
    @PostMapping(path = "", description = "创建会话接口")
    public Rsp<QueryChatRsp> createChat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody CreateChatRequest body) {
        return Rsp.ok(this.aippChatService.createChat(body, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * queryChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param chatId chatId
     * @param body body
     * @return Rsp<QueryChatRsp>
     * @throws AippTaskNotFoundException 未查询到task异常
     * @deprecated 废弃，下个版本删除
     */
    @Deprecated
    @CarverSpan(value = "operation.aippChat.query")
    @PostMapping(path = "/chat_list/{chat_id}", description = "查询会话接口")
    public Rsp<QueryChatRsp> queryChat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("chat_id") @SpanAttr("chat_id") String chatId, @RequestBody QueryChatRequest body)
            throws AippTaskNotFoundException {
        if (StringUtils.isEmpty(body.getAppState())) {
            body.setAppState("active");
        }
        return Rsp.ok(this.aippChatService.queryChat(body, chatId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询会话信息列表
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param queryChatInfoRequest body
     * @return Rsp<QueryChatRsp>
     */
    @CarverSpan(value = "operation.aippChat.chat.info")
    @PostMapping(path = "/chat_info", description = "查询会话信息接口")
    public Rsp<List<ChatInfoRspDto>> queryLatestChatInfo(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody QueryChatInfoRequest queryChatInfoRequest) {
        return Rsp.ok(this.aippChatService.queryChatInfo(queryChatInfoRequest, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * queryChatList
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param body body
     * @return Rsp<RangedResultSet < QueryChatRsp>>
     */
    @CarverSpan(value = "operation.aippChat.queryList")
    @PostMapping(path = "/chat_list", description = "查询会话列表接口")
    public Rsp<RangedResultSet<QueryChatRspDto>> queryChatList(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody QueryChatRequest body) {
        if (StringUtils.isEmpty(body.getAppState())) {
            body.setAppState("active");
        }
        return Rsp.ok(this.aippChatService.queryChatList(body, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * deleteChat
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param appId appId
     * @param chatIds 会话ID, 当传入多个id时，以“,”进行分割
     * @return Rsp<Void>
     */
    @CarverSpan(value = "operation.aippChat.delete")
    @DeleteMapping(path = "", description = "删除会话接口")
    public Rsp<Void> deleteChat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "app_id", required = false) String appId,
            @RequestBody(value = "chat_id", required = false) @SpanAttr("chat_id") String chatIds) {
        this.aippChatService.deleteChat(chatIds, appId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * deleteChat（用于mag网关）
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param appId appId
     * @param chatIds 会话ID, 当传入多个id时，以“,”进行分割
     * @return Rsp<Void>
     */
    @CarverSpan(value = "operation.aippChat.delete")
    @PostMapping(path = "/chats_delete", description = "删除会话接口")
    public Rsp<Void> deleteChatV2(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "app_id", required = false) @SpanAttr("app_id") String appId,
            @RequestBody("chat_id") String chatIds) {
        return this.deleteChat(httpRequest, tenantId, appId, chatIds);
    }
}
