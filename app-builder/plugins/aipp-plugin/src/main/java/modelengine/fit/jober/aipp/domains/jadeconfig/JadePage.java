/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.jadeconfig;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 页面数据.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class JadePage {
    private final List<JadeShape> shapes;

    public JadePage(JSONObject page) {
        this.shapes = new ArrayList<>();
        JSONArray shapeArray = page.getJSONArray("shapes");
        for (int j = 0; j < shapeArray.size(); j++) {
            JSONObject node = shapeArray.getJSONObject(j);
            this.shapes.add(new JadeShape(node));
        }
    }

    /**
     * 通过图形id获取图形数据.
     *
     * @param shapeId 图形id.
     * @return {@link Optional}{@code <}{@link JadeShape}{@code >} 对象.
     */
    public Optional<JadeShape> getShapeById(String shapeId) {
        return this.shapes.stream().filter(s -> StringUtils.equals(shapeId, s.getId())).findFirst();
    }
}
