/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.northbound.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.base.dto.UserFeedbackDto;
import modelengine.jade.app.engine.base.service.UserFeedbackService;

/**
 * 用户反馈北向接口。
 *
 * @author 陈潇文
 * @since 2025-07-18
 */
@Component
@RequestMapping(path = "/api/app/v1/aipp/user")
public class UserFeedbackController extends AbstractController {
    private final UserFeedbackService userFeedbackService;

    /**
     * 用身份校验器 {@link Authenticator} 和 Aipp 用户反馈功能接口 {@link UserFeedbackService} 构造 {@link UserFeedbackController}。
     *
     * @param authenticator 表示身份校验器的 {@link Authenticator}。
     * @param userFeedbackService 表示 Aipp 用户反馈功能接口的 {@link UserFeedbackService}。
     */
    public UserFeedbackController(Authenticator authenticator, UserFeedbackService userFeedbackService) {
        super(authenticator);
        this.userFeedbackService = userFeedbackService;
    }

    /**
     * 创建用户反馈记录。
     *
     * @param userFeedbackDto 表示用户反馈消息体的 {@link UserFeedbackDto}。
     */
    @PostMapping(path = "/feedback", summary = "创建用户反馈记录",
            description = "该接口用于创建用户对一个对话实例的反馈记录。")
    public Rsp<Void> createUsrFeedback(@RequestBody UserFeedbackDto userFeedbackDto) {
        this.userFeedbackService.create(userFeedbackDto);
        return Rsp.ok();
    }

    /**
     * 更新用户反馈信息。
     *
     * @param userFeedbackDto 表示用户反馈消息体的 {@link UserFeedbackDto}。
     * @param instanceId 表示对话实例 Id 的 {@link String}。
     */
    @PatchMapping(path = "/feedback/{instanceId}", summary = "更新用户反馈记录",
            description = "该接口用于更新用户对一个对话实例反馈记录。")
    public Rsp<Void> updateUsrFeedback(@PathVariable("instanceId") String instanceId,
            @RequestBody UserFeedbackDto userFeedbackDto) {
        this.userFeedbackService.updateOne(instanceId, userFeedbackDto);
        return Rsp.ok();
    }

    /**
     * 通过 logId 查询用户反馈记录
     *
     * @param instanceId 表示对话实例 Id 的 {@link String}。
     * @return 表示反馈记录的 {@link UserFeedbackDto}。
     */
    @GetMapping(path = "/feedback/{instanceId}", summary = "查询用户反馈记录",
            description = "该接口可以通过待查询实例的唯一标识符来查询实例的反馈记录。")
    public Rsp<UserFeedbackDto> getAllAnswerByInstanceId(@PathVariable("instanceId") String instanceId) {
        return Rsp.ok(this.userFeedbackService.getUserFeedbackByInstanceId(instanceId));
    }
}
