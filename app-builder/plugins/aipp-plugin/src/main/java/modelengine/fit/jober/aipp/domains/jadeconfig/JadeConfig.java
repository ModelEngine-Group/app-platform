/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.jadeconfig;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Jade流程配置数据.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class JadeConfig {
    private final List<JadePage> pages = new ArrayList<>();

    public JadeConfig(String appearance) {
        JSONArray pageArray = JSONObject.parseObject(appearance).getJSONArray("pages");
        for (int j = 0; j < pageArray.size(); j++) {
            JSONObject node = pageArray.getJSONObject(j);
            this.pages.add(new JadePage(node));
        }
    }

    /**
     * 通过图形id获取图形数据.
     *
     * @param shapeId 图形id.
     * @return {@link Optional}{@code <}{@link JadeShape}{@code >} 对象.
     */
    public Optional<JadeShape> getShapeById(String shapeId) {
        for (JadePage page : this.pages) {
            Optional<JadeShape> shapeOptional = page.getShapeById(shapeId);
            if (shapeOptional.isPresent()) {
                return shapeOptional;
            }
        }
        return Optional.empty();
    }
}
