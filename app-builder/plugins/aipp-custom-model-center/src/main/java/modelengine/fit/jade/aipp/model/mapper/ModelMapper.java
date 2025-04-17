/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.mapper;

import modelengine.fit.jade.aipp.model.po.ModelPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示模型信息持久层接口。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Mapper
public interface ModelMapper {
    /**
     * 根据模型标识查询模型信息。
     *
     * @param modelId 表示模型标识的 {@link String}。
     * @return 模型信息的 {@link ModelPo}。
     */
    ModelPo getModel(String modelId);

    /**
     * 根据模型标识列表批量查询模型信息。
     *
     * @param modelIds 表示模型标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 模型信息列表的 {@link List}{@code <}{@link ModelPo}{@code >}。
     */
    List<ModelPo> listModels(List<String> modelIds);

    /**
     * 插入模型信息。
     *
     * @param model 表示待插入的模型信息的 {@link ModelPo}。
     */
    void insertModel(ModelPo model);

    /**
     * 根据模型标识删除模型信息。
     *
     * @param modelId 表示待删除模型标识的 {@link String}。
     */
    void deleteByModelId(String modelId);
}
