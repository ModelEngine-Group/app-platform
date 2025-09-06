/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 应用 configUI 表单配置
 *
 * @author 方誉州
 * @since 2024-11-12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportForm {
    @Property(description = "configUI 表单 id")
    private String id;

    @Property(description = "configUI 表单名称")
    private String name;

    @Property(description = "configUI 表单样式")
    private Map<String, Object> appearance;

    @Property(description = "configUI 表单类型")
    private String type;

    @Property(description = "表单的版本")
    private String version;

    @Property(description = "表单的套件 id")
    private String formSuiteId;
}
