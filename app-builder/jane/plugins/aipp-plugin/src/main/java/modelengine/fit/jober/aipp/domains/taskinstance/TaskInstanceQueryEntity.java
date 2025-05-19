/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import modelengine.fit.jober.aipp.util.UsefulUtils;

import lombok.Getter;

/**
 * 应用实例的查询数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
@Getter
public class TaskInstanceQueryEntity extends TaskInstanceEntity<TaskInstanceQueryEntity> {
    private String order;
    private String sort;

    TaskInstanceQueryEntity() {
        super();
    }

    /**
     * 设置order.
     *
     * @param order 顺序.
     * @return {TaskInstanceQueryEntity} 对象.
     */
    public TaskInstanceQueryEntity setOrder(String order) {
        UsefulUtils.doIfNotBlank(order, o -> this.order = o);
        return this.self();
    }

    /**
     * 设置sort.
     *
     * @param sort 排序字段.
     * @return {TaskInstanceQueryEntity} 对象.
     */
    public TaskInstanceQueryEntity setSort(String sort) {
        UsefulUtils.doIfNotBlank(sort, o -> this.sort = o);
        return this.self();
    }

    /**
     * 返回自身引用.
     *
     * @return <T> 自身的引用类型.
     */
    @Override
    public TaskInstanceQueryEntity self() {
        return this;
    }
}
