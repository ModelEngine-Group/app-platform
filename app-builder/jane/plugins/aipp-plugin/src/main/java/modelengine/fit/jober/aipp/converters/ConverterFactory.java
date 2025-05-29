/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;

/**
 * 转换器工厂.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Component
public class ConverterFactory {
    private final List<EntityConverter> entityConverters;

    public ConverterFactory(List<EntityConverter> converters) {
        this.entityConverters = Validation.notNull(converters, "Converters not exists.");
    }

    /**
     * 将 T 对象转换为 R.
     *
     * @param t 待转换对象.
     * @param clz 目标类型.
     * @return {@code R} 类型.
     */
    public <T, R> R convert(T t, Class<R> clz) {
        return ObjectUtils.cast(this.entityConverters.stream()
                .filter(ec -> ec.source().equals(t.getClass()) && ec.target().equals(clz))
                .findFirst()
                .map(ec -> ec.convert(ObjectUtils.cast(t)))
                .orElse(null));
    }
}
