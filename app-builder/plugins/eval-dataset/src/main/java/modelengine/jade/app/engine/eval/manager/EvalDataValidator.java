/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.manager;

import java.util.List;

/**
 * 评估数据校验器。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public interface EvalDataValidator {
    /**
     * 校验评估内容是否合法。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @param contents 表示评估内容集合的 {@link List}{@code <}{@link String}{@code >}。
     * @throws modelengine.jade.app.engine.eval.exception.AppEvalDatasetException 当校验失败时。
     */
    void verify(Long datasetId, List<String> contents);

    /**
     * 校验单个评估内容是否合法。
     *
     * @param datasetId 表示评估数据集编号的 {@link Long}。
     * @param content 表示评估内容的 {@link String}。
     * @throws modelengine.jade.app.engine.eval.exception.AppEvalDatasetException 当校验失败时。
     */
    void verify(Long datasetId, String content);
}