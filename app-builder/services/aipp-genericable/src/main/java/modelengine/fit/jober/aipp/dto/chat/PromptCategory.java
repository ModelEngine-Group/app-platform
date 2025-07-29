/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * 灵感大全的提示类别信息。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptCategory {
    @Property(description = "灵感类别标题")
    private String title;
    @Property(description = "灵感类别的唯一标识符")
    private String id;
    @Property(description = "父类别")
    private String parent;
    @Property(description = "是否可被选择")
    private Boolean disable;
    @Property(description = "子类别")
    private List<PromptCategory> children;
}
