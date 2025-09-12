/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.app.engine.base.dto.CollectionAppInfoDto;
import modelengine.jade.app.engine.base.dto.UserAppCollectionDto;
import modelengine.jade.app.engine.base.dto.UserInfoDto;
import modelengine.jade.app.engine.base.mapper.UserAppCollectionMapper;
import modelengine.jade.app.engine.base.mapper.UserInfoMapper;
import modelengine.jade.app.engine.base.po.UserAppCollectionPo;
import modelengine.jade.app.engine.base.po.UserAppInfoAndCollectionPo;
import modelengine.jade.app.engine.base.service.UserAppCollectionService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户收藏应用实现类。
 *
 * @author 陈潇文
 * @since 2024-05-25
 */
@Component
public class UserAppCollectionServiceImpl implements UserAppCollectionService {
    private static final Logger log = Logger.get(UserFeedbackServiceImpl.class);

    /**
     * 默认应用唯一标识。
     */
    private static final String DEFAULT_APP_ID = "3a617d8aeb1d41a9ad7453f2f0f70d61";

    private final UserAppCollectionMapper userAppCollectionMapper;

    private final UserInfoMapper userInfoMapper;

    public UserAppCollectionServiceImpl(UserAppCollectionMapper userAppCollectionMapper,
            UserInfoMapper userInfoMapper) {
        this.userAppCollectionMapper = userAppCollectionMapper;
        this.userInfoMapper = userInfoMapper;
    }

    @Transactional
    @Override
    public Long create(UserAppCollectionDto userCollectionDto) {
        this.userAppCollectionMapper.insert(userCollectionDto);
        this.userAppCollectionMapper.updateCollectionUserCntByAppId(userCollectionDto.getAppId(), 1);
        return userCollectionDto.getId();
    }

    @Transactional
    @Override
    public void deleteByUserInfoAndAppId(String userInfo, String appId) {
        this.userAppCollectionMapper.deleteByUserInfoAndAppId(userInfo, appId);
        this.userAppCollectionMapper.updateCollectionUserCntByAppId(appId, -1);
    }

    @Override
    public void deleteByAppId(String appId) {
        this.userAppCollectionMapper.deleteByAppId(appId);
    }

    @Override
    public List<UserAppCollectionPo> getCollectionsByUserInfo(String userInfo) {
        return this.userAppCollectionMapper.getCollectionsByUserInfo(userInfo);
    }

    @Override
    @Transactional
    public CollectionAppInfoDto getAppInfoByUserInfo(String userInfo) {
        List<UserAppInfoAndCollectionPo> collectionList = this.userAppCollectionMapper.getAppInfoByUserInfo(userInfo);

        UserInfoDto userInfoDto = this.userInfoMapper.get(userInfo);
        if (userInfoDto == null) {
            this.userInfoMapper.insert(UserInfoDto.builder().userName(userInfo).defaultApp(DEFAULT_APP_ID).build());
        }

        UserAppInfoAndCollectionPo defaultApp = this.userAppCollectionMapper.getDefaultAppInfo(userInfo);
        // 如果之前设置的默认应用被删除，此处懒更新重置默认应用
        if (defaultApp == null) {
            this.userInfoMapper.update(UserInfoDto.builder().userName(userInfo).defaultApp(DEFAULT_APP_ID).build());
            defaultApp = this.userAppCollectionMapper.getDefaultAppInfo(userInfo);
        }

        String defaultAppAppId = defaultApp.getAppId();
        collectionList = collectionList.stream()
                .filter(n -> !Objects.equals(defaultAppAppId, n.getAppId()))
                .collect(Collectors.toList());
        return new CollectionAppInfoDto(collectionList, defaultApp);
    }

    @Override
    public void updateCollectionUserCntByAppId(String appId) {
        this.userAppCollectionMapper.updateCollectionUserCntByAppId(appId, 1);
    }

    @Override
    public Integer getCollectionUserCntByAppId(String appId) {
        return this.userAppCollectionMapper.getCollectionUserCntByAppId(appId);
    }
}
