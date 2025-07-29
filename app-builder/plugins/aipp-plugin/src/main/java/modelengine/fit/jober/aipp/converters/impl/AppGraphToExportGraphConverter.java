/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * {@link AppBuilderFlowGraph} -> {@link AppExportFlowGraph}.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Component
public class AppGraphToExportGraphConverter implements EntityConverter {
    @Override
    public Class<AppBuilderFlowGraph> source() {
        return AppBuilderFlowGraph.class;
    }

    @Override
    public Class<AppExportFlowGraph> target() {
        return AppExportFlowGraph.class;
    }

    @Override
    public AppExportFlowGraph convert(Object graph) {
        return Optional.ofNullable(graph)
                .map(ObjectUtils::<AppBuilderFlowGraph>cast)
                .map(s -> AppExportFlowGraph.builder().name(s.getName()).appearance(s.getAppearance()).build())
                .orElse(null);
    }
}
