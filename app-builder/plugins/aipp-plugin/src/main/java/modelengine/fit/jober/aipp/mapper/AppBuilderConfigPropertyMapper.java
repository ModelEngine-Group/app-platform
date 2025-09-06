/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.AppBuilderConfigPropertyPo;

import java.util.List;

/**
 * AppBuilder配置属性映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderConfigPropertyMapper {
    /**
     * 通过配置id查询配置属性
     *
     * @param configId 配置id
     * @return AppBuilder配置属性信息集合
     */
    List<AppBuilderConfigPropertyPo> selectWithConfigId(String configId);

    /**
     * 通过配置属性id查询配置属性
     *
     * @param id 配置属性id
     * @return AppBuilder配置属性信息
     */
    AppBuilderConfigPropertyPo selectWithId(String id);

    /**
     * 插入一条配置属性
     *
     * @param insert 要插入的配置属性
     */
    void insertOne(AppBuilderConfigPropertyPo insert);

    /**
     * 插入多条配置属性
     *
     * @param jadeConfigProperties 要插入的配置属性集合
     */
    void insertMore(List<AppBuilderConfigPropertyPo> jadeConfigProperties);

    /**
     * 更新一条配置属性
     *
     * @param update 要被更新的配置属性
     */
    void updateOne(AppBuilderConfigPropertyPo update);

    /**
     * 通过配置属性id删除多条配置属性
     *
     * @param ids 被删除的配置属性的id集合
     * @return 被删除配置属性数量
     */
    int deleteMore(List<String> ids);

    /**
     * 通过配置id删除配置属性
     *
     * @param configIds 被删除的配置属性的配置id
     */
    void deleteByConfigIds(List<String> configIds);
}
