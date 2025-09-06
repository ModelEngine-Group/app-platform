/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.service.EvalListDataService;
import modelengine.jade.app.engine.task.exception.EvalTaskException;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示评估数据集节点测试类。
 *
 * @author 兰宇晨
 * @since 2024-08-24
 */
@FitTestWithJunit(includeClasses = EvalDatasetComponent.class)
public class EvalDatasetComponentTest {
    @Fit
    private ObjectSerializer serializer;
    @Mock
    private EvalListDataService evalListDataService;

    private List<Map<String, Object>> buildFlowData(Map<String, Object> businessData) {
        Map<String, Object> flowData = new HashMap<>();
        flowData.put(AippConst.BS_DATA_KEY, businessData);
        List<Map<String, Object>> flowDataList = new ArrayList<>();
        flowDataList.add(flowData);
        return flowDataList;
    }

    private Map<String, Object> genBusinessData() {
        Map<String, Object> businessData = new HashMap<>();
        Map<String, Object> evalDataInfo = new HashMap<>();
        evalDataInfo.put("id", "1");
        evalDataInfo.put("version", "1");
        evalDataInfo.put("evalDatasetQuantity", 100);
        businessData.put("testSet", evalDataInfo);
        return businessData;
    }

    @Test
    @DisplayName("测试数据集节点流转正常")
    void shouldOkWhenUseEvalDatasetComponent() {
        EvalDatasetComponent evalDatasetComponent = new EvalDatasetComponent(this.serializer, this.evalListDataService);

        EvalDataEntity entity1 = new EvalDataEntity();
        String data1 = "{\"age\": 100, \"email\": \"out@qq.com\", \"name\": \"Jerry\"}";
        entity1.setId(1L);
        entity1.setContent(data1);
        Map<String, Object> map1 = this.serializer.deserialize(data1, Map.class);

        EvalDataEntity entity2 = new EvalDataEntity();
        String data2 = "{\"age\": 90, \"email\": \"out@qq.com\", \"name\": \"Jerry\"}";
        entity2.setId(2L);
        entity2.setContent(data2);
        Map<String, Object> map2 = this.serializer.deserialize(data2, Map.class);

        List<EvalDataEntity> entityList = Arrays.asList(entity1, entity2);
        PageVo<EvalDataEntity> pageOfEntity = PageVo.of(2, entityList);

        when(this.evalListDataService.listEvalData(any())).thenReturn(pageOfEntity);

        Map<String, Object> businessData = genBusinessData();
        List<Map<String, Object>> flowData = buildFlowData(businessData);
        List<Map<String, Object>> resultFlowData = evalDatasetComponent.handleTask(flowData);

        assertThat(resultFlowData).hasSize(2);
        assertThat(resultFlowData).extracting(map -> map.get(AippConst.BS_DATA_KEY))
                .extracting("output")
                .containsExactly(map1, map2);
    }

    @Test
    @DisplayName("测试数据集节点流转失败")
    void shouldNotOkWhenMissingParam() {
        EvalDatasetComponent evalDatasetComponent = new EvalDatasetComponent(this.serializer, this.evalListDataService);
        List<Map<String, Object>> flowData = Collections.singletonList(new HashMap<>());
        assertThatThrownBy(() -> evalDatasetComponent.handleTask(flowData)).isInstanceOf(EvalTaskException.class);
    }
}