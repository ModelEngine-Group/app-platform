/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import modelengine.jade.app.engine.task.convertor.EvalInstanceConvertor;
import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.mapper.EvalInstanceMapper;
import modelengine.jade.app.engine.task.po.EvalInstancePo;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 表示 {@link EvalInstanceService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Component
public class EvalInstanceServiceImpl implements EvalInstanceService {
    private final EvalInstanceMapper taskInstanceMapper;

    /**
     * 表示评估任务实例服务实现的构建器。
     *
     * @param instanceMapper 表示评估任务实例持久层接口的 {@link EvalInstanceMapper}。
     */
    public EvalInstanceServiceImpl(EvalInstanceMapper instanceMapper) {
        this.taskInstanceMapper = instanceMapper;
    }

    @Override
    public void createEvalInstance(Long taskId, String traceId) {
        EvalInstancePo po = new EvalInstancePo();
        po.setTaskId(taskId);
        po.setTraceId(traceId);
        this.taskInstanceMapper.create(po);
    }

    @Override
    public Long getEvalInstanceId(String traceId) {
        return this.taskInstanceMapper.getInstanceId(traceId).get(0);
    }

    @Override
    public void updateEvalInstance(EvalInstanceEntity entity) {
        EvalInstancePo po = EvalInstanceConvertor.INSTANCE.entityToPo(entity);
        this.taskInstanceMapper.update(po);
    }

    @Override
    public PageVo<EvalInstanceEntity> listEvalInstance(EvalInstanceQueryParam queryParam) {
        List<EvalInstanceEntity> instanceEntities = this.taskInstanceMapper.listEvalInstance(queryParam);
        int instanceCount = this.taskInstanceMapper.countEvalInstance(queryParam);
        return PageVo.of(instanceCount, instanceEntities);
    }

    @Override
    public Long getTaskIdByTraceId(String traceId) {
        return this.taskInstanceMapper.getTaskIdByTraceId(traceId);
    }
}