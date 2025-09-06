/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.po.EvalInstancePo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务实例持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Mapper
public interface EvalInstanceMapper {
    /**
     * 创建评估任务实例。
     *
     * @param po 表示评估任务实例的 {@link EvalInstancePo}。
     */
    void create(EvalInstancePo po);

    /**
     * 通过工作流实例唯一标识查询评估任务实例唯一标识。
     *
     * @param traceId 表示工作流实例唯一标识的 {@link String}。
     * @return 表示评估任务实例唯一标识的 {@link List}{@code <}{@link Long}{@code >}。
     */
    List<Long> getInstanceId(String traceId);

    /**
     * 查询评估任务实例。
     *
     * @param queryParam 表示评估任务实例查询参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例查询结果的 {@link List}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    List<EvalInstanceEntity> listEvalInstance(EvalInstanceQueryParam queryParam);

    /**
     * 统计评估任务实例数量。
     *
     * @param queryParam 表示评估任务实例查询参数的 {@link EvalInstanceQueryParam}。
     * @return 表示评估任务实例统计结果的 {@code int}。
     */
    int countEvalInstance(EvalInstanceQueryParam queryParam);

    /**
     * 更新评估任务实例。
     *
     * @param po 表示评估任务实例的 {@link EvalInstancePo}
     */
    void update(EvalInstancePo po);

    /**
     * 查询评估任务对应的最新实例。
     *
     * @param taskIds {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示对应评估任务实例的 {@link List}{@code <}{@link EvalInstanceEntity}{@code >}。
     */
    List<EvalInstanceEntity> findLatestInstances(List<Long> taskIds);

    /**
     * 根据评估任务运行实例唯一标识查询评估任务唯一标识。
     *
     * @param traceId 表示评估任务运行实例唯一标识
     * @return 表示评估任务运行实例唯一标识的 {@link String}。
     */
    Long getTaskIdByTraceId(String traceId);
}