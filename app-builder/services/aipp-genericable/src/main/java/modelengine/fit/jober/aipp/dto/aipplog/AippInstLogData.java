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

import java.time.LocalDateTime;
import java.util.List;

/**
 * aipp 实例历史记录数据。
 *
 * @author 陈潇文
 * @since 2025-07-16
 */
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AippInstLogData {
    private String aippId;
    private String version;
    private String instanceId;
    private String status;
    private String appName;
    private String appIcon;
    private LocalDateTime createAt;
    private AippInstLogBody question;
    private List<AippInstLogBody> instanceLogBodies;

    /**
     * 转换实例日志为实例日志体。
     */
    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class AippInstLogBody {
        private long logId;
        private String logData;
        private String logType;
        private LocalDateTime createAt;
        private String createUserAccount;
    }
}
