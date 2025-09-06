/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用操作工具
 *
 * @author 陈潇文
 * @since 2024-08-26
 */
public class AppUtils {
    private static final ThreadLocal<List<Object>> APP_CHAT_INFO = new ThreadLocal<>();

    /**
     * 将值为*xxx*的excludeNames替换为{xxx}。
     *
     * @param excludeNames 表示查询接口排除的应用名称的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示替换处理后的询接口排除的应用名称的 {@link List}{@code <}{@link String}{@code >}。
     */
    public static List<String> replaceAsterisks(List<String> excludeNames) {
        return excludeNames
                .stream()
                .map(s -> s.replaceAll("\\*(\\w+)\\*", "{$1}"))
                .collect(Collectors.toList());
    }

    /**
     * 设置线程局部变量，保存 appId 和 isDebug。
     *
     * @param appId 表示应用 id 的 {@link String}。
     * @param isDebug 表示应用是否为调试模式的 {@code boolean}
     */
    public static void setAppChatInfo(String appId, boolean isDebug) {
        APP_CHAT_INFO.set(Arrays.asList(appId, isDebug));
    }

    /**
     * 获取并清除线程局部变量。
     *
     * @return 表示 [appId, isDebug] 的 {@link List}{@code <}{@link Object}{@code >}
     */
    public static List<Object> getAndRemoveAppChatInfo() {
        List<Object> result = APP_CHAT_INFO.get();
        APP_CHAT_INFO.remove();
        return result;
    }
}
