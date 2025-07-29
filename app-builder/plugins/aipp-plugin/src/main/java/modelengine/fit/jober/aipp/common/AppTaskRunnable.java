/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common;

import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.entity.ChatSession;

/**
 * 应用执行接口。
 *
 * @author 张越
 * @since 2025-01-10
 */
public interface AppTaskRunnable {
    /**
     * 运行任务。
     *
     * @param context 上下文信息。
     */
    void run(RunContext context);

    /**
     * 运行任务。
     *
     * @param context 上下文信息。
     * @param chatSession 会话对象。
     */
    void run(RunContext context, ChatSession<Object> chatSession);
}
