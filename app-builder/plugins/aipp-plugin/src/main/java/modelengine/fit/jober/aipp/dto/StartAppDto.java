/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

/**
 * 启动aipp请求
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Data
public class StartAppDto {
    @Property(description = "实例名称", example = "看图说话")
    private String name;

    @Property(description = "Aipp的流程定义ID")
    private int aippMetaId;

    @Property(description = "表单数据")
    private String formData;
}
