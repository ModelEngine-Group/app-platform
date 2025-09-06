/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fit.jober.entity.InstanceCategoryChanged;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * InstancesCategory修改Genericable。
 *
 * @author 陈镕希
 * @since 2023-08-30
 */
public interface OnInstancesCategoryChanged {
    /**
     * process
     *
     * @param messages messages
     */
    @Genericable(id = "e504e51720c242ab8edf5d0ccf97f5cc")
    void process(List<InstanceCategoryChanged> messages);
}
