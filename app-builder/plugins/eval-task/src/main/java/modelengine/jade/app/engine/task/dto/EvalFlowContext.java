/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import static modelengine.jade.app.engine.task.code.EvalTaskRetCode.EVAL_TASK_INPUT_PARAM;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.jade.app.engine.task.exception.EvalTaskException;

import lombok.Data;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * 流程上下文数据提取器。
 *
 * @author 何嘉斌
 * @since 2024-08-26
 */
@Data
public class EvalFlowContext<T> {
    private static final String TRACE_ID_KEY = "traceId";
    private static final String BS_DATA_KEY = "businessData";

    private T data;
    private String traceId;

    /**
     * 表示流程上下文数据提取器的构建器。
     *
     * @param context 表示上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param type 表示数据类型的 {@link Type}。
     * @param <T> 表示上下文信息中的数据类型。
     * @return 表示解析出的上下文信息传输对象的 {@link EvalFlowContext}。
     */
    public static <T> EvalFlowContext<T> from(Map<String, Object> context, Type type) {
        EvalFlowContext<T> flowContext = new EvalFlowContext<>();
        if (!context.containsKey(BS_DATA_KEY)) {
            throw new EvalTaskException(EVAL_TASK_INPUT_PARAM, BS_DATA_KEY);
        }
        flowContext.data = ObjectUtils.toCustomObject(context.get(BS_DATA_KEY), type);
        if (!context.containsKey(TRACE_ID_KEY)) {
            throw new EvalTaskException(EVAL_TASK_INPUT_PARAM, TRACE_ID_KEY);
        }
        Set<String> traceIds = cast(context.get(TRACE_ID_KEY));
        flowContext.traceId = traceIds.stream()
                .findFirst()
                .orElseThrow(() -> new EvalTaskException(EVAL_TASK_INPUT_PARAM, TRACE_ID_KEY));
        return flowContext;
    }
}