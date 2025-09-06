/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.events;

import modelengine.fit.jober.aipp.entity.AippFlowData;
import modelengine.fitframework.event.Event;

/**
 * 插入历史对话结束后事件。
 *
 * @author 高嘉乐
 * @since 2025-01-07
 */
public class InsertConversationEnd implements Event {
    private final AippFlowData aippFlowData;
    private final Object publisher;

    public InsertConversationEnd(Object publisher, AippFlowData aippFlowData) {
        this.publisher = publisher;
        this.aippFlowData = aippFlowData;
    }

    /**
     * 获取指标数据。
     *
     * @return 表示指标上报数据的 {@link AippFlowData}。
     */
    public AippFlowData getMetrics() {
        return this.aippFlowData;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }
}
