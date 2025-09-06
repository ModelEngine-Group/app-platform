/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.entity.elsa;

/**
 * 图的实体
 *
 * @author 李鑫
 * @since 2024/7/27
 */
public class GraphParam {
    private String graphId;

    private String version;

    private String json;

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public String getGraphId() {
        return graphId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
