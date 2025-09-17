/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.serializer.impl.AppBuilderAppSerializer;
import modelengine.fit.jober.aipp.util.CacheUtils;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用创建仓库实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
public class AppBuilderAppRepositoryImpl implements AppBuilderAppRepository {
    private final AppBuilderAppMapper appBuilderAppMapper;
    private final AppBuilderAppSerializer serializer;

    public AppBuilderAppRepositoryImpl(AppBuilderAppMapper appBuilderAppMapper, IconConverter iconConverter) {
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.serializer = new AppBuilderAppSerializer(iconConverter);
    }

    @Override
    public AppBuilderApp selectWithId(String id) {
        // catch里修改为存储app的Po对象
        // 如果存储app，在使用app的时候有可能会对app进行修改，但是缓存存储的对象地址不变，会导致缓存里的app被修改
        // 因此选择存储app的po对象，po对象不对外引用，仅在本类中被转换为app对象
        AppBuilderAppPo appInCache = CacheUtils.APP_CACHE.getIfPresent(id);
        if (appInCache != null) {
            return this.serializer.deserialize(appInCache);
        }
        AppBuilderAppPo poInDb = this.appBuilderAppMapper.selectWithId(id);
        AppBuilderApp appFromDb = this.serializer.deserialize(poInDb);
        if (appFromDb.isPublished()) {
            CacheUtils.APP_CACHE.put(id, poInDb);
        }
        return appFromDb;
    }

    @Override
    public AppBuilderApp selectWithPath(String path) {
        return this.serializer.deserialize(this.appBuilderAppMapper.selectWithPath(path));
    }

    @Override
    public void insertOne(AppBuilderApp appBuilderApp) {
        this.appBuilderAppMapper.insertOne(this.serializer.serialize(appBuilderApp));
    }

    @Override
    public void updateOne(AppBuilderApp appBuilderApp) {
        this.appBuilderAppMapper.updateOne(this.serializer.serialize(appBuilderApp));
    }

    @Override
    public List<AppBuilderApp> selectWithLatestApp(AppQueryCondition cond, String tenantId, long offset, int limit) {
        return this.appBuilderAppMapper.selectByTenantIdWithPage(cond, tenantId, offset, limit)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppBuilderApp> selectWithCondition(AppQueryCondition cond) {
        return this.appBuilderAppMapper.selectWithCondition(cond)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public long countWithLatestApp(String tenantId, AppQueryCondition cond) {
        return this.appBuilderAppMapper.countByTenantId(tenantId, cond);
    }

    @Override
    public void delete(List<String> appIds) {
        this.appBuilderAppMapper.delete(appIds);
    }

    @Override
    public List<String> selectWithSimilarName(String target) {
        return this.appBuilderAppMapper.selectWithSimilarName(target);
    }

    @Override
    public boolean checkPathExists(String path) {
        return this.appBuilderAppMapper.checkPathExists(path);
    }

    @Override
    public void updateGuestMode(String path, Boolean isGuest) {
        this.appBuilderAppMapper.updateGuestMode(path, isGuest);
    }
}
