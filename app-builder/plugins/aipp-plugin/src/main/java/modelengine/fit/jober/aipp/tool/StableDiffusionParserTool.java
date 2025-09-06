/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fit.jober.aipp.dto.image.StableDiffusionInput;
import modelengine.fitframework.annotation.Genericable;

/**
 * StableDiffusion模型json入参解析
 *
 * @author 易文渊
 * @since 2024-06-28
 */
public interface StableDiffusionParserTool {
    /**
     * 解析json字符串。
     *
     * @param input StableDiffusion模型json入参 {@link String}。
     * @return 解析后的SD多模态模型输入 {@link StableDiffusionInput}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jober.aipp.tool.sd.parse")
    StableDiffusionInput parse(String input);
}