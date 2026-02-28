/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import static modelengine.jade.common.code.CommonRetCode.BAD_REQUEST;

import modelengine.fit.http.annotation.ExceptionHandler;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.common.localemessage.ExceptionLocaleService;
import modelengine.jade.common.vo.Result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 默认全局异常处理器。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
public class DefaultHttpExceptionAdvice implements ExceptionLocaleService {
    private static final Logger log = Logger.get(DefaultHttpExceptionAdvice.class);
    private static final String VIOLATION_PREFIX = "{";
    private static final String VIOLATION_SUFFIX = "}";
    private static final List<String> DEFAULT_PROPERTIES = Arrays.asList("fitableId", "genericableId");

    private final LocaleService localeService;
    private final LazyLoader<List<ExceptionLocaleOperator>> localeOperators;

    public DefaultHttpExceptionAdvice(LocaleService localeService) {
        this.localeService = localeService;
        this.localeOperators = new LazyLoader<>(this::getLocaleOperators);
    }

    /**
     * 处理 {@link Throwable} 异常。
     *
     * @param exception 表示异常的 {@link Throwable}。
     * @return 表示统一返回结果的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    @ExceptionHandler(value = Throwable.class, scope = Scope.GLOBAL)
    public Result<Void> handleException(Throwable exception) {
        return Result.error(HttpResponseStatus.INTERNAL_SERVER_ERROR.statusCode(),
                this.localeService.localize(String.valueOf(CommonRetCode.INTERNAL_ERROR.getCode())));
    }

    /**
     * 处理 {@link FitException} 异常。
     *
     * @param exception 表示异常的 {@link FitException}。
     * @return 表示统一返回结果的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    @ExceptionHandler(value = FitException.class, scope = Scope.GLOBAL)
    @Deprecated
    public Result<Void> handleException(FitException exception) {
        List<Object> params = Arrays.asList(new TreeMap<>(this.getFilteredProperties(exception)).values().toArray());
        String localeMessage = this.localeService.localize(String.valueOf(exception.getCode()), params.toArray());
        return Result.error(exception.getCode(), localeMessage);
    }

    /**
     * 处理 {@link ConstraintViolationException} 异常。
     *
     * @param exception 表示异常的 {@link ConstraintViolationException}。
     * @return 表示统一返回结果的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    @ExceptionHandler(value = ConstraintViolationException.class, scope = Scope.GLOBAL)
    @ResponseStatus(code = HttpResponseStatus.BAD_REQUEST)
    public Result<Void> handleException(ConstraintViolationException exception) {
        String localeMessage = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.joining(", "));
        return Result.error(BAD_REQUEST.getCode(), localeMessage);
    }

    /**
     * 处理 {@link IllegalArgumentException} 异常。
     *
     * @param exception 表示异常的 {@link IllegalArgumentException}。
     * @return 表示统一返回结果的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    @ExceptionHandler(value = IllegalArgumentException.class, scope = Scope.GLOBAL)
    @ResponseStatus(code = HttpResponseStatus.BAD_REQUEST)
    public Result<Void> handleException(IllegalArgumentException exception) {
        String localeMessage = this.localeService.localize(String.valueOf(BAD_REQUEST.getCode()));
        return Result.error(BAD_REQUEST.getCode(), localeMessage);
    }

    /**
     * 处理 {@link ModelEngineException} 异常。
     *
     * @param exception 表示异常的 {@link ModelEngineException}。
     * @return 表示统一返回结果的 {@link Result}{@code <}{@link Void}{@code >}。
     */
    @ExceptionHandler(value = ModelEngineException.class, scope = Scope.GLOBAL)
    public Result<Void> handleException(ModelEngineException exception) {
        String localeMessage = this.localeService.localize(String.valueOf(exception.getCode()), exception.getArgs());
        return Result.error(exception.getCode(), localeMessage);
    }

    private String getCode(String message) {
        return message.startsWith(VIOLATION_PREFIX) && message.endsWith(VIOLATION_SUFFIX) ? message.substring(1,
                message.length() - 1) : message;
    }

    @Override
    public String localizeMessage(Throwable throwable) {
        return this.localeOperators.get().stream()
                .filter(item -> item.classObj.isInstance(throwable))
                .findFirst()
                .map(operator -> operator.handle(this, throwable))
                .orElse(StringUtils.EMPTY);
    }

    private List<ExceptionLocaleOperator> getLocaleOperators() {
        List<ExceptionLocaleOperator> operators = new ArrayList<>();
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
            if (annotation == null) {
                continue;
            }
            Arrays.stream(annotation.value())
                    .forEach(value -> operators.add(new ExceptionLocaleOperator(value, method)));
        }
        operators.sort((operator1, operator2) -> {
            if (operator1.classObj.equals(operator2.classObj)) {
                return 0;
            }
            return operator1.classObj.isAssignableFrom(operator2.classObj) ? 1 : -1;
        });
        return operators;
    }

    private Map<String, String> getFilteredProperties(FitException exception) {
        Map<String, String> properties = new HashMap<>(exception.getProperties());
        DEFAULT_PROPERTIES.forEach(properties::remove);
        return properties;
    }

    static class ExceptionLocaleOperator {
        private final Class<? extends Throwable> classObj;
        private final Method method;

        ExceptionLocaleOperator(Class<? extends Throwable> classObj, Method method) {
            this.classObj = classObj;
            this.method = method;
        }

        String handle(Object obj, Throwable throwable) {
            try {
                return ObjectUtils.<Result<?>>cast(this.method.invoke(obj, throwable)).getMsg();
            } catch (IllegalAccessException | InvocationTargetException exception) {
                log.warn("[Locale message] Fail to invoke exception handler.", exception);
                return StringUtils.EMPTY;
            }
        }
    }
}