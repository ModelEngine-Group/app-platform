/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fel.tool.service.ToolChangedObserver;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.carver.tool.repository.pgsql.ToolDataBuilder;
import modelengine.jade.carver.tool.repository.pgsql.mapper.DefinitionMapper;
import modelengine.jade.carver.tool.repository.pgsql.mapper.ToolMapper;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.DefinitionDo;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.ToolDo;
import modelengine.jade.store.service.DefinitionService;
import modelengine.jade.store.service.ToolGroupService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 添加工具组的继承测试。
 *
 * @author 王攀博
 * @since 2024-10-31
 */
@IntegrationTest(scanPackages = "modelengine.jade.carver.tool")
@Sql(before = {
        "sql/create/tool.sql", "sql/create/definition.sql", "sql/create/tool-group.sql",
        "sql/create/definition-group.sql"
})
@DisplayName("Tool group 集成测试")
public class ToolGroupIntegrationTest {
    private static final String UNIQUE_NAME_1 = "3d222474-5eeb-4721-a30d-4f6563ddbbfe";
    private static final String UNIQUE_NAME_2 = "b2de879a-97af-4e94-b0ea-098f00ad5ae4";
    private static final String DEFINITION_GROUP_NAME1 = "automatic-speech-recognition-definition-group-name";
    private static final String GROUP_NAME1 = "automatic-speech-recognition-group-name";

    @Fit
    private ToolGroupService toolGroupService;

    @Spy
    private ToolMapper toolMapper;

    @Fit
    private DefinitionService definitionService;

    @Spy
    private DefinitionMapper definitionMapper;

    @Fit(alias = "json")
    private ObjectSerializer serializer;

    @Mock
    private ToolChangedObserver toolChangedObserver;

    private void mockDefinitionInfo() {
        DefinitionData definitionData = ToolDataBuilder.mockDefinitionData();
        this.definitionService.delete(definitionData.getGroupName(), definitionData.getName());

        String definitionId = this.definitionService.add(definitionData);
        DefinitionDo definitionDo =
                this.definitionMapper.getByName(definitionData.getGroupName(), definitionData.getName());
        definitionDo.setSchema(this.serializer.serialize(definitionData.getSchema()));
        when(this.definitionMapper.getByName(any(), any())).thenReturn(definitionDo);
        assertThat(definitionId).isNotNull();
        assertThat(definitionDo).isNotNull();
        assertThat(definitionDo.getName()).isEqualTo(definitionData.getName());
        assertThat(definitionDo.getDefinitionGroupName()).isEqualTo(definitionData.getGroupName());
    }

    @Test
    @Sql(before = {
            "sql/create/tool.sql", "sql/create/definition.sql", "sql/create/tool-group.sql",
            "sql/create/definition-group.sql"
    })
    @DisplayName("测试插入工具组")
    void shouldOkWhenAddToolGroup() {
        this.mockDefinitionInfo();
        ToolGroupData toolGroupData = ToolDataBuilder.mockToolGroupData();
        this.toolGroupService.add(toolGroupData);

        List<ToolDo> toolDos =
                this.toolMapper.getToolsByGroupName(toolGroupData.getDefGroupName(), toolGroupData.getName());
        assertThat(toolDos.size()).isEqualTo(1);
        assertThat(toolDos.get(0).getDefinitionGroupName()).isEqualTo(toolGroupData.getDefGroupName());
        assertThat(toolDos.get(0).getGroupName()).isEqualTo(toolGroupData.getName());
    }

    @Test
    @Sql(before = {
            "sql/create/tool.sql", "sql/create/definition.sql", "sql/create/tool-group.sql",
            "sql/create/definition-group.sql"
    })
    @DisplayName("测试插入工具组列表")
    void shouldOkWhenAddToolGroupList() {
        this.mockDefinitionInfo();
        ToolGroupData toolGroupData = ToolDataBuilder.mockToolGroupData();
        this.toolGroupService.add(Collections.singletonList(toolGroupData));

        List<ToolDo> toolDos =
                this.toolMapper.getToolsByGroupName(toolGroupData.getDefGroupName(), toolGroupData.getName());
        assertThat(toolDos.size()).isEqualTo(1);
        assertThat(toolDos.get(0).getDefinitionGroupName()).isEqualTo(toolGroupData.getDefGroupName());
        assertThat(toolDos.get(0).getGroupName()).isEqualTo(toolGroupData.getName());
    }

    @Test
    @Sql(before = {
            "sql/create/tool.sql", "sql/create/definition.sql", "sql/create/tool-group.sql",
            "sql/create/definition-group.sql", "sql/insert/tool.sql", "sql/insert/tool-group.sql"
    })
    @DisplayName("测试按工具组删除后，查询不到工具组数据")
    void shouldNullWhenDeleteByGroupName() {
        List<ToolGroupData> toolGroupDataList =
                this.toolGroupService.get(DEFINITION_GROUP_NAME1, Collections.singletonList(GROUP_NAME1));
        this.toolGroupService.delete(DEFINITION_GROUP_NAME1, GROUP_NAME1);
        List<ToolGroupData> toolGroupDataListAfterDelete =
                this.toolGroupService.get(DEFINITION_GROUP_NAME1, Collections.singletonList(GROUP_NAME1));

        assertThat(toolGroupDataList.size()).isEqualTo(1);
        assertThat(toolGroupDataList.get(0).getTools().size()).isEqualTo(2);
        assertThat(toolGroupDataList.get(0).getTools().get(0).getGroupName()).isEqualTo(GROUP_NAME1);

        assertThat(toolGroupDataListAfterDelete).isEmpty();
    }

    @Test
    @Sql(before = {
            "sql/create/tool.sql", "sql/create/definition.sql", "sql/create/tool-group.sql",
            "sql/create/definition-group.sql", "sql/insert/tool.sql", "sql/insert/tool-group.sql"
    })
    @DisplayName("测试按工具组删除后，查询不到工具组数据")
    void shouldNullWhenDeleteByDefinitionGroupName() {
        List<ToolGroupData> toolGroupDataList = this.toolGroupService.get(DEFINITION_GROUP_NAME1);
        this.toolGroupService.deleteByDefinitionGroupName(DEFINITION_GROUP_NAME1);
        List<ToolGroupData> toolGroupDataListAfterDelete = this.toolGroupService.get(DEFINITION_GROUP_NAME1);

        assertThat(toolGroupDataList.size()).isEqualTo(1);
        assertThat(toolGroupDataList.get(0).getTools().size()).isEqualTo(2);
        assertThat(toolGroupDataList.get(0).getTools().get(0).getGroupName()).isEqualTo(GROUP_NAME1);

        assertThat(toolGroupDataListAfterDelete).isNullOrEmpty();
    }
}
