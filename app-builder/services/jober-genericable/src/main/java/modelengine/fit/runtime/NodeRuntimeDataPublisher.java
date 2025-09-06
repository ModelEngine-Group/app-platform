/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.runtime;

import modelengine.fit.runtime.entity.RuntimeData;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 流程运行时数据发布者.
 *
 * @author 张越
 * @since 2024-05-23
 */
public interface NodeRuntimeDataPublisher {
    /**
     * 获取自定义参数的key.
     *
     * @return {@link List}{@code <}{@link String}{@code >} 参数key的集合.
     */
    @Genericable(id = "4def51abad114e498969b21b0dc31a3e")
    List<String> getExtraParamKeys();

    /**
     * 发送流程运行时数据.
     *
     * @param runtimeData 运行时数据.
     */
    @Genericable(id = "913eed457d51427c943cdf8f761bf551")
    void onPublish(RuntimeData runtimeData);
}
