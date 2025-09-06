/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import modelengine.jade.app.engine.task.convertor.EvalTaskConvertor;
import modelengine.jade.app.engine.task.dto.EvalTaskQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.entity.EvalTaskEntity;
import modelengine.jade.app.engine.task.mapper.EvalInstanceMapper;
import modelengine.jade.app.engine.task.mapper.EvalTaskMapper;
import modelengine.jade.app.engine.task.po.EvalTaskPo;
import modelengine.jade.app.engine.task.service.EvalTaskService;
import modelengine.jade.app.engine.task.vo.EvalTaskVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表示 {@link EvalTaskService} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Component
public class EvalTaskServiceImpl implements EvalTaskService {
    private final EvalTaskMapper taskMapper;
    private final EvalInstanceMapper instanceMapper;

    /**
     * 表示评估任务服务实现的构建器。
     *
     * @param taskMapper 表示评估任务服务持久层接口的 {@link EvalTaskMapper}。
     * @param instanceMapper 表示评估任务实例持久层接口的 {@link EvalInstanceMapper}。
     */
    public EvalTaskServiceImpl(EvalTaskMapper taskMapper, EvalInstanceMapper instanceMapper) {
        this.taskMapper = taskMapper;
        this.instanceMapper = instanceMapper;
    }

    @Override
    public void createEvalTask(EvalTaskEntity entity) {
        EvalTaskPo evalTaskPo = EvalTaskConvertor.INSTANCE.entityToPo(entity);
        this.taskMapper.create(evalTaskPo);
    }

    @Override
    public PageVo<EvalTaskVo> listEvalTask(EvalTaskQueryParam queryParam) {
        List<EvalTaskEntity> evalTaskEntities = this.taskMapper.listEvalTask(queryParam);
        int evalTaskCount = this.taskMapper.countEvalTask(queryParam);
        Map<Long, EvalTaskEntity> taskEntityMap =
                evalTaskEntities.stream().collect(Collectors.toMap(EvalTaskEntity::getId, e -> e));
        List<Long> taskIds = new ArrayList<>(taskEntityMap.keySet());
        taskIds.sort(Collections.reverseOrder());

        if (taskIds.isEmpty()) {
            return PageVo.of(evalTaskCount, Collections.emptyList());
        }

        List<EvalInstanceEntity> evalInstanceEntities = this.instanceMapper.findLatestInstances(taskIds);
        Map<Long, EvalInstanceEntity> instanceEntityMap =
                evalInstanceEntities.stream().collect(Collectors.toMap(EvalInstanceEntity::getTaskId, e -> e));

        List<EvalTaskVo> evalTaskVos = taskIds.stream().map(taskId -> {
            EvalTaskEntity evalTaskEntity = taskEntityMap.get(taskId);
            EvalInstanceEntity evalInstanceEntity =
                    instanceEntityMap.getOrDefault(taskId, new EvalInstanceEntity());
            return EvalTaskConvertor.INSTANCE.mapToVo(evalTaskEntity, evalInstanceEntity);
        }).collect(Collectors.toList());
        return PageVo.of(evalTaskCount, evalTaskVos);
    }

    @Override
    public void deleteEvalTask(List<Long> taskIds) {
        this.taskMapper.updateDeletedTask(taskIds);
    }
}