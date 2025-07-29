/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * Aipp创建实例响应
 *
 * @author 刘信宏
 * @since 2024-02-22
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceCreateDto {
    @Property(description = "Aipp实例ID")
    @JSONField(name = "instance_id")
    private String instanceId;

    @Property(description = "Aipp版本")
    @JSONField(name = "version")
    private String version;

    @Property(description = "Aipp版本ID")
    @JSONField(name = "version_id")
    private String versionId;
}
