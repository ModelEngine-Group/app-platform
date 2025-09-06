/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.base.dto.UsrFeedbackDto;

import java.util.List;

/**
 * Aipp用户反馈功能接口
 *
 * @since 2024-5-24
 *
 */
public interface UsrFeedbackService {
    /**
     * 创建用户反馈信息
     *
     * @param usrFeedbackDto 用户反馈信息
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrFeedbackService.create")
    void create(UsrFeedbackDto usrFeedbackDto);

    /**
     * 更新用户反馈记录
     *
     * @param instanceId 应用实例id
     * @param usrFeedbackDto 用户反馈信息
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrFeedbackService.update")
    void updateOne(String instanceId, UsrFeedbackDto usrFeedbackDto);


    /**
     * 删除用户反馈记录
     *
     * @param instanceId 对话实例Id
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrFeedbackService.delete")
    void deleteByLogId(String instanceId);

    /**
     * 获取所有用户反馈记录
     *
     * @return 用户反馈列表
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrFeedbackService.getAllUsrFeedbacks")
    List<UsrFeedbackDto> getAllUsrFeedbacks();

    /**
     * 通过logId查询用户反馈记录
     *
     * @param instanceId 对话实例Id
     * @return 用户反馈信息
     */
    @Genericable(id = "modelengine.jade.app.engine.base.service.UsrFeedbackService.getUsrFeedbackByInstanceId")
    UsrFeedbackDto getUsrFeedbackByInstanceId(String instanceId);
}
