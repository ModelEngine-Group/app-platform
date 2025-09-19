/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.template.render;

import modelengine.fitframework.annotation.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link TemplateService} 的实现类。
 *
 * @author 孙怡菲
 * @since 2025-08-29
 */
@Component
public class TemplateServiceImpl implements TemplateService {
    private static final Pattern PLACEHOLDER_PATTERN  = Pattern.compile("\\{\\{\\s*(\\w+)\\s*}}");

    @Override
    public String renderTemplate(String template, Map<String, Object> args) {
        if (template == null) {
            return null;
        }

        if (args == null) {
            args = Map.of();
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = args.getOrDefault(key, "");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(value)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
