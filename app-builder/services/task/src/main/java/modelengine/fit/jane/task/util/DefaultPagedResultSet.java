/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 为 {@link PagedResultSet} 提供默认实现。
 *
 * @param <T> 表示分页结果集中包含数据记录的类型。
 * @author 梁济时
 * @since 2023-12-12
 */
class DefaultPagedResultSet<T> implements PagedResultSet<T> {
    private final List<T> results;

    private final PaginationResult pagination;

    DefaultPagedResultSet(List<T> results, PaginationResult pagination) {
        this.results = results;
        this.pagination = pagination;
    }

    @Override
    public List<T> results() {
        return this.results;
    }

    @Override
    public PaginationResult pagination() {
        return this.pagination;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultPagedResultSet<?> that = (DefaultPagedResultSet<?>) obj;
            return Objects.equals(this.pagination(), that.pagination()) && CollectionUtils.equals(this.results(),
                    that.results());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        List<Object> values = new ArrayList<>(this.results().size() + 2);
        values.add(this.getClass());
        values.add(this.pagination());
        values.addAll(this.results());
        return Arrays.hashCode(values.toArray());
    }

    @Override
    public String toString() {
        return StringUtils.format("[pagination={0}, results={1}]", this.pagination(), this.results());
    }
}
