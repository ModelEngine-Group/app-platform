/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 生成图像DTO
 *
 * @author 杨祥宇
 * @since 2024/11/28
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateImageDto {
    @Property(description = "图片名称")
    private String name;

    @Property(description = "图片详细描述")
    private String description;

    @Property(description = "图片宽度")
    private int width;

    @Property(description = "图片高度")
    private int height;

    /**
     * 获取图片尺寸
     *
     * @return 图片尺寸
     */
    public String getSize() {
        return width + "x" + height;
    }
}
