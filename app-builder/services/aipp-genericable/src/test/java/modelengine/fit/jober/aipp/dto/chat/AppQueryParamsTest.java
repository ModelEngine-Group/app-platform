/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * {@link AppQueryParams} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 AppQueryParams")
class AppQueryParamsTest {
    @Test
    @DisplayName("用构建器构建查询应用状态类时，返回成功")
    void constructAppQueryParams() {
        AppQueryParams condition = AppQueryParams.builder()
                .ids(Arrays.asList("id1", "id2"))
                .name("AppName")
                .state("ACTIVE")
                .excludeNames(Arrays.asList("Exclude1", "Exclude2"))
                .build();

        assertThat(condition).isNotNull();
        assertThat(condition.getIds()).hasSize(2);
        assertThat(condition.getIds().get(0)).isEqualTo("id1");
        assertThat(condition.getName()).isEqualTo("AppName");
        assertThat(condition.getState()).isEqualTo("ACTIVE");
        assertThat(condition.getExcludeNames()).hasSize(2);
        assertThat(condition.getExcludeNames().get(0)).isEqualTo("Exclude1");
    }
}
