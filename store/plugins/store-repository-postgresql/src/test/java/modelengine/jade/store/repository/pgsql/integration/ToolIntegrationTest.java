/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.model.ListResult;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.store.entity.query.ToolQuery;
import modelengine.jade.store.entity.transfer.StoreToolData;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.StoreToolService;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 表示 工具 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-18
 */
@IntegrationTest(scanPackages = "modelengine.jade.store")
@Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql"})
@DisplayName("Tool 集成测试")
public class ToolIntegrationTest {
    private static final String UNIQUE_NAME = "b2de879a-97af-4e94-b0ea-098f00ad5ae4";
    private static final List<String> DEF_GROUP_LIST =
            Arrays.asList("depth-estimation-definition-group-name", "image-to-image-definition-group-name");

    @Fit
    private StoreToolService storeToolService;

    @Mock
    private ToolService toolService;

    @Mock
    private DefinitionGroupService defGroupService;

    @BeforeEach
    void setup() {
        when(this.toolService.getToolsByIdentifier(anyList())).thenReturn(ListResult.create(Arrays.asList(this.getToolDataResult()),
                1));
        when(this.toolService.getAllToolVersions(any())).thenReturn(ListResult.create(Arrays.asList(this.getToolDataResult()),
                1));
        when(this.toolService.getTool(any())).thenReturn(this.getToolDataResult());
        when(this.toolService.getToolByVersion(any(), any())).thenReturn(this.getToolDataResult());
        when(this.defGroupService.findExistDefGroups(anySet())).thenReturn(DEF_GROUP_LIST);
        when(this.defGroupService.get(any())).thenReturn(new DefinitionGroupData());
    }

    @Test
    @Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql", "sql/insert/tag.sql"})
    @DisplayName("测试查询单个工具")
    void shouldOkWhenGetTool() {
        StoreToolData tool = this.storeToolService.getTool(UNIQUE_NAME);
        assertThat(tool.getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE")));
    }

    @Test
    @Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql", "sql/insert/tag.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具列表")
    void shouldOkWhenGetTools() {
        ToolQuery toolQuery = new ToolQuery();
        toolQuery.setToolName("image-to-image");
        ListResult<StoreToolData> tools = this.storeToolService.getTools(toolQuery);
        assertThat(tools.getData().get(0).getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE")));
    }

    @Test
    @Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql", "sql/insert/tag.sql", "sql/insert/tool.sql"})
    @DisplayName("测试模糊查询工具列表")
    void shouldOkWhenSearchTools() {
        ToolQuery toolQuery = new ToolQuery();
        toolQuery.setToolName("image-to-image");
        ListResult<StoreToolData> tools = this.storeToolService.searchTools(toolQuery);
        assertThat(tools.getData().get(0).getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE")));
    }

    @Test
    @Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql", "sql/insert/tag.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具所有版本")
    void shouldOkWhenGetAllVersions() {
        ToolQuery toolQuery = new ToolQuery();
        toolQuery.setToolName("image-to-image");
        ListResult<StoreToolData> tools = this.storeToolService.getAllToolVersions(toolQuery);
        assertThat(tools.getData().get(0).getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE")));
    }

    @Test
    @Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql", "sql/insert/tag.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询工具通过版本")
    void shouldOkWhenGetByVersion() {
        StoreToolData storeToolData = this.storeToolService.getToolByVersion(UNIQUE_NAME, "1.0.0");
        assertThat(storeToolData.getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE")));
    }

    @Test
    @Sql(before = {"sql/create/tag.sql", "sql/create/tool.sql", "sql/insert/tag.sql", "sql/insert/tool.sql"})
    @DisplayName("测试查询已存在的定义组")
    void shouldOkWhenFindExistDefGroup() {
        ListResult<DefinitionGroupData> existDefGroups = this.storeToolService.findExistDefGroups(DEF_GROUP_LIST);
        assertThat(existDefGroups.getCount()).isEqualTo(2);
    }

    private ToolData getToolDataResult() {
        ToolData toolData = new ToolData();
        toolData.setUniqueName(UNIQUE_NAME);
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        toolData.setSchema(schema);
        toolData.setRunnables(new HashMap<>());
        toolData.setExtensions(new HashMap<>());
        toolData.setVersion("version");
        toolData.setLatest(true);
        return toolData;
    }
}
