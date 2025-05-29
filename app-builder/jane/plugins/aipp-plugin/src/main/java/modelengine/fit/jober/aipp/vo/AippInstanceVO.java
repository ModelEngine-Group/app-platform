/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * aipp instance展示类.
 *
 * @author 张越
 * @since 2024-05-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceVO {
    private List<String> ancestors;
    private String aippInstanceId;
    private String tenantId;
    private String aippInstanceName;
    private String status;
    private String formMetadata;
    private Map<String, String> formArgs;
    private String startTime;
    private String endTime;
    private List<AippLogVO> aippInstanceLogs;
}