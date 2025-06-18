/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support.processor;

import static java.util.prefs.Preferences.MAX_NAME_LENGTH;
import static modelengine.fel.tool.ToolSchema.DEFAULT_PARAMETER;
import static modelengine.fel.tool.ToolSchema.DESCRIPTION;
import static modelengine.fel.tool.ToolSchema.NAME;
import static modelengine.fel.tool.ToolSchema.PARAMETERS;
import static modelengine.fel.tool.ToolSchema.PARAMETERS_PROPERTIES;
import static modelengine.fel.tool.ToolSchema.PARAMETERS_REQUIRED;
import static modelengine.fel.tool.ToolSchema.PROPERTIES_TYPE;
import static modelengine.fel.tool.info.schema.ToolsSchema.FIT;
import static modelengine.fel.tool.info.schema.ToolsSchema.FITABLE_ID;
import static modelengine.fel.tool.info.schema.ToolsSchema.MAX_FIT_TAG_LENGTH;
import static modelengine.fel.tool.info.schema.ToolsSchema.TAGS;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.store.code.PluginRetCode.LENGTH_EXCEEDED_LIMIT_FIELD;
import static modelengine.jade.store.tool.upload.support.processor.ToolProcessor.enhanceSchema;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.buildDefGroupMap;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import modelengine.fel.tool.ToolSchema;
import modelengine.fel.tool.info.entity.ToolJsonEntity;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.list.ListRemoveDuplicationConflictResolver;
import modelengine.fitframework.merge.map.MapConflictResolver;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link ToolProcessor} 的测试。
 *
 * @author 李金绪
 * @since 2024-10-30
 */
@DisplayName("测试 ToolProcessor")
public class ToolProcessorTest {
    private static final String TOOL_JSON = "src/test/resources/tools.json";
    private static final String HTTP_TOOL_JSON = "src/test/resources/toolsHttp.json";

    private final JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    private ToolProcessor processor;
    private ToolJsonEntity tool;
    private ToolJsonEntity httpTool;
    private Map<String, Object> helper;

    @BeforeEach
    void setup() {
        this.processor = new ToolProcessor(this.serializer);
        this.tool = getFileInfo(new File(TOOL_JSON), serializer, ToolJsonEntity.class);
        this.httpTool = getFileInfo(new File(HTTP_TOOL_JSON), serializer, ToolJsonEntity.class);
        this.helper = new HashMap<>();
    }

    @Test
    @DisplayName("当校验工具 json 时，成功")
    void shouldOkWhenNewJson() {
        assertDoesNotThrow(() -> {
            this.processor.validate(this.tool, this.helper);
        });
    }

