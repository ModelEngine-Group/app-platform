/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;
import java.util.Map;

/**
 * 表示文件。
 *
 * @author 陈镕希
 * @since 2023-10-10
 */
public interface File extends DomainObject {
    /**
     * 返回一个构建器，用以构建文件的新实例。
     *
     * @return 表示用以构建文件新实例的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultFile.Builder();
    }

    /**
     * 获取文件的名称。
     *
     * @return 表示文件名称。
     */
    String name();

    /**
     * 获取文件的内容。
     *
     * @return 表示文件内容。
     */
    byte[] content();

    /**
     * 为文件提供构建器。
     *
     * @author 陈镕希
     * @since 2023-10-10
     */
    interface Builder extends DomainObject.Builder<File, Builder> {
        /**
         * 设置文件的名称。
         *
         * @param name 表示文件的名称。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置文件的内容。
         *
         * @param bytes 表示文件的内容。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder content(byte[] bytes);
    }

    /**
     * 为 {@link File} 提供声明。
     *
     * @author 陈镕希
     * @since 2023-10-10
     */
    interface Declaration {
        /**
         * 返回一个构建器，用以构建文件的新实例。
         *
         * @return 表示用以构建文件的构建器的 {@link Builder}。
         */
        static Builder custom() {
            return new DefaultFile.Declaration.Builder();
        }

        /**
         * 获取文件的名称。
         *
         * @return 表示文件的名称的 {@link UndefinableValue}{@code <}{@link String}{@code >}。
         */
        UndefinableValue<String> name();

        /**
         * 获取文件的内容。
         *
         * @return 表示文件的内容的 {@link UndefinableValue}{@code <byte[]>}。
         */
        UndefinableValue<byte[]> content();

        /**
         * 为文件的声明提供构建器。
         *
         * @author 陈镕希
         * @since 2023-10-10
         */
        interface Builder {
            /**
             * 设置文件的名称。
             *
             * @param name 表示文件名称。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder name(String name);

            /**
             * 设置文件的内容。
             *
             * @param content 表示文件内容。
             * @return 表示当前构建器的 {@link Builder}。
             */
            Builder content(byte[] content);

            /**
             * 构建文件的声明。
             *
             * @return 表示新构建的文件的声明的实例的 {@link Declaration}。
             */
            Declaration build();
        }
    }

    /**
     * 为 {@link File} 提供存储能力。
     *
     * @author 陈镕希
     * @since 2023-10-10
     */
    interface Repo {
        /**
         * 上传文件。
         *
         * @param declaration 表示文件的声明的 {@link Declaration}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示新创建的文件的 {@link File}。
         */
        File upload(Declaration declaration, OperationContext context);

        /**
         * 下载文件。
         *
         * @param fileId 表示文件唯一标识的 {@link String}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示下载的文件的 {@link File}。
         */
        File download(String fileId, OperationContext context);

        /**
         * 获取文件信息
         *
         * @param fileIds 表示文件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
         * @param context 表示操作上下文的 {@link OperationContext}。
         * @return 表示文件唯一标识与文件名称的 {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}。
         */
        Map<String, String> fileInfo(List<String> fileIds, OperationContext context);
    }
}
