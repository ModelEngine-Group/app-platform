/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.uid;

import modelengine.fitframework.annotation.Genericable;

/**
 * 全局唯一 ID 生成器接口定义。
 *
 * @author 易文渊
 * @since 2024-07-29
 */
public interface UidGenerator {
    /**
     * 获取全局唯一顺序递增 ID。
     *
     * @return 表示全局唯一 ID 的 {@code long}。
     */
    @Genericable("modelengine.jade.app.engine.uid.get")
    long getUid();
}