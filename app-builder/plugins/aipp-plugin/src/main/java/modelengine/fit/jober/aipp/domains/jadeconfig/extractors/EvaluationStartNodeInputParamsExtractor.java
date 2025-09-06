/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.jadeconfig.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 评估开始节点入参提取器.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class EvaluationStartNodeInputParamsExtractor implements InputParamsExtractor {
    @Override
    public JSONArray extract(JSONObject shape) {
        return new JSONArray();
    }
}
