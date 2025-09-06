/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.mapper.I18nMapper;
import modelengine.fit.jober.aipp.po.I18nPo;
import modelengine.fit.jober.aipp.repository.I18nRepository;

import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 国际化仓库实现类
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@Component
public class I18nRepositoryImpl implements I18nRepository {
    private final I18nMapper i18nMapper;

    public I18nRepositoryImpl(I18nMapper i18nMapper) {
        this.i18nMapper = i18nMapper;
    }

    @Override
    public Map<String, Map<String, String>> selectResource() {
        List<I18nPo> i18nPoList = this.i18nMapper.selectResource();
        return i18nPoList.stream().collect(Collectors.groupingBy(
                I18nPo::getLanguage,
                Collectors.toMap(
                        I18nPo::getKey,
                        I18nPo::getValue,
                        (existing, replacement) -> existing)
                )
        );
    }
}
