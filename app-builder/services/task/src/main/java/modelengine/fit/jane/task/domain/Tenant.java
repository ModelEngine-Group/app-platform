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
 * 表示租户。
 *
 * @author 陈镕希
 * @since 2023-10-11
 */
public interface Tenant extends DomainObject {
    /**
     * 返回一个构建器，用以构建租户的新实例。
     *
     * @return 表示用以构建租户新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTenant.Builder();
    }

    /**
     * 名称
     *
     * @return 表示名称的 {@link String}。
     */
    String name();

    /**
     * 描述
     *
     * @return 表示描述的 {@link String}。
     */
    String description();

    /**
     * 获取avatarId
     *
     * @return 表示avatarId的 {@link String}。
     */
    String avatarId();

    /**
     * 获取成员列表
     *
     * @return 表示成员的 {@link List<String>}。
     */
    List<TenantMember> members();

    /**
     * 获取tag列表
     *
     * @return 表示标签的 {@link List<String>}。
     */
    List<String> tags();

    /**
     * 访问级别
     *
     * @return 表示级别的 {@link TenantAccessLevel}。
     */
    TenantAccessLevel accessLevel();

    /**
     * 是否有权限
     *
     * @param repo 表示租户存储能力的 {@link Repo}。
     * @param globalUserId 表示整个用户id的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示是否允许的 {@link boolean}。
     */
    boolean isPermitted(Repo repo, String globalUserId, OperationContext context);

    /**
     * 为租户提供构建器。
     *
     * @author 梁济时
     * @since 2023-10-11
     */
    interface Builder extends DomainObject.Builder<Tenant, Builder> {
        /**
         * 设置租户的名称。
         *
         * @param name 表示租户的名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置租户的描述。
         *
         * @param description 表示租户的描述的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 设置租户的头像id。
         *
         * @param avatarId 表示租户的头像id的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder avatarId(String avatarId);

        /**
         * 设置租户的成员列表。
         *
         * @param members 表示租户成员列表的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder members(List<TenantMember> members);

        /**
         * 设置租户的标签列表。
         *
         * @param tags 表示租户的标签列表的 {@link List}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder tags(List<String> tags);

        /**
         * 设置租户的公共空间权限。
         *
         * @param accessLevel 表示租户的公共空间权限的 {@link TenantAccessLevel}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder accessLevel(TenantAccessLevel accessLevel);
    }

    /**
     * 为 {@link Tenant} 提供声明。
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
            return new DefaultTenant.Declaration.Builder();
        }

        /**
         * 获取租户的名称。
         *
         * @return 表示租户名称的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> name();

        /**
         * 获取租户的描述。
         *
         * @return 表示租户描述的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> description();

        /**
         * 获取租户的头像id。
         *
         * @return 表示租户头像id的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> avatarId();

        /**
         * 设置租户的成员列表。
         *
         * @return 表示当前构建器的 {@link Tenant.Builder}。
         */
        UndefinableValue<List<String>> members();

        /**
         * 获取租户的标签列表。
         *
         * @return 表示租户标签列表的 {@link UndefinableValue}{@code <}{@link List}
         * {@code <}{@link String}
         * {@code >}{@code >}。
         */
        UndefinableValue<List<String>> tags();

        /**
         * 设置租户的公共空间权限。
         *
         * @return 表示租户的公共空间权限的 {@link Tenant.Builder}。
         */
        UndefinableValue<TenantAccessLevel> accessLevel();

        /**
         * 为租户的声明提供构建器。
         *
         * @author 陈镕希
         * @since 2023-10-11
         */
        interface Builder {
            /**
             * 设置租户的名称。
             *
             * @param name 表示租户名称的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder name(String name);

            /**
             * 设置租户的描述。
             *
             * @param description 表示租户描述的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder description(String description);

            /**
             * 设置租户的头像id。
             *
             * @param avatarId 表示租户头像id的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder avatarId(String avatarId);

            /**
             * 设置租户的成员列表。
             *
             * @param members 表示租户成员列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder members(List<String> members);

            /**
             * 设置租户的标签列表。
             *
             * @param tags 表示租户标签列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder tags(List<String> tags);

            /**
             * 设置租户的公共空间权限。
             *
             * @param accessLevel 表示租户空间权限的 {@link TenantAccessLevel}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder accessLevel(TenantAccessLevel accessLevel);

            /**
             * 构建租户的声明。
             *
             * @return 表示新构建的租户的声明的实例的 {@link Declaration}。
             */
            Declaration build();
        }
    }

    /**
     * 为 {@link Tenant} 提供存储能力。
     *
     * @author 陈镕希
     * @since 2023-10-10
     */
    interface Repo {
        /**
         * 创建租户。
         *
         * @param declaration 表示租户声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的租户的 {@link Tenant}。
         */
        Tenant create(Declaration declaration, OperationContext context);

