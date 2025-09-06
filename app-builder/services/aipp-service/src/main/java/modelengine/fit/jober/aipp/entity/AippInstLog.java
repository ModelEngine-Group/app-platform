/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * aipp实例历史记录
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippInstLog {
    @Property(description = "创建时间")
    LocalDateTime createAt;

    @Property(description = "创建用户账号")
    String createUserAccount;

    @Property(description = "path")
    String path;

    /**
     * 预留字段, 暂时为null
     */
    @Property(description = "预留字段")
    String reserve;

    @Property(description = "log id")
    private Long logId;

    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "aipp version")
    private String version;

    @Property(description = "aipp type(NORMAL/PREVIEW)")
    private String aippType;

    @Property(description = "aipp实例id")
    private String instanceId;

    @Property(description = "aipp实例历史数据")
    private String logData;

    @Property(description = "历史数据类型 {@link AippInstLogType}")
    private String logType;
}
