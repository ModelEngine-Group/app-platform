/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.mapper;

import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;
import modelengine.jade.app.engine.eval.po.EvalDataPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表示评估数据持久层接口。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Mapper
public interface EvalDataMapper {
    /**
     * 批量插入评估数据。
     *
     * @param evalDataList 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}。
     */
    void insertAll(List<EvalDataPo> evalDataList);

    /**
     * 查询评估数据。
     *
     * @param queryParam 表示评估数据查询参数的 {@link EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@link List}{@code <}{@link EvalDataEntity}{@code >}。
     */
    List<EvalDataEntity> listEvalData(EvalDataQueryParam queryParam);

    /**
     * 统计评估数据数量。
     *
     * @param queryParam 表示评估数据查询参数的 {@link EvalDataQueryParam}。
     * @return 表示评估数据查询结果的 {@code int}。
     */
    int countEvalData(EvalDataQueryParam queryParam);

    /**
     * 批量软删除评估数据，更新数据到期时间。
     *
     * @param dataIds 表示评估数据列表的 {@link List}{@code <}{@link Long}{@code >}。
     * @param expiredVersion 表示数据到期版本 {@link Long}。
     * @param updatedAt 表示数据更新时间的 {@link LocalDateTime}。
     * @param updatedBy 表示数据更新者的 {@link String}。
     * @return 表示成功修改的行数 {@code int}。
     */
    int updateExpiredVersion(@Param("list") List<Long> dataIds, @Param("version") Long expiredVersion,
            @Param("updatedAt") LocalDateTime updatedAt, @Param("updatedBy") String updatedBy);

    /**
     * 批量硬删除指定数据集的所有评估数据。
     *
     * @param datasetIds 表示数据集编号的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示成功删除的行数 {@code int}。
     */
    int deleteAll(List<Long> datasetIds);

    /**
     * 查询数据集的最新版本。
     *
     * @param datasetId 表示评估数据查询参数的 {@link Long}。
     * @return 表示评估数据版本查询结果的 {@link EvalVersionEntity}。
     */
    EvalVersionEntity getLatestVersion(Long datasetId);
}