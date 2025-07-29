/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.repository.impl;

import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.appversion.serializer.AppVersionSerializer;
import modelengine.fit.jober.aipp.enums.AippSortKeyEnum;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用创建仓库实现类
 *
 * @author 张越
 * @since 2025-01-14
 */
@Component
public class AppVersionRepositoryImpl implements AppVersionRepository {
    private final AppBuilderAppMapper mapper;
    private final AppVersionSerializer serializer;

    public AppVersionRepositoryImpl(AppBuilderAppMapper mapper, AppVersionFactory appVersionFactory,
            IconConverter iconConverter) {
        this.mapper = mapper;
        this.serializer = new AppVersionSerializer(appVersionFactory, this, iconConverter);
    }

    @Override
    public Optional<AppVersion> selectById(String id) {
        return Optional.ofNullable(this.mapper.selectWithId(id)).map(this.serializer::deserialize);
    }

    @Override
    public void update(AppVersion appVersion) {
        this.mapper.updateOne(this.serializer.serialize(appVersion));
        Optional.ofNullable(appVersion).ifPresent(AppVersion::processIconPath);
    }

    @Override
    public boolean checkPathExists(String path) {
        return this.mapper.checkPathExists(path);
    }

    @Override
    public List<String> selectWithSimilarName(String appName) {
        return this.mapper.selectWithSimilarName(appName);
    }

    @Override
    public void save(AppVersion appVersion) {
        this.mapper.insertOne(this.serializer.serialize(appVersion));
        Optional.ofNullable(appVersion).ifPresent(AppVersion::processIconPath);
    }

    @Override
    public List<AppVersion> selectByCondition(AppQueryCondition cond) {
        // 校验，同时重新设置sort的值.
        if (StringUtils.isNotBlank(cond.getSort())) {
            cond.setSort(DirectionEnum.getDirection(cond.getSort()).getValue());
        }

        // 校验orderBy.
        if (StringUtils.isNotBlank(cond.getOrderBy())) {
            AippSortKeyEnum.getSortKey(cond.getOrderBy());
        }
        return this.mapper.selectWithCondition(cond)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AppVersion> selectByPath(String path) {
        return Optional.of(this.mapper.selectWithPath(path)).map(this.serializer::deserialize);
    }

    @Override
    public List<AppVersion> selectByAppSuiteId(String appSuiteId) {
        return this.mapper.selectByAppSuiteId(appSuiteId)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppVersion> pageListByTenantId(AppQueryCondition cond, String tenantId, long offset, int limit) {
        return this.mapper.selectByTenantIdWithPage(cond, tenantId, offset, limit)
                .stream()
                .map(this.serializer::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public long countByTenantId(AppQueryCondition cond, String tenantId) {
        return this.mapper.countByTenantId(tenantId, cond);
    }

    @Override
    public void deleteByIds(List<String> appIds) {
        this.mapper.delete(appIds);
    }

    @Override
    public String getAppSuiteIdByAppId(String appId) {
        return this.selectById(appId).map(av -> av.getData().getAppSuiteId()).orElse(null);
    }
}
