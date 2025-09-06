/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.aipplog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * aipp实例历史记录查询条件
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippLogQueryCondition {
    @Property(name = "aipp_id")
    private String aippId;

    @Property(name = "instance_id")
    private String instanceId;

    @Property(name = "after_at")
    private LocalDateTime afterAt;
}
