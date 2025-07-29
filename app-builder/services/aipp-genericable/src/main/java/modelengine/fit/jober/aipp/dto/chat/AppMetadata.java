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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用元数据。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppMetadata {
    @Property(description = "应用名称")
    private String name;
    @Property(description = "应用类型")
    private String type;
    @Property(description = "创建者")
    private String createBy;
    @Property(description = "更新者")
    private String updateBy;
    @Property(description = "版本")
    private String version;
    @Property(description = "创建时间")
    private LocalDateTime createAt;
    @Property(description = "更新时间")
    private LocalDateTime updateAt;
    @Property(description = "唯一标识符")
    private String id;
    @Property(description = "属性")
    private Map<String, Object> attributes;
    @Property(description = "状态")
    private String state;
    @Property(description = "标签")
    private List<String> tags;
}
