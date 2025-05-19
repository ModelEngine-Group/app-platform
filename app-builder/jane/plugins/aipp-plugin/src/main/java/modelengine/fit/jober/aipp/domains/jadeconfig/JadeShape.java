/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.jadeconfig;

import modelengine.fit.jober.aipp.domains.jadeconfig.extractors.EvaluationStartNodeInputParamsExtractor;
import modelengine.fit.jober.aipp.domains.jadeconfig.extractors.InputParamsExtractor;
import modelengine.fit.jober.aipp.domains.jadeconfig.extractors.NullInputParamsExtractor;
import modelengine.fit.jober.aipp.domains.jadeconfig.extractors.StartNodeInputParamsExtractor;
import modelengine.fit.jober.aipp.domains.jadeconfig.extractors.StateNodeInputParamsExtractor;
import modelengine.fit.jober.aipp.domains.jadeconfig.extractors.TaskNodeInputParamsExtractor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 图形数据.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class JadeShape {
    private static final Map<String, InputParamsExtractor> EXTRACTORS = MapBuilder.<String, InputParamsExtractor>get()
            .put("startNodeStart", new StartNodeInputParamsExtractor())
            .put("evaluationStartNodeStart", new EvaluationStartNodeInputParamsExtractor())
            .put("endNodeEnd", new NullInputParamsExtractor())
            .put("evaluationEndNodeEnd", new NullInputParamsExtractor())
            .put("jadeEvent", new NullInputParamsExtractor())
            .put("conditionNodeCondition", new NullInputParamsExtractor())
            .put("manualCheckNodeState", new TaskNodeInputParamsExtractor())
            .put("intelligentFormNodeState", new TaskNodeInputParamsExtractor())
            .build();

    private final JSONObject shape;
    private Map<String, Object> params;

    public JadeShape(JSONObject shape) {
        this.shape = shape;
        JSONArray inputParams = this.getInputParam();
        if (inputParams != null) {
            this.params = this.extractingExpandObject(this.getInputParam());
        }
    }

    private List<Object> extractingExpandArray(JSONArray value) {
        List<Object> result = new ArrayList<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (this.isFromInput(jsonObject)) {
                result.add(jsonObject.get("value"));
                continue;
            }
            if (this.isFromExpand(jsonObject)) {
                this.handleExpandType(jsonObject, result);
            }
        }
        return result;
    }

    private void handleExpandType(JSONObject jsonObject, List<Object> result) {
        if (this.isArray(jsonObject)) {
            List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
            result.add(array);
            return;
        }
        if (this.isObject(jsonObject)) {
            Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
            if (MapUtils.isNotEmpty(map)) {
                result.add(map);
            }
        }
    }

    // 如果type是Object，那么调用这个方法获取一个Map<String, Object>
    private Map<String, Object> extractingExpandObject(JSONArray value) {
        Map<String, Object> result = new HashMap<>();
        for (int index = 0; index < value.size(); index++) {
            JSONObject jsonObject = value.getJSONObject(index);
            if (this.isFromInput(jsonObject)) {
                result.put(jsonObject.getString("name"), jsonObject.get("value"));
                continue;
            }
            if (this.isFromExpand(jsonObject)) {
                this.handleExpandType(jsonObject, result);
            }
        }
        return result;
    }

    private void handleExpandType(JSONObject jsonObject, Map<String, Object> result) {
        if (this.isArray(jsonObject)) {
            List<Object> array = this.extractingExpandArray(jsonObject.getJSONArray("value"));
            result.put(jsonObject.getString("name"), array);
            return;
        }
        if (this.isObject(jsonObject)) {
            Map<String, Object> map = this.extractingExpandObject(jsonObject.getJSONArray("value"));
            if (MapUtils.isNotEmpty(map)) {
                result.put(jsonObject.getString("name"), map);
            }
        }
    }

    private boolean isFromExpand(JSONObject jsonObject) {
        return StringUtils.equalsIgnoreCase("Expand", jsonObject.getString("from"));
    }

    private boolean isFromInput(JSONObject jsonObject) {
        return StringUtils.equalsIgnoreCase("Input", jsonObject.getString("from"));
    }

    private boolean isObject(JSONObject jsonObject) {
        return StringUtils.equalsIgnoreCase("Object", jsonObject.getString("type"));
    }

    private boolean isArray(JSONObject jsonObject) {
        return StringUtils.equalsIgnoreCase("Array", jsonObject.getString("type"));
    }

    /**
     * 获取id.
     *
     * @return {@link String} 唯一标识.
     */
    public String getId() {
        return this.shape.getString("id");
    }

    /**
     * 获取值.
     *
     * @param key 键值.
     * @return 值.
     */
    public Optional<Object> getValue(String key) {
        return Optional.ofNullable(this.params.get(key));
    }

    private JSONArray getInputParam() {
        String nodeType = this.shape.getString("type");
        InputParamsExtractor extractor = Optional.ofNullable(EXTRACTORS.get(nodeType))
                .orElseGet(StateNodeInputParamsExtractor::new);
        return extractor.extract(this.shape);
    }
}
