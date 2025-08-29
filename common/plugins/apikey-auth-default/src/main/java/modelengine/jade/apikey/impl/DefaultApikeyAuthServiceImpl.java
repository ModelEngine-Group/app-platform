/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.apikey.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.jade.apikey.ApikeyAuthService;

/**
 * 表示北向接口 Apikey 鉴权的默认实现。
 *
 * @author 陈潇文
 * @since 2025-07-07
 */
@Component
public class DefaultApikeyAuthServiceImpl implements ApikeyAuthService {
    private final String userName;

    public DefaultApikeyAuthServiceImpl(@Value("${userName}") String userName) {
        this.userName = userName;
    }

    @Override
    public String authApikeyInfo(String apikey) {
        return this.userName;
    }
}
