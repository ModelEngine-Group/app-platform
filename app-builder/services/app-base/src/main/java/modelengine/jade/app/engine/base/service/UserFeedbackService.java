/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.base.dto.UserFeedbackDto;

import java.util.List;

/**
 * 应用编排用户反馈功能接口。
 *
 * @author 陈潇文
 * @since 2024-05-24
 */
public interface UserFeedbackService {
    /**
     * 创建用户反馈信息。
     *
     * @param userFeedbackDto 表示用户反馈信息的 {@link UserFeedbackDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserFeedbackService.create")
    void create(UserFeedbackDto userFeedbackDto);

    /**
     * 更新用户反馈记录
     *
     * @param instanceId 表示应用实例唯一标识的 {@link String}。
     * @param userFeedbackDto 表示用户反馈信息的 {@link UserFeedbackDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserFeedbackService.update")
    void updateOne(String instanceId, UserFeedbackDto userFeedbackDto);


    /**
     * 删除用户反馈记录。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserFeedbackService.delete")
    void deleteByLogId(String instanceId);

    /**
     * 获取所有用户反馈记录。
     *
     * @return 表示用户反馈列表的 {@link List}{@code <}{@link UserFeedbackDto}{@code >}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserFeedbackService.getAllUserFeedbacks")
    List<UserFeedbackDto> getAllUserFeedbacks();

    /**
     * 通过日志唯一标识查询用户反馈记录。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     * @return 表示用户反馈信息的 {@link UserFeedbackDto}。
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UserFeedbackService.getUserFeedbackByInstanceId")
    UserFeedbackDto getUserFeedbackByInstanceId(String instanceId);
}
