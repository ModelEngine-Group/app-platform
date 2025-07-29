/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.model.PromptGenerateDto;
import modelengine.fit.jober.aipp.service.AippModelService;
import modelengine.jade.service.annotations.CarverSpan;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

/**
 * 提示词服务接口.
 *
 * @author 张越
 * @since 2024/11/29
 */
@Component
@RequestMapping(path = "/v1/api/model", group = "aipp提示词管理接口")
public class AippPromptController extends AbstractController {
    private final AippModelService aippModelService;

    public AippPromptController(Authenticator authenticator, AippModelService aippModelService) {
        super(authenticator);
        this.aippModelService = aippModelService;
    }

    /**
     * 智能生成提示词.
     *
     * @param httpRequest httpRequest
     * @param param 请求参数
     * @return Rsp<QueryChatRsp>
     */
    @CarverSpan(value = "operation.aippPrompt.prompt")
    @PostMapping(path = "/prompt", description = "智能生成提示词")
    public Rsp<String> prompt(HttpClassicServerRequest httpRequest, @RequestBody PromptGenerateDto param) {
        return Rsp.ok(this.aippModelService.generatePrompt(param, this.contextOf(httpRequest, "")));
    }
}
