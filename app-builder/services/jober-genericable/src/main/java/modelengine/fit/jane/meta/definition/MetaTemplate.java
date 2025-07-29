/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.definition;

import modelengine.fit.jane.meta.property.MetaTemplateProperty;

import java.util.List;

/**
 * 表示metaTemplate结构体。
 *
 * @author 陈镕希
 * @since 2024-02-04
 */
public class MetaTemplate {
    private String id;

    private String name;

    private String description;

    private List<MetaTemplateProperty> properties;

    /**
     * MetaTemplate
     */
    public MetaTemplate() {
    }

    public MetaTemplate(String id, String name, String description, List<MetaTemplateProperty> properties) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MetaTemplateProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<MetaTemplateProperty> properties) {
        this.properties = properties;
    }
}
