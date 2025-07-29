/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 表示用于解析评估节点信息的实体。
 *
 *  @author 兰宇晨
 *  @since 2024-9-26
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvalNodeEntity {
    /**
     * 表示评估节点名称。
     */
    private String nodeName;

    /**
     * 表示评估节点唯一标识。
     */
    private String nodeMetaId;
}
