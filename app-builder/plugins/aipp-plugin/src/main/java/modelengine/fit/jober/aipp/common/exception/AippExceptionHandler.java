/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import lombok.RequiredArgsConstructor;
import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * aipp通用异常处理。 fit框架优先执行 Scope.PLUGIN，其次Scope.GLOBAL
 *
 * @author 刘信宏
 * @since 2024-01-30
 */
@Component
@RequiredArgsConstructor
public class AippExceptionHandler {
    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));

    private static final Logger log = Logger.get(AippExceptionHandler.class);

    private final Plugin plugin;

    /**
     * handleThrowable
     *
     * @param exception exception
     * @return Rsp<String>
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleThrowable(Throwable exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(modelengine.fit.jober.aipp.common.exception.AippErrCode.UNKNOWN.getErrorCode(),
                AippErrCode.UNKNOWN.getMessage());
    }

    /**
     * AippParamException 处理器。
     *
     * @param exception 异常的 {@link AippParamException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippParamException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippParamException(AippParamException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * AippException 处理器。
     *
     * @param exception 异常的 {@link AippException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippException(AippException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * AippNotFoundException 处理器。
     *
     * @param exception 异常的 {@link AippNotFoundException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippNotFoundException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippNotFoundException(AippNotFoundException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * AippForbiddenException 处理器。
     *
     * @param exception 异常的 {@link AippForbiddenException}。
     * @return 包含异常信息的通用返回体的 {@link Rsp}{@code <}{@link String}{@code >}
     */
    @ExceptionHandler(AippForbiddenException.class)
    @ResponseStatus(HttpResponseStatus.OK)
    public Rsp<String> handleAippForbiddenException(AippForbiddenException exception) {
        log.error(exception.getClass().getName(), exception);
        return Rsp.err(exception.getCode(),
                getLocaleMessage(String.valueOf(exception.getCode()), exception.getMessage(), exception.getArgs(),
                        exception.getContext()));
    }

    /**
     * 获取国际化异常信息
     *
     * @param code 异常码
     * @param defaultMsg 默认信息
     * @param params 参数
     * @param context 上下文
     * @return String 本地异常信息
     */
    private String getLocaleMessage(String code, String defaultMsg, Object[] params, OperationContext context) {
        if (context == null || StringUtils.isEmpty(context.getLanguage())) {
            return defaultMsg;
        }
        Locale locale = Locale.getDefault();
        try {
            String language = context.getLanguage();
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
            locale = StringUtils.isNotEmpty(language) ? Locale.lookup(list, LOCALES) : Locale.getDefault();
        } catch (Exception ex) {
            log.error("parse language from userContext failed, language is {}", context.getLanguage());
        }
        try {
            return plugin.sr().getMessageWithDefault(locale, code, defaultMsg, params);
        } catch (NullPointerException | UnsupportedOperationException | ClassCastException e) {
            log.warn("Localized exception messageException occurred: {}, {}", code, params);
            return defaultMsg;
        }
    }
}
