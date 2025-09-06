/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接入的模型服务的信息。
 *
 * @author 方誉州
 * @since 2024-09-13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelAccessInfo {
    private String serviceName;
    private String tag;
    private String baseUrl;
    private String accessKey;
    private String type;

    /**
     * 获取是否是系统模型，系统模型需要不一样的 http 调用安全参数。
     *
     * @return 是否是系统模型。
     */
    public boolean isSystemModel() {
        return this.tag.contains("INTERNAL") || this.tag.contains("EXTERNAL");
    }
}
