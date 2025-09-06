/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

/**
 * 表示用户。
 *
 * @author 梁济时
 * @since 2023-11-17
 */
public interface User {
    /**
     * 获取用户的账号。
     *
     * @return 表示用户账号的 {@link String}。
     */
    String account();

    /**
     * 获取用户的名称。
     *
     * @return 表示用户名称的 {@link String}。
     */
    String name();

    /**
     * 获取用户的全限定名。（Fully Qualified Name）
     *
     * @return 表示用户全限定名的 {@link String}。
     */
    String fqn();

    /**
     * 为用户提供构建器。
     *
     * @author 梁济时
     * @since 2023-11-17
     */
    interface Builder {
        /**
         * 设置用户的账号。
         *
         * @param id 表示用户账号的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder account(String id);

        /**
         * 设置用户的名称。
         *
         * @param name 表示用户名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置用户的全限定名。（Fully Qualified Name）
         *
         * @param fqn 表示用户全限定名的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder fqn(String fqn);

        /**
         * 构建用户新实例。
         *
         * @return 表示新构建的用户实例的 {@link User}。
         */
        User build();
    }

    /**
     * 返回一个构建器，用以构建用户的新实例。
     *
     * @return 表示新构建的用户实例的 {@link User}。
     */
    static Builder custom() {
        return new DefaultUser.Builder();
    }
}
