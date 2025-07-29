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
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fitframework.annotation.Property;

import java.util.List;
import java.util.Map;

/**
 * Aipp实例信息
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceDto {
    @Property(description = "Aipp实例ID", name = "aipp_instance_id")
    private String aippInstanceId;

    @Property(description = "用户ID", name = "tenant_id")
    private String tenantId;

    @Property(description = "aipp实例名称", name = "aipp_instance_name")
    private String aippInstanceName;

    /** {@link modelengine.fit.jober.aipp.enums.MetaInstStatusEnum} **/
    @Property(description = "aipp实例当前的状态", example = "running,end ", name = "status")
    private String status;

    @Property(description = "当前节点的表单元数据", name = "form_metadata")
    private String formMetadata;

    @Property(description = "用于填充当前表单的数据", name = "form_args")
    private Map<String, String> formArgs;

    @Property(description = "aipp实例开始执行时间", name = "start_time")
    private String startTime;

    @Property(description = "aipp实例结束运行时间", name = "end_time")
    private String endTime;

    @Property(description = "aipp实例运行记录", name = "instance_log")
    private List<AippInstLog> aippInstanceLogs;
}