        /**
         * 更新租户。
         *
         * @param tenantId 表示待更新的租户的唯一标识的 {@link String}。
         * @param declaration 表示租户声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void patch(String tenantId, Declaration declaration, OperationContext context);

        /**
         * 删除租户。
         *
         * @param tenantId 表示待删除的租户的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(String tenantId, OperationContext context);

        /**
         * 检索租户。
         *
         * @param tenantId 表示待检索的租户的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的租户的 {@link Tenant}。
         */
        Tenant retrieve(String tenantId, OperationContext context);

        /**
         * 查询租户。
         *
         * @param filter 表示租户过滤器的 {@link Filter}。
         * @param offset 表示查询到的租户的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示查询到的租户的结果集中的最大数量的 32 位整数。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Tenant}{@code >}。
         */
        RangedResultSet<Tenant> list(Filter filter, long offset, int limit, OperationContext context);

        /**
         * 查询我的租户，包含我有权限的和public的租户。
         *
         * @param myTenantIds 表示我有权限的租户唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
         * @param offset 表示查询到的租户的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示查询到的租户的结果集中的最大数量的 32 位整数。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Tenant}{@code >}。
         */
        RangedResultSet<Tenant> listMy(List<String> myTenantIds, long offset, int limit, OperationContext context);

        /**
         * 插入租户成员。
         *
         * @param tenantId 表示租户的唯一标识的 {@link String}。
         * @param members 表示租户成员列表的 {@link List}{@code <}{@link String}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void insertMembers(String tenantId, List<String> members, OperationContext context);

        /**
         * 通过租户信息删除租户成员。
         *
         * @param tenant 表示租户 {@link Tenant}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void deleteMemberByTenant(Tenant tenant, OperationContext context);

        /**
         * 批量删除租户成员。
         *
         * @param tenantId 表示租户的唯一标识的 {@link String}。
         * @param memberIds 表示需要删除的用户唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void deleteMembersById(String tenantId, List<String> memberIds, OperationContext context);

        /**
         * 批量删除租户成员。
         *
         * @param tenantId 表示租户的唯一标识的 {@link String}。
         * @param userIds 表示需要删除的用户Id列表的 {@link List}{@code <}{@link String}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void deleteMembersByUserId(String tenantId, List<String> userIds, OperationContext context);

        /**
         * 根据用户Id搜索所属租户Id列表
         *
         * @param userId 表示用户的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示所属租户Id列表的 {@link List}{@code <}{@link String}{@code >}。
         */
        List<String> listTenantIdsByUserId(String userId, OperationContext context);

        /**
         * 查询租户成员。
         *
         * @param filter 表示租户成员过滤器的 {@link TenantMember.Filter}。
         * @param offset 表示查询到的租户的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示查询到的租户的结果集中的最大数量的 32 位整数。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TenantMember}{@code >}。
         */
        RangedResultSet<TenantMember> listMember(TenantMember.Filter filter, long offset, int limit,
            OperationContext context);
    }

    /**
     * 为租户提供过滤配置。
     *
     * @author 梁济时
     * @since 2023-10-17
     */
    interface Filter {
        /**
         * 返回一个构建器，用以构建租户的过滤配置。
         *
         * @return 表示用以构建租户配置的新实例的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTenant.Filter.Builder();
        }

        /**
         * 获取待过滤的租户的唯一标识。
         *
         * @return 表示唯一标识的列表的 {@link UndefinableValue}{@code <}{@link List}{@code <}{@link String}{@code >>}。
         */
        UndefinableValue<List<String>> ids();

        /**
         * 获取待过滤的租户的名称。
         *
         * @return 表示名称的列表的 {@link UndefinableValue}{@code <}{@link List}{@code <}{@link String}{@code >>}。
         */
        UndefinableValue<List<String>> names();

        /**
         * 获取待过滤的租户的标签。
         *
         * @return 表示标签的列表的 {@link UndefinableValue}{@code <}{@link List}{@code <}{@link String}{@code >>}。
         */
        UndefinableValue<List<String>> tags();

        /**
         * 获取待过滤的租户的权限。
         *
         * @return 表示租户的权限的 {@link UndefinableValue}{@code <}{@link List}{@code <}
         * {@link TenantAccessLevel}
         * {@code >}{@code >}。
         */
        UndefinableValue<List<TenantAccessLevel>> accessLevels();

        /**
         * 为租户的过滤配置提供构建器。
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
             * 设置待过滤的租户的名称。
             *
             * @param names 表示名称列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder names(List<String> names);

            /**
             * 设置待过滤的标签。
             *
             * @param tags 表示标签列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder tags(List<String> tags);

            /**
             * 设置待过滤的租户空间权限。
             *
             * @param accessLevels 表示级别列表的 {@link List}{@code <}{@link TenantAccessLevel}{@code >}。
             * @return 表示租户过滤空间权限的 {@link TenantAccessLevel}。
             */
            Builder accessLevels(List<TenantAccessLevel> accessLevels);

            /**
             * 构建租户过滤配置的新实例。
             *
             * @return 表示租户过滤配置新实例的 {@link Filter}。
             */
            Filter build();
        }
    }
}
