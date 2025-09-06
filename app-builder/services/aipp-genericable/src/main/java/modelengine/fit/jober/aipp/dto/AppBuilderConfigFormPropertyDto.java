/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import static modelengine.fitframework.inspection.Validation.notNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表示 config 表单项 的 dto 对象。
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderConfigFormPropertyDto {
    private String id;
    private String name;
    private String dataType;
    private Object defaultValue;
    private String from;
    private String group;
    private String description;
    private List<AppBuilderConfigFormPropertyDto> children;
    private String nodeId;

    /**
     * 给父节点添加子节点。
     *
     * @param child 表示子节点的 {@link AppBuilderConfigFormPropertyDto}。
     */
    public void addChild(AppBuilderConfigFormPropertyDto child) {
        notNull(child, "Child cannot be null.");
        children.add(child);
    }
}
