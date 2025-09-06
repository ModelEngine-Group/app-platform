/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.image.service.impl;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.image.ImageModel;
import modelengine.fel.core.image.ImageOption;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.resource.web.Media;
import modelengine.jade.app.engine.image.code.ImageGenerationRetCode;
import modelengine.jade.app.engine.image.entity.GenerateImageParam;
import modelengine.jade.app.engine.image.service.ImageGenerator;
import modelengine.jade.common.exception.ModelEngineException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 表示 {@link ImageGenerator} 的 fit 实现。
 *
 * @author 何嘉斌
 * @since 2024-12-17
 */
@Component
public class ImageGeneratorImpl implements ImageGenerator {
    private static final String IMAGE_SIZE = "256x256";
    private static final int MAX_IMAGE_COUNT = 5;

    private final String baselUrl;
    private final String imageGenModel;
    private final ImageModel imageModel;

    public ImageGeneratorImpl(@Value("${openai-urls.internal}") String baselUrl,
            @Value("${model.imageGen.model}") String imageGenModel, ImageModel imageModel) {
        this.baselUrl = baselUrl;
        this.imageGenModel = imageGenModel;
        this.imageModel = imageModel;
    }

    @Override
    @Fitable("default")
    public List<Media> generateImage(GenerateImageParam imageParam) {
        notNull(imageParam, "The image generation param cannot be null.");
        String prompt = imageParam.getDesc();
        ImageOption option = ImageOption.custom()
                .baseUrl(this.baselUrl)
                .model(this.imageGenModel)
                .size(IMAGE_SIZE)
                .build();
        int imageCount = Math.min(notNull(imageParam.getImageCount(), "The image count cannot be null."),
                MAX_IMAGE_COUNT);
        return IntStream.range(0, imageCount)
                .parallel()
                .mapToObj(i -> this.imageModel.generate(prompt, option))
                .flatMap(List::stream)
                .map(this::imageToS3Url)
                .collect(Collectors.toList());
    }

    private Media imageToS3Url(Media entity) {
        byte[] image = Base64.getDecoder().decode(entity.getData().getBytes());
        // 暂时去除s3依赖
        String url = "mockUrl";
        try {
            return new Media(new URL(url));
        } catch (MalformedURLException ex) {
            throw new ModelEngineException(ImageGenerationRetCode.MALFORMED_URL, ex, url);
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString().replace("-", "") + ".jpeg";
    }
}