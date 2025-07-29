/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.mapper;

import modelengine.jade.app.engine.base.dto.UsrFeedbackDto;

import java.util.List;

/**
 * 用户反馈映射
 *
 * @since 2024-5-24
 *
 */
public interface UsrFeedbackMapper {
    /**
     * 用户反馈创建接口
     *
     * @param usrFeedbackDto 用户反馈信息
     */
    void insert(UsrFeedbackDto usrFeedbackDto);

    /**
     * 用户反馈更新接口
     *
     * @param instanceId 对话实例id
     * @param usrFeedback 用户反馈
     * @param usrFeedbackText 用户反馈文本
     */
    void updateOne(String instanceId, Integer usrFeedback, String usrFeedbackText);

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
    List<UsrFeedbackDto> getAllUsrFeedbacks();

    /**
     * 通过日志Id获取用户反馈信息
     *
     * @param instanceId 对话实例id
     * @return 用户反馈信息
     */
    UsrFeedbackDto getUsrFeedbackByInstanceId(String instanceId);
}
