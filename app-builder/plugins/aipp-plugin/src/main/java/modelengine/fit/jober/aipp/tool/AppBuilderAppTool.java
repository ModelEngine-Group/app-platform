/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fitframework.annotation.Genericable;

/**
 * app工具
 *
 * @author 邬涨财
 * @since 2024-05-20
 */
public interface AppBuilderAppTool {
    /**
     * 创建app
     *
     * @param appInfo app信息
     * @param userId 创建用户的id
     * @return app
     */
    @Genericable(id = "modelengine.fit.jober.aipp.tool.create.app")
    String createApp(String appInfo, String userId);
}
