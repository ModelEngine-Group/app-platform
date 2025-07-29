/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.jade.common.globalization.LocaleService;

import modelengine.jade.knowledge.enums.KnowledgeRetrievalParam;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 表示 {@link KnowledgeI18nService} 的默认实现。
 *
 * @author 马朝阳
 * @since 2024-10-10
 */
@Component
public class KnowledgeI18nServiceImpl implements KnowledgeI18nService {
    private static final String PARAM_DESCRIPTION = ".description";

    private final LocaleService localeService;

    public KnowledgeI18nServiceImpl(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Fitable("knowledge.localize.parameter")
    @Override
    public KnowledgeI18nInfo localizeText(KnowledgeRetrievalParam paramType) {
        return new KnowledgeI18nInfo(this.localeService.localize(paramType.value()),
                this.localeService.localize(paramType.value() + PARAM_DESCRIPTION));
    }

    @Fitable("knowledge.localize.text")
    @Override
    public String localizeText(String name) {
        return this.localeService.localize(name);
    }
}
