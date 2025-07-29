/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.model.Range;
import modelengine.fitframework.util.StringUtils;

/**
 * 批量返回相关统计信息。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class RangeResult {
    private final long offset;

    private final int limit;

    private final long total;

    public RangeResult(long offset, int limit, long total) {
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }

    static RangeResult create(Range range, long total) {
        Validation.notNull(range, "The range for result cannot be null.");
        return create(range.getOffset(), range.getLimit(), total);
    }

    static RangeResult create(long offset, int limit, long total) {
        return new RangeResult(offset, limit, total);
    }

    public long getOffset() {
        return this.offset;
    }

    public int getLimit() {
        return this.limit;
    }

    public long getTotal() {
        return this.total;
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}, total={2}]", this.getOffset(), this.getLimit(),
                this.getTotal());
    }
}
