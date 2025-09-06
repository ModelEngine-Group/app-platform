/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fitframework.model.RangedResultSet;

import java.util.List;

/**
 * 为三方系统授权提供数据模型。
 *
 * @author 梁济时
 * @since 2023-11-27
 */
public interface Authorization extends DomainObject {
    /**
     * 获取授权的三方系统。
     *
     * @return 表示三方系统的 {@link String}。
     */
    String system();

    /**
     * 获取进行授权的用户的唯一标识。
     *
     * @return 表示用户唯一标识的 {@link String}。
     */
    String user();

    /**
     * 获取授权的令牌。
     *
     * @return 表示授权的令牌的 {@link String}。
     */
    String token();

    /**
     * 获取令牌有效期的毫秒数。
     *
     * @return 表示令牌有效期的毫秒数的 {@link Long}。
     */
    Long expiration();

    /**
     * 为三方系统授权提供构建器。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    interface Builder extends DomainObject.Builder<Authorization, Builder> {
        /**
         * 设置授权的三方系统。
         *
         * @param system 表示三方系统的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder system(String system);

        /**
         * 设置进行授权的用户的唯一标识。
         *
         * @param user 表示用户唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder user(String user);

        /**
         * 设置授权的令牌。
         *
         * @param token 表示授权的令牌的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder token(String token);

        /**
         * 设置令牌有效期的毫秒数。
         *
         * @param expiration 表示令牌有效期的毫秒数的 {@link Long}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder expiration(Long expiration);
    }

    /**
     * 返回一个构建器，用以构建 {@link Authorization} 的默认实现的新实例。
     *
     * @return 表示用以构建新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultAuthorization.Builder();
    }

    /**
     * 为三方系统授权提供声明。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    interface Declaration {
        /**
         * 获取授权的三方系统。
         *
         * @return 表示三方系统的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> system();

        /**
         * 获取授权的用户的唯一标识。
         *
         * @return 表示用户唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> user();

        /**
         * 获取授权的令牌。
         *
         * @return 表示授权的令牌的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> token();

        /**
         * 获取令牌有效期的毫秒数。
         *
         * @return 表示令牌有效期毫秒数的 {@link UndefinableValue}{@code <}{@link Long}{@code >}。
         */
        UndefinableValue<Long> expiration();

        /**
         * 为三方系统授权的声明提供构建器。
         *
         * @author 梁济时
         * @since 2023-11-27
         */
        interface Builder {
            /**
             * 设置授权的三方系统。
             *
             * @param system 表示三方系统的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder system(String system);

            /**
             * 设置进行授权的用户的唯一标识。
             *
             * @param user 表示用户唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder user(String user);

            /**
             * 设置授权的令牌。
             *
             * @param token 表示授权的令牌的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder token(String token);

            /**
             * 设置令牌有效期的毫秒数。
             *
             * @param expiration 表示令牌有效期的毫秒数的 {@link Long}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder expiration(Long expiration);

            /**
             * 构建三方系统授权声明的新实例。
             *
             * @return 表示新构建的三方系统授权声明的 {@link Declaration}。
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建三方系统授权声明的新实例。
         *
         * @return 表示用以构建三方系统授权声明的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultAuthorization.Declaration.Builder();
        }
    }

    /**
     * 为三方系统授权提供过滤条件。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    interface Filter {
        /**
         * 获取待查询的唯一标识的列表。
         *
         * @return 表示唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> ids();

        /**
         * 获取待查询的系统的列表。
         *
         * @return 表示待查询的系统的列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> systems();

        /**
         * 获取待查询的用户的唯一标识。
         *
         * @return 表示用户唯一标识的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> users();

        /**
         * 为三方系统授权的过滤条件系统构建器。
         *
         * @author 梁济时
         * @since 2023-11-27
         */
        interface Builder {
            /**
             * 设置待查询的唯一标识的列表。
             *
             * @param ids 表示唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder ids(List<String> ids);

            /**
             * 设置待查询的三方系统的列表。
             *
             * @param systems 表示三方系统的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder systems(List<String> systems);

            /**
             * 设置待查询的用户的唯一标识的列表。
             *
             * @param users 表示用户唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder users(List<String> users);

            /**
             * 构建三方系统授权的过滤条件。
             *
             * @return 表示三方系统授权的过滤条件的 {@link Filter}。
             */
            Filter build();
        }

        /**
         * 返回一个构建器，用以构建三方系统授权的过滤条件。
         *
         * @return 表示用以构建三方系统授权的过滤条件的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultAuthorization.Filter.Builder();
        }
    }

    /**
     * 为系统授权提供存储能力。
     *
     * @author 梁济时
     * @since 2023-11-27
     */
    interface Repo {
        /**
         * 创建三方系统授权。
         *
         * @param declaration 表示三方系统授权的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的三方系统授权的 {@link Authorization}。
         */
        Authorization create(Declaration declaration, OperationContext context);

        /**
         * 修改三方系统授权。
         *
         * @param id 表示待修改的三方系统授权的唯一标识的 {@link String}。
         * @param declaration 表示三方系统授权的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void patch(String id, Declaration declaration, OperationContext context);

        /**
         * 删除三方系统授权。
         *
         * @param id 表示待删除的三方系统授权的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(String id, OperationContext context);

        /**
         * 检索三方系统授权。
         *
         * @param id 表示待检索的三方系统授权的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示指定唯一标识对应的三方系统授权的 {@link Authorization}。
         */
        Authorization retrieve(String id, OperationContext context);

        /**
         * 查询三方系统授权。
         *
         * @param filter 表示过滤条件的 {@link Filter}。
         * @param offset 表示待查询的的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示待查询的结果集在中允许包含数据对象的最大数量的 32 为整数。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询三方系统授权的分页结果集的 {@link RangedResultSet}{@code <}{@link Authorization}{@code >}。
         */
        RangedResultSet<Authorization> list(Filter filter, long offset, int limit,
                OperationContext context);

        /**
         * 根据系统，用户检索三方系统授权。
         *
         * @param system 表示待检索的三方系统 {@link String}。
         * @param user 表示创建人 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示指定唯一标识对应的三方系统授权的 {@link Authorization}。
         */
        Authorization retrieveSystemUser(String system, String user, OperationContext context);
    }
}
