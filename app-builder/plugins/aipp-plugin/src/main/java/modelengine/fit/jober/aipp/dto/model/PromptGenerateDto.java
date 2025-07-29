/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fit.jober.aipp.dto.AippComponentItemDto;
import modelengine.fitframework.annotation.Property;

/**
 * 生成提示词的请求参数.
 *
 * @author 张越
 * @since 2024/11/29
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptGenerateDto extends AippComponentItemDto {
    @Property(description = "用户输入", name = "input")
    private String input;

    @Property(description = "模型名称", name = "model")
    private String model;

    @Property(description = "温度", name = "temperature")
    private Double temperature;

    @Property(description = "模型标签", name = "modelTag")
    private String modelTag;

    @Property(description = "模板类型，用户模板/系统模板", name = "templateType")
    private String templateType;
}
