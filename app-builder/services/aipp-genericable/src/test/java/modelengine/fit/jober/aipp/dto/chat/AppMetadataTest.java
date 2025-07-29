/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link AppMetadata} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 AppMetadata")
class AppMetadataTest {
    @Test
    @DisplayName("用构建器构建应用元数据类时，返回成功")
    void constructAppMetadata() {
        String name = "MyApp";
        String type = "Web";
        String createBy = "Admin";
        String updateBy = "Admin";
        String version = "1.0.0";
        LocalDateTime createAt = LocalDateTime.now();
        LocalDateTime updateAt = LocalDateTime.now();
        String id = "123456";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        List<String> tags = Arrays.asList("tag1", "tag2");

        AppMetadata adapter = AppMetadata.builder()
                .name(name)
                .type(type)
                .createBy(createBy)
                .updateBy(updateBy)
                .version(version)
                .createAt(createAt)
                .updateAt(updateAt)
                .id(id)
                .attributes(attributes)
                .state("Active")
                .tags(tags)
                .build();

        assertThat(adapter.getName()).isEqualTo(name);
        assertThat(adapter.getType()).isEqualTo(type);
        assertThat(adapter.getCreateBy()).isEqualTo(createBy);
        assertThat(adapter.getUpdateBy()).isEqualTo(updateBy);
        assertThat(adapter.getVersion()).isEqualTo(version);
        assertThat(adapter.getCreateAt()).isEqualTo(createAt);
        assertThat(adapter.getUpdateAt()).isEqualTo(updateAt);
        assertThat(adapter.getId()).isEqualTo(id);
        assertThat(adapter.getAttributes()).isEqualTo(attributes);
        assertThat(adapter.getState()).isEqualTo("Active");
    }
}
