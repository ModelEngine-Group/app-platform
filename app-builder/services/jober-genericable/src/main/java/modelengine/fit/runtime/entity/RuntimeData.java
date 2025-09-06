/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.runtime.entity;

import java.util.List;
import java.util.Map;

/**
 * 流程运行时数据.
 *
 * @author 张越
 * @since 2024-05-23
 */
public class RuntimeData {
    /**
     * 在carver的情况下，此时nodeInfos只有一个.
     * 在和前端对接的情况下，是已跑完的节点的集合.
     */
    private List<NodeInfo> nodeInfos;

    // 每启动一个实例就会创建一个traceId.
    private String traceId;

    // aipp的实例id.
    private String aippInstanceId;

    // 一个flow的定义.
    private String flowDefinitionId;

    // 开始时间.
    private long startTime;

    // 结束时间.
    private long endTime;

    private long executeTime;

    // 流程是否结束.
    private boolean isFinished;

    /**
     * 流程自定义参数.
     */
    private Map<String, Object> extraParams;

    // 是否已发布.
    private boolean isPublished;

    /**
     * 获取节点列表.
     *
     * @return {@link List}{@code <}{@link String}{@code >} 节点列表.
     */
    public List<NodeInfo> getNodeInfos() {
        return this.nodeInfos;
    }

    /**
     * 设置节点列表.
     *
     * @param nodeInfos 节点列表信息.
     */
    public void setNodeInfos(List<NodeInfo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    /**
     * 获取traceId.
     *
     * @return 追溯id.
     */
    public String getTraceId() {
        return this.traceId;
    }

    /**
     * 设置traceId.
     *
     * @param traceId 设置追溯id.
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * 获取aipp实例id.
     *
     * @return {@link String} 实例id.
     */
    public String getAippInstanceId() {
        return this.aippInstanceId;
    }

    /**
     * 设置aippInstanceId.
     *
     * @param aippInstanceId 实例id.
     */
    public void setAippInstanceId(String aippInstanceId) {
        this.aippInstanceId = aippInstanceId;
    }

    /**
     * 获取流程定义id.
     *
     * @return {@link String} 流程定义id.
     */
    public String getFlowDefinitionId() {
        return this.flowDefinitionId;
    }

    /**
     * 设置流程定义id.
     *
     * @param flowDefinitionId 流程定义id.
     */
    public void setFlowDefinitionId(String flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }

    /**
     * 获取开始时间.
     *
     * @return 开始时间.
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * 设置开始时间.
     *
     * @param startTime 开始时间.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间.
     *
     * @return 结束时间.
     */
    public long getEndTime() {
        return this.endTime;
    }

    /**
     * 设置结束时间.
     *
     * @param endTime 结束时间.
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取额外参数.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 参数map.
     */
    public Map<String, Object> getExtraParams() {
        return this.extraParams;
    }

    /**
     * 设置额外参数.
     *
     * @param extraParams 额外的参数.
     */
    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }

    /**
     * 是否已发布.
     *
     * @return true/false.
     */
    public boolean isPublished() {
        return this.isPublished;
    }

    /**
     * 设置发布状态.
     *
     * @param isPublished 发布状态.
     */
    public void setPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    /**
     * 获取执行时间.
     *
     * @return 执行时间.
     */
    public long getExecuteTime() {
        return this.executeTime;
    }

    /**
     * 设置执行时间.
     *
     * @param executeTime 执行时间
     */
    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
