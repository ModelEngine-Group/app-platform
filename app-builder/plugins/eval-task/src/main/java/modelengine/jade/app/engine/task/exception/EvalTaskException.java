/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.exception;

import modelengine.jade.app.engine.task.code.EvalTaskRetCode;

import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示应用评估异常。
 *
 * @author 何嘉斌
 * @since 2024-08-20
 */
public class EvalTaskException extends FitException {
    /**
     * 应用评估异常构造函数。
     *
     * @param code 表示返回码的 {@link EvalTaskRetCode}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public EvalTaskException(EvalTaskRetCode code, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args));
    }

    /**
     * 应用评估异常构造函数。
     *
     * @param code 表示返回码的 {@link EvalTaskRetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public EvalTaskException(EvalTaskRetCode code, Throwable cause, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args), cause);
    }
}