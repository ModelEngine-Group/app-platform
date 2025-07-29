/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.code;

import modelengine.jade.common.code.RetCode;
import modelengine.jade.common.model.ModelInfo;

/**
 * 应用评估模块返回码枚举，返回码最大值为 256。
 *
 * @author 何嘉斌
 * @since 2024-08-20
 */
public enum EvalTaskRetCode implements RetCode, ModelInfo {
    /**
     * 流程上下文中必须参数为空。
     */
    EVAL_TASK_INPUT_PARAM(130802001, "Input param is empty, empty param is {0}."),

    /**
     * 流水线上下文数据为空。
     */
    EVAL_TASK_CONTEXT(130802002, "Flow context is empty."),

    /**
     * 找不到实体。
     */
    ENTITY_NOT_FOUND(130802003, "Cannot find entity {0} by id: {1}."),

    /**
     * 调用算法工具失败。
     */
    EVAL_ALGORITHM_TOOL_ERROR(130802004, "Execute algorithm tool fail, err: {0}.");

    private final int code;
    private final String msg;

    EvalTaskRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}