/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;

import java.util.List;

/**
 * AppBuilder配置属性持久化层
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderConfigPropertyRepository {
    /**
     * 通过配置id查询配置属性
     *
     * @param configId 要查询的配置属性的配置id
     * @return 查询到的配置属性信息集合
     */
    List<AppBuilderConfigProperty> selectWithConfigId(String configId);

    /**
     * 通过配置属性id查询配置属性
     *
     * @param id 要查询的配置属性的id
     * @return 查询到的配置属性信息
     */
    AppBuilderConfigProperty selectWithId(String id);

    /**
     * 插入一条配置属性
     *
     * @param appBuilderConfigProperty 要插入的配置属性信息
     */
    void insertOne(AppBuilderConfigProperty appBuilderConfigProperty);

    /**
     * 插入多条配置属性
     *
     * @param appBuilderConfigProperties 要插入的配置属性信息集合
     */
    void insertMore(List<AppBuilderConfigProperty> appBuilderConfigProperties);

    /**
     * 更新一条配置属性
     *
     * @param appBuilderConfigProperty 要更新的配置属性信息
     */
    void updateOne(AppBuilderConfigProperty appBuilderConfigProperty);

    /**
     * 根据配置属性id删除多条配置属性
     *
     * @param ids 被删除的配置属性信息的id集合
     * @return 被删除配置属性的数量
     */
    int deleteMore(List<String> ids);

    /**
     * 通过配置id删除配置属性
     *
     * @param ids 被删除的配置属性的配置id集合
     */
    void deleteByConfigIds(List<String> ids);
}
