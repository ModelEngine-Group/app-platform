/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Genericable;

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
    @Genericable(id = "modelengine.fit.jober.aipp.service.start.app")
    String createAippInstance(String aippId, String version, Map<String, Object> initContext, OperationContext context);

    /**
     * 通过 App 唯一标识启动一个 Aipp。
     *
     * @param appId App 唯一标识。
     * @param isDebug 是否调试启动。
     * @param initContext 流程初始化的businessData。
     * @param context 操作上下文。
     * @return Aipp 实例唯一标识。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.start.aipp")
    String createLatestAippInstanceByAppId(String appId, boolean isDebug, Map<String, Object> initContext,
            OperationContext context);

    /**
     * 查询对话实例是否在运行中。
     *
     * @param instanceId 表示对话实例唯一标识的 {@link String}.
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示对话实例是否在运行中的 {@link Boolean}。
     */
    @Genericable(id = "modelengine.fit.aipp.service.runtime.getInstanceStatus")
    Boolean isInstanceRunning(String instanceId, OperationContext context);
}
