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
import modelengine.jade.app.engine.base.dto.UsrAppCollectionDto;
import modelengine.jade.app.engine.base.po.UsrAppCollectionPo;
import modelengine.jade.app.engine.base.po.UsrAppInfoAndCollectionPo;
import modelengine.jade.app.engine.base.service.UsrAppCollectionService;
import modelengine.jade.app.engine.base.mapper.UserInfoMapper;
import modelengine.jade.app.engine.base.mapper.UsrAppCollectionMapper;

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
public class UsrAppCollectionServiceImpl implements UsrAppCollectionService {
    private static final Logger log = Logger.get(UsrFeedbackServiceImpl.class);

    /**
     * 默认应用id
     */
    private static final String DEFAULT_APP_ID = "3a617d8aeb1d41a9ad7453f2f0f70d61";

    private final UsrAppCollectionMapper usrAppCollectionMapper;

    private final UserInfoMapper userInfoMapper;

    public UsrAppCollectionServiceImpl(UsrAppCollectionMapper userAppCollectionMapper,
                                       UserInfoMapper userInfoMapper) {
        this.usrAppCollectionMapper = userAppCollectionMapper;
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 创建应用收藏记录
     *
     * @param usrCollectionDto 用户应用收藏信息
     * @return 应用收藏记录id
     */
    @Transactional
    @Override
    public Long create(UsrAppCollectionDto usrCollectionDto) {
        usrAppCollectionMapper.insert(usrCollectionDto);
        usrAppCollectionMapper.updateCollectionUsrCntByAppId(usrCollectionDto.getAppId(), 1);
        return usrCollectionDto.getId();
    }

    /**
     * 通过id删除应用收藏记录
     *
     * @param usrInfo 用户信息
     * @param appId 应用Id
     */
    @Transactional
    @Override
    public void deleteByUsrInfoAndAppId(String usrInfo, String appId) {
        usrAppCollectionMapper.deleteByUsrInfoAndAppId(usrInfo, appId);
        usrAppCollectionMapper.updateCollectionUsrCntByAppId(appId, -1);
    }

    /**
     * 删除应用相关收藏记录
     *
     * @param appId 应用Id
     */
    @Override
    public void deleteByAppId(String appId) {
        usrAppCollectionMapper.deleteByAppId(appId);
    }

    /**
     * 通过用户信息获取应用收藏列表
     *
     * @param usrInfo 用户信息
     * @return 应用收藏列表
     */
    @Override
    public List<UsrAppCollectionPo> getCollectionsByUsrInfo(String usrInfo) {
        return usrAppCollectionMapper.getCollectionsByUsrInfo(usrInfo);
    }

    /**
     * 获取应用信息
     *
     * @param usrInfo 用户信息
     * @return 应用信息消息类
     */
    @Override
    @Transactional
    public CollectionAppInfoDto getAppInfoByUsrInfo(String usrInfo) {
        List<UsrAppInfoAndCollectionPo> collectionList = usrAppCollectionMapper.getAppInfoByUsrInfo(usrInfo);

        UserInfoDto userInfoDto = userInfoMapper.get(usrInfo);
        if (userInfoDto == null) {
            userInfoMapper.insert(UserInfoDto.builder().userName(usrInfo).defaultApp(DEFAULT_APP_ID).build());
        }

        UsrAppInfoAndCollectionPo defaultApp = usrAppCollectionMapper.getDefaultAppInfo(usrInfo);
        // 如果之前设置的默认应用被删除，此处懒更新重置默认应用
        if (defaultApp == null) {
            userInfoMapper.update(UserInfoDto.builder().userName(usrInfo).defaultApp(DEFAULT_APP_ID).build());
            defaultApp = usrAppCollectionMapper.getDefaultAppInfo(usrInfo);
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
    public void updateCollectionUsrCntByAppId(String appId) {
        usrAppCollectionMapper.updateCollectionUsrCntByAppId(appId, 1);
    }

    /**
     * 通过应用id获取收藏用户数量
     *
     * @param appId 应用id
     * @return 应用收藏用户数量
     */
    @Override
    public Integer getCollectionUsrCntByAppId(String appId) {
        return usrAppCollectionMapper.getCollectionUsrCntByAppId(appId);
    }
}
