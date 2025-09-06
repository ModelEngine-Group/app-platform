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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.eval.service.EvalListDataService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalListDataServiceImpl} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-08-26
 */
@FitTestWithJunit(includeClasses = EvalListDataServiceImpl.class)
public class EvalListDataServiceImplTest {
    @Fit
    private EvalListDataService evalListDataService;

    @Mock
    private EvalDataMapper evalDataMapper;

    @BeforeEach
    void setUp() {
        doNothing().when(this.evalDataMapper).insertAll(anyList());
        when(this.evalDataMapper.updateExpiredVersion(anyList(), anyLong(), any(), any())).thenReturn(1);
    }

    @Test
    @DisplayName("查询数据成功")
    void shouldOkWhenListEvalData() {
        EvalDataEntity entity = new EvalDataEntity();
        entity.setId(1L);
        entity.setContent("abcd");
        List<EvalDataEntity> entities = Collections.singletonList(entity);

        when(this.evalDataMapper.listEvalData(any())).thenReturn(entities);
        when(this.evalDataMapper.countEvalData(any())).thenReturn(1);

        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(3L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(2);

        PageVo<EvalDataEntity> response = this.evalListDataService.listEvalData(queryParam);
        assertThat(response).extracting(PageVo::getTotal,
                res -> res.getItems().size(),
                res -> res.getItems().get(0).getContent()).containsExactly(1, 1, "abcd");
    }
}
