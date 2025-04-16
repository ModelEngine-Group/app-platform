/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.repository;

import modelengine.fit.jade.aipp.model.po.ModelAccessPo;
import modelengine.fit.jade.aipp.model.po.ModelPo;
import modelengine.fit.jade.aipp.model.po.UserModelPo;

import java.util.List;

/**
 * 表示用户模型信息的持久化层的接口。
 *
 * @author lixin
 * @since 2025/3/11
 */
public interface UserModelRepo {
    /**
     * 根据用户标识来查询该用户可用的模型列表。
     *
     * @param userId 表示用户标识。
     * @return 表示该用户可用的模型列表 {@link List}{@code <}{@link ModelPo}{@code >}。
     */
    List<ModelPo> getModelList(String userId);

    /**
     * 查询特定的模型访问信息。
     *
     * @param userId 表示用户标识。
     * @param tag 表示模型标签。
     * @param name 表示模型名称。
     * @return 模型访问信息 {@link ModelAccessPo}。
     */
    ModelAccessPo getModelAccessInfo(String userId, String tag, String name);

    /**
     * 获取一个用户的默认模型。
     *
     * @param userId 表示用户标识。
     * @return 模型信息 {@link ModelPo}.
     */
    ModelPo getDefaultModel(String userId);

    /**
     * 根据用户标识来查询该用户可用的用户模型列表。
     *
     * @param userId 表示用户标识。
     * @return 表示该用户可用的用户模型列表 {@link List}{@code <}{@link UserModelPo}{@code >}。
     */
    List<UserModelPo> listUserModels(String userId);

    /**
     * 根据模型标识列表批量查询模型信息。
     *
     * @param modelIds 表示模型标识列表。
     * @return 模型信息列表的 {@link List}{@code <}{@link ModelPo}{@code >}。
     */
    List<ModelPo> listModels(List<String> modelIds);

    /**
     * 查询该用户是否存在默认模型。
     *
     * @param userId 表示用户标识。
     * @return 是否存在默认模型的 {@code int}。
     */
    int hasDefaultModel(String userId);

    /**
     * 插入一条模型信息。
     *
     * @param modelPo 表示要插入的模型数据。
     */
    void insertModel(ModelPo modelPo);

    /**
     * 插入一条用户模型关联信息。
     *
     * @param userModelPo 表示要插入的用户模型数据。
     */
    void insertUserModel(UserModelPo userModelPo);

    /**
     * 根据模型标识删除该模型的用户关联信息。
     *
     * @param modelId 表示模型标识。
     */
    void deleteByModelId(String modelId);

    /**
     * 设置某个模型为该用户的默认模型。
     *
     * @param userId 表示用户标识。
     * @param modelId 表示要设置为默认的模型标识。
     * @return 受影响的记录行数的 {@code int}。
     */
    int switchDefaultForUser(String userId, String modelId);

    /**
     * 根据模型标识查询模型信息。
     *
     * @param modelId 表示模型标识。
     * @return 模型信息的 {@link ModelPo}。
     */
    ModelPo getModel(String modelId);
}
