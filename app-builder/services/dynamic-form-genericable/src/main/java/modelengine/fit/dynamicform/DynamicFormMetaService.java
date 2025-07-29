/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform;

import modelengine.fit.dynamicform.entity.FormMetaInfo;
import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 表单的FIT服务
 *
 * @author 夏斐
 * @since 2023/12/12
 */
public interface DynamicFormMetaService {
    /**
     * 查询表单的元数据信息
     *
     * @param parameter 查询参数，可以批量查询
     * @return 表单中的元数据字段列表，批量返回
     */
    @Genericable(id = "250869a27a044d4dadd09681a1e35b4b")
    List<FormMetaInfo> query(List<FormMetaQueryParameter> parameter);
}
