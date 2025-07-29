/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

/**
 * 大模型对话流式接口的注册中心服务。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
public interface AppWsRegistryService {
    /**
     * 注册大模型对话命令。
     *
     * @param method 表示命令名称的 {@link String}。
     * @param command 表示命令对象的 {@link AppWsCommand}。
     */
    void register(String method, AppWsCommand<?> command);

    /**
     * 注销大模型对话命令。
     *
     * @param method 表示命令名称的 {@link String}。
     */
    void unregister(String method);

    /**
     * 获取大模型对话命令。
     *
     * @param method 表示命令名称的 {@link String}。
     * @return 表示命令对象的 {@link AppWsCommand}。
     */
    AppWsCommand<?> getCommand(String method);

    /**
     * 获取命令入参类型。
     *
     * @param method 表示命令名称的 {@link String}。
     * @return 表示命令入参类型的 {@link Class}。
     */
    Class<?> getParamClass(String method);
}
