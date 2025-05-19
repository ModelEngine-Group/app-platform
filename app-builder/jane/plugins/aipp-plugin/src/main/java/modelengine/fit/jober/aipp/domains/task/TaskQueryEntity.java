/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotBlank;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.enums.AippSortKeyEnum;

import lombok.Getter;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 应用任务的查询数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public class TaskQueryEntity extends TaskEntity<TaskQueryEntity> {
    private final List<String> appSuiteIds;
    private final List<String> taskIds;
    private final List<String> names;
    private final List<String> categories;
    private final List<String> creators;
    private final List<String> orderBys;
    private final List<String> versions;
    private final Map<String, List<String>> queryAttributes;

    @Getter
    private final long offset;

    @Getter
    private final int limit;

    @Getter
    private boolean isLatest;

    TaskQueryEntity(long offset, int limit) {
        this.offset = offset;
        this.limit = limit;
        this.appSuiteIds = new ArrayList<>();
        this.taskIds = new ArrayList<>();
        this.names = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.creators = new ArrayList<>();
        this.orderBys = new ArrayList<>();
        this.versions = new ArrayList<>();
        this.queryAttributes = new HashMap<>();
        this.isLatest = false;
    }

    /**
     * 添加应用唯一标识.
     *
     * @param appSuiteId 应用唯一标识.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addAppSuiteId(String appSuiteId) {
        doIfNotBlank(appSuiteId, this.appSuiteIds::add);
        return this.self();
    }

    /**
     * 批量添加应用唯一标识.
     *
     * @param appSuiteIds 应用唯一标识集合.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addAppSuiteIds(List<String> appSuiteIds) {
        if (CollectionUtils.isNotEmpty(appSuiteIds)) {
            this.appSuiteIds.addAll(appSuiteIds);
        }
        return this.self();
    }

    /**
     * 添加名称.
     *
     * @param name 名称.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addName(String name) {
        doIfNotBlank(name, this.names::add);
        return this.self();
    }

    /**
     * 添加分类.
     *
     * @param category 分类.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addCategory(String category) {
        doIfNotBlank(category, this.categories::add);
        return this.self();
    }

    /**
     * 添加创建者.
     *
     * @param creator 创建者.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addCreator(String creator) {
        doIfNotBlank(creator, this.creators::add);
        return this.self();
    }

    /**
     * 添加版本号.
     *
     * @param version 版本号.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addVersion(String version) {
        doIfNotBlank(version, this.versions::add);
        return this.self();
    }

    /**
     * 设置属性.
     *
     * @param key 属性的键.
     * @param value 属性的值.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity putQueryAttribute(String key, String value) {
        doIfNotBlank(key, k -> {
            List<String> values = this.queryAttributes.computeIfAbsent(key, (kk) -> new ArrayList<>());
            doIfNotBlank(value, values::add);
        });
        return this.self();
    }

    /**
     * 是否查询最新数据.
     *
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity latest() {
        this.isLatest = true;
        return this.self();
    }

    /**
     * 转换为 {@link MetaFilter} 对象.
     *
     * @return {@link MetaFilter} 对象.
     */
    public MetaFilter toMetaFilter() {
        return new MetaFilter(this.appSuiteIds, this.taskIds, this.names, this.categories, this.creators,
                this.orderBys, this.versions, this.queryAttributes);
    }

    /**
     * 添加默认排序规则.
     *
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addOrderBy() {
        return this.addOrderBy(null, null);
    }

    /**
     * 添加排序规则.
     *
     * @param sort 排序字段.
     * @param order 顺序.
     * @return {@link TaskQueryEntity} 对象.
     */
    public TaskQueryEntity addOrderBy(String sort, String order) {
        String orderBy = String.format(Locale.ROOT, "%s(%s)",
                DirectionEnum.getDirection(nullIf(order, DirectionEnum.DESCEND.name())).getValue(),
                AippSortKeyEnum.getSortKey(nullIf(sort, AippSortKeyEnum.UPDATE_AT.name())).getKey());
        this.orderBys.add(orderBy);
        return this.self();
    }

    @Override
    public TaskQueryEntity self() {
        return this;
    }
}
