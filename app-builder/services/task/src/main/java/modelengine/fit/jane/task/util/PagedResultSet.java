/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import java.util.List;

/**
 * 表示分页结果集。
 *
 * @param <T> 表示分页结果集中包含数据记录的类型。
 * @author 梁济时
 * @since 2023-12-12
 */
public interface PagedResultSet<T> {
    /**
     * 获取分页结果集中包含数据记录的列表。
     *
     * @return 表示分页结果集中包含数据记录的列表的 {@link List}。
     */
    List<T> results();

    /**
     * 获取分页结果信息。
     *
     * @return 表示分页结果信息的 {@link PaginationResult}。
     */
    PaginationResult pagination();

    /**
     * 创建分页结果集。
     *
     * @param results 表示分页结果集中包含数据记录的列表的 {@link List}。
     * @param pagination 表示分页结果的 {@link PaginationResult}。
     * @param <T> 表示分页结果集中包含数据记录的类型。
     * @return 表示新创建的分页结果集的 {@link PagedResultSet}。
     */
    static <T> PagedResultSet<T> create(List<T> results, PaginationResult pagination) {
        return new DefaultPagedResultSet<>(results, pagination);
    }
}
