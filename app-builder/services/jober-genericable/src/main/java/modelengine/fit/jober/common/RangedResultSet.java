/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common;

import modelengine.fitframework.model.Range;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 批量返回结构体。
 *
 * @author 陈镕希
 * @since 2023-08-29
 */
public class RangedResultSet<T> {
    private List<T> results;

    private RangeResult range;

    /**
     * RangedResultSet
     */
    public RangedResultSet() {
    }

    public RangedResultSet(List<T> results, RangeResult range) {
        this.results = results;
        this.range = range;
    }

    /**
     * create
     *
     * @param results results
     * @param offset offset
     * @param limit limit
     * @param total total
     * @return RangedResultSet
     */
    public static <T> RangedResultSet<T> create(List<T> results, long offset, int limit, long total) {
        return create(results, RangeResult.create(offset, limit, total));
    }

    /**
     * create
     *
     * @param results results
     * @param range range
     * @param total total
     * @return RangedResultSet
     */
    public static <T> RangedResultSet<T> create(List<T> results, Range range, long total) {
        return create(results, RangeResult.create(range, total));
    }

    /**
     * create
     *
     * @param results results
     * @param range range
     * @return RangedResultSet
     */
    public static <T> RangedResultSet<T> create(List<T> results, RangeResult range) {
        return new RangedResultSet<>(results, range);
    }

    /**
     * 集合是否为空.
     *
     * @return 返回集合是否是空的 {@code boolean}。
     */
    public boolean isEmpty() {
        return this.range.getTotal() == 0;
    }

    /**
     * 获取第一个元素.
     *
     * @return {@link Optional}{@code <}{@code T}{@code >} Optional对象.
     */
    public Optional<T> getFirst() {
        return this.isEmpty() ? Optional.empty() : Optional.of(this.results.get(0));
    }

    public List<T> getResults() {
        return this.results;
    }

    public RangeResult getRange() {
        return this.range;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public void setRange(RangeResult range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return StringUtils.format("[range={0}, results={1}]", this.getRange(), this.getResults());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RangedResultSet<?> that = (RangedResultSet<?>) o;
        return Objects.equals(results, that.results) && Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results, range);
    }
}
