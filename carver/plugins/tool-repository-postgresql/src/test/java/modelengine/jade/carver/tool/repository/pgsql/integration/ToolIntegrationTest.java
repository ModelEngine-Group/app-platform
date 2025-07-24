/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.model.ListResult;
import modelengine.fel.tool.model.entity.ToolIdentifier;
import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.model.transfer.ToolData;
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
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 工具 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-18
 */
@IntegrationTest(scanPackages = "modelengine.jade.carver.tool")
@Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql"})
@DisplayName("Tool 集成测试")
public class ToolIntegrationTest {
    private static final String UNIQUE_NAME_1 = "3d222474-5eeb-4721-a30d-4f6563ddbbfe";
    private static final String UNIQUE_NAME_2 = "b2de879a-97af-4e94-b0ea-098f00ad5ae4";
    private static final String DEFINITION_GROUP_NAME1 = "automatic-speech-recognition-definition-group-name";
    private static final String GROUP_NAME1 = "automatic-speech-recognition-group-name";

    @Fit
    private ToolService toolService;

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
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql"})
    @DisplayName("测试插入工具")
    void shouldOkWhenAddTool() {
        this.mockDefinitionInfo();
        ToolData toolData = ToolDataBuilder.mockToolData();
        String uniqueName = this.toolService.addTool(toolData);
        assertThat(uniqueName).isEqualTo("uniqueName");

        ToolDo toolDo = this.toolMapper.getToolByUniqueName(uniqueName);
        assertThat(toolDo.getVersion()).isEqualTo("version");
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试插入工具重复时，报错")
    void shouldExWhenAddToolRepeat() {
        assertThatThrownBy(() -> {
            this.mockDefinitionInfo();
            ToolData toolData = ToolDataBuilder.mockToolData();
            toolData.setUniqueName("b2de879a-97af-4e94-b0ea-098f00ad5ae4");
            toolData.setVersion("1.0.0");
            this.toolService.addTool(toolData);
        }).isInstanceOf(Exception.class)
                .hasMessageContaining(
                        "Error updating database.  Cause: org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException");
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql"})
    @DisplayName("测试插入工具列表")
    void shouldOkWhenAddTools() {
        this.mockDefinitionInfo();
        ToolData toolData = ToolDataBuilder.mockToolData();
        this.toolService.addTools(Arrays.asList(toolData));

        ToolDo toolDo = this.toolMapper.getToolByUniqueName(toolData.getUniqueName());
        assertThat(toolDo.getVersion()).isEqualTo("version");
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql"})
    @DisplayName("测试插入工具列表")
    void shouldReturnDoWhenAddAndGetByGroupAndName() {
        this.mockDefinitionInfo();
        ToolData toolData = ToolDataBuilder.mockToolData();
        this.toolService.addTools(toolData.getDefGroupName(), toolData.getGroupName(), Arrays.asList(toolData));

        ToolDo toolDo = this.toolMapper.getToolByUniqueName(toolData.getUniqueName());
        assertThat(toolDo.getVersion()).isEqualTo("version");
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试根据唯一标识删除工具")
    void shouldOkWhenDeleteByUniqueName() {
        ToolData oriTool = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(oriTool).isNotNull();
        this.toolService.deleteTool(UNIQUE_NAME_1);
        ToolData nowTool = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(nowTool).isNull();
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试根据唯一标识删除工具列表")
    void shouldOkWhenDeleteByUniqueNameList() {
        ToolData oriTool1 = this.toolService.getTool(UNIQUE_NAME_1);
        ToolData oriTool2 = this.toolService.getTool(UNIQUE_NAME_2);
        assertThat(oriTool1).isNotNull();
        assertThat(oriTool2).isNotNull();
        this.toolService.deleteTools(Arrays.asList(UNIQUE_NAME_1, UNIQUE_NAME_2));
        ToolData nowTool1 = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(nowTool1).isNull();
        ToolData nowTool2 = this.toolService.getTool(UNIQUE_NAME_2);
        assertThat(nowTool2).isNull();
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试根据唯一标识和版本删除工具")
    void shouldOkWhenDeleteByUniqueNameVersion() {
        ToolData oriTool1 = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(oriTool1).isNotNull();
        this.toolService.deleteToolByVersion(UNIQUE_NAME_1, "1.0.0");
        ToolData nowTool1 = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(nowTool1).isNotNull();
        this.toolService.deleteToolByVersion(UNIQUE_NAME_1, "2.0.0");
        ToolData nowTool2 = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(nowTool2).isNull();
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具通过唯一标识")
    void shouldOkWhenGetTool() {
        ToolData oriTool1 = this.toolService.getTool(UNIQUE_NAME_1);
        assertThat(oriTool1.getLatest()).isEqualTo(true);
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具通过唯一标识和版本")
    void shouldOkWhenGetToolByVersion() {
        ToolData oriTool1 = this.toolService.getToolByVersion(UNIQUE_NAME_1, "1.0.0");
        assertThat(oriTool1.getLatest()).isEqualTo(false);
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具的所有版本")
    void shouldOkWhenGetToolAllVersions() {
        ListResult<ToolData> toolDataList = this.toolService.getAllToolVersions(UNIQUE_NAME_1);
        assertThat(toolDataList.getCount()).isEqualTo(2);
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具通过组合标识")
    void shouldOkWhenGetToolByIdentifier() {
        ToolIdentifier toolIdentifier1 = new ToolIdentifier(UNIQUE_NAME_1, "1.0.0");
        ToolIdentifier toolIdentifier2 = new ToolIdentifier(UNIQUE_NAME_2, "1.0.0");
        ListResult<ToolData> toolDataList =
                this.toolService.getToolsByIdentifier(Arrays.asList(toolIdentifier1, toolIdentifier2));
        assertThat(toolDataList.getCount()).isEqualTo(2);
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试按照实现组删除后无法查询到")
    void shouldReturnNullWhenGetToolByDefGroupNameAndGroupName() {
        List<ToolData> toolDataList = this.toolService.getTools(DEFINITION_GROUP_NAME1, GROUP_NAME1);
        this.toolService.deleteTools(DEFINITION_GROUP_NAME1, GROUP_NAME1);
        List<ToolData> toolDataListAfterDelete = this.toolService.getTools(DEFINITION_GROUP_NAME1, GROUP_NAME1);

        assertThat(toolDataList.size()).isEqualTo(2);
        assertThat(toolDataListAfterDelete).isNullOrEmpty();
    }

    @Test
    @Sql(before = {"sql/create/tool.sql", "sql/create/definition.sql", "sql/insert/tool.sql"})
    @DisplayName("测试按照定义组删除后无法查询到")
    void shouldReturnNullWhenGetToolByDefGroupName() {
        List<ToolData> toolDataList = this.toolService.getTools(DEFINITION_GROUP_NAME1);
        this.toolService.deleteToolsByDefinitionGroupName(DEFINITION_GROUP_NAME1);
        List<ToolData> toolDataListAfterDelete = this.toolService.getTools(DEFINITION_GROUP_NAME1);

        assertThat(toolDataList.size()).isEqualTo(2);
        assertThat(toolDataListAfterDelete).isNullOrEmpty();
    }
}
