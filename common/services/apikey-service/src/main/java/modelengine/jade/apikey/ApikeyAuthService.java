/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.apikey;

/**
 * apikey 鉴权服务。
 *
 * @author 陈潇文
 * @since 2025-07-07
 */
public interface ApikeyAuthService {
    /**
     * api key 鉴权。
     *
     * @param apikey 表示 api key 的 {@link String}。
     * @return String 表示鉴权成功后的用户名 {@link String}。
     */
    String authApikeyInfo(String apikey);
}
