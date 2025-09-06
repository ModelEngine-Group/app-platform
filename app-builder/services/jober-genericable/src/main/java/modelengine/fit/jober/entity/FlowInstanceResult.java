/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

/**
 * 流程实例返回结构体
 *
 * @author 杨祥宇
 * @since 2023/12/11
 */
public class FlowInstanceResult {
    /**
     * 流程实例id标识
     */
    private String id;

    public FlowInstanceResult() {
        this(null);
    }

    /**
     * 构造结果
     *
     * @param flowInstanceId 流程实例id
     */
    public FlowInstanceResult(String flowInstanceId) {
        this.id = flowInstanceId;
    }

    /**
     * 获取流程实例id
     *
     * @return 流程实例id
     */
    public String getId() {
        return id;
    }
}
