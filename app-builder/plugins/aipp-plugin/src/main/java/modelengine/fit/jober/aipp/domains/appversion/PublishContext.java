/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.enums.AppCategory;

import lombok.Getter;
import lombok.Setter;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 发布上下文.
 *
 * @author 张越
 * @since 2025-01-16
 */
@Getter
public class PublishContext {
    private final AppBuilderAppDto publishData;
    private final OperationContext operationContext;
    private final LocalDateTime operateTime;

    @Setter
    private FlowInfo flowInfo;

    public PublishContext(AppBuilderAppDto publishData, OperationContext operationContext) {
        this.publishData = publishData;
        this.operationContext = operationContext;
        this.operateTime = LocalDateTime.now();
    }

    /**
     * 获取graph数据.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 对象.
     */
    public Map<String, Object> getAppearance() {
        return this.publishData.getFlowGraph().getAppearance();
    }

    /**
     * 是否是app.
     *
     * @return true/false.
     */
    public boolean isApp() {
        return StringUtils.equalsIgnoreCase(this.publishData.getType(), AppCategory.APP.getType());
    }

    /**
     * 是否是waterFlow.
     *
     * @return true/false.
     */
    public boolean isWaterFlow() {
        return StringUtils.equalsIgnoreCase(this.publishData.getType(), AppCategory.WATER_FLOW.getType());
    }
}
