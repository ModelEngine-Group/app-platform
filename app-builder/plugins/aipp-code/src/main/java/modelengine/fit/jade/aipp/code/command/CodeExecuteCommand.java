/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 表示代码执行的命令。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeExecuteCommand {
    @Property(description = "代码节点引用的参数", name = "args")
    @NotNull(message = "Args cannot be null.")
    private Map<String, Object> args;

    @Property(description = "代码节点需执行的用户代码", name = "code")
    @NotBlank(message = "Code cannot be blank.")
    private String code;

    @Property(description = "用户代码编写语言", name = "language")
    @NotNull(message = "Invalid code language.")
    private ProgrammingLanguage language;
}