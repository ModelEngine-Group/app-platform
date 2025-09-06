/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.entity.EvalDatasetEntity;
import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDatasetMapper;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.jade.app.engine.eval.service.EvalDatasetService;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDatasetServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@FitTestWithJunit(includeClasses = EvalDatasetServiceImpl.class)
public class EvalDatasetServiceImplTest {
    private static final List<String> TEST_CONTENTS = Arrays.asList("test1", "test2");

    @Fit
    private EvalDatasetService evalDatasetService;

    @Mock
    private EvalDatasetMapper evalDatasetMapper;

    @Mock
    private EvalDataService evalDataService;

    @Mock
    private EvalDatasetVersionManager versionManager;

    @AfterEach
    void teardown() {
        clearInvocations(this.evalDatasetMapper, this.evalDataService);
    }

    @Test
    @DisplayName("创建评估数据集成功")
    void shouldOkWhenInsertAll() {
        doNothing().when(this.evalDataService).insertAll(anyLong(), anyList());
        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setName("ds1");
        entity.setDescription("test dataset");
        entity.setContents(TEST_CONTENTS);
        entity.setSchema("{}");
        entity.setAppId("1");
        this.evalDatasetService.create(entity);
        verify(this.evalDatasetMapper, times(1)).create(any());
    }

    @Test
    @DisplayName("删除评估数据集成功")
    void shouldOkWhenDelete() {
        doNothing().when(this.evalDataService).hardDelete(anyList());
        this.evalDatasetService.delete(Arrays.asList(1L, 2L));
        verify(this.evalDatasetMapper, times(1)).delete(any());
    }

    @Test
    @DisplayName("查询全量评估数据集元数据成功")
    void shouldOkWhenListEvalDataset() {
        Long id = 1L;
        String name = "name";
        String desc = "desc";

        EvalDatasetVo vo = new EvalDatasetVo();
        vo.setId(id);
        vo.setName(name);
        vo.setDescription(desc);
        List<EvalDatasetVo> vos = Collections.singletonList(vo);

        when(this.evalDatasetMapper.listEvalDataset(any())).thenReturn(vos);
        when(this.evalDatasetMapper.countEvalDataset(any())).thenReturn(1);

        EvalDatasetQueryParam queryParam = new EvalDatasetQueryParam();
        PageVo<EvalDatasetVo> response = this.evalDatasetService.listEvalDataset(queryParam);

        EvalDatasetVo firstVo = response.getItems().get(0);
        assertThat(response).extracting(PageVo::getTotal, r -> r.getItems().size()).containsExactly(1, 1);
        assertThat(firstVo).extracting(EvalDatasetVo::getId, EvalDatasetVo::getName, EvalDatasetVo::getDescription)
                .containsExactly(id, name, desc);
    }

    @Test
    @DisplayName("根据数据集唯一标识查询评估数据集元数据成功")
    void shouldOkWhenGetEvalDatasetById() {
        Long id = 1L;
        String name = "name";
        String desc = "desc";
        String schema = "schema";

        EvalVersionEntity version = new EvalVersionEntity();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tempTime = LocalDateTime.parse("2024-10-08 17:16:34", formatter);
        version.setVersion(1L);
        version.setCreatedTime(tempTime);
        List<EvalVersionEntity> versions = Collections.singletonList(version);

        EvalDatasetVo vo = new EvalDatasetVo();
        vo.setId(null);
        vo.setName(name);
        vo.setDescription(desc);
        vo.setSchema(schema);

        when(this.evalDatasetMapper.getEvalDatasetById(id)).thenReturn(vo);
        when(this.versionManager.getLatestVersion(id)).thenReturn(version);
        EvalDatasetVo response = this.evalDatasetService.getEvalDatasetById(id);
        assertThat(response).extracting(EvalDatasetVo::getId,
                EvalDatasetVo::getName,
                EvalDatasetVo::getDescription,
                EvalDatasetVo::getSchema,
                EvalDatasetVo::getVersions).containsExactly(null, name, desc, schema, versions);
    }

    @Test
    @DisplayName("修改数据集信息成功")
    void shouldOkWhenUpdateDataset() {
        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setName("datasetName1");
        entity.setDescription("datasetDesc1");

        this.evalDatasetService.updateEvalDataset(entity);
        verify(this.evalDatasetMapper, times(1)).updateEvaldataset(any());
    }

    @Test
    @DisplayName("修改数据集信息成功")
    void shouldOkWhenUpdateDataset1() {
        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setName("datasetName1");
        entity.setDescription(null);

        this.evalDatasetService.updateEvalDataset(entity);
        verify(this.evalDatasetMapper, times(1)).updateEvaldataset(any());
    }
}