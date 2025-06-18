/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support.processor;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import modelengine.fel.tool.info.entity.DefinitionGroupEntity;
import modelengine.fel.tool.info.entity.ToolJsonEntity;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link DefinitionProcessor} 的测试类。
 *
 * @author 李金绪
 * @since 2024-10-30
 */
@DisplayName("测试 DefinitionProcessor")
public class DefinitionProcessorTest {
    private static final String TOOL_JSON = "src/test/resources/tools.json";
    private static final String HTTP_TOOL_JSON = "src/test/resources/toolsHttp.json";

    private final JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    private DefinitionProcessor processor;
    private ToolJsonEntity tool;
    private ToolJsonEntity httpTool;

    @BeforeEach
    void setup() {
        this.processor = new DefinitionProcessor(this.serializer);
        this.tool = getFileInfo(new File(TOOL_JSON), serializer, ToolJsonEntity.class);
        this.httpTool = getFileInfo(new File(HTTP_TOOL_JSON), serializer, ToolJsonEntity.class);
    }

    @Test
    @DisplayName("当校验工具 json 时，成功")
    void shouldOkWhenNewJson() {
        assertDoesNotThrow(() -> this.processor.validate(this.tool, null));
    }

    @Test
    @DisplayName("当校验 http 工具 json 时，成功")
    void shouldOkWheHttpJson() {
        assertDoesNotThrow(() -> this.processor.validate(this.httpTool, null));
    }

    @Test
    @DisplayName("当 defGroups 为空时，抛出异常")
    void shouldExWhenDefGroupEmpty() {
        assertThatThrownBy(() -> {
            this.tool.setDefinitionGroups(Collections.emptyList());
            this.processor.validate(this.tool, null);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property and cannot be empty. [file='tools.json', "
                        + "property='definitionGroups']");
    }

    @Test
    @DisplayName("当 defGroups.defs 为空时，抛出异常")
    void shouldExWhenDefEmpty() {
        assertThatThrownBy(() -> {
            DefinitionGroupEntity definitionGroupEntity = this.tool.getDefinitionGroups().get(0);
            definitionGroupEntity.setDefinitions(Collections.emptyList());
            this.processor.validate(this.tool, null);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property and cannot be empty. [file='tools.json', "
                        + "property='definitionGroups[].definitions']");
    }

    @Test
    @DisplayName("当生成 DefGroupData 时，成功")
    void shouldOkWhenBuildNewDefGroupData() {
        List<DefinitionGroupData> res = cast(this.processor.transform(this.tool, null));
        assertThat(res).isNotEmpty();
    }
}
