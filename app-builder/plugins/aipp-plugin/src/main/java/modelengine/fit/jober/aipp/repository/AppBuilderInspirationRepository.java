/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.condition.InspirationQueryCondition;
import modelengine.fit.jober.aipp.po.InspirationPo;

import java.util.List;
import java.util.Optional;

/**
 * 用户自定义灵感相关数据库操作对象
 *
 * @author 陈潇文
 * @since 2024-10-19
 */
public interface AppBuilderInspirationRepository {
    /**
     * 查找“我的”类目id
     *
     * @param aippId 所属aipp id
     * @param parentId 父类目id
     * @param user 创建者
     * @return 类目id
     */
    Optional<String> findCustomCategoryId(String aippId, String parentId, String user);

    /**
     * 添加一条灵感
     *
     * @param inspirationPo Inspiration数据对象
     */
    void addCustomInspiration(InspirationPo inspirationPo);

    /**
     * 更新一条灵感
     *
     * @param inspirationId 灵感id
     * @param inspirationPo Inspiration数据对象
     */
    void updateCustomInspiration(String inspirationId, InspirationPo inspirationPo);

    /**
     * 删除一条灵感
     *
     * @param aippId 所属aipp id
     * @param categoryId 类目id
     * @param inspirationId 灵感id
     * @param createUser 创建者
     */
    void deleteCustomInspiration(String aippId, String categoryId, String inspirationId, String createUser);

    /**
     * 批量查询满足条件的灵感
     *
     * @param condition 查询条件
     * @return 灵感集合
     */
    List<InspirationPo> selectWithCondition(InspirationQueryCondition condition);
}
