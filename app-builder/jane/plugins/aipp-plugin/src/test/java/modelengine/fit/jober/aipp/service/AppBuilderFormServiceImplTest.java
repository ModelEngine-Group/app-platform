/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.condition.FormQueryCondition;
import modelengine.fit.jober.aipp.config.AippFormCreateConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.dto.AppBuilderFormDto;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.impl.AppBuilderFormServiceImpl;
import modelengine.fit.jober.common.RangedResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为 {@link AppBuilderFormServiceImpl} 提供测试
 *
 * @author 陈潇文
 * @since 2024/11/22
 */
@ExtendWith(MockitoExtension.class)
public class AppBuilderFormServiceImplTest {
    private static AppBuilderForm form;

    private AppBuilderFormService appBuilderFormService;

    @Mock
    private AppBuilderFormRepository appBuilderFormRepository;

    private AippFormCreateConfig aippFormCreateConfig;

    @Mock
    private UploadedFileManageService uploadedFileManageService;

    @BeforeAll
    static void beforeAll() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("fileUuid", "456");
        appearance.put("fileName", "form.zip");
        appearance.put("schema", new HashMap<>());
        form = AppBuilderForm.builder().appearance(appearance).build();
    }

    @BeforeEach
    void before() {
        this.aippFormCreateConfig = new AippFormCreateConfig();
        this.aippFormCreateConfig.setMaximumNum(400L);
        this.appBuilderFormService = new AppBuilderFormServiceImpl(this.appBuilderFormRepository,
                this.aippFormCreateConfig, this.uploadedFileManageService, null);
    }

    @Test
    @DisplayName("创建智能表单成功")
    void testCreateSmartFormSuccess() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("fileUuid", "456");
        appearance.put("fileName", "form.zip");
        appearance.put("schema", new HashMap<>());
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder().name("test").appearance(appearance).build();
        doNothing().when(this.appBuilderFormRepository).insertOne(any(AppBuilderForm.class));
        AppBuilderFormDto result = this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        verify(this.appBuilderFormRepository, times(1)).insertOne(any(AppBuilderForm.class));
        Assertions.assertEquals(result.getName(), "test");
        Assertions.assertEquals(result.getAppearance().get("imgUrl"), "/var/share/123/form.png");
        Assertions.assertEquals(result.getAppearance().get("iframeUrl"), "/var/share/123/build/index.html");
        Assertions.assertEquals(result.getAppearance().get("fileUuid"), "456");
        Assertions.assertEquals(result.getAppearance().get("fileName"), "form.zip");
        Assertions.assertEquals(result.getAppearance().get("description"), "test form");
        Assertions.assertEquals(result.getVersion(), "1.0.0");
    }

    @Test
    @DisplayName("缺少imgUrl, 创建智能表单失败")
    void testCreateSmartFormFailWhenWithoutImgUrl() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("fileUuid", "456");
        appearance.put("fileName", "form.zip");
        appearance.put("schema", new HashMap<>());
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder().name("test").appearance(appearance).build();
        AippException exception = Assertions.assertThrows(AippException.class, () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002131, exception.getCode());
    }

    @Test
    @DisplayName("缺少iframeUrl, 创建智能表单失败")
    void testCreateSmartFormFailWhenWithoutiframeUrl() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("fileUuid", "456");
        appearance.put("fileName", "form.zip");
        appearance.put("schema", new HashMap<>());
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder().name("test").appearance(appearance).build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002131, exception.getCode());
    }

    @Test
    @DisplayName("缺少schema, 创建智能表单失败")
    void testCreateSmartFormFailWhenWithoutSchema() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("fileUuid", "456");
        appearance.put("fileName", "form.zip");
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder().name("test").appearance(appearance).build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002131, exception.getCode());
    }

    @Test
    @DisplayName("缺少fileUuid, 创建智能表单失败")
    void testCreateSmartFormFailWhenWithoutfileUuid() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("schema", new HashMap<>());
        appearance.put("fileName", "form.zip");
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder().name("test").appearance(appearance).build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002131, exception.getCode());
    }

    @Test
    @DisplayName("缺少fileName, 创建智能表单失败")
    void testCreateSmartFormFailWhenWithoutfileName() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("schema", new HashMap<>());
        appearance.put("fileUuid", "456");
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder().name("test").appearance(appearance).build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002131, exception.getCode());
    }

    @Test
    @DisplayName("表单数量已经达到上限，创建表单失败")
    void testCreateSmartFormFailWhenNumUpToMaximum() {
        AppBuilderFormDto appBuilderFormDto =
                AppBuilderFormDto.builder().id("formId").name("test").appearance(new HashMap<>()).build();
        when(this.appBuilderFormRepository.countWithType(anyString(), any())).thenReturn(400L);
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002134, exception.getCode());
    }

    @Test
    @DisplayName("表单名称重复，创建表单失败")
    void testCreateSmartFormFailWhenNameIsExisted() {
        AppBuilderFormDto appBuilderFormDto =
                AppBuilderFormDto.builder().id("formId").name("test").appearance(new HashMap<>()).build();
        when(this.appBuilderFormRepository.selectWithName(anyString(), any())).thenReturn(form);
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002135, exception.getCode());
    }

    @Test
    @DisplayName("表单名称不符合规范，创建表单失败")
    void testCreateSmartFormFailWhenNameNotUpToFormat() {
        AppBuilderFormDto appBuilderFormDto =
                AppBuilderFormDto.builder().id("formId").name("123***haha").appearance(new HashMap<>()).build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002917, exception.getCode());
    }

    @Test
    @DisplayName("表单名称超出最大长度，创建表单失败")
    void testCreateSmartFormFailWhenNameLengthOutOfBound() {
        AppBuilderFormDto appBuilderFormDto = AppBuilderFormDto.builder()
                .id("formId")
                .name("testtesttesttesttesttesttesttesttesttest"
                        + "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest")
                .appearance(new HashMap<>())
                .build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002129, exception.getCode());
    }

    @Test
    @DisplayName("表单描述超出最大长度，创建表单失败")
    void testCreateSmartFormFailWhenDescriptionLengthOutOfBound() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description",
                "test form description.test form description.test form description.test form description.test form "
                        + "description.test form description.test form description.test form description.test form "
                        + "description.test form description.test form description.test form description.test form "
                        + "description.test form description.test form description.test form description.test form "
                        + "description.test form description.test form description.test form description.test form "
                        + "description.test form description.test form description.test form description.test form "
                        + "description.test form description.test form description.test form description.test form "
                        + "description.");
        AppBuilderFormDto appBuilderFormDto =
                AppBuilderFormDto.builder().id("formId").name("test").appearance(appearance).build();
        AippException exception = Assertions.assertThrows(AippException.class,
                () -> {
            this.appBuilderFormService.create(appBuilderFormDto, new OperationContext());
        });
        Assertions.assertEquals(90002130, exception.getCode());
    }

    @Test
    @DisplayName("更新表单成功")
    void testUpdateSmartFormSuccess() {
        Map<String, Object> appearance = new HashMap<>();
        appearance.put("description", "test form");
        appearance.put("imgUrl", "/var/share/123/form.png");
        appearance.put("iframeUrl", "/var/share/123/build/index.html");
        appearance.put("fileUuid", "456");
        appearance.put("fileName", "form.zip");
        appearance.put("schema", new HashMap<>());
        AppBuilderFormDto appBuilderFormDto =
                AppBuilderFormDto.builder().id("formId").name("test").appearance(appearance).build();
        doNothing().when(this.appBuilderFormRepository).updateOne(any(AppBuilderForm.class));
        when(this.appBuilderFormRepository.selectWithId(any())).thenReturn(form);
        AppBuilderFormDto result =
                this.appBuilderFormService.update(appBuilderFormDto, appBuilderFormDto.getId(), new OperationContext());
        verify(this.appBuilderFormRepository, times(1)).updateOne(any(AppBuilderForm.class));
        Assertions.assertEquals(result.getName(), "test");
        Assertions.assertEquals(result.getAppearance().get("imgUrl"), "/var/share/123/form.png");
        Assertions.assertEquals(result.getAppearance().get("iframeUrl"), "/var/share/123/build/index.html");
        Assertions.assertEquals(result.getAppearance().get("fileUuid"), "456");
        Assertions.assertEquals(result.getAppearance().get("fileName"), "form.zip");
        Assertions.assertEquals(result.getAppearance().get("description"), "test form");
        Assertions.assertEquals(result.getId(), "formId");
    }

    @Test
    @DisplayName("表单id有误，更新表单失败")
    void testUpdateSmartFormFailWhenFormIdIsError() {
        AppBuilderFormDto appBuilderFormDto =
                AppBuilderFormDto.builder().id("formId").name("test").appearance(new HashMap<>()).build();
        when(this.appBuilderFormRepository.selectWithId(any())).thenReturn(null);
        AippException exception = Assertions.assertThrows(AippException.class, () -> {
            this.appBuilderFormService.update(appBuilderFormDto, appBuilderFormDto.getId(), new OperationContext());
        });
        Assertions.assertEquals(90002133, exception.getCode());
    }

    @Test
    @DisplayName("查询表单成功")
    void testQuerySmartFormSuccess() {
        AppBuilderForm appBuilderForm1 =
                AppBuilderForm.builder().id("formId1").name("test1").appearance(new HashMap<>()).build();
        AppBuilderForm appBuilderForm2 =
                AppBuilderForm.builder().id("formId2").name("test2").appearance(new HashMap<>()).build();
        when(this.appBuilderFormRepository.selectWithCondition(any(FormQueryCondition.class))).thenReturn(Arrays.asList(
                appBuilderForm1,
                appBuilderForm2));
        RangedResultSet<AppBuilderFormDto> result =
                this.appBuilderFormService.query(0L, 10, null, new OperationContext());
        List<AppBuilderFormDto> forms = result.getResults();
        Assertions.assertEquals(forms.size(), 2);
        Assertions.assertEquals(forms.get(0).getId(), "formId1");
        Assertions.assertEquals(forms.get(1).getId(), "formId2");
    }

    @Test
    @DisplayName("删除表单成功")
    void testDeleteSmartFormSuccess() {
        when(this.appBuilderFormRepository.selectWithId(anyString())).thenReturn(form);
        doNothing().when(this.appBuilderFormRepository).delete(anyList());
        doNothing().when(this.uploadedFileManageService).changeRemovableWithFileUuid(anyString(), any());
        Assertions.assertDoesNotThrow(() -> this.appBuilderFormService.delete("formId", new OperationContext()));
    }
}
