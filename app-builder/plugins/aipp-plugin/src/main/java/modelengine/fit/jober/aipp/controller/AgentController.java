/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.dto.AgentCreateInfoDto;
import modelengine.fit.jober.aipp.entity.AgentInfoEntity;
import modelengine.fit.jober.aipp.service.AgentInfoGenerateService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

/**
 * 表示智能体信息获取接口集。
 *
 * @author 兰宇晨
 * @since 2024-12-6
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/agent")
public class AgentController extends AbstractController {
    private static final Logger log = Logger.get(AgentController.class);

    private final Authenticator authenticator;
    private final AgentInfoGenerateService agentInfoGenerateService;

    /**
     * 构造函数。
     *
     * @param authenticator 表示认证器的 {@link Authenticator}。
     * @param agentInfoGenerateService 表示生成智能体基本信息的 {@link AgentInfoGenerateService}。
     */
    public AgentController(Authenticator authenticator, AgentInfoGenerateService agentInfoGenerateService) {
        super(authenticator);
        this.authenticator = authenticator;
        this.agentInfoGenerateService = agentInfoGenerateService;
    }

    /**
     * 表示生成智能体信息的接口。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param dto 表示用于生成智能体信息的 {@link AgentCreateInfoDto}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @CarverSpan(value = "operation.aippChat.query")
    @PostMapping
    public Rsp<AgentInfoEntity> generateAgentInfo(HttpClassicServerRequest request,
            @RequestBody @Validated @SpanAttr("description:$.description") AgentCreateInfoDto dto,
            @PathVariable("tenant_id") String tenantId) {
        AgentInfoEntity entity = new AgentInfoEntity();
        OperationContext context = this.contextOf(request, tenantId);
        try {
            entity.setName(this.agentInfoGenerateService.generateName(dto.getDescription(), context));
            entity.setGreeting(this.agentInfoGenerateService.generateGreeting(dto.getDescription(), context));
            entity.setPrompt(this.agentInfoGenerateService.generatePrompt(dto.getDescription(), context));
            entity.setTools(
                    this.agentInfoGenerateService.selectTools(dto.getDescription(), context.getOperator(), context));
        } catch (ClientException e) {
            // 模型生成内容超时的情况下，提醒用户更换默认模型
            log.error("Failed to generate agent infos.", e);
            throw new AippException(AippErrCode.GENERATE_CONTENT_FAILED, "agent infos", e.getMessage());
        }
        return Rsp.ok(entity);
    }
}
