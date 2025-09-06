/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

/**
 * 为 {@link Authenticator} 提供不含sso的默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Component
public class BaseAuthenticator implements Authenticator {
    @Override
    public User authenticate(HttpClassicServerRequest request) {
        UserContext userContext = Validation.notNull(UserContextHolder.get(), "The user context cannot be null.");
        return User.custom()
                .account(userContext.getName())
                .name(userContext.getName())
                .fqn(userContext.getName())
                .build();
    }
}
