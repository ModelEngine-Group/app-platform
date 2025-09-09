/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.jade.app.engine.base.dto.UserFeedbackDto;
import modelengine.jade.app.engine.base.service.UserFeedbackService;
import modelengine.jade.app.engine.base.mapper.UserFeedbackMapper;

import java.util.List;

/**
 * Aipp用户反馈功能实现类
 *
 * @since 2024-5-24
 *
 */
@Component
public class UserFeedbackServiceImpl implements UserFeedbackService {
    private static final Logger log = Logger.get(UserFeedbackServiceImpl.class);

    private final UserFeedbackMapper userFeedbackMapper;

    public UserFeedbackServiceImpl(UserFeedbackMapper userFeedbackMapper) {
        this.userFeedbackMapper = userFeedbackMapper;
    }

    @Override
    @Fitable(id = "UserFeedbackCreate")
    public void create(UserFeedbackDto userFeedbackDto) {
        this.userFeedbackMapper.insert(userFeedbackDto);
    }

    @Override
    @Fitable(id = "UserFeedbackUpdate")
    public void updateOne(String instanceId, UserFeedbackDto userFeedbackDto) {
        this.userFeedbackMapper.updateOne(instanceId, userFeedbackDto.getUserFeedback(), userFeedbackDto.getUserFeedbackText());
    }

    @Override
    @Fitable(id = "UserFeedbackDelete")
    public void deleteByLogId(String instanceId) {
        this.userFeedbackMapper.deleteByLogId(instanceId);
    }

    @Override
    @Fitable(id = "UserGetAllUserFeedbacks")
    public List<UserFeedbackDto> getAllUserFeedbacks() {
        return this.userFeedbackMapper.getAllUserFeedbacks();
    }

    @Override
    @Fitable(id = "getUserFeedbackByInstanceId")
    public UserFeedbackDto getUserFeedbackByInstanceId(String instanceId) {
        return this.userFeedbackMapper.getUserFeedbackByInstanceId(instanceId);
    }
}
