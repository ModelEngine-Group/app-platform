/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用元数据Dto
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppMetadataDto {
    private String name;

    private String type;

    private String createBy;

    private String updateBy;

    private String version;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private String id;

    private Map<String, Object> attributes;

    private String state;

    private List<String> tags;

    private String appType;

    private String appCategory;

    private String appBuiltType;
}
