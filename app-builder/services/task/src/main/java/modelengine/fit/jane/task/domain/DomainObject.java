/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import java.time.LocalDateTime;

/**
 * 表示领域对象。
 *
 * @author 梁济时
 * @since 2023-09-12
 */
public interface DomainObject {
    /**
     * 获取领域对象的唯一标识。
     *
     * @return 表示对象唯一标识的 {@link String}。
     */
    String id();

    /**
     * 获取领域对象的创建人。
     *
     * @return 表示创建人的 {@link String}。
     */
    String creator();

    /**
     * 获取领域对象的创建时间。
     *
     * @return 表示创建时间的 {@link LocalDateTime}。
     */
    LocalDateTime creationTime();

    /**
     * 获取领域对象的上次修改人。
     *
     * @return 表示上次修改人的 {@link String}。
     */
    String lastModifier();

    /**
     * 获取领域对象的上次修改时间。
     *
     * @return 表示上次修改时间的 {@link LocalDateTime}。
     */
    LocalDateTime lastModificationTime();

    /**
     * 为领域对象提供构建器。
     *
     * @param <D> 表示待构建的领域对象的类型。
     * @param <B> 表示构建器的实际类型。
     * @author 梁济时
     * @since 2023-09-12
     */
    interface Builder<D extends DomainObject, B extends Builder<D, B>> {
        /**
         * 设置领域对象的唯一标识。
         *
         * @param id 表示领域对象唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B id(String id);

        /**
         * 设置领域对象的创建人。
         *
         * @param creator 表示创建人的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B creator(String creator);

        /**
         * 设置领域对象的创建时间。
         *
         * @param creationTime 表示创建时间的 {@link LocalDateTime}。
         * @return 表示当前构建器的 {@link B}。
         */
        B creationTime(LocalDateTime creationTime);

        /**
         * 设置领域对象的上次修改人。
         *
         * @param lastModifier 表示上次修改人的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        B lastModifier(String lastModifier);

        /**
         * 设置领域对象的上次修改时间。
         *
         * @param lastModificationTime 表示上次修改时间的 {@link LocalDateTime}。
         * @return 表示当前构建器的 {@link B}。
         */
        B lastModificationTime(LocalDateTime lastModificationTime);

        /**
         * 构建领域对象的新实例。
         *
         * @return 表示新构建的领域对象实例的 {@link D}。
         */
        D build();
    }
}
