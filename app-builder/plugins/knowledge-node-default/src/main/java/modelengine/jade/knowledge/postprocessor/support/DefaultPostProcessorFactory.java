/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.postprocessor.support;

import modelengine.jade.knowledge.postprocessor.FactoryOption;
import modelengine.jade.knowledge.postprocessor.PostProcessorFactory;

import modelengine.fel.core.document.DocumentPostProcessor;
import modelengine.fel.core.document.support.RerankDocumentProcessor;
import modelengine.fel.core.document.support.postprocessor.RrfPostProcessor;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文档后置处理器工厂的默认实现。
 *
 * @author 刘信宏
 * @since 2024-09-28
 */
@Component
public class DefaultPostProcessorFactory implements PostProcessorFactory {
    private final HttpClassicClientFactory httpClientFactory;

    DefaultPostProcessorFactory(HttpClassicClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public List<DocumentPostProcessor> create(FactoryOption factoryOption) {
        List<DocumentPostProcessor> processors = Stream.of(new RrfPostProcessor()).collect(Collectors.toList());
        if (factoryOption.isEnableRerank()) {
            processors.add(new RerankDocumentProcessor(this.httpClientFactory, factoryOption.getRerankOption()));
        }
        return processors;
    }
}
