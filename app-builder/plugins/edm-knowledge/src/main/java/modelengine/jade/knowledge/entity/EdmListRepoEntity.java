/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import modelengine.jade.knowledge.dto.EdmRepoRecord;

import lombok.Data;

import java.util.List;

/**
 * 知识库列表数据.
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Data
public class EdmListRepoEntity {
    /**
     * 列表数据。
     */
    private List<EdmRepoRecord> records;

    /**
     * 当前数据。
     */
    private int current;

    /**
     * 大小。
     */
    private int size;

    /**
     * 总量。
     */
    private int total;

    /**
     * 页数。
     */
    private int pages;
}