/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service;

import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.common.vo.PageVo;

/**
 * 表示评估任务实例服务。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
public interface EvalInstanceService {
    /**
     * 创建评估任务实例。
     *
     * @param taskId 表示评估任务业务对象的 {@link Long}。
     * @param traceId 表示评估任务实例运行业务对象的 {@link String}。
     */
    void createEvalInstance(Long taskId, String traceId);

    /**
     * 通过工作流实例唯一标识查询评估任务实例唯一标识。
     *
     * @param traceId 表示工作流实例唯一标识的 {@link String}。
     * @return 表示评估任务实例唯一标识的 {@link Long}。
     */
    Long getEvalInstanceId(String traceId);

    /**
     * 更新评估任务实例。
     *
     * @param entity 表示评估任务实例业务对象的 {@link Long}。
     */
    void updateEvalInstance(EvalInstanceEntity entity);

    /**
     * 分页查询评估任务实例。
     *
     * @param queryParam 表示评估任务实例查询参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例查询结果的 {@link PageVo}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    PageVo<EvalInstanceEntity> listEvalInstance(EvalInstanceQueryParam queryParam);

    /**
     * 根据评估任务运行实例唯一标识查询评估任务唯一标识。
     *
     * @param traceId 表示评估任务运行实例唯一标识。
     * @return 表示评估任务运行实例唯一标识的 {@link String}。
     */
    Long getTaskIdByTraceId(String traceId);
}