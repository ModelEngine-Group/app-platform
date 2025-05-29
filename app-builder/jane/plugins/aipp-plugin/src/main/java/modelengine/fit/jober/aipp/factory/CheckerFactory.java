/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.factory;

import static modelengine.fit.jober.aipp.enums.NodeType.END_NODE;
import static modelengine.fit.jober.aipp.enums.NodeType.LLM_NODE;
import static modelengine.fit.jober.aipp.enums.NodeType.MANUAL_CHECK_NODE;
import static modelengine.fit.jober.aipp.enums.NodeType.OLD_RETRIEVAL_NODE;
import static modelengine.fit.jober.aipp.enums.NodeType.RETRIEVAL_NODE;
import static modelengine.fit.jober.aipp.enums.NodeType.TOOL_INVOKE_NODE;

import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.Checker;
import modelengine.fit.jober.aipp.service.impl.EndNodeChecker;
import modelengine.fit.jober.aipp.service.impl.LlmNodeChecker;
import modelengine.fit.jober.aipp.service.impl.ManualCheckNodeChecker;
import modelengine.fit.jober.aipp.service.impl.RetrievalNodeChecker;
import modelengine.fit.jober.aipp.service.impl.ToolInvokeNodeChecker;
import modelengine.jade.store.service.PluginToolService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Initialize;

import java.util.HashMap;
import java.util.Map;

/**
 * checker 的工厂类
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Component
public class CheckerFactory {
    private static final Map<String, Checker> CHECKER_MAP = new HashMap<>();

    private final AippModelCenter fetchModelService;

    private final PluginToolService pluginToolService;

    public CheckerFactory(AippModelCenter fetchModelService, PluginToolService pluginToolService) {
        this.fetchModelService = fetchModelService;
        this.pluginToolService = pluginToolService;
    }

    @Initialize
    private void initializeCheckerMap() {
        CHECKER_MAP.put(LLM_NODE.type(), new LlmNodeChecker(fetchModelService, pluginToolService));
        CHECKER_MAP.put(TOOL_INVOKE_NODE.type(), new ToolInvokeNodeChecker(pluginToolService));
        CHECKER_MAP.put(RETRIEVAL_NODE.type(), new RetrievalNodeChecker());
        CHECKER_MAP.put(OLD_RETRIEVAL_NODE.type(), new RetrievalNodeChecker());
        CHECKER_MAP.put(MANUAL_CHECK_NODE.type(), new ManualCheckNodeChecker());
        CHECKER_MAP.put(END_NODE.type(), new EndNodeChecker());
    }

    /**
     * 获取节点类型对应的checker
     *
     * @param type 节点类型
     * @return 对应checker
     */
    public static Checker getChecker(String type) {
        return CHECKER_MAP.getOrDefault(type, (dto, context) -> {
            throw new AippException(AippErrCode.UNSUPPORTED_NODE_TYPE, type);
        });
    }
}
