/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.jober.aipp.mapper.I18nMapper;
import modelengine.fit.jober.aipp.po.I18nPo;
import modelengine.fit.jober.aipp.repository.I18nRepository;
import modelengine.fit.jober.aipp.repository.impl.I18nRepositoryImpl;
import modelengine.fit.jober.aipp.service.impl.DatabaseFieldLocaleServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 表示 {@link DatabaseFieldLocaleService} 的单元测试。
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@DisplayName("DatabaseFieldLocaleService")
public class DatabaseFieldLocaleServiceImplTest {
    private I18nMapper i18nMapper;
    private DatabaseFieldLocaleServiceImplTestExt databaseFieldLocaleServiceImplTestExt;

    @BeforeEach
    void setUp() {
        this.i18nMapper = mock(I18nMapper.class);
        I18nRepositoryImpl i18nRepositoryImpl = new I18nRepositoryImpl(this.i18nMapper);
        this.databaseFieldLocaleServiceImplTestExt = new DatabaseFieldLocaleServiceImplTestExt(i18nRepositoryImpl);
    }

    @Test
    @DisplayName("查询字段国际化信息时，返回正确结果")
    void shouldSuccessWhenGetLocaleResource() {
        List<I18nPo> i18nPoList = new ArrayList<>();
        i18nPoList.add(new I18nPo("123", "name", "zh", "张三"));
        i18nPoList.add(new I18nPo("123", "name", "en", "ZhangSan"));
        Mockito.when(this.i18nMapper.selectResource()).thenReturn(i18nPoList);
        this.databaseFieldLocaleServiceImplTestExt.loadResource();
        assertThat(this.databaseFieldLocaleServiceImplTestExt.getLocaleMessage("name", Locale.ENGLISH))
                .isEqualTo("ZhangSan");
        assertThat(this.databaseFieldLocaleServiceImplTestExt.getLocaleMessage("name", Locale.CHINESE))
                .isEqualTo("张三");
    }

    private static class DatabaseFieldLocaleServiceImplTestExt extends DatabaseFieldLocaleServiceImpl {
        public DatabaseFieldLocaleServiceImplTestExt(I18nRepository i18nRepository) {
            super(i18nRepository);
        }

        @Override
        public void loadResource() {
            super.loadResource();
        }
    }
}
