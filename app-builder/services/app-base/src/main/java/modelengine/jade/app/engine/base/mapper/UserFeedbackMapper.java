/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UserFeedbackDto;

import java.util.List;

/**
 * 用户反馈映射
 *
 * @since 2024-5-24
 *
 */
public interface UserFeedbackMapper {
    /**
     * 用户反馈创建接口
     *
     * @param userFeedbackDto 用户反馈信息
     */
    void insert(UserFeedbackDto userFeedbackDto);

    /**
     * 用户反馈更新接口
     *
     * @param instanceId 对话实例id
     * @param userFeedback 用户反馈
     * @param userFeedbackText 用户反馈文本
     */
    void updateOne(String instanceId, Integer userFeedback, String userFeedbackText);

    /**
     * 通过日志Id删除用户反馈记录
     *
     * @param instanceId 对话实例id
     */
    void deleteByLogId(String instanceId);

    /**
     * 获取用户反馈列表
     *
     * @return 用户反馈列表
     */
    List<UserFeedbackDto> getAllUserFeedbacks();

    /**
     * 通过日志Id获取用户反馈信息
     *
     * @param instanceId 对话实例id
     * @return 用户反馈信息
     */
    UserFeedbackDto getUserFeedbackByInstanceId(String instanceId);
}
