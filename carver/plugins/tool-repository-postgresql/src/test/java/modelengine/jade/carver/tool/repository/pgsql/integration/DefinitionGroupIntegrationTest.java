/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.integration;

import static modelengine.jade.carver.tool.repository.pgsql.ToolDataBuilder.mockDefinitionGroupData;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.service.ToolChangedObserver;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.carver.tool.repository.pgsql.mapper.DefinitionMapper;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.DefinitionDo;
import modelengine.jade.store.service.DefinitionGroupService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 添加定义组的集成测试。
 *
 * @author 王攀博
 * @since 2024-10-31
 */
@IntegrationTest(scanPackages = "modelengine.jade.carver.tool")
@Sql(before = {"sql/create/definition.sql", "sql/create/definition-group.sql", "sql/create/tool.sql"})
@DisplayName("Definition group 集成测试")
public class DefinitionGroupIntegrationTest {
    private static final String GROUP_NAME1 = "depth-estimation-definition-group-name";

    @Fit
    private DefinitionGroupService definitionGroupService;

    @Spy
    private DefinitionMapper definitionMapper;

    @Mock
    private ToolChangedObserver toolChangedObserver;

    @Test
    @Sql(before = {"sql/create/definition.sql", "sql/create/definition-group.sql"})
    @DisplayName("测试插入工具定义组")
    void shouldReturnDefinitionGroupWhenAddDefinitionGroupAndGet() {
        DefinitionGroupData definitionGroupData = mockDefinitionGroupData();
        this.definitionGroupService.delete(definitionGroupData.getName());

        String definitionGroupName = this.definitionGroupService.add(definitionGroupData);
        List<DefinitionDo> definitionDos = this.definitionMapper.getByGroup(definitionGroupData.getName());

        assertThat(definitionGroupName).isEqualTo(definitionGroupData.getName());
        assertThat(definitionDos).isNotNull();
        assertThat(definitionDos.size()).isEqualTo(1);
        assertThat(definitionDos.get(0).getDefinitionGroupName()).isEqualTo(definitionGroupData.getName());
    }

    @Test
    @Sql(before = {"sql/create/definition.sql", "sql/create/definition-group.sql"})
    @DisplayName("测试插入工具定义组列表")
    void shouldReturnWhenAddDefinitionGroupAndGet() {
        DefinitionGroupData definitionGroupData = mockDefinitionGroupData();
        this.definitionGroupService.delete(definitionGroupData.getName());

        this.definitionGroupService.add(Collections.singletonList(definitionGroupData));
        List<DefinitionDo> definitionDos = this.definitionMapper.getByGroup(definitionGroupData.getName());

        assertThat(definitionDos).isNotNull();
        assertThat(definitionDos.size()).isEqualTo(1);
        assertThat(definitionDos.get(0).getDefinitionGroupName()).isEqualTo(definitionGroupData.getName());
    }

    @Test
    @Sql(before = {
            "sql/create/definition.sql", "sql/create/definition-group.sql", "sql/insert/definition.sql",
            "sql/insert/definition-group.sql"
    })
    @DisplayName("测试删除定义组后再查询，定义组是否存在")
    void shouldReturnNullWhenDeleteGivenDefinitionName() {
        DefinitionGroupData definitionGroupData = this.definitionGroupService.get(GROUP_NAME1);
        this.definitionGroupService.delete(GROUP_NAME1);
        DefinitionGroupData definitionGroupDataNew = this.definitionGroupService.get(GROUP_NAME1);

        assertThat(definitionGroupData).isNotNull();
        assertThat(definitionGroupDataNew).isNull();
    }

    @Test
    @Sql(before = {
            "sql/create/definition.sql", "sql/create/definition-group.sql", "sql/insert/definition.sql",
            "sql/insert/definition-group.sql"
    })
    @DisplayName("测试删除定义组列表后再查询，定义组是否存在")
    void shouldReturnNullWhenDeleteGivenGroupName() {
        // given
        Set<String> groupNames = new HashSet<>();
        groupNames.add(GROUP_NAME1);

        // when
        DefinitionGroupData definitionGroupData = this.definitionGroupService.get(GROUP_NAME1);
        String firstExistDefGroup = this.definitionGroupService.findFirstExistDefGroup(groupNames);
        this.definitionGroupService.delete(Collections.singletonList(GROUP_NAME1));
        String firstExistDefGroupsAfterDelete = this.definitionGroupService.findFirstExistDefGroup(groupNames);

        // then
        assertThat(definitionGroupData).isNotNull();
        assertThat(firstExistDefGroup.isEmpty()).isFalse();
        assertThat(firstExistDefGroupsAfterDelete.isEmpty()).isTrue();
    }
}
