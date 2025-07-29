/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.postprocessor;

import modelengine.fel.core.document.DocumentPostProcessor;

import java.util.List;

/**
 * 文档后置处理器工厂。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
public interface PostProcessorFactory {
    /**
     * 创建文档后置处理器。
     *
     * @param factoryOption 表示工厂配置参数的 {@link FactoryOption}。
     * @return 表示文档后置处理器列表的 {@link List}{@code <}{@link DocumentPostProcessor}{@code >}。
     */
    List<DocumentPostProcessor> create(FactoryOption factoryOption);
}
