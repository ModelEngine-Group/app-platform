/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.jade.knowledge.postprocessor.FactoryOption;
import modelengine.jade.knowledge.postprocessor.PostProcessorFactory;
import modelengine.jade.knowledge.postprocessor.support.DefaultPostProcessorFactory;

import modelengine.fel.core.document.DocumentPostProcessor;
import modelengine.fel.core.document.support.RerankOption;
import modelengine.fit.http.client.okhttp.OkHttpClassicClientFactory;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * {@link PostProcessorFactory} 的测试。
 *
 * @author 刘信宏
 * @since 2024-10-08
 */
@FitTestWithJunit(includeClasses = {DefaultPostProcessorFactory.class, OkHttpClassicClientFactory.class})
public class PostProcessorFactoryTest {
    @Fit
    private PostProcessorFactory processorFactory;

    @Test
    void shouldOkWhenCreateWithoutRerankOption() {
        List<DocumentPostProcessor> postProcessors = processorFactory.create(new FactoryOption(false, null));
        assertThat(postProcessors).hasSize(1);
    }

    @Test
    void shouldOkWhenCreateWithRerankOption() {
        RerankOption rerankOption = RerankOption.custom().query("query").baseUri("/uri").build();
        List<DocumentPostProcessor> postProcessors = processorFactory.create(new FactoryOption(true, rerankOption));
        assertThat(postProcessors).hasSize(2);
    }
}
