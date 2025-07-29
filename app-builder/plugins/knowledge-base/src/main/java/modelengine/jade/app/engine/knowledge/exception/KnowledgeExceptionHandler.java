/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.exception;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.jade.app.engine.knowledge.common.KnowledgeRsp;
import modelengine.jade.app.engine.knowledge.service.exception.ServiceException;

/**
 * 处理知识库Controller相关异常。
 *
 * @since 2024-06-18
 */
@Component
@RequiredArgsConstructor
public class KnowledgeExceptionHandler {
    private static final Logger log = Logger.get(KnowledgeExceptionHandler.class);

    /**
     * ServiceException 处理器。
     *
     * @param exception 异常的 {@link ServiceException}。
     * @return 包含异常信息的通用返回体的 {@link KnowledgeRsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public KnowledgeRsp handleKnowledgeException(ServiceException exception) {
        log.error(exception.getClass().getName(), exception);
        return KnowledgeRsp.err(400, exception.getMessage());
    }
}
