/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.definition;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;

import java.util.List;
import java.util.Map;

/**
 * 表示meta声明信息结构体。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public class MetaDeclarationInfo {
    private Undefinable<String> name;

    private Undefinable<String> category;

    private Undefinable<Map<String, Object>> attributes;

    private Undefinable<String> metaTemplateId;

    private Undefinable<List<MetaPropertyDeclarationInfo>> properties;

    public MetaDeclarationInfo() {
        this(null, null, null, null, null);
    }

    public MetaDeclarationInfo(Undefinable<String> name, Undefinable<String> category,
            Undefinable<Map<String, Object>> attributes, Undefinable<String> metaTemplateId,
            Undefinable<List<MetaPropertyDeclarationInfo>> properties) {
        this.name = nullIf(name, Undefinable.undefined());
        this.category = nullIf(category, Undefinable.undefined());
        this.attributes = nullIf(attributes, Undefinable.undefined());
        this.metaTemplateId = nullIf(metaTemplateId, Undefinable.undefined());
        this.properties = nullIf(properties, Undefinable.undefined());
    }

    public Undefinable<String> getName() {
        return name;
    }

    public void setName(Undefinable<String> name) {
        this.name = nullIf(name, Undefinable.undefined());
    }

    public Undefinable<String> getCategory() {
        return category;
    }

    public void setCategory(Undefinable<String> category) {
        this.category = nullIf(category, Undefinable.undefined());
    }

    public Undefinable<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Undefinable<Map<String, Object>> attributes) {
        this.attributes = nullIf(attributes, Undefinable.undefined());
    }

    public Undefinable<String> getMetaTemplateId() {
        return metaTemplateId;
    }

    public void setMetaTemplateId(Undefinable<String> metaTemplateId) {
        this.metaTemplateId = nullIf(metaTemplateId, Undefinable.undefined());
    }

    public Undefinable<List<MetaPropertyDeclarationInfo>> getProperties() {
        return properties;
    }

    public void setProperties(Undefinable<List<MetaPropertyDeclarationInfo>> properties) {
        this.properties = nullIf(properties, Undefinable.undefined());
    }
}
