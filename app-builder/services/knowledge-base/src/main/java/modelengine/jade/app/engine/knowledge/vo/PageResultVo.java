/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询
 *
 * @since 2024/5/20
 */
@Getter
@Setter
@AllArgsConstructor
public class PageResultVo<T> {
    /** 总数 */
    private long count;

    /** 查询结果列表 */
    private List<T> result;
}
