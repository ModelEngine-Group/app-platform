/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link PromptCategory} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 PromptCategory")
class PromptCategoryTest {
    @Test
    @DisplayName("用构建器构建灵感大全查询类时，返回成功")
    void constructPromptCategory() {
        String title = "创意灵感";
        String id = "1";
        String parent = "root";
        Boolean disable = false;
        PromptCategory childCategory =
                PromptCategory.builder().title("子类别1").id("2").parent(id).disable(false).children(null).build();
        List<PromptCategory> children = Collections.singletonList(childCategory);
        PromptCategory category =
                PromptCategory.builder().title(title).id(id).parent(parent).disable(disable).children(children).build();

        assertThat(category.getTitle()).isEqualTo(title);
        assertThat(category.getId()).isEqualTo(id);
        assertThat(category.getParent()).isEqualTo(parent);
        assertThat(category.getDisable()).isEqualTo(disable);
        assertThat(category.getChildren()).isNotNull();
        assertThat(category.getChildren()).hasSize(1);
        PromptCategory child = category.getChildren().get(0);
        assertThat(child.getTitle()).isEqualTo("子类别1");
        assertThat(child.getId()).isEqualTo("2");
        assertThat(child.getParent()).isEqualTo(id);
        assertThat(child.getDisable()).isFalse();
    }
}
