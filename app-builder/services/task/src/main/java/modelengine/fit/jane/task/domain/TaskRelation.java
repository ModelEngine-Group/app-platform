/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fitframework.model.RangedResultSet;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 为任务关联提供数据模型
 *
 * @author 罗书强
 * @since 2023-12-28
 */
public interface TaskRelation {
    /**
     * 关联关系的唯一标识。
     *
     * @return 表示关联关系唯一标识的 {@link String}。
     */
    String id();

    /**
     * 关联方的唯一标识。
     *
     * @return 表示关联方的唯一标识的 {@link String}。
     */
    String objectId1();

    /**
     * 关联方的类型。
     *
     * @return 表示关联方类型的 {@link String}。
     */
    String objectType1();

    /**
     * 被关联方的唯一标识。
     *
     * @return 表示被关联方的唯一标识的 {@link String}。
     */
    String objectId2();

    /**
     * 被关联方的类型。
     *
     * @return 表示被关联方的类型的 {@link String}。
     */
    String objectType2();

    /**
     * 关联的类型。
     *
     * @return 表示关联的类型的 {@link String}。
     */
    String relationType();

    /**
     * 关联信息的创建人。
     *
     * @return 表示关联信息的创建人的 {@link String}。
     */
    String createdBy();

    /**
     * 关联信息的创建时间。
     *
     * @return 表示关联信息的创建时间的 {@link String}。
     */
    LocalDateTime createdAt();

    /**
     * Builder
     */
    interface Builder {
        /**
         * 关联关系的唯一标识。
         *
         * @param id 表示关联关系的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder id(String id);

        /**
         * 关联方的唯一标识。
         *
         * @param objectId1 表示关联方的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder objectId1(String objectId1);

        /**
         * 关联方的类型。
         *
         * @param objectType1 表示关联方的类型的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder objectType1(String objectType1);

        /**
         * 被关联方的唯一标识。
         *
         * @param objectId2 表示被关联方的唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder objectId2(String objectId2);

        /**
         * 被关联方的类型。
         *
         * @param objectType2 表示被关联方的类型的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder objectType2(String objectType2);

        /**
         * 关联的类型。
         *
         * @param relationType 表示关联的类型的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder relationType(String relationType);

        /**
         * 关联信息的创建人。
         *
         * @param createdBy 表示关联信息的创建人的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder createdBy(String createdBy);

        /**
         * 关联信息的创建时间。
         *
         * @param createdAt 表示关联信息的创建时间的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder createdAt(LocalDateTime createdAt);

        /**
         * 关联信息的构建
         *
         * @return 关联信息实体
         */
        TaskRelation build();
    }

    /**
     * 返回一个构建器，用以构建 {@link TaskRelation} 的默认实现的新实例。
     *
     * @return 表示用以构建新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTaskRelation.Builder();
    }

    /**
     * 为任务关联提供声明。
     *
     * @author 罗书强
     * @since 2023-12-29
     */
    interface Declaration {
        /**
         * 关联关系的唯一标识。
         *
         * @return 表示关联关系唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> id();

        /**
         * 关联方的唯一标识。
         *
         * @return 表示关联方的唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> objectId1();

        /**
         * 关联方的类型。
         *
         * @return 表示关联方类型的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> objectType1();

        /**
         * 被关联方的唯一标识。
         *
         * @return 表示被关联方唯一标识的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> objectId2();

        /**
         * 被关联方的类型。
         *
         * @return 表示被关联方类型的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> objectType2();

        /**
         * 关联的类型。
         *
         * @return 表示关联类型的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> relationType();

        /**
         * 关联信息的创建人。
         *
         * @return 表示关联信息创建人的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> createdBy();

        /**
         * 关联信息的创建时间。
         *
         * @return 表示关联信息创建时间的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<LocalDateTime> createdAt();

        /**
         * 为任务关联的声明提供构建器。
         *
         * @author 罗书强
         * @since 2023-12-29
         */
        interface Builder {
            /**
             * 关联关系的唯一标识。
             *
             * @param id 表示关联关系的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder id(String id);

            /**
             * 关联方的唯一标识。
             *
             * @param objectId1 表示关联方的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder objectId1(String objectId1);

            /**
             * 关联方的类型。
             *
             * @param objectType1 表示关联方的类型的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder objectType1(String objectType1);

            /**
             * 被关联方的唯一标识。
             *
             * @param objectId2 表示被关联方的唯一标识的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder objectId2(String objectId2);

            /**
             * 被关联方的类型。
             *
             * @param objectType2 表示被关联方的类型的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder objectType2(String objectType2);

            /**
             * 关联的类型。
             *
             * @param relationType 表示关联的类型的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder relationType(String relationType);

            /**
             * 关联信息的创建人。
             *
             * @param createdBy 表示关联信息的创建人的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder createdBy(String createdBy);

            /**
             * 关联信息的创建时间。
             *
             * @param createdAt 表示关联信息的创建时间的 {@link String}。
             * @return 表示当前构建器的 {@link TaskRelation.Builder}。
             */
            Builder createdAt(LocalDateTime createdAt);

