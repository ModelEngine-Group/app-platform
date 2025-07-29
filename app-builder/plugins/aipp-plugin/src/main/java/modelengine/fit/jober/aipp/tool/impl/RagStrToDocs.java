/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.impl;

import modelengine.fel.core.document.Document;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.tool.RagTool;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RagTool} 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-26
 */
@Component
public class RagStrToDocs implements RagTool {
    @Fitable("naive.rag.StrToDocs")
    @Override
    public List<Document> contentToDocs(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        ArrayList<String> splitTexts = StringUtils.split(content, AippConst.CONTENT_DELIMITER, ArrayList::new);
        return splitTexts.stream()
                .map(text -> Document.custom()
                        .text(text)
                        .metadata(MapBuilder.<String, Object>get().put("score", 0.5).build())
                        .build())
                .collect(Collectors.toList());
    }
}
