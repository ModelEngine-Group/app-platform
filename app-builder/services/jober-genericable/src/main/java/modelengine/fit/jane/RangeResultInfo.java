/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示分页查询的范围结果信息。
 *
 * @author 梁济时
 * @since 2023-11-07
 */
public class RangeResultInfo extends RangeInfo {
    private Long total;

    public RangeResultInfo() {
        this(null, null, null);
    }

    /**
     * 表示分页查询的范围结果信息。
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @param total 总计数量
     */
    public RangeResultInfo(Long offset, Integer limit, Long total) {
        super(offset, limit);
        this.total = total;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && Objects.equals(this.getTotal(), ObjectUtils.<RangeResultInfo>cast(obj).getTotal());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.getOffset(), this.getLimit(), this.getTotal()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}, total={2}]", this.getOffset(), this.getLimit(),
                this.getTotal());
    }
}
