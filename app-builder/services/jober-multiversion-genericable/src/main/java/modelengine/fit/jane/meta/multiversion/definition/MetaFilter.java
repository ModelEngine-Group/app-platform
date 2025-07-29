/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.multiversion.definition;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 查询Meta所用Filter。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public class MetaFilter {
    private List<String> metaIds;

    private List<String> versionIds;

    private List<String> names;

    private List<String> categories;

    private List<String> creators;

    private List<String> orderBys;

    private List<String> versions;

    private Map<String, List<String>> attributes;

    public MetaFilter() {
        this(null, null, null, null, null, null, null, null);
    }

    public MetaFilter(List<String> metaIds, List<String> versionIds, List<String> names, List<String> categories,
            List<String> creators, List<String> orderBys, List<String> versions, Map<String, List<String>> attributes) {
        this.metaIds = nullIf(metaIds, Collections.emptyList());
        this.versionIds = nullIf(versionIds, Collections.emptyList());
        this.names = nullIf(names, Collections.emptyList());
        this.categories = nullIf(categories, Collections.emptyList());
        this.creators = nullIf(creators, Collections.emptyList());
        this.orderBys = nullIf(orderBys, Collections.emptyList());
        this.versions = nullIf(versions, Collections.emptyList());
        this.attributes = nullIf(attributes, Collections.emptyMap());
    }

    public List<String> getMetaIds() {
        return metaIds;
    }

    public void setMetaIds(List<String> metaIds) {
        this.metaIds = nullIf(metaIds, Collections.emptyList());
    }

    public List<String> getVersionIds() {
        return versionIds;
    }

    public void setVersionIds(List<String> versionIds) {
        this.versionIds = nullIf(versionIds, Collections.emptyList());
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = nullIf(names, Collections.emptyList());
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = nullIf(categories, Collections.emptyList());
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = nullIf(creators, Collections.emptyList());
    }

    public List<String> getOrderBys() {
        return orderBys;
    }

    public void setOrderBys(List<String> orderBys) {
        this.orderBys = nullIf(orderBys, Collections.emptyList());
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = nullIf(versions, Collections.emptyList());
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = nullIf(attributes, Collections.emptyMap());
    }
}
