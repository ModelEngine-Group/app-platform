/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.retriever;

import modelengine.jade.knowledge.entity.RetrieverOption;

import modelengine.fel.core.document.MeasurableDocument;
import modelengine.fitframework.inspection.Nonnull;

import java.util.List;

/**
 * 检索处理器。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
public interface RetrieverHandler {
    /**
     * 执行检索。
     *
     * @param query 表示问题内容的 {@link List}{@code <}{@link String}{@code >}。
     * @param option 表示检索配置的 {@link RetrieverOption}。
     * @return 表示文档内容的 {@link List}{@code <}{@link MeasurableDocument}{@code >}。
     */
    List<MeasurableDocument> handle(@Nonnull List<String> query, @Nonnull RetrieverOption option);
}
