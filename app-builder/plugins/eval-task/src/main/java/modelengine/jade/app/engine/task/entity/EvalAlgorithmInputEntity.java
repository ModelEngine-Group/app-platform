/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.Map;

/**
 * 表示用于解析评估算法入参的实体。
 *
 * @author 兰宇晨
 * @since 2024-9-26
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvalAlgorithmInputEntity {
    /**
     * 算法节点入参。
     */
    private Map<String, Object> algorithmArgs;

    /**
     * 算法节点及格分。
     */
    private double passScore;

    /**
     * 调用算法信息。
     */
    private Map<String, Object> algorithmInfo;

    /**
     * 算法插件唯一名称。
     */
    private String uniqueName;
}
