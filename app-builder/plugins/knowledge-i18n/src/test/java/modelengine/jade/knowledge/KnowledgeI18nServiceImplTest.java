/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import modelengine.jade.common.globalization.impl.LocaleServiceImpl;
import modelengine.jade.common.locale.LocaleUtil;

import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.enums.IndexType;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Locale;

/**
 * 表示 {@link KnowledgeI18nServiceImpl} 的测试用例。
 *
 * @author 马朝阳
 * @since 2024-10-10
 */
@FitTestWithJunit(includeClasses = {KnowledgeI18nServiceImpl.class, LocaleServiceImpl.class})
public class KnowledgeI18nServiceImplTest {
    @Fit
    private KnowledgeI18nService knowledgeI18nService;

    @Mock
    private MockedStatic<LocaleUtil> localeUtil;

    @Test
    @DisplayName("根据检索信息和系统语言查询国际化信息成功")
    void shouldOkWhenQueryI18nPropertyAndLanguage() {
        this.localeUtil = mockStatic(LocaleUtil.class);
        this.localeUtil.when(LocaleUtil::getLocale).thenReturn(Locale.CHINA);
        KnowledgeI18nInfo semanticInfo = this.knowledgeI18nService.localizeText(IndexType.SEMANTIC);
        assertThat(semanticInfo.getName()).isEqualTo("语义检索");
        assertThat(semanticInfo.getDescription()).isEqualTo("基于文本的含义检索出最相关的内容");

        KnowledgeI18nInfo referenceInfo = this.knowledgeI18nService.localizeText(FilterType.REFERENCE_TOP_K);
        assertThat(referenceInfo.getName()).isEqualTo("引用上限");
        assertThat(referenceInfo.getDescription()).isEqualTo("最大召回知识条数");

        this.localeUtil.when(LocaleUtil::getLocale).thenReturn(Locale.ENGLISH);
        KnowledgeI18nInfo fullTextInfo = this.knowledgeI18nService.localizeText(IndexType.FULL_TEXT);
        assertThat(fullTextInfo.getName()).isEqualTo("Full-text search");
        assertThat(fullTextInfo.getDescription()).isEqualTo(
                "Searches for content in the full text, which is suitable for querying keywords and data with special"
                        + " subject-predicate structures.");

        KnowledgeI18nInfo similarityInfo = this.knowledgeI18nService.localizeText(FilterType.SIMILARITY_THRESHOLD);
        assertThat(similarityInfo.getName()).isEqualTo("Lowest Correlation");
        assertThat(similarityInfo.getDescription()).isEqualTo("Lowest correlation of search text.");

        KnowledgeI18nInfo rerankInfo = new KnowledgeI18nInfo(this.knowledgeI18nService.localizeText("rerankParam"),
                this.knowledgeI18nService.localizeText("rerankParam.description"));
        assertThat(rerankInfo.getName()).isEqualTo("Re-sort Results");
        assertThat(rerankInfo.getDescription()).isEqualTo(
                "Re-sort the retrieved candidate documents according to the relevance to the query.");
    }
}
