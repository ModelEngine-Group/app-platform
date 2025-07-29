/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import static modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum.RUNNING;
import static modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.po.EvalInstancePo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link EvalInstanceMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@MybatisTest(classes = {EvalInstanceMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskInstanceMapper")
public class EvalInstanceMapperTest {
    @Fit
    private EvalInstanceMapper mapper;

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("插入评估任务实例后，回填主键成功")
    void shouldOkWhenCreateEvalInstance() {
        EvalInstancePo po = new EvalInstancePo();
        po.setTaskId(1L);
        po.setTraceId("ID");
        this.mapper.create(po);
        assertThat(po.getTaskId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("根据工作流实例唯一标识查询评估任务实例成功")
    void shouldOkWhenGetEvalInstanceByTraceId() {
        List<Long> ids = this.mapper.getInstanceId("trace1");
        assertThat(ids).isEqualTo(Arrays.asList(1L, 2L));
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_eval_instance.sql"})
    @DisplayName("查询评估任务实例成功")
    void shouldOkWhenQueryEvalInstance() {
        EvalInstanceQueryParam queryParam = new EvalInstanceQueryParam();
        queryParam.setTaskId(1L);
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);
        List<EvalInstanceEntity> entities = this.mapper.listEvalInstance(queryParam);
        assertThat(entities.size()).isEqualTo(2);
        assertThat(entities).extracting(EvalInstanceEntity::getStatus,
                        EvalInstanceEntity::getPassRate,
                        EvalInstanceEntity::getCreatedBy)
                .containsExactly(tuple(RUNNING, 76.0, "sky"), tuple(SUCCESS, 95.0, "fang"));
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_eval_instance.sql"})
    @DisplayName("统计评估任务实例成功")
    void shouldOkWhenCountEvalInstance() {
        EvalInstanceQueryParam queryParam = new EvalInstanceQueryParam();
        queryParam.setTaskId(1L);
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);
        int count = this.mapper.countEvalInstance(queryParam);
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_eval_instance.sql"})
    @DisplayName("修改评估任务实例成功")
    void shouldOKWhenUpdateEvalDatasetDescAndName() {
        EvalInstanceQueryParam queryParam = new EvalInstanceQueryParam();
        queryParam.setTaskId(1L);
        queryParam.setPageSize(1);
        queryParam.setPageIndex(1);
        List<EvalInstanceEntity> entities = this.mapper.listEvalInstance(queryParam);
        assertThat(entities.size()).isEqualTo(1);
        assertThat(entities.get(0)).extracting(EvalInstanceEntity::getStatus,
                EvalInstanceEntity::getPassRate,
                EvalInstanceEntity::getCreatedBy).containsExactly(RUNNING, 76.0, "sky");

        LocalDateTime time = LocalDateTime.now();
        EvalInstancePo po = new EvalInstancePo();
        po.setId(1L);
        po.setFinishedAt(time);
        po.setPassRate(100.0);
        po.setStatus(SUCCESS);
        this.mapper.update(po);

        entities = this.mapper.listEvalInstance(queryParam);
        assertThat(entities.size()).isEqualTo(1);
        assertThat(entities.get(0)).extracting(EvalInstanceEntity::getStatus,
                EvalInstanceEntity::getPassRate,
                EvalInstanceEntity::getFinishedAt).containsExactly(SUCCESS, 100.0, time);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("查询评估任务最新实例")
    void shouldOkWhenQueryLatestEvalInstance() {
        List<EvalInstanceEntity> entities = this.mapper.findLatestInstances(Arrays.asList(1L, 2L));
        assertThat(entities.size()).isEqualTo(2);
        assertThat(entities).extracting(EvalInstanceEntity::getStatus,
                EvalInstanceEntity::getPassRate,
                EvalInstanceEntity::getPassCount).containsExactly(tuple(SUCCESS, 95.0, 10), tuple(RUNNING, 0.0, 0));
    }
}