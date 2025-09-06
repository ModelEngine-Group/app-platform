/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;
import java.util.Map;

/**
 * DataService使用的Filter结构体
 *
 * @author 陈镕希
 * @since 2023-06-12
 */
public class Filter {
    /**
     * 同步数据的起始时间
     */
    private Long startTime;

    /**
     * 同步数据的结束时间
     */
    private Long endTime;

    /**
     * 同步数据的元数据
     */
    private String metaData;

    /**
     * 同步数据的类型
     */
    private String category;

    /**
     * 同步数据的状态 INIT、COMPLETED等
     */
    private List<String> status;

    /**
     * 所需返回数据字段
     */
    private List<String> returnField;

    /**
     * 附加的filter字段
     */
    private Map<String, String> filterMap;

    /**
     * Filter
     */
    public Filter() {
    }

    public Filter(Long startTime, Long endTime, String metaData, String category, List<String> status,
            List<String> returnField, Map<String, String> filterMap) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.metaData = metaData;
        this.category = category;
        this.status = status;
        this.returnField = returnField;
        this.filterMap = filterMap;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getReturnField() {
        return returnField;
    }

    public void setReturnField(List<String> returnField) {
        this.returnField = returnField;
    }

    public Map<String, String> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {
        this.filterMap = filterMap;
    }
}