    @Test
    @DisplayName("当校验工具 json.toolGroups 为空时，抛出异常")
    void shouldExWhenToolGroupEmpty() {
        assertThatThrownBy(() -> {
            this.tool.setToolGroups(Collections.emptyList());
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property and cannot be empty. [file='tools.json', "
                        + "property='toolGroups']");
    }

    @Test
    @DisplayName("当校验工具 json.toolGroups.tools 为空时，抛出异常")
    void shouldExWhenToolGroupToolsEmpty() {
        assertThatThrownBy(() -> {
            this.tool.getToolGroups().get(0).setTools(Collections.emptyList());
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property and cannot be empty. [file='tools.json', "
                        + "property='toolGroups[].tools']");
    }

    @Test
    @DisplayName("当校验工具，外部无标签且 extensions 为空时，抛出异常")
    void shouldExWhenTagNotInExtensions() {
        assertThatThrownBy(() -> {
            this.tool.getToolGroups().get(0).getTools().get(0).setExtensions(null);
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The file must contain the property and cannot be empty. [file='tools.json', "
                        + "property='toolGroups[].tools[].extensions']");
    }

    @Test
    @DisplayName("当校验工具的标签不符合规范时，抛出异常")
    void shouldExWhenTagInvalid() {
        assertThatThrownBy(() -> {
            String longTag = this.createLongString(MAX_NAME_LENGTH + 1);
            this.tool.getToolGroups().get(0).getTools().get(0).getExtensions().put(TAGS, Arrays.asList(longTag));
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasFieldOrPropertyWithValue("code", LENGTH_EXCEEDED_LIMIT_FIELD.getCode());
    }

    @Test
    @DisplayName("当 runnbales.fit 不包含 genericable， 抛出异常")
    void shouldExWhenFitInRunnablesWithoutFitableId() {
        assertThatThrownBy(() -> {
            this.tool.getToolGroups().get(0).getTools().get(0).getRunnables().put(FIT, new HashMap<String, String>());
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining(
                        "The file must contain the property. [file='tools.json', property='schema.runnables"
                                + ".fitableId']");
    }

    @Test
    @DisplayName("当 runnbales.fit.genericableId 不符合规范， 抛出异常")
    void shouldExWhenFitInRunnablesInvalid() {
        assertThatThrownBy(() -> {
            Map<String, String> fit = new HashMap<>();
            fit.put(FITABLE_ID, this.createLongString(MAX_FIT_TAG_LENGTH + 1));
            this.tool.getToolGroups().get(0).getTools().get(0).getRunnables().put(FIT, fit);
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The fitable id or genericable id does not meet the naming requirements.");
    }

    @Test
    @DisplayName("当 fitable,genericable 在 json 中重复时，抛出异常")
    void shouldExWhenFitRepeat() {
        assertThatThrownBy(() -> {
            Map<String, Object> fit = cast(this.tool.getToolGroups().get(0).getTools().get(0).getRunnables().get(FIT));
            this.tool.getToolGroups().get(0).getTools().get(1).getRunnables().put(FIT, fit);
            this.processor.validate(this.tool, this.helper);
        }).isInstanceOf(ModelEngineException.class)
                .hasMessageContaining("The current operation has duplicate fitable id and genericable id.");
    }

    private String createLongString(int length) {
        return Stream.generate(() -> "A").limit(length).collect(Collectors.joining());
    }

    @Test
    @DisplayName("当生成 ToolGroupData 时，成功")
    void shouldOkWhenBuildNewDefGroupData() {
        List<ToolGroupData> res = cast(this.processor.transform(this.tool, this.helper));
        assertThat(res).isNotEmpty();
    }

    @Test
    @DisplayName("测试当合并 http 插件的 schema 时，成功")
    void shouldOkWhenEnhanceSchema() {
        List<ToolGroupData> toolGroups = cast(this.processor.transform(this.httpTool, this.helper));
        assertThat(toolGroups.get(0).getTools().get(0).getSchema().containsKey(PARAMETERS)).isFalse();
        DefinitionProcessor defProcessor = new DefinitionProcessor(this.serializer);
        List<DefinitionGroupData> defGroups = cast(defProcessor.transform(this.httpTool, this.helper));
        Map<String, Map<String, Object>> defGroupMap = cast(buildDefGroupMap(defGroups));
        enhanceSchema(toolGroups, defGroupMap);
        assertThat(toolGroups.get(0).getTools().get(0).getSchema().containsKey(PARAMETERS)).isTrue();
    }

    @Test
    @DisplayName("测试使用 MapUtils 的冲突集合，成功")
    void shouldOkWhenMergeSchema() {
        ConflictResolverCollection registry = ConflictResolverCollection.createBasicOverwriteCollection();
        registry.add(Map.class, ObjectUtils.cast(new MapConflictResolver<>()));
        registry.add(List.class, ObjectUtils.cast(new ListRemoveDuplicationConflictResolver<>()));
        Map<String, Object> mapSchemaDef = buildSchemaDef();
        Map<String, Object> mapSchemaTool = buildSchemaTool();
        Map<String, Object> mapSchemaMerge = MapUtils.merge(mapSchemaDef, mapSchemaTool, registry);
        Map<String, Object> parameters = cast(mapSchemaMerge.get(PARAMETERS));
        List<String> requiredActual = cast(parameters.get(PARAMETERS_REQUIRED));
        List<String> requiredExcept = Arrays.asList("p1", "p2");
        assertThat(requiredActual).isEqualTo(requiredExcept);
        Map<String, String> properties = cast(parameters.get(PARAMETERS_PROPERTIES));
        assertThat(properties.containsKey("p1")).isTrue();
        Map<String, String> p1 = cast(properties.get("p1"));
        assertThat(p1).isNotNull();
        assertThat(p1.get(DESCRIPTION)).isEqualTo("The first parameter is specific string.");
    }

    private static Map<String, Object> buildSchemaDef() {
        return MapBuilder.<String, Object>get()
                .put(NAME, "defName")
                .put(DESCRIPTION, "This is a demo definition schema.")
                .put(ToolSchema.PARAMETERS,
                        MapBuilder.<String, Object>get()
                                .put(PROPERTIES_TYPE, "object")
                                .put(ToolSchema.PARAMETERS_PROPERTIES,
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put(PROPERTIES_TYPE, "string")
                                                                .put(DESCRIPTION, "The first parameter is string.")
                                                                .build())
                                                .build())
                                .put(PARAMETERS_REQUIRED, Arrays.asList("p1"))
                                .build())
                .put(ToolSchema.PARAMETERS_ORDER, Collections.singletonList("pt"))
                .put(ToolSchema.RETURN_SCHEMA, MapBuilder.<String, Object>get().put(PROPERTIES_TYPE, "string").build())
                .build();
    }

    private static Map<String, Object> buildSchemaTool() {
        return MapBuilder.<String, Object>get()
                .put(NAME, "defName")
                .put(DESCRIPTION, "This is a demo definition schema.")
                .put(ToolSchema.PARAMETERS,
                        MapBuilder.<String, Object>get()
                                .put(PROPERTIES_TYPE, "object")
                                .put(ToolSchema.PARAMETERS_PROPERTIES,
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put(PROPERTIES_TYPE, "string")
                                                                .put(DESCRIPTION,
                                                                        "The first parameter is specific string.")
                                                                .build())
                                                .put("p2",
                                                        MapBuilder.<String, Object>get()
                                                                .put(PROPERTIES_TYPE, "string")
                                                                .put(DEFAULT_PARAMETER,
                                                                        "The first parameter is string.")
                                                                .build())
                                                .build())
                                .put(PARAMETERS_REQUIRED, Arrays.asList("p1", "p2"))
                                .build())
                .put(ToolSchema.PARAMETERS_ORDER, Collections.singletonList("pt"))
                .put(ToolSchema.RETURN_SCHEMA, MapBuilder.<String, Object>get().put(PROPERTIES_TYPE, "string").build())
                .build();
    }
}
