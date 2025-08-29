/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.domain.division.entity;

import modelengine.jade.authentication.context.UserContext;

/**
 * 表示当前线程持有的用户信息。
 *
 * @author 邬涨财
 * @since 2025-08-12
 */
public class UserInfoHolder {
    private static final ThreadLocal<UserInfo> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 获取本地线程变量的用户信息。
     *
     * @return 表示当前用户信息的 {@link UserContext}。
     */
    public static UserInfo get() {
        return USER_INFO_THREAD_LOCAL.get();
    }

    /**
     * 设置本地线程变量的用户信息。
     *
     * @param userInfo 表示当前用户信息的 {@link UserContext}。
     */
    public static void set(UserInfo userInfo) {
        USER_INFO_THREAD_LOCAL.set(userInfo);
    }

    /**
     * 移除本地线程变量的用户信息。
     *
     */
    public static void remove() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}
