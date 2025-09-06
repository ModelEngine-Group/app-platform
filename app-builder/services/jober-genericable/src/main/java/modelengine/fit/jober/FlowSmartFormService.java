/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Map;

/**
 * 流程实例智能表单 Genericable。
 *
 * @author 李哲峰
 * @since 2023-12-13
 */
public interface FlowSmartFormService {
    /**
     * 智能表单处理
     *
     * @param contexts 流程上下文信息
     * @param sheetId 表单Id
     */
    @Genericable(id = "htctmizg0mydwnt2ttbbp8jlgo2e9e0w")
    void handleSmartForm(List<Map<String, Object>> contexts, String sheetId);
}
