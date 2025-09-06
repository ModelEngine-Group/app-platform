/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.bo.RequestSourceBo;

import lombok.Builder;
import lombok.Data;

/**
 * 请求来源分布视图。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@Data
@Builder
public class RequestSourceVo {
    private String departmentName;
    private int requestCount;

    /**
     * 从请求来源信息业务对象的转换方法。
     *
     * @param requestSourceBo 表示请求来源信息业务对象的 {@link RequestSourceBo}。
     * @return 表示请求来源信息视图的 {@link RequestSourceVo}。
     */
    public static RequestSourceVo from(RequestSourceBo requestSourceBo) {
        notNull(requestSourceBo, "RequestSourceBo cannot be null.");
        return RequestSourceVo.builder()
                .departmentName(requestSourceBo.getDepartmentName())
                .requestCount(Math.toIntExact(requestSourceBo.getCount()))
                .build();
    }
}