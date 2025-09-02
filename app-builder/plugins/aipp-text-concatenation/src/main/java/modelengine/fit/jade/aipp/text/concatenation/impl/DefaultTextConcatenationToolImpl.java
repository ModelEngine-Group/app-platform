/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.text.concatenation.impl;

import modelengine.fel.tool.annotation.Attribute;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fit.jade.aipp.template.render.TemplateService;
import modelengine.fit.jade.aipp.text.concatenation.service.TextConcatenationTool;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * {@link TextConcatenationTool} 的实现类。
 *
 * @author 孙怡菲
 * @since 2025-08-28
 */
@Component
@Group(name = "implGroup_text_concatenation_tool")
public class DefaultTextConcatenationToolImpl implements TextConcatenationTool {
    private static final String FITABLE_ID = "default_impl";

    private final TemplateService templateService;

    public DefaultTextConcatenationToolImpl(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Fitable(FITABLE_ID)
    @ToolMethod(name = "renderTemplateDefault", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "BASIC"),
            @Attribute(key = "tags", value = "TEXTCONCATENATENODE")
    })
    @Property(description = "默认的文本拼接实现")
    @Override
    public String renderTemplate(String template, Map<String, Object> args) {
        return this.templateService.renderTemplate(template, args);
    }
}