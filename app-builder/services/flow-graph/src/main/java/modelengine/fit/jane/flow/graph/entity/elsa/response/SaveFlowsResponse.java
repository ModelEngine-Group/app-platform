/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.entity.elsa.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 保存流程定义response
 *
 * @author 杨祥宇
 * @since 2023/10/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveFlowsResponse {
    private int code;

    private FlowDataInfo data;

    private String msg;

    /**
     * FlowDataInfo
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowDataInfo {
        private String updateTime;
    }
}
