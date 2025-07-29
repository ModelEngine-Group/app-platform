/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.image.entity;

import static modelengine.fitframework.inspection.Validation.notNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 图片生成参数定义。
 *
 * @author 何嘉斌
 * @since 2024-12-17
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateImageParam {
    @Property(description = "图片描述模板填充")
    private Map<String, String> args;

    @Property(name = "description", description = "图片描述模板")
    private String descriptionTemplate;

    @Property(description = "图片数量")
    private Integer imageCount;

    /**
     * 获取完整图片描述。
     *
     * @return 表示完整图片描述的 {@link String}。
     */
    public String getDesc() {
        notNull(this.descriptionTemplate, "The image generation description cannot be null.");
        notNull(this.args, "The image generation args cannot be null.");
        return new DefaultStringTemplate(this.descriptionTemplate).render(this.args);
    }
}