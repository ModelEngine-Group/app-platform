/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.template.render;

import java.util.Map;

/**
 * 模板渲染服务接口，提供基于变量替换的模板渲染功能。
 *
 * @author 孙怡菲
 * @since 2025-08-29
 */
public interface TemplateService {
    /**
     * 渲染文本模板。
     *
     * @param template 模板内容，包含变量占位符。
     * @param args 模板变量映射，key 为变量名，value 为变量值。
     * @return 渲染后的完整文本内容。
     */
    String renderTemplate(String template, Map<String, Object> args);
}
