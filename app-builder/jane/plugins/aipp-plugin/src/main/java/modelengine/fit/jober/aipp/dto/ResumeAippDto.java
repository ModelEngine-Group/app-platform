/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 恢复实例运行的启动参数类
 *
 * @author 陈潇文
 * @since 2024-10-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAippDto {
    @PathVariable(name = "tenant_id")
    @Property(description = "租户id")
    private String tenantId;

    @PathVariable(name = "log_id")
    @Property(description = "日志id")
    private Long logId;

    @RequestParam(name = "is_debug")
    @Property(description = "是否为调试状态")
    private boolean isDebug;

    @PathVariable(name = "instance_id")
    @Property(description = "实例id")
    private String instanceId;
}
