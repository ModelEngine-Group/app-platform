/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;
import java.util.Map;

/**
 * 查询实例所用Filter。
 *
 * @author 陈镕希
 * @since 2023-09-25
 */
public class InstanceQueryFilter {
    private List<String> ids;

    private List<String> typeIds;

    private List<String> sourceIds;

    private List<String> tags;

    private List<String> categories;

    private Map<String, List<String>> infos;

    private List<String> orderBy;

    /**
     * InstanceQueryFilter
     */
    public InstanceQueryFilter() {
    }

    public InstanceQueryFilter(List<String> ids, List<String> typeIds, List<String> sourceIds, List<String> tags,
            List<String> categories, Map<String, List<String>> infos, List<String> orderBy) {
        this.ids = ids;
        this.typeIds = typeIds;
        this.sourceIds = sourceIds;
        this.tags = tags;
        this.categories = categories;
        this.infos = infos;
        this.orderBy = orderBy;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<String> typeIds) {
        this.typeIds = typeIds;
    }

    public List<String> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<String> sourceIds) {
        this.sourceIds = sourceIds;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Map<String, List<String>> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, List<String>> infos) {
        this.infos = infos;
    }

    public List<String> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<String> orderBy) {
        this.orderBy = orderBy;
    }
}
