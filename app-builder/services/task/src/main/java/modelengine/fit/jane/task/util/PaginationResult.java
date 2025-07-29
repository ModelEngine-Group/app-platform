/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

/**
 * 表示分页结果。
 *
 * @author 梁济时
 * @since 2023-12-12
 */
public interface PaginationResult extends Pagination {
    /**
     * 表示全量结果集包含数据记录的数量。
     *
     * @return 表示全量结果集包含数据记录的数量的 64 位整数。
     */
    long total();

    /**
     * 创建分页结果的新实例。
     *
     * @param pagination 表示分页信息的 {@link Pagination}。
     * @param total 表示全量结果集包含数据记录的数量的 64 位整数。
     * @return 表示新创建的分页结果的新实例的 {@link PaginationResult}。
     */
    static PaginationResult create(Pagination pagination, long total) {
        return new DefaultPaginationResult(pagination, total);
    }

    /**
     * 创建分页结果的新实例。
     *
     * @param offset 表示偏移量的 64 位整数。
     * @param limit 表示分页结果集中数据记录最大数量的 32 位整数。
     * @param total 表示全量结果集包含数据记录的数量的 64 位整数。
     * @return 表示新创建的分页结果的新实例的 {@link PaginationResult}。
     */
    static PaginationResult create(long offset, int limit, long total) {
        return create(new DefaultPagination(offset, limit), total);
    }
}
