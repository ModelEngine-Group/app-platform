/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.controller;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.base.dto.UserFeedbackDto;
import modelengine.jade.app.engine.base.service.UserFeedbackService;

import java.util.List;

/**
 * 处理用户反馈请求。
 *
 * @author 陈潇文
 * @since 2024-05-24
 */
@Component
@RequestMapping("/aipp/user")
public class UserFeedbackController {
    private final UserFeedbackService userFeedbackService;

    public UserFeedbackController(UserFeedbackService userFeedbackService) {
        this.userFeedbackService = userFeedbackService;
    }

    /**
     * 创建用户反馈记录。
     *
     * @param userFeedbackDto 表示用户反馈消息体的 {@link UserFeedbackDto}。
     */
    @PostMapping("/feedback")
    public void createUserFeedback(@RequestBody UserFeedbackDto userFeedbackDto) {
        this.userFeedbackService.create(userFeedbackDto);
    }

    /**
     * 更新用户反馈信息。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     * @param userFeedbackDto 表示用户反馈消息体的 {@link UserFeedbackDto}。
     */
    @PatchMapping("/feedback/{instanceId}")
    public void updateUserFeedback(@PathVariable("instanceId") String instanceId,
                                  @RequestBody UserFeedbackDto userFeedbackDto) {
        this.userFeedbackService.updateOne(instanceId, userFeedbackDto);
    }

    /**
     * 删除用户反馈信息。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     */
    @DeleteMapping("/feedback/{instanceId}")
    public void deleteByLogId(@PathVariable("instanceId") String instanceId) {
        this.userFeedbackService.deleteByLogId(instanceId);
    }

    /**
     * 获取用户反馈信息列表。
     *
     * @return 表示用户反馈信息列表的 {@link List}{@code <}{@link UserFeedbackDto}{@code >}。
     */
    @GetMapping("/feedbacks")
    public List<UserFeedbackDto> getAllUserFeedbacks() {
        return this.userFeedbackService.getAllUserFeedbacks();
    }

    /**
     * 通过日志唯一标识获取对话信息列表。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     * @return 表示对话信息的 {@link UserFeedbackDto}。
     */
    @GetMapping("/feedback/{instanceId}")
    public UserFeedbackDto getAllAnswerByInstanceId(@PathVariable("instanceId") String instanceId) {
        return this.userFeedbackService.getUserFeedbackByInstanceId(instanceId);
    }
}
