/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.vo.AippLogVO;

/**
 * log流式服务接口.
 *
 * @author 张越
 * @since 2024-05-23
 */
public interface AippLogStreamService {
    /**
     * 推送日志信息到前端.
     *
     * @param log 日志对象.
     */
    void send(AippLogVO log);
}
