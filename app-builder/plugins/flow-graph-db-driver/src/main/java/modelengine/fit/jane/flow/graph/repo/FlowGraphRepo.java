/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.repo;

import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;
import modelengine.fitframework.model.RangedResultSet;

import java.util.List;
import java.util.Optional;

/**
 * FlowGraphRepo
 * 流程graph数据Repo
 *
 * @author 孙怡菲
 * @since 2023-10-28
 */
public interface FlowGraphRepo {
    /**
     * save
     *
     * @param flowGraphDefinition flowGraphDefinition
     */
    void save(FlowGraphDefinition flowGraphDefinition);

    /**
     * find
     *
     * @param tenantId tenantId
     * @param flowId flowId
     * @param version version
     * @return Optional<FlowGraphDefinition>
     */
    Optional<FlowGraphDefinition> find(String tenantId, String flowId, String version);

    /**
     * findFlowByFlowIdAndVersion
     *
     * @param flowId flowId
     * @param version version
     * @return FlowGraphDefinition
     */
    FlowGraphDefinition findFlowByFlowIdAndVersion(String flowId, String version);

    /**
     * findFlowsByFlowId
     *
     * @param flowId flowId
     * @return List<FlowGraphDefinition>
     */
    List<FlowGraphDefinition> findFlowsByFlowId(String flowId);

    /**
     * delete
     *
     * @param flowId flowId
     * @param version version
     * @return int
     */
    int delete(String flowId, String version);

    /**
     * findByUserOrTag
     *
     * @param createdBy createdBy
     * @param tag tag
     * @param offset offset
     * @param limit limit
     * @return List<FlowGraphDefinition>
     */
    List<FlowGraphDefinition> findByUserOrTag(String createdBy, String tag, int offset, int limit);

    /**
     * findByFlowIdsOrUser
     *
     * @param flowIds flowIds
     * @param createdBy createdBy
     * @param offset offset
     * @param limit limit
     * @return List<FlowGraphDefinition>
     */
    List<FlowGraphDefinition> findByFlowIdsOrUser(List<String> flowIds, String createdBy, int offset, int limit);

    /**
     * getCount
     *
     * @param flowIds flowIds
     * @param createdBy createdBy
     * @return getCount
     */
    int getCount(List<String> flowIds, String createdBy);

    /**
     * getFlowList
     *
     * @param flowIds flowIds
     * @param createdBy createdBy
     * @param offset offset
     * @param limit limit
     * @return RangedResultSet<FlowGraphDefinition>
     */
    RangedResultSet<FlowGraphDefinition> getFlowList(List<String> flowIds, String createdBy, int offset, int limit);
}
