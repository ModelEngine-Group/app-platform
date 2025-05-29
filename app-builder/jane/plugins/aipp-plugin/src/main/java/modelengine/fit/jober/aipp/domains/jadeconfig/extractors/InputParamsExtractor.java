/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.jadeconfig.extractors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 输入参数提取器.
 *
 * @author 张越
 * @since 2025-01-14
 */
public interface InputParamsExtractor {
    /**
     * 提取.
     *
     * @param shape 图形数据.
     * @return {@link JSONArray} 对象.
     */
    JSONArray extract(JSONObject shape);
}
