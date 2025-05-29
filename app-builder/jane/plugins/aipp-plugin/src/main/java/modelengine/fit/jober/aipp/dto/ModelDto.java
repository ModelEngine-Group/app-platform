/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.jade.store.entity.transfer.ModelData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模型的传输类。
 *
 * @author 李金绪
 * @since 2024/6/13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDto {
    private List<ModelData> modelDatas;
    private int total;
}
