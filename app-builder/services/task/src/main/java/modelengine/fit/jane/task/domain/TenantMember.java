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
 * 表示租户成员。
 *
 * @author 陈镕希
 * @since 2023-10-23
 */
public interface TenantMember extends DomainObject {
    /**
     * 返回一个构建器，用以构建租户成员的新实例。
     *
     * @return 表示用以构建租户成员新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTenantMember.Builder();
    }

    /**
     * 租户id
     *
     * @return 租户id
     */
    String tenantId();

    /**
     * user id
     *
     * @return user id
     */
    String userId();

    /**
     * 为租户成员提供构建器。
     *
     * @author 陈镕希
     * @since 2023-10-23
     */
    interface Builder extends DomainObject.Builder<TenantMember, Builder> {
        /**
         * 设置租户成员所属租户的唯一标识。
         *
         * @param tenantId 表示租户成员所属租户唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tenantId(String tenantId);

        /**
         * 设置租户成员的唯一标识。
         *
         * @param userId 表示租户成员唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder userId(String userId);
    }

    /**
     * 为 {@link TenantMember} 提供声明。
     *
     * @author 陈镕希
     * @since 2023-10-11
     */
    interface Declaration {
        /**
         * 返回一个构建器，用以构建租户的新实例。
         *
         * @return 表示用以构建租户的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTenantMember.Declaration.Builder();
        }

        /**
         * 获取租户成员所属租户的唯一标识。
         *
         * @return 表示租户成员所属租户唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> tenantId();

        /**
         * 获取租户成员的唯一标识。
         *
         * @return 表示租户成员的唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> userId();

        /**
         * 为租户成员的声明提供构建器。
         *
         * @author 陈镕希
         * @since 2023-10-11
         */
        interface Builder {
            /**
             * 设置租户成员所属租户的唯一标识。
             *
             * @param tenantId 表示租户成员所属租户的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder tenantId(String tenantId);

            /**
             * 设置租户成员的唯一标识。
             *
             * @param userId 表示租户成员的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder userId(String userId);

            /**
             * 构建租户成员的声明。
             *
             * @return 表示新构建的租户成员的声明的实例的 {@link Declaration}。
             */
            Declaration build();
        }
    }

    /**
     * 为 {@link TenantMember} 提供存储能力。
     *
     * @author 陈镕希
     * @since 2023-10-10
     */
    interface Repo {
        /**
         * 创建租户成员。
         *
         * @param declaration 表示租户成员声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的租户成员的 {@link TenantMember}。
         */
        TenantMember create(Declaration declaration, OperationContext context);

        /**
         * 删除租户成员。
         *
         * @param tenantId 表示待删除的租户唯一标识的 {@link String}。
         * @param userId 表示待删除的租户成员的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(String tenantId, String userId, OperationContext context);

        /**
         * 查询租户成员。
         *
         * @param filter 表示租户成员过滤器的 {@link Filter}。
         * @param offset 表示查询到的租户成员的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示查询到的租户成员的结果集中的最大数量的 32 位整数。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TenantMember}{@code >}。
         */
        RangedResultSet<TenantMember> list(Filter filter, long offset, int limit,
            OperationContext context);
    }

    /**
     * 为租户成员提供过滤配置。
     *
     * @author 梁济时
     * @since 2023-10-17
     */
    interface Filter {
        /**
         * 返回一个构建器，用以构建租户成员的过滤配置。
         *
         * @return 表示用以构建租户配置的新实例的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTenantMember.Filter.Builder();
        }

        /**
         * 获取待过滤的租户成员的唯一标识。
         *
         * @return 表示唯一标识的列表的 {@link UndefinableValue}{@code <}{@link List}{@code <}{@link String}{@code >>}。
         */
        UndefinableValue<List<String>> ids();

        /**
         * 获取待过滤的租户的唯一标识。
         *
         * @return 表示唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> tenantId();

        /**
         * 获取待过滤的租户成员的唯一标识。
         *
         * @return 表示租户成员唯一标识的列表的 {@link UndefinableValue}{@code <}{@link List}{@code <}
         * {@link String}{@code >>}。
         */
        UndefinableValue<List<String>> userIds();

        /**
         * 为租户成员的过滤配置提供构建器。
         *
         * @author 梁济时
         * @since 2023-10-17
         */
        interface Builder {
            /**
             * 设置待过滤的租户的唯一标识。
             *
             * @param ids 表示租户唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder ids(List<String> ids);

            /**
             * 设置待过滤的租户的唯一标识。
             *
             * @param tenantId 表示租户的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder tenantId(String tenantId);

            /**
             * 设置待过滤的租户成员的唯一标识。
             *
             * @param userIds 表示租户成员的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder userIds(List<String> userIds);

            /**
             * 构建租户过滤配置的新实例。
             *
             * @return 表示租户过滤配置新实例的 {@link Filter}。
             */
            Filter build();
        }
    }
}
