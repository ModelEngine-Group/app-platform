/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.text.concatenation.service;

import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 文本拼接工具接口，提供基于模板的文本渲染功能。
 *
 * @author 孙怡菲
 * @since 2025-08-28
 */
@Group(name = "defGroup_text_concatenation_tool")
public interface TextConcatenationTool {
    /**
     * 渲染文本模板。
     *
     * @param template 模板内容。
     * @param args 拼接的变量映射，key 为变量名，value 为变量值。
     * @return 渲染后的文本内容。
     */
    @ToolMethod(name = "renderTemplate", description = "用于文本拼接的工具")
    @Genericable("modelengine.jade.concatenate.text")
    String renderTemplate(@Property(description = "模板内容", required = true) String template,
            @Property(description = "拼接的变量", required = true) Map<String, Object> args);
}
