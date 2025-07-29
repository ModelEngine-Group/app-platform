/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.condition;

import lombok.Data;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;

/**
 * 表单排序条件
 *
 * @author 熊以可
 * @since 2023/12/12
 */
@Data
public class FormQueryCondition {
    @Property(description = "表单名称过滤", example = "看图说话表单")
    @RequestQuery(name = "form_name", required = false)
    private String formName;

    @Property(description = "表单创建人过滤", example = "张三 z00000001")
    @RequestQuery(name = "create_user", required = false)
    private String createUser;

    @Property(description = "排序条件,支持字段:create_time/update_time", example = "update_time")
    @RequestQuery(name = "sort", required = false, defaultValue = "update_time")
    private String sort;

    @Property(description = "排序方向,descend表示降序，ascend表示升序", example = "descend")
    @RequestQuery(name = "order", required = false, defaultValue = "descend")
    private String order;
}
