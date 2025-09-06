/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.fel.core.image.ImageModel;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.app.engine.image.entity.GenerateImageParam;
import modelengine.jade.app.engine.image.service.impl.ImageGeneratorImpl;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link ImageGeneratorImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-12-18
 */
@DisplayName("测试 ImageGenerator 的实现")
@FitTestWithJunit(includeClasses = ImageGeneratorImpl.class)
@Disabled
public class ImageGeneratorImplTest {
    @Fit
    private ImageGeneratorImpl imageGenerator;

    @Mock
    private ImageModel imageModel;

    @Test
    @DisplayName("生成一张图片成功")
    void shouldOkWhenGenerateOneImage() {
        Media image = new Media("image/jpeg", "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBg");
        when(this.imageModel.generate(anyString(), any())).thenReturn(Collections.singletonList(image));
        GenerateImageParam imageParam = new GenerateImageParam();
        imageParam.setArgs(new HashMap<>());
        imageParam.setDescriptionTemplate("desc");
        imageParam.setImageCount(1);
        List<Media> images = this.imageGenerator.generateImage(imageParam);
        assertThat(images.size()).isEqualTo(1);
        assertThat(images.get(0).getData()).isEqualTo("https://modelengine.com");
    }

    @Test
    @DisplayName("生成多张图片成功")
    void shouldOkWhenGenerateMultipleImages() {
        List<Media> images =
                Collections.singletonList(new Media("image/jpeg", "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBg"));
        when(this.imageModel.generate(anyString(), any())).thenReturn(images, images, images);
        GenerateImageParam imageParam = new GenerateImageParam();
        imageParam.setArgs(new HashMap<>());
        imageParam.setDescriptionTemplate("desc");
        imageParam.setImageCount(3);
        images = this.imageGenerator.generateImage(imageParam);
        assertThat(images.size()).isEqualTo(3);
        assertThat(images.stream().map(Media::getData).collect(Collectors.toList())).contains("https://modelengine1.com",
                "https://modelengine2.com",
                "https://modelengine3.com");
    }

    @Test
    @DisplayName("缺失必填字段时，fit 调用失败")
    void shouldFailWhenGenerateImageWithoutRequiredField() {
        Media image = new Media("image/jpeg", "fake image data");
        when(imageModel.generate(anyString(), any())).thenReturn(Collections.singletonList(image));
        GenerateImageParam imageParam = new GenerateImageParam();
        assertThatThrownBy(() ->
                this.imageGenerator.generateImage(imageParam)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("生成 url 不符合规范时，调用失败")
    void shouldFailWhenGenerateImageWithInvalidFileUrl() {
        Media image = new Media("image/jpeg", "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBg");
        when(this.imageModel.generate(anyString(), any())).thenReturn(Collections.singletonList(image));
        GenerateImageParam imageParam = new GenerateImageParam();
        imageParam.setArgs(new HashMap<>());
        imageParam.setDescriptionTemplate("desc");
        imageParam.setImageCount(1);
        assertThatThrownBy(() -> this.imageGenerator.generateImage(imageParam)).isInstanceOf(ModelEngineException.class)
                .extracting("message")
                .isEqualTo("Malformed URL has occurred: bad url.");
        ;
    }
}