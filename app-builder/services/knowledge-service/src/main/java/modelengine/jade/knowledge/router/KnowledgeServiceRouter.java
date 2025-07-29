/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.router;

import modelengine.fitframework.broker.client.Invoker;

/**
 * 知识库服务路由处理类。
 *
 * @author 陈潇文
 * @since 2025-04-27
 */
public interface KnowledgeServiceRouter {

    /**
     * 获取知识库服务路由。
     *
     * @param genericableClass 表示泛服务的类型的 {@link Class}{@code <?>}。
     * @param genericableId 表示泛服务的唯一标识的 {@link String}。
     * @param groupId 表示分组的 {@link String}。
     * @return 表示调用器的 {@link Invoker}。
     */
    Invoker getInvoker(Class<?> genericableClass, String genericableId, String groupId);
} 