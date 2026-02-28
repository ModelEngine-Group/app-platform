/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.command;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 代码执行命令处理器接口定义。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
public interface CodeExecuteCommandHandler {
    /**
     * 代码执行命令。
     *
     * @param command 表示代码执行命令的 {@link CodeExecuteCommand}。
     * @return 表示代码执行结果的 {@link Object}。
     */
    Object handle(@Valid @NotNull(message = "Command cannot be null.") CodeExecuteCommand command);
}