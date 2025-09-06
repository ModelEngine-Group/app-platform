/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import java.util.Locale;

/**
 * 国际化字段值处理接口。
 * 用于获取国际化信息，支持数据库内置参数的动态替换
 *
 * @author 陈潇文
 * @since 2024-8-19
 */
public interface DatabaseFieldLocaleService {
    /**
     * 用于获取国际化信息
     *
     * @param key 表示替换的字段key的 {@link String}。
     * @param locale 表示国际化字段语言类型的 {@link String}.
     * @return 国际化信息。
     */
    String getLocaleMessage(String key, Locale locale);
}
