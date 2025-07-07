/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.carver.ListResult;
import modelengine.jade.store.entity.query.AppQuery;
import modelengine.jade.store.entity.transfer.AppPublishData;
import modelengine.jade.store.repository.pgsql.mapper.AppMapper;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 表示 应用 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-19
 */
@IntegrationTest(scanPackages = "modelengine.jade.store")
@Sql(scripts = {"sql/create/app.sql", "sql/create/tag.sql"})
@DisplayName("App 集成测试")
public class AppIntegrationTest {
    @Fit
    private AppService appService;

    @Spy
    private AppMapper appMapper;

    @Mock
    private ToolService toolService;
    @Mock
    private ToolGroupService toolGroupService;
    @Mock
    private DefinitionGroupService defGroupService;

    @Test
    @Sql(scripts = {"sql/insert/app.sql", "sql/insert/tag.sql"})
    @DisplayName("测试添加应用-添加")
    void shouldOkWhenAddAppByAdd() {
        AppQuery appQuery = new AppQuery.Builder().appCategory("chatbot").build();
        assertThat(this.appMapper.getAppsCount(appQuery)).isEqualTo(2);

        AppPublishData appData = this.mockAppPublishData();
        appData.setUniqueName(null);
        doNothing().when(this.defGroupService).add(anyList());
        doNothing().when(this.toolGroupService).add(anyList());

        this.appService.publishApp(appData);
        assertThat(this.appMapper.getAppsCount(appQuery)).isEqualTo(3);
        when(this.toolService.getTool(appData.getUniqueName())).thenReturn(appData);
        AppPublishData appDataSql = this.appService.getApp(appData.getUniqueName());
        assertThat(appDataSql.getTags()).isEqualTo(new HashSet<>(Arrays.asList("MOCKTAG")));
    }

    @Test
    @Sql(scripts = {"sql/insert/app.sql", "sql/insert/tag.sql"})
    @DisplayName("测试添加应用-更新")
    void shouldOkWhenAddAppByUpdate() {
        AppQuery appQuery = new AppQuery.Builder().appCategory("chatbot").build();
        assertThat(this.appMapper.getAppsCount(appQuery)).isEqualTo(2);

        AppPublishData appData = this.mockAppPublishData();
        appData.setUniqueName("uniqueName1");
        appData.setVersion("2.0.0");
        AppPublishData appDataOld = this.mockAppPublishData();
        appDataOld.setUniqueName("uniqueName1");
        appDataOld.setVersion("1.0.0");
        Mockito.when(this.toolService.upgradeTool(appData)).thenReturn("uniqueName1");
        this.appService.publishApp(appData);
        assertThat(this.appMapper.getAppsCount(appQuery)).isEqualTo(2);
    }

    @Test
    @Sql(scripts = {"sql/insert/app.sql", "sql/insert/tag.sql"})
    @DisplayName("测试获取应用")
    void shouldOkWhenGetApp() {
        AppPublishData appData = this.mockAppPublishData();
        appData.setUniqueName("uniqueName1");
        when(this.toolService.getTool(any())).thenReturn(appData);
        AppPublishData app = this.appService.getApp("uniqueName1");
        assertThat(app.getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE", "FIT")));
    }

    @Test
    @Sql(scripts = {"sql/insert/app.sql", "sql/insert/tag.sql"})
    @DisplayName("测试获取应用集合")
    void shouldOkWhenGetApps() {
        AppQuery appQuery = new AppQuery.Builder().appCategory("chatbot").build();
        when(this.toolService.getTool(any())).thenReturn(this.mockAppPublishData());
        ListResult<AppPublishData> apps = this.appService.getApps(appQuery);
        assertThat(apps.getCount()).isEqualTo(2);
        assertThat(apps.getData().get(1).getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE", "FIT")));
    }

    @Test
    @Sql(scripts = "sql/insert/app.sql")
    @DisplayName("测试删除应用")
    void shouldOkWhenDeleteApp() {
        AppQuery appQuery = new AppQuery.Builder().appCategory("chatbot").build();
        assertThat(this.appMapper.getAppsCount(appQuery)).isEqualTo(2);

        when(this.toolService.deleteTool(any())).thenReturn(null);
        when(this.toolService.getTool(any())).thenReturn(this.mockAppPublishData());
        String deleteName = this.appService.deleteApp("uniqueName1");
        assertThat(deleteName).isEqualTo("uniqueName1");
        assertThat(this.appMapper.getAppsCount(appQuery)).isEqualTo(1);
    }

    private AppPublishData mockAppPublishData() {
        AppPublishData appData = new AppPublishData();
        appData.setModifier("mockModifier");
        appData.setCreator("mockCreator");
        appData.setSource("mockSource");
        appData.setIcon("mockIcon");
        appData.setDownloadCount(100);
        appData.setLikeCount(200);
        appData.setTags(new HashSet<>(Arrays.asList("mockTag")));

        appData.setName("mockName");
        appData.setUniqueName("mockUniqueName");
        appData.setDescription("mockDescription");
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        appData.setSchema(schema);
        appData.setRunnables(new HashMap<>());
        appData.setExtensions(new HashMap<>());
        appData.setVersion("1.0.0");
        appData.setLatest(true);
        return appData;
    }
}
