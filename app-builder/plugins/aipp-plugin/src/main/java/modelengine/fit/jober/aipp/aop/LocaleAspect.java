/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.aop;

import modelengine.fit.jober.aipp.service.DatabaseFieldLocaleService;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Around;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Pointcut;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.locale.LocaleUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段值国际化注解
 *
 * @author 陈潇文
 * @since 2024-08-19
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LocaleAspect {
    private static final Logger log = Logger.get(LocaleAspect.class);
    private static final String I18N_PATTERN = "i18n_appBuilder_\\{(.*?)\\}";
    private static final Pattern PATTERN = Pattern.compile(I18N_PATTERN);
    private static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));

    private final DatabaseFieldLocaleService localeService;

    @Pointcut("@annotation(modelengine.fit.jober.aipp.aop.Locale)")
    private void localePointCut() {
    }

    /**
     * 有@Localize注解的字段就进行国际化替换.
     *
     * @param pjp {@link ProceedingJoinPoint} 对象.
     * @return 经过国际化替换后的对象。
     * @throws Throwable 异常。
     */
    @Around("localePointCut()")
    public Object localize(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        if (result instanceof Iterable) {
            for (Object item : (Iterable) result) {
                localizeFields(item);
            }
        } else {
            localizeFields(result);
        }
        return result;
    }

    private void localizeFields(Object object) {
        if (object == null) {
            return;
        }
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(LocaleField.class)) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value instanceof String) {
                    field.set(object, getReplacedValue((String) value, LocaleUtil.getLocale()));
                }
            } catch (IllegalAccessException e) {
                log.error("get field locale source failed, field: {0} ", field);
            }
        }
    }

    private String getReplacedValue(String fieldValue, Locale locale) {
        Matcher matcher = PATTERN.matcher(fieldValue);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String localizedValue = this.localeService.getLocaleMessage(key, locale);
            if (StringUtils.isNotEmpty(localizedValue)) {
                localizedValue = localizedValue.replaceAll("\\\\", "\\\\\\\\");
            }
            matcher.appendReplacement(result, localizedValue);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
