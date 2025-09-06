/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * appengine统计数据
 *
 * @author 陈潇文
 * @since 2024-12-26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {
    @Property(description = "已发布的应用数量")
    private long publishedAppNum;

    @Property(description = "未发布的应用数量")
    private long unpublishedAppNum;

    @Property(description = "表单数量")
    private long formNum;

    @Property(description = "插件数量")
    private int pluginNum;
}
