/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.mapper;

import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.po.EvalDatasetPo;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表示评估数据集持久层接口。
 *
 * @author 兰宇晨
 * @since 2024-07-27
 */
@Mapper
public interface EvalDatasetMapper {
    /**
     * 创建评估数据集。
     *
     * @param evalDatasetPo 表示评估数据集信息的 {@link EvalDatasetPo}。
     */
    void create(EvalDatasetPo evalDatasetPo);

    /**
     * 根据数据集编号删除评估数据集。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @return 表示成功删除的行数 {@code int}。
     */
    int deleteById(Long datasetId);

    /**
     * 根据数据集编号删除评估数据集。
     *
     * @param datasetIds 表示评估数据集的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示成功删除的行数 {@code int}。
     */
    int delete(@Param("list") List<Long> datasetIds);

    /**
     * 表示获取数据集数据规范。
     *
     * @param datasetId 表示评估数据查询参数的 {@link Long}。
     * @return 表示数据集对应的数据规范的 {@link String}。
     */
    String getSchema(Long datasetId);

    /**
     * 分页查询评估数据集元数据。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集元数据查询结果的 {@link List}{@code <}{@link EvalDatasetVo}{@code >}。
     */
    List<EvalDatasetVo> listEvalDataset(EvalDatasetQueryParam queryParam);

    /**
     * 根据数据集唯一标识查询评估数据集元数据。
     *
     * @param datasetId 表示评估数据集数据集唯一标识的 {@link Long}。
     * @return 表示评估数据集元数据查询结果的 {@link EvalDatasetVo}。
     */
    EvalDatasetVo getEvalDatasetById(Long datasetId);

    /**
     * 统计评估数据集数量。
     *
     * @param queryParam 表示评估数据集查询参数的 {@link EvalDatasetQueryParam}。
     * @return 表示评估数据集统计结果的 {@code int}。
     */
    int countEvalDataset(EvalDatasetQueryParam queryParam);

    /**
     * 修改评估数据集信息。
     *
     * @param evalDatasetPo 表示评估数据集信息的 {@link EvalDatasetPo}。
     */
    void updateEvaldataset(EvalDatasetPo evalDatasetPo);
}