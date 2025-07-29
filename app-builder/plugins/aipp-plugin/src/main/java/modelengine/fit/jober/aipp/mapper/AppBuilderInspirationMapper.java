/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.condition.InspirationQueryCondition;
import modelengine.fit.jober.aipp.po.InspirationPo;

import java.util.List;
import java.util.Optional;

/**
 * 用户自定义灵感Mapper
 *
 * @author 陈潇文
 * @since 2024-10-19
 */
public interface AppBuilderInspirationMapper {
    /**
     * 批量查询满足条件的灵感
     *
     * @param condition 查询条件
     * @return 灵感集合
     */
    List<InspirationPo> selectWithCondition(InspirationQueryCondition condition);

    /**
     * 查找“我的”类目的id
     *
     * @param aippId 灵感aippid
     * @param parentId 父类目id
     * @param user 创建者
     * @return 类目id
     */
    Optional<String> findCustomCategoryId(String aippId, String parentId, String user);

    /**
     * 插入一条灵感
     *
     * @param inspirationPo Inspiration数据对象
     */
    void insertOne(InspirationPo inspirationPo);

    /**
     * 更新一条灵感
     *
     * @param inspirationId 要修改的灵感id
     * @param inspirationPo 需要修改的Inspiration数据对象
     */
    void updateOne(String inspirationId, InspirationPo inspirationPo);

    /**
     * 删除一条灵感
     *
     * @param aippId 需要删除的灵感aippid
     * @param categoryId 需要删除的灵感类目id
     * @param inspirationId 需要删除的灵感id
     * @param createUser 需要删除的灵感创建者
     */
    void deleteOne(String aippId, String categoryId, String inspirationId, String createUser);
}
