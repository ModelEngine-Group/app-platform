/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UserFeedbackDto;

import java.util.List;

/**
 * 用户反馈映射。
 *
 * @author 陈潇文
 * @since 2024-05-24
 */
public interface UserFeedbackMapper {
    /**
     * 用户反馈创建接口。
     *
     * @param userFeedbackDto 表示用户反馈信息的 {@link UserFeedbackDto}。
     */
    void insert(UserFeedbackDto userFeedbackDto);

    /**
     * 用户反馈更新接口。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     * @param userFeedback 表示用户反馈的 {@link Integer}。
     * @param userFeedbackText 表示用户反馈文本的 {@link String}。
     */
    void updateOne(String instanceId, Integer userFeedback, String userFeedbackText);

    /**
     * 通过日志唯一标识删除用户反馈记录。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     */
    void deleteByLogId(String instanceId);

    /**
     * 获取用户反馈列表。
     *
     * @return 表示用户反馈列表的 {@link List}{@code <}{@link UserFeedbackDto}{@code >}。
     */
    List<UserFeedbackDto> getAllUserFeedbacks();

    /**
     * 通过日志唯一标识获取用户反馈信息。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}。
     * @return 表示用户反馈信息的 {@link UserFeedbackDto}。
     */
    UserFeedbackDto getUserFeedbackByInstanceId(String instanceId);
}
