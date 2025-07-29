/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service;

import modelengine.jade.app.engine.task.dto.EvalTaskQueryParam;
import modelengine.jade.app.engine.task.entity.EvalTaskEntity;
import modelengine.jade.app.engine.task.vo.EvalTaskVo;
import modelengine.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示评估任务服务。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
public interface EvalTaskService {
    /**
     * 创建评估任务。
     *
     * @param entity 表示评估任务业务对象的 {@link EvalTaskEntity}。
     */
    void createEvalTask(EvalTaskEntity entity);

    /**
     * 通过唯一标识查询评估任务元数据。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务元数据查询结果的 {@link PageVo}{@code <}{@link EvalTaskVo}{@code >}。
     */
    PageVo<EvalTaskVo> listEvalTask(EvalTaskQueryParam queryParam);

    /**
     * 删除评估任务。
     *
     * @param taskIds 表示评估任务删除列表的 {@link List}{@code <}{@link Long}{@code >}
     */
    void deleteEvalTask(List<Long> taskIds);
}