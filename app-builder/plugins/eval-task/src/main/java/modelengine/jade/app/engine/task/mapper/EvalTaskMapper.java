/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import modelengine.jade.app.engine.task.dto.EvalTaskQueryParam;
import modelengine.jade.app.engine.task.entity.EvalTaskEntity;
import modelengine.jade.app.engine.task.po.EvalTaskPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表示评估任务持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Mapper
public interface EvalTaskMapper {
    /**
     * 创建评估任务。
     *
     * @param evalTaskPo 表示评估任务信息的 {@link EvalTaskPo}。
     */
    void create(EvalTaskPo evalTaskPo);

    /**
     * 分页查询评估任务元数据。
     *
     * @param queryParam 表示评估任务查询参数的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务元数据查询结果的 {@link List}{@code <}{@link EvalTaskEntity}{@code >}。
     */
    List<EvalTaskEntity> listEvalTask(EvalTaskQueryParam queryParam);

    /**
     * 统计评估任务数量。
     *
     * @param queryParam 表示评估任务查询参数的 {@link EvalTaskQueryParam}。
     * @return 表示评估任务统计结果的 {@code int}。
     */
    int countEvalTask(EvalTaskQueryParam queryParam);

    /**
     * 批量软删除评估任务，更新评估任务状态。
     *
     * @param taskIds 表示评估任务的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示成功修改的行数 {@code int}。
     */
    int updateDeletedTask(@Param("list") List<Long> taskIds);
}