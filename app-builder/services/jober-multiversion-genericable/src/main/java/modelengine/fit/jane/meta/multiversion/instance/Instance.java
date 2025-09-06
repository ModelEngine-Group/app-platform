/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.multiversion.instance;

import java.util.List;
import java.util.Map;

/**
 * 表示meta实例结构体。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public class Instance {
    private String id;

    private Map<String, String> info;

    private List<String> tags;

    /**
     * Instance
     */
    public Instance() {
    }

    public Instance(String id, Map<String, String> info, List<String> tags) {
        this.id = id;
        this.info = info;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
