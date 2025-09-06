/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 百度千帆 检索pipeline config参数。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@Builder
public class QianfanPipelineConfigQueryParam {
    /**
     * pipeline id。
     */
    private String id;
    /**
     * 检索配置。
     */
    private List<QianfanPipelineQueryParam> pipeline;
}
