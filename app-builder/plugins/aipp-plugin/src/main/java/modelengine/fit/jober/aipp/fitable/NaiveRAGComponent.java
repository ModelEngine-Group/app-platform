/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.waterflow.spi.FlowableService;
import modelengine.jade.app.engine.knowledge.dto.KbVectorSearchDto;
import modelengine.jade.app.engine.knowledge.service.KnowledgeBaseService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * naive RAG 组件实现
 *
 * @author 黄夏露
 * @since 2024-04-15
 */
@Component
public class NaiveRAGComponent implements FlowableService {
    private static final Logger log = Logger.get(NaiveRAGComponent.class);

    private final KnowledgeBaseService knowledgeBaseService;

    public NaiveRAGComponent(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * 根据知识库生成检索结果的实现
     *
     * @param flowData 流程执行上下文数据，包含用户选中的知识库列表
     * @return 流程执行上下文数据，包含生成的检索结果
     */
    @Fitable("modelengine.fit.jober.aipp.fitable.NaiveRAGComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("NaiveRAGComponent business data {}", businessData);
        String ragOutput = StringUtils.EMPTY;
        List<Map<String, Object>> knowledgeList = ObjectUtils.cast(businessData.get("knowledge"));
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(knowledgeList)) {
            idList = knowledgeList.stream()
                    .filter(MapUtils::isNotEmpty)
                    .map(map -> Long.valueOf(ObjectUtils.cast(map.get("id"))))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(idList)) {
            KbVectorSearchDto kbVectorSearchCondition = this.buildKbVectorSearchDto(idList, businessData);
            List<String> chunksList = this.knowledgeBaseService.vectorSearchKnowledgeTable(kbVectorSearchCondition);
            ragOutput = String.join(AippConst.CONTENT_DELIMITER, chunksList);
        }
        Map<String, Object> output = new HashMap<>();
        output.put("retrievalOutput", ragOutput);
        businessData.put("output", output);
        return flowData;
    }

    private KbVectorSearchDto buildKbVectorSearchDto(List<Long> tableIdList, Map<String, Object> businessData) {
        KbVectorSearchDto kbVectorSearchCondition = new KbVectorSearchDto();
        kbVectorSearchCondition.setTableId(tableIdList);
        kbVectorSearchCondition.setContent(ObjectUtils.cast(businessData.get("query")));
        kbVectorSearchCondition.setTopK(ObjectUtils.cast(businessData.get("maximum")));
        kbVectorSearchCondition.setThreshold(0.5F);
        return kbVectorSearchCondition;
    }
}
