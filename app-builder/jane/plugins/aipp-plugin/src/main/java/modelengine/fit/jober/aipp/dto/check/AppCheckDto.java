/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.check;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 应用配置检查DTO
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppCheckDto {
    private String type;
    private List<NodeInfo> nodeInfos;

    /**
     * 待检查配置中的节点信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NodeInfo {
        private String nodeId;
        private String nodeName;
        private List<Map<String, Object>> configs;
    }
}
