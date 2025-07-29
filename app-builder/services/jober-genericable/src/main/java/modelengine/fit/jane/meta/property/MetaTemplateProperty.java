/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.meta.property;

/**
 * Meta模板属性
 *
 * @author 陈镕希
 * @since 2024-02-04
 */
public class MetaTemplateProperty {
    private String id;

    private String name;

    private String dataType;

    private int sequence;

    private String column;

    private String taskTemplateId;

    /**
     * MetaTemplateProperty
     */
    public MetaTemplateProperty() {
    }

    public MetaTemplateProperty(String id, String name, String dataType, int sequence, String column,
            String taskTemplateId) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.sequence = sequence;
        this.column = column;
        this.taskTemplateId = taskTemplateId;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getTaskTemplateId() {
        return taskTemplateId;
    }

    public void setTaskTemplateId(String taskTemplateId) {
        this.taskTemplateId = taskTemplateId;
    }
}
