/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.repo;

import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;

import modelengine.fit.jane.flow.graph.FlowGraphMapper;
import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * DefaultFlowGraphRepo
 * {@link FlowGraphRepo} 的默认实现
 *
 * @author 孙怡菲
 * @since 2023-10-28
 */
@Component
public class DefaultFlowGraphRepo implements FlowGraphRepo {
    private static final Logger log = Logger.get(DefaultFlowGraphRepo.class);

    private final FlowGraphMapper flowGraphMapper;

    public DefaultFlowGraphRepo(FlowGraphMapper flowGraphMapper) {
        this.flowGraphMapper = flowGraphMapper;
    }

    @Override
    public void save(FlowGraphDefinition flowGraphDefinition) {
        String tenantId = flowGraphDefinition.getTenant();
        String id = flowGraphDefinition.getFlowId();
        String version = flowGraphDefinition.getVersion();
        Optional<FlowGraphDefinition> findOne = flowGraphMapper.find(tenantId, id, version);
        if (!findOne.isPresent()) {
            flowGraphMapper.create(flowGraphDefinition);
        } else {
            if (!StringUtils.equals(findOne.get().getStatus(), "unpublished")) {
                log.error("graph data with id: {} version: {} has been published, can not be modified.", id, version);
                throw new JobberException(ErrorCodes.FLOW_MODIFY_PUBLISHED_GRAPH, id, version);
            }
            flowGraphMapper.update(id, version, tenantId, flowGraphDefinition);
        }
    }

    @Override
    public Optional<FlowGraphDefinition> find(String tenantId, String flowId, String version) {
        return flowGraphMapper.find(tenantId, flowId, version);
    }

    /**
     * 查询流程定义详情
     *
     * @param flowId 流程定义id
     * @param version 流程版本
     * @return 流程定义详情
     */
    @Override
    public FlowGraphDefinition findFlowByFlowIdAndVersion(String flowId, String version) {
        return flowGraphMapper.findFlowByFlowIdAndVersion(flowId, version);
    }

    /**
     * 根据flow_id去获取流程定义版本列表
     *
     * @param flowId 流程定义id
     * @return 版本列表
     */
    @Override
    public List<FlowGraphDefinition> findFlowsByFlowId(String flowId) {
        return flowGraphMapper.findFlowsByFlowId(flowId);
    }

    @Override
    public int delete(String flowId, String version) {
        Validation.notNull(flowId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "flowId"));
        Validation.notNull(version, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "version"));
        return flowGraphMapper.delete(flowId, version);
    }

    @Override
    public List<FlowGraphDefinition> findByUserOrTag(String createdBy, String tag, int offset, int limit) {
        return flowGraphMapper.findByUserOrTag(createdBy, tag, offset, limit);
    }

    @Override
    public int getCount(List<String> flowIds, String createdBy) {
        return flowGraphMapper.getCount(flowIds, createdBy);
    }

    @Override
    public List<FlowGraphDefinition> findByFlowIdsOrUser(List<String> flowIds, String createdBy, int offset,
            int limit) {
        return flowGraphMapper.findByFlowIdsOrUser(flowIds, createdBy, offset, limit);
    }

    @Override
    public RangedResultSet<FlowGraphDefinition> getFlowList(List<String> flowIds, String createdBy, int offset,
            int limit) {
        List<FlowGraphDefinition> flowGraphList = flowGraphMapper.findByFlowIdsOrUser(flowIds, createdBy, offset,
                limit);
        int total = flowGraphMapper.getCount(flowIds, createdBy);
        return RangedResultSet.create(flowGraphList, offset, limit, total);
    }
}
