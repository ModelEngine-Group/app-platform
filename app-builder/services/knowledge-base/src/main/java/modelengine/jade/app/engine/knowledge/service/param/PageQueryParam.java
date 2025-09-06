/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.service.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 知识表查询参数结构体
 *
 * @since 2024-05-20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryParam {
    /** 分页查询的偏移量 */
    private Integer pageNum;

    /** 分页查询数量限制 */
    private Integer pageSize;
}
