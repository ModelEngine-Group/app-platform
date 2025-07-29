/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.service.AppWsCommand;
import modelengine.fitframework.util.StringUtils;

/**
 * 大模型会话流式接口命令的基类。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
public abstract class AbstractAppWsCommand<T> implements AppWsCommand<T> {
    /**
     * 将用户信息设置入操作上下文中。
     *
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param name 表示用户姓名的 {@link String}。
     * @param account 表示用户工号的 {@link String}。
     */
    protected void setUserInOperationContext(OperationContext context, String name, String account) {
        if (StringUtils.isNotBlank(context.getOperator())) {
            return;
        }
        context.setName(name);
        context.setAccount(account);
        if (!account.isEmpty() && !Character.isDigit(account.charAt(0))) {
            context.setOperator(name + ' ' + account.substring(1));
        } else {
            context.setOperator(name + ' ' + account);
        }
    }
}
