/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.template.render;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.parameterization.ParameterizationMode;
import modelengine.fitframework.parameterization.ParameterizedString;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;

import java.util.Collections;
import java.util.Map;

/**
 * {@link TemplateService} 的实现类。
 *
 * @author 孙怡菲
 * @since 2025-08-29
 */
@Component
public class TemplateServiceImpl implements TemplateService {
    @Override
    public String renderTemplate(String template, Map<String, Object> args) {
        if (template == null) {
            return null;
        }
        if (args == null) {
            args = Map.of();
        }
        Map<String, Object> params = nullIf(args, Collections.emptyMap());
        ParameterizedStringResolver resolver =
                ParameterizedStringResolver.create("{{", "}}", '/', ParameterizationMode.LENIENT_EMPTY);
        ParameterizedString parameterizedString = resolver.resolve(template);
        return parameterizedString.format(params, null);
    }
}
