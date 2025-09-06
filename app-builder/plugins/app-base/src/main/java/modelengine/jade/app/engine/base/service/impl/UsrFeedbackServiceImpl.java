/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.jade.app.engine.base.dto.UsrFeedbackDto;
import modelengine.jade.app.engine.base.service.UsrFeedbackService;
import modelengine.jade.app.engine.base.mapper.UsrFeedbackMapper;

import java.util.List;

/**
 * Aipp用户反馈功能实现类
 *
 * @since 2024-5-24
 *
 */
@Component
public class UsrFeedbackServiceImpl implements UsrFeedbackService {
    private static final Logger log = Logger.get(UsrFeedbackServiceImpl.class);

    private final UsrFeedbackMapper usrFeedbackMapper;

    public UsrFeedbackServiceImpl(UsrFeedbackMapper userFeedbackMapper) {
        this.usrFeedbackMapper = userFeedbackMapper;
    }

    @Override
    @Fitable(id = "UsrFeedbackCreate")
    public void create(UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackMapper.insert(usrFeedbackDto);
    }

    @Override
    @Fitable(id = "UsrFeedbackUpdate")
    public void updateOne(String instanceId, UsrFeedbackDto usrFeedbackDto) {
        usrFeedbackMapper.updateOne(instanceId, usrFeedbackDto.getUsrFeedback(), usrFeedbackDto.getUsrFeedbackText());
    }

    @Override
    @Fitable(id = "UsrFeedbackDelete")
    public void deleteByLogId(String instanceId) {
        usrFeedbackMapper.deleteByLogId(instanceId);
    }

    @Override
    @Fitable(id = "UsrGetAllUsrFeedbacks")
    public List<UsrFeedbackDto> getAllUsrFeedbacks() {
        return usrFeedbackMapper.getAllUsrFeedbacks();
    }

    @Override
    @Fitable(id = "getUsrFeedbackByInstanceId")
    public UsrFeedbackDto getUsrFeedbackByInstanceId(String instanceId) {
        return usrFeedbackMapper.getUsrFeedbackByInstanceId(instanceId);
    }
}
