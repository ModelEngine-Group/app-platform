/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.router;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;

/**
 * 知识库服务路由处理类实现。
 *
 * @author 陈潇文
 * @since 2025-04-28
 */
@Component
public class KnowledgeServiceRouterImpl implements KnowledgeServiceRouter {
    private final BrokerClient brokerClient;

    /**
     * 表示 {@link KnowledgeServiceRouter} 的构造器。
     *
     * @param brokerClient 表示fit调度器的 {@link BrokerClient}。
     */
    public KnowledgeServiceRouterImpl(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public Invoker getRouter(Class<?> genericableClass, String genericableId, String groupId) {
        return this.brokerClient.getRouter(genericableClass, genericableId).route(new FitableIdFilter(groupId));
    }
}
