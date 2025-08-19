/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.apikey;

import modelengine.jade.apikey.impl.DefaultApikeyAuthServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link DefaultApikeyAuthServiceImpl} 的测试类。
 *
 * @author 陈潇文
 * @since 2025-07-07
 */
public class DefaultApikeyAuthServiceImplTest {
    @Test
    void testAuthApikeyInfo() {
        String username = "Jade";
        DefaultApikeyAuthServiceImpl service = new DefaultApikeyAuthServiceImpl(username);
        Assertions.assertEquals(username, service.authApikeyInfo(null));
        Assertions.assertEquals(username, service.authApikeyInfo(""));
        Assertions.assertEquals(username, service.authApikeyInfo("any-string"));
        Assertions.assertEquals(username, service.authApikeyInfo("Bearer ME-sk-1234567890abcdef-abcdef1234567890abcdef1234567890"));
    }
}