            /**
             * 构建任务关联声明的新实例。
             *
             * @return 表示新构建的任务关联声明的 {@link Declaration}。
             */
            Declaration build();
        }

        /**
         * 返回一个构建器，用以构建任务关联的新实例。
         *
         * @return 表示用以构建任务关联声明的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTaskRelation.Declaration.Builder();
        }
    }

    /**
     * repo接口
     */
    interface Repo {
        /**
         * 创建关联关系。
         *
         * @param declaration 表示关联关系的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示关联关系的 {@link TaskRelation}。
         */
        TaskRelation create(Declaration declaration, OperationContext context);

        /**
         * 删除关联任务关系。
         *
         * @param relationId 表示待删除的关联任务关系的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         */
        void delete(String relationId, OperationContext context);

        /**
         * 检索关联任务关系。
         *
         * @param relationId 表示待检索的关联任务关系的唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的关联任务关系的 {@link TaskRelation}。
         */
        TaskRelation retrieve(String relationId, OperationContext context);

        /**
         * 查询关联任务关系。
         *
         * @param filter 表示任务关联过滤器。{@link Filter}。
         * @param offset 表示查询到的关联任务关系的结果集在全量结果集中的偏移量的 64 位整数。
         * @param limit 表示查询到的关联任务关系的结果集中的最大数量的 32 位整数。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskRelation}{@code >}。
         */
        RangedResultSet<TaskRelation> list(Filter filter, long offset, int limit,
                OperationContext context);
    }

    /**
     * 提供任务关联过滤
     *
     * @author 罗书强
     * @since 2024-01-03
     */
    interface Filter {
        /**
         * 设置待查询的唯一标识的列表
         *
         * @return 唯一标识的列表
         */
        List<String> ids();

        /**
         * 设置待查询的关联方唯一标识的列表。
         *
         * @return 关联方唯一标识的列表
         */
        List<String> objectId1s();

        /**
         * 设置待查询的被关联方的唯一标识的列表。
         *
         * @return 被关联方的唯一标识的列表
         */
        List<String> objectId2s();

        /**
         * 为任务关联的过滤条件系统构建器。
         *
         * @author 罗书强
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
             * 设置待查询的关联方唯一标识的列表。
             *
             * @param objectId1s 表示关联方的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder objectId1s(List<String> objectId1s);

            /**
             * 设置待查询的被关联方的唯一标识的列表。
             *
             * @param objectId2s 表示被关联方的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder objectId2s(List<String> objectId2s);

            /**
             * 构建任务关联的过滤条件。
             *
             * @return 表示任务关联的过滤条件的 {@link Filter}。
             */
            Filter build();
        }

        /**
         * 返回一个构建器，用以构建任务关联的过滤条件。
         *
         * @return 表示用以构建任务关联的过滤条件的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultTaskRelation.Filter.Builder();
        }
    }
}
