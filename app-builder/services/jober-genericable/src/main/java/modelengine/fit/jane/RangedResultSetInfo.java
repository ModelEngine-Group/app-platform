/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 为分页查询提供结果集。
 *
 * @author 梁济时
 * @since 2023-11-07
 */
public class RangedResultSetInfo<T> {
    private List<T> results;

    private RangeResultInfo range;

    public RangedResultSetInfo() {
        this(null, null);
    }

    public RangedResultSetInfo(List<T> results, RangeResultInfo range) {
        this.results = results;
        this.range = range;
    }

    public List<T> getResults() {
        return this.results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public RangeResultInfo getRange() {
        return this.range;
    }

    public void setRange(RangeResultInfo range) {
        this.range = range;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            RangedResultSetInfo<T> another = cast(obj);
            return CollectionUtils.equals(this.getResults(), another.getResults()) && Objects.equals(this.getRange(),
                    another.getRange());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        List<T> actualResults = nullIf(this.getResults(), Collections.emptyList());
        List<Object> objects = new ArrayList<>(actualResults.size() + 2);
        objects.add(this.getClass());
        objects.addAll(actualResults);
        objects.add(this.getRange());
        return Arrays.hashCode(objects.toArray());
    }

    @Override
    public String toString() {
        return StringUtils.format("[results={0}, range={1}]", CollectionUtils.toString(this.getResults()),
                this.getRange());
    }
}
