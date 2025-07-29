/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service;

import modelengine.jade.app.engine.eval.entity.EvalDatasetEntity;
import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示评估数据集服务。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
public interface EvalDatasetService {
    /**
     * 插入评估数据集。
     *
     * @param entity 表示评估数据集业务对象的 {@link EvalDatasetEntity}。
     */
    void create(EvalDatasetEntity entity);

    /**
     * 删除评估数据集。
     *
     * @param datasetIds 表示评估数据集编号的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void delete(List<Long> datasetIds);

    /**
     * 分页查询评估数据集元数据。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集元数据查询结果的 {@link PageVo}{@code <}{@link EvalDatasetVo}{@code >}。
     */
    PageVo<EvalDatasetVo> listEvalDataset(EvalDatasetQueryParam queryParam);

    /**
     * 通过唯一标识查询评估数据集元数据。
     *
     * @param datasetId 表示评估数据集唯一标识的 {@link Long}。
     * @return 表示评估数据集元数据查询结果的 {@link EvalDatasetVo}。
     */
    EvalDatasetVo getEvalDatasetById(Long datasetId);

    /**
     * 更新评估数据集信息。
     *
     * @param updateEntity 表示新的评估数据集信息的 {@link EvalDatasetEntity}。
     */
    void updateEvalDataset(EvalDatasetEntity updateEntity);
}