/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.util;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 为 {@link Pagination} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-12-12
 */
class DefaultPagination implements Pagination {
    private final long offset;

    private final int limit;

    DefaultPagination(long offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public long offset() {
        return this.offset;
    }

    @Override
    public int limit() {
        return this.limit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultPagination that = (DefaultPagination) obj;
            return this.offset() == that.offset() && this.limit() == that.limit();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new long[] {this.getClass().hashCode(), this.offset(), this.limit()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}]", this.offset(), this.limit());
    }
}
