/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fit.dynamicform.entity.FormMetaInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aipp节点表单
 *
 * @author 刘信宏
 * @since 2023-12-25
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippNodeForms {
    /**
     * 节点类型
     * 查看 {@link modelengine.fit.jober.entity.consts.NodeTypes}
     */
    private String type;

    /**
     * metaInfo
     */
    private List<FormMetaInfo> metaInfo;
}
