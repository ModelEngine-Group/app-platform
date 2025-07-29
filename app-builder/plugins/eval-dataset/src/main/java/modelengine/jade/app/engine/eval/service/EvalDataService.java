/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service;

import java.util.List;

/**
 * 表示评估数据服务。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
public interface EvalDataService {
    /**
     * 批量插入评估数据。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @param contents 表示评估内容集合的 {@link List}{@code <}{@link String}{@code >}。
     */

    void insertAll(Long datasetId, List<String> contents);

    /**
     * 批量软删除评估数据。
     *
     * @param dataIds 表示评估数据编号的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void delete(List<Long> dataIds);

    /**
     * 修改评估数据。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @param dataId 表示评估数据编号的 {@link Long}。
     * @param content 表示评估内容的 {@link String}。
     */
    void update(Long datasetId, Long dataId, String content);

    /**
     * 删除指定评估数据集的全部评估数据。
     *
     * @param datasetIds 表示评估数据编号的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void hardDelete(List<Long> datasetIds);
}