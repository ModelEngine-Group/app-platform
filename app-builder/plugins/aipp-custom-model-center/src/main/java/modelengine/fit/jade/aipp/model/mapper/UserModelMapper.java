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
     * @param userId 表示用户标识。
     * @return 用户模型关系列表 {@link List}{@code <}{@link UserModelPo}{@code >}.
     */
    List<UserModelPo> listUserModels(String userId);

    /**
     * 根据用户标识获取默认用户模型关系。
     *
     * @param userId 表示用户标识。
     * @return 默认的用户模型关系。
     */
    UserModelPo getDefault(String userId);

    /**
     * 判断该用户是否已有模型绑定记录。
     *
     * @param userId 用户标识。
     * @return true 表示已有模型，false 表示无记录。
     */
    boolean userHasDefaultModel(String userId);

    /**
     * 插入用户模型绑定关系。
     *
     * @param userModel 用户模型关系对象。
     */
    void addUserModel(UserModelPo userModel);

    /**
     * 根据模型ID删除用户模型绑定关系。
     *
     * @param modelId 模型ID。
     */
    void deleteByModelId(String modelId);

    /**
     * 查找指定用户最新创建的模型记录（按 created_at 降序排序，取第一条）。
     *  @param userId 用户标识。
     */
    UserModelPo findLatestUserModel(String userId);

    /**
     * 将指定用户所有模型记录的 is_default 状态更新，
     * 如果记录的 model_id 等于传入的 modelId，则设置为 1，否则设置为 0。
     *
     * @param userId  用户ID
     * @param modelId 模型ID
     * @return 更新的记录数
     */
    int switchDefaultForUser(@Param("userId") String userId, @Param("modelId") String modelId);

}
