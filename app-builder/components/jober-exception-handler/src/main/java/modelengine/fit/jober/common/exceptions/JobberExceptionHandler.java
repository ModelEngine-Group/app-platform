/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.common.exceptions;

import static modelengine.fit.jober.common.ErrorCodes.SERVER_INTERNAL_ERROR;
import static modelengine.fit.jober.common.ErrorCodes.UN_EXCEPTED_ERROR;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.model.JoberResponse;
import modelengine.fit.jober.common.util.ParamUtils;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.transaction.TransactionPreparationException;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 通用异常处理。
 *
 * @author 陈镕希
 * @since 2023-06-27
 */
@Component
@RequiredArgsConstructor
public class JobberExceptionHandler {
    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));

    private static final Logger log = Logger.get(JobberExceptionHandler.class);

    private final Plugin plugin;

    /**
     * handleThrowable
     *
     * @param exception exception
     * @return JoberResponse<String>
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public JoberResponse<String> handleThrowable(Throwable exception) {
        log.error(exception.getClass().getName(), exception);
        return JoberResponse.fail(UN_EXCEPTED_ERROR.getErrorCode(),
                StringUtils.format(UN_EXCEPTED_ERROR.getMessage(), exception.getMessage()));
    }

    /**
     * JobberParamException处理器。
     *
     * @param exception 异常的 {@link JobberParamException}。
     * @return 包含异常信息的通用返回体的 {@link JoberResponse}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(JobberParamException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public JoberResponse<String> handleJobberParamException(JobberParamException exception) {
        log.error(exception.getClass().getName(), exception);
        return JoberResponse.fail(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        ParamUtils.convertOperationContext(exception.getContext())));
    }

    /**
     * BadRequestException处理器。
     *
     * @param exception 异常的 {@link BadRequestException}。
     * @return 包含异常信息的通用返回体的 {@link JoberResponse}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpResponseStatus.BAD_REQUEST)
    public JoberResponse<String> handleBadRequestException(BadRequestException exception) {
        log.error(exception.getClass().getName(), exception);
        return JoberResponse.fail(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        ParamUtils.convertOperationContext(exception.getContext())));
    }

    /**
     * NotFoundException处理器。
     *
     * @param exception 异常的 {@link NotFoundException}。
     * @return 包含异常信息的通用返回体的 {@link JoberResponse}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpResponseStatus.NOT_FOUND)
    public JoberResponse<String> handleNotFoundException(NotFoundException exception) {
        log.error(exception.getClass().getName(), exception);
        return JoberResponse.fail(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        ParamUtils.convertOperationContext(exception.getContext())));
    }

    /**
     * DataAccessException处理器。
     *
     * @param exception 异常的 {@link DataAccessException}。
     * @return 包含异常信息的通用返回体的 {@link JoberResponse}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public JoberResponse<String> handleDataAccessException(DataAccessException exception) {
        log.error(exception.getMessage(), exception);
        return JoberResponse.fail(SERVER_INTERNAL_ERROR.getErrorCode(), SERVER_INTERNAL_ERROR.getMessage());
    }

    /**
     * handleJobberParamException
     *
     * @param exception exception
     */
    @ExceptionHandler(TransactionPreparationException.class)
    public void handleJobberParamException(TransactionPreparationException exception) {
        log.error("Catch TransactionPreparationException!");
    }

    /**
     * handleConflictException
     *
     * @param exception exception
     * @return JoberResponse<String>
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpResponseStatus.CONFLICT)
    public JoberResponse<String> handleConflictException(ConflictException exception) {
        log.error(exception.getMessage(), exception);
        return JoberResponse.fail(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        ParamUtils.convertOperationContext(exception.getContext())));
    }

    /**
     * handleGoneException
     *
     * @param exception exception
     * @return JoberResponse<String>
     */
    @ExceptionHandler(GoneException.class)
    @ResponseStatus(HttpResponseStatus.GONE)
    public JoberResponse<String> handleGoneException(GoneException exception) {
        log.error(exception.getMessage(), exception);
        return JoberResponse.fail(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        ParamUtils.convertOperationContext(exception.getContext())));
    }

    /**
     * JobberException处理器。
     *
     * @param exception 异常的 {@link JobberException}。
     * @return 包含异常信息的通用返回体的 {@link JoberResponse}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(JobberException.class)
    @ResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR)
    public JoberResponse<String> handleJobberException(JobberException exception) {
        log.error(exception.getClass().getName(), exception);
        return JoberResponse.fail(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        ParamUtils.convertOperationContext(exception.getContext())));
    }

    /**
     * 获取国际化异常信息
     *
     * @param code code
     * @param defaultMsg defaultMsg
     * @param params params
     * @param context context
     * @return String
     */
    private String getLocaleMessage(String code, String defaultMsg, Object[] params, OperationContext context) {
        if (context == null || StringUtils.isEmpty(context.language())) {
            return defaultMsg;
        }
        String language = context.language();
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
        Locale locale = StringUtils.isNotEmpty(language) ? Locale.lookup(list, LOCALES) : Locale.getDefault();
        try {
            return plugin.sr().getMessage(locale, code, params);
        } catch (FitException e) {
            log.warn("locale message exception: {}, {}", code, params);
            return defaultMsg;
        }
    }
}
