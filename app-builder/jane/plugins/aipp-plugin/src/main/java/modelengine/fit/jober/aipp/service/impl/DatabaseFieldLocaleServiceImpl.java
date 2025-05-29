/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jober.aipp.repository.I18nRepository;
import modelengine.fit.jober.aipp.service.DatabaseFieldLocaleService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Initialize;
import modelengine.fitframework.log.Logger;

import java.util.Locale;
import java.util.Map;

/**
 * 国际化字段值处理实现。
 * 用于获取国际化信息，支持数据库内置参数的动态替换
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@Component
public class DatabaseFieldLocaleServiceImpl implements DatabaseFieldLocaleService {
    private static final Logger log = Logger.get(DatabaseFieldLocaleServiceImpl.class);
    private static Map<String, Map<String, String>> resourceMap = null;

    private final I18nRepository i18nRepository;

    public DatabaseFieldLocaleServiceImpl(I18nRepository i18nRepository) {
        this.i18nRepository = i18nRepository;
    }

    @Override
    public String getLocaleMessage(String key, Locale locale) {
        return resourceMap.get(locale.getLanguage())
                .getOrDefault(key, resourceMap.get(Locale.ENGLISH.getLanguage()).get(key));
    }

    /**
     * 加载国际化资源。
     */
    @Initialize
    protected void loadResource() {
        log.info("load locale resource.");
        resourceMap = this.i18nRepository.selectResource();
    }
}
