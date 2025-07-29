/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据库存储的应用模板的结构。
 *
 * @author 方誉州
 * @since 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppTemplatePo {
    private String id;
    private String name;
    private String builtType;
    private String category;
    private String attributes;
    private String appType;
    private long like;
    private long collection;
    private long usage;
    private String version;
    private String configId;
    private String flowGraphId;
    private String createBy;
    private LocalDateTime createAt;
    private String updateBy;
    private LocalDateTime updateAt;
}
