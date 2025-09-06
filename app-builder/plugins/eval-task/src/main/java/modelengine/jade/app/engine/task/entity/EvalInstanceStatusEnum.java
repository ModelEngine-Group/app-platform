/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

/**
 * 表示评估任务实例状态的枚举类。
 *
 * @author 兰宇晨
 * @since 2024-08-17
 */
public enum EvalInstanceStatusEnum {
    /**
     * 评估中
     */
    RUNNING,

    /**
     * 评估失败
     */
    FAILED,

    /**
     * 评估成功
     */
    SUCCESS;
}
