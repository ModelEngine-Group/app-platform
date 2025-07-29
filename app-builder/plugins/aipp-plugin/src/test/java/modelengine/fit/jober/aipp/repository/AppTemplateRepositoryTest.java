/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.converters.impl.IconConverterImpl;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.mapper.AppTemplateMapper;
import modelengine.fit.jober.aipp.repository.impl.AppTemplateRepositoryImpl;
import modelengine.fit.jober.aipp.service.DatabaseBaseTest;
import modelengine.fitframework.annotation.Fit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 应用模板数据库操作类单元测试。
 *
 * @author 方誉州
 * @since 2025-01-16
 */
public class AppTemplateRepositoryTest extends DatabaseBaseTest {
    @Fit
    private final AppTemplateMapper templateMapper =
            sqlSessionManager.openSession(true).getMapper(AppTemplateMapper.class);

    private AppTemplateRepository templateRepository;

    private IconConverter iconConverter;

    @BeforeEach
    void setup() {
        this.iconConverter = new IconConverterImpl("/api/jober");
        this.templateRepository = new AppTemplateRepositoryImpl(this.templateMapper, this.iconConverter);
    }

    @Test
    @DisplayName("测试根据名称模糊查询应用模板")
    void testQueryTemplateWithName() {
        TemplateQueryCondition cond = TemplateQueryCondition.builder().name("熊猫").offset(0).limit(8).build();
        List<AppTemplate> result = this.templateRepository.selectWithCondition(cond);
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("测试根据应用类型分类查询应用模板")
    void testQueryTemplateWithCategories() {
        TemplateQueryCondition cond = TemplateQueryCondition.builder()
                .categories(Arrays.asList("chatbot", "workflow"))
                .offset(0)
                .limit(8)
                .build();
        List<AppTemplate> result = this.templateRepository.selectWithCondition(cond);
        assertThat(result).hasSize(6);
    }

    @Test
    @DisplayName("测试根据应用模板业务类型查询应用模板")
    void testQueryTemplateWithAppType() {
        TemplateQueryCondition cond = TemplateQueryCondition.builder()
                .appType(Arrays.asList("finance", "postman"))
                .offset(0)
                .limit(8)
                .build();
        List<AppTemplate> result = this.templateRepository.selectWithCondition(cond);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("测试查询应用模板并排序")
    void testQueryTemplateWithOrderBy() {
        TemplateQueryCondition cond = TemplateQueryCondition.builder()
                .orderBy("usage")
                .offset(0)
                .limit(8)
                .build();
        List<AppTemplate> result = this.templateRepository.selectWithCondition(cond);
        assertThat(result).hasSize(6)
                .element(0)
                .extracting(AppTemplate::getId).isEqualTo("3e29eb82f92f43259b4c514ddb96c0a8");
    }
}
