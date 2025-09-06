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

import java.util.List;

/**
 * 分页查询流程定义列表response
 *
 * @author 杨祥宇
 * @since 2023/10/11
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPageResponse {
    private int code;

    private List<FlowInfo> data;

    private int size;

    private int count;

    private int cursor;

    private String msg;

    /**
     * FlowInfo
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowInfo {
        private int id;

        private String documentId;

        private String text;

        private String spaceName;

        private String type;

        private String parentId;

        private String extension;

        private String createUser;

        private String updateUser;

        private String createTime;

        private String updateTime;

        private String roleId;

        private String version;

        private String versionStatus;

        private String releaseTime;

        private int runningInstance;

        private int allInstance;

        private int errorInstance;
    }
}
