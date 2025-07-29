/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

/**
 * flow data元数据
 *
 * @author songyongtan
 * @since 2024/10/16
 */
public class FlowDataMeta {
    /**
     * 流程定义的标识
     */
    private String definitionId;

    /**
     * 流程运行实例标识
     */
    private String instanceId;

    /**
     * flow data 的标识
     */
    private String id;

    /**
     * 构造flow data meta
     *
     * @param id flow data 的标识
     * @param instanceId 流程运行实例标识
     * @param definitionId 流程定义的标识
     */
    public FlowDataMeta(String definitionId, String instanceId, String id) {
        this.definitionId = definitionId;
        this.instanceId = instanceId;
        this.id = id;
    }

    /**
     * 获取 flow data 的标识
     *
     * @return flow data 的标识
     */
    public String getId() {
        return id;
    }

    /**
     * 获取流程运行实例标识
     *
     * @return 流程运行实例标识
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * 获取流程定义的标识
     *
     * @return 流程定义的标识
     */
    public String getDefinitionId() {
        return definitionId;
    }
}
