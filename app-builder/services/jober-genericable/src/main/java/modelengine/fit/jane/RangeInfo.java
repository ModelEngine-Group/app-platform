/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 表示范围信息。
 *
 * @author 梁济时
 * @since 2023-11-07
 */
public class RangeInfo {
    private Long offset;

    private Integer limit;

    public RangeInfo() {
        this(null, null);
    }

    public RangeInfo(Long offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            RangeInfo another = (RangeInfo) obj;
            return Objects.equals(this.getOffset(), another.getOffset()) && Objects.equals(this.getLimit(),
                    another.getLimit());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.getOffset(), this.getLimit()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[offset={0}, limit={1}]", this.getOffset(), this.getLimit());
    }
}
