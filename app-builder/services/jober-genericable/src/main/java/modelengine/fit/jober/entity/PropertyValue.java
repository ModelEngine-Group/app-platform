/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

/**
 * PropertyValue实体类
 *
 * @author 梁济时
 * @since 2023-08-28
 */
public class PropertyValue {
    private String property;

    private String dataType;

    private String value;

    /**
     * PropertyValue
     */
    public PropertyValue() {
    }

    public PropertyValue(String property, String dataType, String value) {
        this.property = property;
        this.dataType = dataType;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
