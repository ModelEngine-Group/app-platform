/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.jade.knowledge.FilterConfig;
import modelengine.jade.knowledge.enums.FilterType;
import modelengine.jade.knowledge.support.FlatFilterConfig;

import org.junit.jupiter.api.Test;

/**
 * {@link FilterConfig} 的测试。
 *
 * @author 刘信宏
 * @since 2024-09-24
 */
public class FilterConfigTest {
    @Test
    void shouldOkWhenFilledWithRequiredConfig() {
        FlatFilterConfig flatOption = new FlatFilterConfig(FilterConfig.custom()
                .name("query")
                .type(FilterType.REFERENCE_TOP_K)
                .description("description")
                .defaultValue(3)
                .minimum(1)
                .maximum(10)
                .build());
        assertThat(flatOption).extracting(FilterConfig::name, FlatFilterConfig::type,
                        FilterConfig::description)
                .containsSequence("query", FilterType.REFERENCE_TOP_K.value(), "description");
    }

    @Test
    void shouldFailedWhenFilledWithoutRequiredConfig() {
        assertThatThrownBy(() -> this.getBuilder().type(FilterType.REFERENCE_TOP_K).minimum(1).maximum(10).build())
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.getBuilder().name("query").minimum(1).maximum(10).build())
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.getBuilder().name("query").type(FilterType.REFERENCE_TOP_K).maximum(10).build())
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.getBuilder().name("query").type(FilterType.REFERENCE_TOP_K).minimum(1).build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    private FilterConfig.Builder getBuilder() {
        return FilterConfig.custom().description("description").defaultValue(3);
    }
}
