/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.config.FormFileUploadConfig;
import modelengine.fit.jober.aipp.dto.GenerateImageDto;
import modelengine.fit.jober.aipp.service.FileService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.validation.FormFileValidator;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.entity.serializer.MultiPartEntitySerializer;
import modelengine.fit.http.header.ContentType;
import modelengine.fit.http.header.HeaderValue;
import modelengine.fit.http.header.ParameterCollection;
import modelengine.fit.http.header.support.DefaultContentType;
import modelengine.fit.http.header.support.DefaultHeaderValue;
import modelengine.fit.http.header.support.DefaultParameterCollection;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.ObjectUtils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 为 {@link FileService} 提供测试
 *
 * @author 邬涨财
 * @since 2024-11-30
 */
@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {
    private static final String URL = "http://127.0.0.1:8000/v1/images/generations";

    @Mock
    private HttpClassicClientFactory httpClassicClientFactory;

    private FileService fileService;

    @Mock
    private FormFileValidator formFileValidator;

    @Mock
    private UploadedFileManageService uploadedFileManageService;

    private FormFileUploadConfig formFileUploadConfig;

    private final EntitySerializer<PartitionedEntity> multiPartEntitySerializer = new MultiPartEntitySerializer();

    private final HttpMessage httpMessage = mock(HttpMessage.class);

    @BeforeEach
    void setUp() {
        ParameterCollection parameterCollection = new DefaultParameterCollection();
        parameterCollection.set("boundary", "--token");
        HeaderValue headerValue = new DefaultHeaderValue("multipart/form-data", parameterCollection);
        ContentType contentType = new DefaultContentType(headerValue);
        Optional<ContentType> optionalContentType = Optional.of(contentType);
        when(this.httpMessage.contentType()).thenReturn(optionalContentType);
        this.fileService = new FileServiceImpl(this.httpClassicClientFactory,
                URL,
                "model",
                this.formFileValidator,
                this.uploadedFileManageService,
                this.formFileUploadConfig,
                "form",
                "form/temporary",
                "", "form", "/file/");
    }

    @Test
    @DisplayName("测试生成图片成功")
    public void testGenerateImageSuccess() {
        GenerateImageDto imageDto = new GenerateImageDto("name", "description", 200, 200);
        HttpClassicClient httpClassicClient = Mockito.mock(HttpClassicClient.class);
        HttpClassicClientRequest request = Mockito.mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> httpRes = Mockito.mock(HttpClassicClientResponse.class);
        ObjectEntity<Object> objectEntity = Mockito.mock(ObjectEntity.class);
        Map<String, Object> resultData = new HashMap<>();
        List<Object> data = new ArrayList<>();
        Map<String, Object> json = new HashMap<>();
        json.put("b64_json", "image");
        data.add(json);
        resultData.put("data", data);

        when(httpClassicClientFactory.create()).thenReturn(httpClassicClient);
        when(httpClassicClient.createRequest(HttpRequestMethod.POST, URL)).thenReturn(request);
        when(request.exchange()).thenReturn(httpRes);
        when(httpRes.statusCode()).thenReturn(HttpResponseStatus.OK.statusCode());
        when(httpRes.objectEntity()).thenReturn(Optional.ofNullable(objectEntity));
        when(objectEntity.object()).thenReturn(resultData);

        Rsp<String> res = fileService.generateImage(imageDto);

        Assertions.assertEquals("image", res.getData());
    }

    @Test
    @DisplayName("测试生成图片调用大模型失败")
    public void testGenerateImageFailed() {
        GenerateImageDto imageDto = new GenerateImageDto("name", "description", 200, 200);
        HttpClassicClient httpClassicClient = Mockito.mock(HttpClassicClient.class);
        HttpClassicClientRequest request = Mockito.mock(HttpClassicClientRequest.class);
        HttpClassicClientResponse<Object> httpRes = Mockito.mock(HttpClassicClientResponse.class);
        ObjectEntity<Object> objectEntity = Mockito.mock(ObjectEntity.class);
        Map<String, Object> resultData = new HashMap<>();
        List<Object> data = new ArrayList<>();
        Map<String, Object> json = new HashMap<>();
        json.put("b64_json", "image");
        data.add(json);
        resultData.put("data", data);

        when(httpClassicClientFactory.create()).thenReturn(httpClassicClient);
        when(httpClassicClient.createRequest(HttpRequestMethod.POST, URL)).thenReturn(request);
        when(request.exchange()).thenReturn(httpRes);
        when(httpRes.statusCode()).thenReturn(HttpResponseStatus.GATEWAY_TIMEOUT.statusCode());

        assertThrows(AippException.class, () -> {
            fileService.generateImage(imageDto);
        });
    }

    @Test
    @DisplayName("当上传的表单不是zip格式，抛异常")
    void givenNotZipFileThenThrowException() {
        PartitionedEntity entity = Mockito.mock(PartitionedEntity.class);
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> this.fileService.uploadSmartForm(entity, "test.7z", this.buildOperationContext()));
        Assertions.assertEquals(AippErrCode.UPLOADED_FORM_FILE_FORMAT_ERROR.getErrorCode(), exception.getCode());
    }

    @Test
    @DisplayName("当上传的表单内容不存在，抛异常")
    void givenNotExistEntitiesThenThrowException() {
        PartitionedEntity entity = Mockito.mock(PartitionedEntity.class);
        Mockito.when(entity.entities()).thenReturn(new ArrayList<>());
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> this.fileService.uploadSmartForm(entity, "test.zip", this.buildOperationContext()));
        Assertions.assertEquals(AippErrCode.NO_FILE_UPLOAD_ERROR.getErrorCode(), exception.getCode());
    }

    @Test
    @DisplayName("当解压缩文件时，如果文件小于5M，解压缩成功")
    void givenNotExceed5MThenUnzipSucceed()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException {
        Method unZipFormFileMethod = FileServiceImpl.class.getDeclaredMethod("unZipFormFile",
                String.class,
                String.class,
                String.class,
                List.class);
        unZipFormFileMethod.setAccessible(true);
        List<NamedEntity> namedEntities = new ArrayList<>();
        NamedEntity namedEntity = Mockito.mock(NamedEntity.class);
        InputStream inputStream =
                FileServiceImplTest.class.getClassLoader().getResourceAsStream("form/testNotExceed5M.zip");
        FileEntity fileEntity =
                FileEntity.create(httpMessage, "entityFileName", inputStream, 0, FileEntity.Position.INLINE, null);
        when(namedEntity.asFile()).thenReturn(fileEntity);
        namedEntities.add(namedEntity);
        String fromTemporaryPath = Paths.get(ClassLoader.getSystemResource("form_temporary").toURI()).toString();
        String from = Paths.get(ClassLoader.getSystemResource("form").toURI()).toString();
        File unzipFile = ObjectUtils.cast(unZipFormFileMethod.invoke(this.fileService,
                "form/testNotExceed5M.zip",
                fromTemporaryPath,
                from,
                namedEntities));
        List<File> unzipFiles = FileUtils.list(unzipFile);
        Assertions.assertEquals(unzipFiles.size(), 3);
        FileUtils.delete(unzipFile);
    }

    @Test
    @DisplayName("校验表单文件成功")
    void testValidateFormSuccess() throws NoSuchMethodException, URISyntaxException {
        Method validateFormMethod = FileServiceImpl.class.getDeclaredMethod("validateForm", File[].class);
        validateFormMethod.setAccessible(true);

        java.net.URL url = FileServiceImplTest.class.getClassLoader().getResource("file/formFile1");
        File file = new File(url.toURI());
        File[] files = file.listFiles();

        Assertions.assertDoesNotThrow(() -> validateFormMethod.invoke(this.fileService, (Object) files));
    }

    @Test
    @DisplayName("校验表单文件失败，包含多余文件")
    void testValidateFormFailedWhenHasExtraFile() throws NoSuchMethodException, URISyntaxException {
        Method validateFormMethod = FileServiceImpl.class.getDeclaredMethod("validateForm", File[].class);
        validateFormMethod.setAccessible(true);

        java.net.URL url = FileServiceImplTest.class.getClassLoader().getResource("file/formFile2");
        File file = new File(url.toURI());
        File[] files = file.listFiles();
        InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class,
                () -> validateFormMethod.invoke(this.fileService, (Object) files));
        Assertions.assertInstanceOf(AippException.class, exception.getCause());
        AippException aippException = ObjectUtils.cast(exception.getCause());
        Assertions.assertEquals(AippErrCode.CONTAIN_EXTRA_FILE.getErrorCode(), aippException.getCode());
    }

    @Test
    @DisplayName("校验表单文件失败，缺少文件")
    void testValidateFormFailedWhenMissingFile() throws NoSuchMethodException, URISyntaxException {
        Method validateFormMethod = FileServiceImpl.class.getDeclaredMethod("validateForm", File[].class);
        validateFormMethod.setAccessible(true);

        java.net.URL url = FileServiceImplTest.class.getClassLoader().getResource("file/formFile3");
        File file = new File(url.toURI());
        File[] files = file.listFiles();
        InvocationTargetException exception = Assertions.assertThrows(InvocationTargetException.class,
                () -> validateFormMethod.invoke(this.fileService, (Object) files));
        Assertions.assertInstanceOf(AippException.class, exception.getCause());
        AippException aippException = ObjectUtils.cast(exception.getCause());
        Assertions.assertEquals(AippErrCode.FORM_FILE_MISSING.getErrorCode(), aippException.getCode());
    }

    @Test
    @DisplayName("获取表单文件列表成功")
    void testGetFormFilesSuccess()
            throws NoSuchMethodException, URISyntaxException, InvocationTargetException, IllegalAccessException {
        Method getFormFilesMethod = FileServiceImpl.class.getDeclaredMethod("getFiles", File.class);
        getFormFilesMethod.setAccessible(true);

        java.net.URL url = FileServiceImplTest.class.getClassLoader().getResource("file/formFile1");
        File file = new File(url.toURI());

        File[] files = ObjectUtils.cast(getFormFilesMethod.invoke(this.fileService, file));
        Assertions.assertEquals(3, files.length);
    }

    @NotNull
    private OperationContext buildOperationContext() {
        return new OperationContext();
    }
}