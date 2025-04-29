/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示用户知识库配置Dto
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeConfigDto {
    /**
     * 知识库配置唯一id。
     */
    private Long id;
    /**
     * 知识库平台名称。
     */
    private String name;
    /**
     * 用户id。
     */
    private String userId;
    /**
     * 知识库api key。
     */
    private String apiKey;
    /**
     * 知识库平台groupId。
     */
    private String groupId;
    /**
     * 是否为默认使用。
     */
    private Boolean isDefault;
}
