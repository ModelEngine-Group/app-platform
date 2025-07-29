/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AippQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDetailDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippOverviewRspDto;
import modelengine.fit.jober.aipp.dto.AippVersionDto;

import java.util.List;

/**
 * aipp编排服务层接口
 *
 * @author 刘信宏
 * @since 2023-12-12
 */
public interface AippFlowService {
    /**
     * 查询aipp详情
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @return aipp 详情
     */
    Rsp<AippDetailDto> queryAippDetail(String aippId, String version, OperationContext context);

    /**
     * 查询aipp列表
     *
     * @param cond 过滤条件
     * @param page 分页
     * @param context 操作上下文
     * @return aipp 概况
     */
    PageResponse<AippOverviewRspDto> listAipp(AippQueryCondition cond, PaginationCondition page,
            OperationContext context);

    /**
     * 查询指定aipp的版本列表
     *
     * @param aippId aippId
     * @param context 操作上下文
     * @return aipp 版本概况
     */
    List<AippVersionDto> listAippVersions(String aippId, OperationContext context);

    /**
     * 删除aipp
     *
     * @param aippId aippId
     * @param version aipp版本
     * @param context 操作上下文
     * @throws AippForbiddenException 禁止删除aipp异常
     */
    void deleteAipp(String aippId, String version, OperationContext context) throws AippForbiddenException;

    /**
     * 创建aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id和版本信息
     * @throws AippParamException 入参异常
     * @throws AippException 创建aipp异常
     */
    AippCreateDto create(AippDto aippDto, OperationContext context) throws AippException;

    /**
     * 更新aipp
     *
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     * @throws AippForbiddenException 禁止更新aipp异常
     * @throws AippParamException 入参异常
     */
    AippCreateDto update(AippDto aippDto, OperationContext context) throws AippForbiddenException, AippParamException;

    /**
     * 退出预览aipp的清理
     *
     * @param previewAippId 预览版本的aippId
     * @param previewVersion 预览版本号
     * @param context 操作上下文
     */
    void cleanPreviewAipp(String previewAippId, String previewVersion, OperationContext context);

    /**
     * 升级aipp
     *
     * @param baselineVersion 基线版本
     * @param aippDto aipp定义
     * @param context 操作上下文
     * @return aipp id信息
     */
    AippCreateDto upgrade(String baselineVersion, AippDto aippDto, OperationContext context);
}
