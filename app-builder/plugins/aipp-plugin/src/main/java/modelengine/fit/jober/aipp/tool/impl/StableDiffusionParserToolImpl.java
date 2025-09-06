/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.impl;

import modelengine.fel.core.format.json.JsonOutputParser;
import modelengine.fel.core.pattern.Parser;
import modelengine.fit.jober.aipp.dto.image.StableDiffusionInput;
import modelengine.fit.jober.aipp.tool.StableDiffusionParserTool;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;

/**
 * StableDiffusion模型json入参解析器实现
 *
 * @author 易文渊
 * @since 2024-06-28
 */
@Component
public class StableDiffusionParserToolImpl implements StableDiffusionParserTool {
    private final Parser<String, StableDiffusionInput> parser;

    public StableDiffusionParserToolImpl(@Fit(alias = "json") ObjectSerializer objectSerializer) {
        this.parser = JsonOutputParser.create(objectSerializer, StableDiffusionInput.class);
    }

    @Override
    @Fitable("default")
    public StableDiffusionInput parse(String input) {
        return parser.parse(input);
    }
}