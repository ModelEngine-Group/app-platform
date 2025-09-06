/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 检索配置实体。
 *
 * @author 刘信宏
 * @since 2024-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RetrieverOption extends RetrieverServiceOption {
    /**
     * 知识库标识列表。
     */
    private List<String> repoIds;

    /**
     * 用户标识。
     */
    private String apiKey;
}
