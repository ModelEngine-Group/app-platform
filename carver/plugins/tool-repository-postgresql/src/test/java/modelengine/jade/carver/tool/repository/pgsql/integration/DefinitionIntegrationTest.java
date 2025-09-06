/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.service.ToolChangedObserver;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.carver.tool.repository.pgsql.ToolDataBuilder;
import modelengine.jade.carver.tool.repository.pgsql.mapper.DefinitionMapper;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.DefinitionDo;
import modelengine.jade.store.service.DefinitionService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 定义的继承测试。
 *
 * @author 王攀博
 * @since 2024-10-31
 */
@IntegrationTest(scanPackages = "modelengine.jade.carver.tool")
@Sql(before = {"sql/create/definition.sql", "sql/create/tool.sql"})
@DisplayName("Definition 集成测试")
public class DefinitionIntegrationTest {
    private static final String DEFINITION_NAME1 = "depth-estimation";
    private static final String GROUP_NAME1 = "depth-estimation-definition-group-name";

    @Fit
    private DefinitionService definitionService;

    @Spy
    private DefinitionMapper definitionMapper;

    @Mock
    private ToolChangedObserver toolChangedObserver;

    @Test
    @Sql(before = {"sql/create/definition.sql", "sql/create/tool.sql"})
    @DisplayName("测试插入工具定义")
    void shouldReturnWhenAddDefinitionAndGet() {
        DefinitionData definitionData = ToolDataBuilder.mockDefinitionData();
        this.definitionService.delete(definitionData.getGroupName(), definitionData.getName());

        String definitionId = this.definitionService.add(definitionData);
        DefinitionDo definitionDo =
                this.definitionMapper.getByName(definitionData.getGroupName(), definitionData.getName());

        assertThat(definitionId).isNotNull();
        assertThat(definitionDo).isNotNull();
        assertThat(definitionDo.getName()).isEqualTo(definitionData.getName());
        assertThat(definitionDo.getDefinitionGroupName()).isEqualTo(definitionData.getGroupName());
    }

    @Test
    @Sql(before = {"sql/create/definition.sql", "sql/create/tool.sql"})
    @DisplayName("测试插入工具定义列表")
    void shouldReturnDoWhenAddListAndGet() {
        DefinitionData definitionData = ToolDataBuilder.mockDefinitionData();
        this.definitionService.delete(definitionData.getGroupName(), definitionData.getName());
        this.definitionService.add(Collections.singletonList(definitionData));
        DefinitionDo definitionDoNew =
                this.definitionMapper.getByName(definitionData.getGroupName(), definitionData.getName());

        assertThat(definitionDoNew).isNotNull();
        assertThat(definitionDoNew.getName()).isEqualTo(definitionData.getName());
        assertThat(definitionDoNew.getName()).isEqualTo(definitionData.getName());
        assertThat(definitionDoNew.getDefinitionGroupName()).isEqualTo(definitionData.getGroupName());
    }

    @Test
    @Sql(before = {"sql/create/definition.sql", "sql/create/tool.sql", "sql/insert/definition.sql"})
    @DisplayName("测试根据定义名删除定义")
    void shouldReturnNullWhenDeleteGivenDefinitionName() {
        DefinitionData definitionData = this.definitionService.get(GROUP_NAME1, DEFINITION_NAME1);
        this.definitionService.delete(GROUP_NAME1, DEFINITION_NAME1);
        DefinitionData definitionDataNew = this.definitionService.get(GROUP_NAME1, DEFINITION_NAME1);

        assertThat(definitionData).isNotNull();
        assertThat(definitionDataNew).isNull();
    }

    @Test
    @Sql(before = {"sql/create/definition.sql", "sql/create/tool.sql", "sql/insert/definition.sql"})
    @DisplayName("测试根据定义组名删除定义列表")
    void shouldReturnNullWhenDeleteGivenGroupName() {
        List<DefinitionData> definitionDataList = this.definitionService.get(GROUP_NAME1);
        this.definitionService.delete(GROUP_NAME1);
        List<DefinitionData> definitionDataListNew = this.definitionService.get(GROUP_NAME1);

        assertThat(definitionDataList).isNotEmpty();
        assertThat(definitionDataListNew).isNullOrEmpty();
    }
}
