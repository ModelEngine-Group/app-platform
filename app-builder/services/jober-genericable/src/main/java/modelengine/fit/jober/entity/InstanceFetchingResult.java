/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

import java.util.List;

/**
 * 为数据获取提供结果。
 *
 * @author 梁济时
 * @since 2023-09-11
 */
public class InstanceFetchingResult {
    private List<InstanceInfo> instances;

    private Boolean isMore;

    /**
     * InstanceFetchingResult
     */
    public InstanceFetchingResult() {
    }

    public InstanceFetchingResult(List<InstanceInfo> instances, Boolean isMore) {
        this.instances = instances;
        this.isMore = isMore;
    }

    public List<InstanceInfo> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceInfo> instances) {
        this.instances = instances;
    }

    public Boolean getIsMore() {
        return isMore;
    }

    public void setIsMore(Boolean isMore) {
        this.isMore = isMore;
    }
}
