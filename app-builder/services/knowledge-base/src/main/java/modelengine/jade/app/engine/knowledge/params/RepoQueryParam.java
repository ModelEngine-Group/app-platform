/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * repo查询参数
 *
 * @since 2024/5/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepoQueryParam {
    /** 页数 */
    private Integer offset;

    /** 单页大小 */
    private Integer size;

    /** 知识库名称模糊查询字段 */
    private String name;
}
