/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.controller.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * KnowledgeRepoInfo
 *
 * @author 何嘉斌
 * @since 2024-09-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeRepoInfo {
    String id;

    String name;

    String desc;
}
