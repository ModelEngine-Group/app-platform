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
import modelengine.jade.app.engine.base.dto.UserInfoDto;
import modelengine.jade.app.engine.base.dto.UserAppCollectionDto;
import modelengine.jade.app.engine.base.po.UserAppCollectionPo;
import modelengine.jade.app.engine.base.po.UserAppInfoAndCollectionPo;
import modelengine.jade.app.engine.base.service.UserAppCollectionService;
import modelengine.jade.app.engine.base.mapper.UserInfoMapper;
import modelengine.jade.app.engine.base.mapper.UserAppCollectionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户收藏应用实现类
 *
 * @since 2024-5-25
 *
 */
@Component
public class UserAppCollectionServiceImpl implements UserAppCollectionService {
    private static final Logger log = Logger.get(UserFeedbackServiceImpl.class);

    /**
     * 默认应用id
     */
    private static final String DEFAULT_APP_ID = "3a617d8aeb1d41a9ad7453f2f0f70d61";

    private final UserAppCollectionMapper userAppCollectionMapper;

    private final UserInfoMapper userInfoMapper;

    public UserAppCollectionServiceImpl(UserAppCollectionMapper userAppCollectionMapper,
                                       UserInfoMapper userInfoMapper) {
        this.userAppCollectionMapper = userAppCollectionMapper;
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 创建应用收藏记录
     *
     * @param userCollectionDto 用户应用收藏信息
     * @return 应用收藏记录id
     */
    @Transactional
    @Override
    public Long create(UserAppCollectionDto userCollectionDto) {
        this.userAppCollectionMapper.insert(userCollectionDto);
        this.userAppCollectionMapper.updateCollectionUserCntByAppId(userCollectionDto.getAppId(), 1);
        return userCollectionDto.getId();
    }

    /**
     * 通过id删除应用收藏记录
     *
     * @param userInfo 用户信息
     * @param appId 应用Id
     */
    @Transactional
    @Override
    public void deleteByUserInfoAndAppId(String userInfo, String appId) {
        this.userAppCollectionMapper.deleteByUserInfoAndAppId(userInfo, appId);
        this.userAppCollectionMapper.updateCollectionUserCntByAppId(appId, -1);
    }

    /**
     * 删除应用相关收藏记录
     *
     * @param appId 应用Id
     */
    @Override
    public void deleteByAppId(String appId) {
        this.userAppCollectionMapper.deleteByAppId(appId);
    }

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param userInfo 用户信息
     * @return 应用收藏列表
     */
    @Override
    public List<UserAppCollectionPo> getCollectionsByUserInfo(String userInfo) {
        return this.userAppCollectionMapper.getCollectionsByUserInfo(userInfo);
    }

    /**
     * 获取应用信息
     *
     * @param userInfo 用户信息
     * @return 应用信息消息类
     */
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

    /**
     * 通过应用id更新收藏用户数量
     *
     * @param appId 应用id
     */
    @Override
    public void updateCollectionUserCntByAppId(String appId) {
        this.userAppCollectionMapper.updateCollectionUserCntByAppId(appId, 1);
    }

    /**
     * 通过应用id获取收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数量
     */
    @Override
    public Integer getCollectionUserCntByAppId(String appId) {
        return this.userAppCollectionMapper.getCollectionUserCntByAppId(appId);
    }
}
