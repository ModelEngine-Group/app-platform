/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity.instance;

import java.util.List;
import java.util.Map;

/**
 * 表示任务实例。
 *
 * @author 陈镕希
 * @since 2023-09-08
 */
public class Instance {
    private String id;

    private String typeId;

    private String sourceId;

    private Map<String, String> info;

    private List<String> tags;

    private List<String> categories;

    /**
     * Instance
     */
    public Instance() {
    }

    public Instance(String id, String typeId, String sourceId, Map<String, String> info, List<String> tags,
            List<String> categories) {
        this.id = id;
        this.typeId = typeId;
        this.sourceId = sourceId;
        this.info = info;
        this.tags = tags;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
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
}
