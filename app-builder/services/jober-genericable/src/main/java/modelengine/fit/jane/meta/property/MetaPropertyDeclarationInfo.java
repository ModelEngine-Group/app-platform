/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.property;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.Undefinable;

import java.util.Map;

/**
 * Meta属性声明信息结构体。
 *
 * @author 陈镕希
 * @since 2023-12-08
 */
public class MetaPropertyDeclarationInfo {
    private Undefinable<String> name;

    private Undefinable<String> dataType;

    private Undefinable<String> description;

    private Undefinable<Boolean> required;

    private Undefinable<Boolean> identifiable;

    private Undefinable<String> scope;

    private Undefinable<Map<String, Object>> attribute;

    public MetaPropertyDeclarationInfo() {
        this(null, null, null, null, null, null, null);
    }

    public MetaPropertyDeclarationInfo(Undefinable<String> name, Undefinable<String> dataType,
            Undefinable<String> description, Undefinable<Boolean> required, Undefinable<Boolean> identifiable,
            Undefinable<String> scope, Undefinable<Map<String, Object>> attribute) {
        this.name = nullIf(name, Undefinable.undefined());
        this.dataType = nullIf(dataType, Undefinable.undefined());
        this.description = nullIf(description, Undefinable.undefined());
        this.required = nullIf(required, Undefinable.undefined());
        this.identifiable = nullIf(identifiable, Undefinable.undefined());
        this.scope = nullIf(scope, Undefinable.undefined());
        this.attribute = nullIf(attribute, Undefinable.undefined());
    }

    public Undefinable<String> getName() {
        return name;
    }

    public void setName(Undefinable<String> name) {
        this.name = nullIf(name, Undefinable.undefined());
    }

    public Undefinable<String> getDataType() {
        return dataType;
    }

    public void setDataType(Undefinable<String> dataType) {
        this.dataType = nullIf(dataType, Undefinable.undefined());
    }

    public Undefinable<String> getDescription() {
        return description;
    }

    public void setDescription(Undefinable<String> description) {
        this.description = nullIf(description, Undefinable.undefined());
    }

    public Undefinable<Boolean> getRequired() {
        return required;
    }

    public void setRequired(Undefinable<Boolean> required) {
        this.required = nullIf(required, Undefinable.undefined());
    }

    public Undefinable<Boolean> getIdentifiable() {
        return identifiable;
    }

    public void setIdentifiable(Undefinable<Boolean> identifiable) {
        this.identifiable = nullIf(identifiable, Undefinable.undefined());
    }

    public Undefinable<String> getScope() {
        return scope;
    }

    public void setScope(Undefinable<String> scope) {
        this.scope = nullIf(scope, Undefinable.undefined());
    }

    public Undefinable<Map<String, Object>> getAttribute() {
        return attribute;
    }

    public void setAttribute(Undefinable<Map<String, Object>> attribute) {
        this.attribute = nullIf(attribute, Undefinable.undefined());
    }
}
