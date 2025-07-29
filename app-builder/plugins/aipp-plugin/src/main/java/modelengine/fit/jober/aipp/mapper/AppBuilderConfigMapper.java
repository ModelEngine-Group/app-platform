/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.AppBuilderConfigPo;

import java.util.List;

/**
 * AppBuilder配置映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderConfigMapper {
    /**
     * 通过id查询AppBuilder配置
     *
     * @param id 配置id
     * @return AppBuilder配置信息
     */
    AppBuilderConfigPo selectWithId(String id);

    /**
     * 通过id查询AppBuilder配置
     *
     * @param ids 配置id集合
     * @return AppBuilder配置信息
     */
    List<AppBuilderConfigPo> selectWithIds(List<String> ids);

    /**
     * 插入一个配置信息
     *
     * @param insert 要插入配置信息
     */
    void insertOne(AppBuilderConfigPo insert);

    /**
     * 更新一个配置信息
     *
     * @param update 要更新的配置信息
     */
    void updateOne(AppBuilderConfigPo update);

    /**
     * 通过AppId查询配置信息
     *
     * @param appId appId
     * @return AppBuilder配置信息
     */
    AppBuilderConfigPo selectWithAppId(String appId);

    /**
     * 删除配置信息
     *
     * @param ids 被删除的配置id集合
     */
    void delete(List<String> ids);
}
