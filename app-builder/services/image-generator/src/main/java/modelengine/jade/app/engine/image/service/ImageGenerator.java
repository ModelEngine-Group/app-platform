/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.image.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.resource.web.Media;
import modelengine.jade.app.engine.image.entity.GenerateImageParam;

import java.util.List;

/**
 * 生成图片工具。
 *
 * @author 何嘉斌
 * @since 2024-12-17
 */
public interface ImageGenerator {
    /**
     * 生成图片工具。
     *
     * @param imageParam 表示图片参数的 {@link GenerateImageParam}。
     * @return 表示 base64 编码图片的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.jade.app.engine.image.service.generateImage")
    List<Media> generateImage(GenerateImageParam imageParam);
}