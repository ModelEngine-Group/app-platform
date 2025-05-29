/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.image;

import lombok.Data;

/**
 * StableDiffusion pipeline模型的输入
 *
 * @author 易文渊
 * @since 2024-06-28
 */
@Data
public class StableDiffusionInput {
    private String prompt;
    private String negativePrompt;
}