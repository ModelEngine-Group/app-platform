/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.store.entity.query.ModelQuery;
import modelengine.jade.store.entity.transfer.ModelData;
import modelengine.jade.store.service.HuggingFaceModelService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 模型 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-14
 */
@IntegrationTest(scanPackages = "modelengine.jade.store")
@Sql(before = "sql/create/model.sql")
@DisplayName("Model 集成测试")
public class ModelIntegrationTest {
    @Fit
    private HuggingFaceModelService huggingFaceModelService;

    @Test
    @Sql(before = {"sql/create/model.sql", "sql/insert/model.sql"})
    @DisplayName("测试查询模型列表")
    void shouldOkWhenGetModels() {
        ModelQuery modelQuery = new ModelQuery("fill-mask", null, null);
        List<ModelData> models = this.huggingFaceModelService.getModels(modelQuery);
        int count = this.huggingFaceModelService.getCount(modelQuery.getTaskName());
        assertThat(models.size()).isEqualTo(3);
        assertThat(count).isEqualTo(3);
    }
}
