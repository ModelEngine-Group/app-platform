/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单元数据信息
 *
 * @author 夏斐
 * @since 2023/12/13
 */
@Data
public class FormMetaInfo {
    /**
     * 表单id
     */
    private String formId;

    /**
     * 表单版本
     */
    private String version;

    /**
     * 表单的元信息列表
     */
    private List<FormMetaItem> formMetaItems;

    public FormMetaInfo(String formId, String version) {
        this.formId = formId;
        this.version = version;
        this.formMetaItems = new ArrayList<>();
    }
}
