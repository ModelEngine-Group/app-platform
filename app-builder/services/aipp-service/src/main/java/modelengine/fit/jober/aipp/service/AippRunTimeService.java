/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.condition.AippInstanceQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.dto.AippInstanceDto;
import modelengine.fit.jober.aipp.vo.MetaVo;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * aipp运行时服务层接口
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
public interface AippRunTimeService {
    /**
     * 指定版本启动一个Aipp
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @return 实例id
     */
    String createAippInstance(String aippId, String version, Map<String, Object> initContext, OperationContext context);

    /**
     * 查询app对应的metaVo
     *
     * @param appId app的id
     * @param isDebug 是否查询debug阶段的meta
     * @param context 操作上下文
     * @return meta的id和version
     */
    MetaVo queryLatestMetaVoByAppId(String appId, boolean isDebug, OperationContext context);

    /**
     * 指定版本，启动一个流程
     *
     * @param metaInstId 实例id
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @param isDebug 是否是调试状态
     * @return 实例id
     */
    Choir<Object> startFlowWithUserSelectMemory(String metaInstId, Map<String, Object> initContext,
            OperationContext context, boolean isDebug);

    /**
     * 删除应用实例
     *
     * @param context 操作上下文
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     */
    void deleteAippInstance(String aippId, String version, String instanceId, OperationContext context);

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @param logId 日志id
     * @param context 操作上下文
     * @param isDebug 是否是调试状态
     * @return SSE流
     */
    Choir<Object> resumeAndUpdateAippInstance(String instanceId, Map<String, Object> formArgs, Long logId,
            OperationContext context, boolean isDebug);

    /**
     * 表单的实例终止
     *
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @param logId 表示日志id
     * @param context 操作上下文
     * @return 终止对话后返回的信息
     */
    String terminateInstance(String instanceId, Map<String, Object> msgArgs, Long logId, OperationContext context);

    /**
     * 终止aipp实例
     *
     * @param context 操作上下文
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @return 终止对话后返回的信息
     */
    String terminateInstance(String instanceId, Map<String, Object> msgArgs, OperationContext context);

    /**
     * 终止aipp全部实例
     *
     * @param aippId aipp id
     * @param versionId versionId
     * @param isDeleteLog 是否删除aipp log
     * @param context 操作上下文
     */
    void terminateAllPreviewInstances(String aippId, String versionId, boolean isDeleteLog, OperationContext context);
}
