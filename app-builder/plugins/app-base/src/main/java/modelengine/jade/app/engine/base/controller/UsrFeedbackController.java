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
import modelengine.jade.app.engine.base.dto.UsrFeedbackDto;
import modelengine.jade.app.engine.base.service.UsrFeedbackService;

import java.util.List;

/**
 * 处理用户Aipp请求
 *
 * @since 2024-5-24
 *
 */
@Component
@RequestMapping("/aipp/usr")
public class UsrFeedbackController {
    private final UsrFeedbackService usrFeedbackService;

    public UsrFeedbackController(UsrFeedbackService usrFeedbackService) {
        this.usrFeedbackService = usrFeedbackService;
    }

    /**
     * 创建用户反馈记录
     *
     * @param usrFeedbackDto 用户反馈消息体
     */
    @PostMapping("/feedback")
    public void createUsrFeedback(@RequestBody UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackService.create(usrFeedbackDto);
    }

    /**
     * 更新用户反馈信息
     *
     * @param usrFeedbackDto 用户反馈消息体
     * @param instanceId 对话实例Id
     */
    @PatchMapping("/feedback/{instanceId}")
    public void updateUsrFeedback(@PathVariable("instanceId") String instanceId,
                                  @RequestBody UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackService.updateOne(instanceId, usrFeedbackDto);
    }

    /**
     * 删除用户反馈信息
     *
     * @param instanceId 对话实例Id
     */
    @DeleteMapping("/feedback/{instanceId}")
    public void deleteByLogId(@PathVariable("instanceId") String instanceId) {
        usrFeedbackService.deleteByLogId(instanceId);
    }

    /**
     * 获取用户反馈信息列表
     *
     * @return 用户反馈信息列表
     */
    @GetMapping("/feedbacks")
    public List<UsrFeedbackDto> getAllUsrFeedbacks() {
        return usrFeedbackService.getAllUsrFeedbacks();
    }

    /**
     * 通过LogId获取对话信息列表
     *
     * @param instanceId 对话实例Id
     * @return 对话信息
     */
    @GetMapping("/feedback/{instanceId}")
    public UsrFeedbackDto getAllAnswerByInstanceId(@PathVariable("instanceId") String instanceId) {
        return usrFeedbackService.getUsrFeedbackByInstanceId(instanceId);
    }
}
