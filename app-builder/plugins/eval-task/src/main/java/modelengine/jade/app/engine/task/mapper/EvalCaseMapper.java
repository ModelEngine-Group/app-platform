/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import modelengine.jade.app.engine.task.dto.EvalCaseQueryParam;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.po.EvalCasePo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务用例持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalCaseMapper {
    /**
     * 创建评估任务用例。
     *
     * @param casePo 表示评估任务用例信息的 {@link EvalCasePo}。
     */
    void create(EvalCasePo casePo);

    /**
     * 通过评估实例唯一标识查询评估用例。
     *
     * @param instanceId 表示评估任务实例唯一标识的 {@link Long}。
     * @return 表示查询结果评估任务用例业务对象的 {@link List}{@code <}{@link EvalCaseEntity}{@code >}。
     */
    List<EvalCaseEntity> getCaseByInstanceId(Long instanceId);

    /**
     * 通过评估实例唯一标识分页查询评估用例。
     *
     * @param queryParam 表示评估任务实例查询数据的 {@link EvalCaseQueryParam}。
     * @return 表示查询结果评估任务用例业务对象的 {@link List}{@code <}{@link EvalCaseEntity}{@code >}。
     */
    List<EvalCaseEntity> listEvalCase(EvalCaseQueryParam queryParam);

    /**
     * 通过评估实例唯一标识统计评估用例。
     *
     * @param instanceId 表示评估任务实例唯一标识的 {@link Long}。
     * @return 表示查询结果评估任务用例统计数据的 {@link int}。
     */
    int countEvalCase(Long instanceId);

    /**
     * 通过评估实例唯一标识统计评估用例。
     *
     * @param instanceId 表示评估任务实例唯一标识的 {@link Long}。
     * @return 表示查询结果评估任务用例统计数据的 {@link int}。
     */
    int countByInstanceId(Long instanceId);
}