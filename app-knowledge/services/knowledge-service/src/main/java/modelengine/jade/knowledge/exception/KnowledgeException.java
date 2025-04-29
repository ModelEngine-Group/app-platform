/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.exception;

import modelengine.jade.common.code.RetCode;
import modelengine.jade.common.exception.ModelEngineException;

/**
 * 表示知识库接口异常。
 *
 * @author 陈潇文
 * @since 2025-04-24
 */
public class KnowledgeException extends ModelEngineException {

    /**
     * Knowledge 异常构造函数。
     *
     * @param code 表示返回码 {@link RetCode}。
     * @param args 表示异常信息参数的 {@link Object}{@code []}。
     */
    public KnowledgeException(RetCode code, Object... args) {
        super(code, args);
    }

    /**
     * Knowledge 异常构造函数。
     *
     * @param code 表示返回码 {@link RetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param args 表示异常信息参数的  {@link Object}{@code []}。
     */
    public KnowledgeException(RetCode code, Throwable cause, Object... args) {
        super(code, cause, args);
    }
}
