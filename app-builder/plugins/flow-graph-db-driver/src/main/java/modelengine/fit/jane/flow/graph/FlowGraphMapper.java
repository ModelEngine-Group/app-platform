/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph;

import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * FlowGraphMapper
 * 流程graph数据MybatisMapper类
 *
 * @author 孙怡菲
 * @since 2023-10-28
 */
@Mapper
public interface FlowGraphMapper {
    /**
     * create
     *
     * @param flowGraphDefinition flowGraphDefinition
     */
    void create(@Param("flowData") FlowGraphDefinition flowGraphDefinition);

    /**
     * find
     *
     * @param tenantId tenantId
     * @param id id
     * @param version version
     * @return Optional<FlowGraphDefinition>
     */
    Optional<FlowGraphDefinition> find(@Param("tenant") String tenantId, @Param("id") String id,
            @Param("version") String version);

    /**
     * update
     *
     * @param id id
     * @param version version
     * @param tenantId tenantId
     * @param updateData updateData
     */
    void update(@Param("id") String id, @Param("version") String version, @Param("tenant") String tenantId,
            @Param("updateData") FlowGraphDefinition updateData);

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
    int delete(@Param("flowId") String flowId, @Param("version") String version);

    /**
     * findByUserOrTag
     *
     * @param createdBy createdBy
     * @param tags tags
     * @param offset offset
     * @param limit limit
     * @return List<FlowGraphDefinition>
     */
    List<FlowGraphDefinition> findByUserOrTag(@Param("createdBy") String createdBy, @Param("tags") String tags,
            @Param("offset") int offset, @Param("limit") int limit);

    /**
     * getCount
     *
     * @param flowIds flowIds
     * @param createdBy createdBy
     * @return int
     */
    int getCount(@Param("flowIds") List<String> flowIds, @Param("createdBy") String createdBy);

    /**
     * findByFlowIdsOrUser
     *
     * @param flowIds flowIds
     * @param createdBy createdBy
     * @param offset offset
     * @param limit limit
     * @return List<FlowGraphDefinition>
     */
    List<FlowGraphDefinition> findByFlowIdsOrUser(@Param("flowIds") List<String> flowIds,
            @Param("createdBy") String createdBy, @Param("offset") int offset, @Param("limit") int limit);
}
