/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.mapper;

import modelengine.fit.jade.aipp.model.po.UserModelPo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表示用户模型关系信息持久层接口。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Mapper
public interface UserModelMapper {
    /**
     * 根据用户标识获取用户模型关系列表。
     *
     * @param userId 表示用户标识的 {@link String}。
     * @return 用户模型关系列表的 {@link List}{@code <}{@link UserModelPo}{@code >}。
     */
    List<UserModelPo> listUserModels(String userId);

    /**
     * 根据用户标识获取默认用户模型关系。
     *
     * @param userId 表示用户标识的 {@link String}。
     * @return 默认的用户模型关系的 {@link UserModelPo}。
     */
    UserModelPo getDefault(String userId);

    /**
     * 判断指定用户是否已绑定默认模型。
     *
     * @param userId 表示用户标识的 {@link String}。
     * @return 若已绑定默认模型则返回 {@code true}，否则返回 {@code false}。
     */
    boolean hasDefaultModel(String userId);

    /**
     * 插入用户模型绑定关系。
     *
     * @param userModel 表示待插入的用户模型关系对象的 {@link UserModelPo}。
     */
    void insertUserModel(UserModelPo userModel);

    /**
     * 根据模型标识删除对应的用户模型绑定关系。
     *
     * @param modelId 表示模型标识的 {@link String}。
     */
    void deleteByModelId(String modelId);

    /**
     * 查找指定用户最新创建的模型绑定记录（按创建时间降序，取第一条）。
     *
     * @param userId 表示用户标识的 {@link String}。
     * @return 最新的用户模型关系的 {@link UserModelPo}。
     */
    UserModelPo findLatestUserModel(String userId);

    /**
     * 更新指定用户的所有模型绑定记录的默认状态。
     * <p>若记录中的模型标识与传入的 {@code modelId} 一致，则设置为默认（1），否则设为非默认（0）。</p>
     *
     * @param userId 表示用户标识的 {@link String}。
     * @param modelId 表示需设为默认的模型标识的 {@link String}。
     * @return 成功更新的记录条数的 {@code int}。
     */
    int setDefault(@Param("userId") String userId, @Param("modelId") String modelId);
}
