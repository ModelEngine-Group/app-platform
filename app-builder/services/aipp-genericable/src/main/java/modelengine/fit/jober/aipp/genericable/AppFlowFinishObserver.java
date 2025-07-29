/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable;

import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 表示 App 流程停止完成的观察者。
 *
 * @author 邬涨财
 * @since 2024-05-24
 */
public interface AppFlowFinishObserver {
    /**
     * 获取流程结束的数据。
     *
     * @param data 表示流程结束的数据的 {@link String}。
     * @param attributes 表示流程的属性 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.service.app.finished")
    void onFinished(String data, Map<String, Object> attributes);
}
