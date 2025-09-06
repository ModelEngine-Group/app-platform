/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.postprocessor;

import lombok.AllArgsConstructor;
import lombok.Data;
import modelengine.fel.core.rerank.RerankOption;

/**
 * 文档后置处理器工厂的配置参数。
 *
 * @author 刘信宏
 * @since 2024-09-29
 */
@Data
@AllArgsConstructor
public class FactoryOption {
    /**
     * 重排使能标记。
     */
    private boolean enableRerank;

    /**
     * 重排模型参数。
     */
    private RerankOption rerankOption;
}
