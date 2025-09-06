/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.genericable.adapter;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData;

import java.util.List;

/**
 * aipp 实例历史记录适配器类。
 *
 * @author 陈潇文
 * @since 2025-07-08
 */
public interface AippLogServiceAdapter {
    /**
     * 查询指定 chatId 的最近 5 次实例记录。
     *
     * @param chatId 表示会话 id 的 {@link String}。
     * @param context 表示登录信息的 {@link OperationContext}。
     * @param appId 表示应用 id 的 {@link String}。
     * @return 表示 log 数据的 {@link List}{@code <}{@link AippInstLogData}{@code >}。
     */
    List<AippInstLogData> queryChatRecentChatLog(String chatId, String appId, OperationContext context);
}
