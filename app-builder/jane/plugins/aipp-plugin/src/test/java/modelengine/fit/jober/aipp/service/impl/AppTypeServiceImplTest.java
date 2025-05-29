/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jober.aipp.dto.AppTypeDto;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppTypeMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppTypePo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用业务类型服务实现的测试
 *
 * @author songyongtan
 * @since 2025/1/6
 */
@ExtendWith(MockitoExtension.class)
class AppTypeServiceImplTest {
    @Mock
    private AppBuilderAppTypeMapper appBuilderAppTypeMapper;

    private AppTypeServiceImpl appTypeService;

    @BeforeEach
    void setUp() {
        this.appTypeService = new AppTypeServiceImpl(this.appBuilderAppTypeMapper);
    }

    @Test
    void shouldCallMapperWhenQueryAll() {
        List<AppBuilderAppTypePo> expectResult = new ArrayList<>();
        String tenantId = "tenant";
        AppBuilderAppTypePo expectType = new AppBuilderAppTypePo("1", "type1", tenantId, LocalDateTime.now(),
                LocalDateTime.now());
        expectResult.add(expectType);
        Mockito.when(this.appBuilderAppTypeMapper.queryAll(tenantId)).thenReturn(expectResult);

        List<AppTypeDto> result = this.appTypeService.queryAll(tenantId);

        Assertions.assertEquals(expectResult.size(), result.size());
        Assertions.assertEquals(expectType.getId(), result.get(0).getId());
        Assertions.assertEquals(expectType.getName(), result.get(0).getName());
    }

    @Test
    void shouldCallMapperWhenQueryById() {
        String tenantId = "tenant";
        AppBuilderAppTypePo expectPo = new AppBuilderAppTypePo("1", "type1", tenantId, LocalDateTime.now(),
                LocalDateTime.now());
        Mockito.when(this.appBuilderAppTypeMapper.query(expectPo.getId(), tenantId)).thenReturn(expectPo);

        AppTypeDto result = this.appTypeService.query(expectPo.getId(), tenantId);

        Assertions.assertEquals(expectPo.getId(), result.getId());
        Assertions.assertEquals(expectPo.getName(), result.getName());
    }

    @Test
    void shouldCallMapperWhenAddGivenTypeWithId() {
        String tenantId = "tenant";
        AppBuilderAppTypePo expectPo = new AppBuilderAppTypePo("1", "type1", tenantId, LocalDateTime.now(),
                LocalDateTime.now());
        Mockito.doNothing()
                .when(this.appBuilderAppTypeMapper)
                .insert(Mockito.argThat(
                        po -> po.getId().equals(expectPo.getId()) && po.getName().equals(expectPo.getName())
                                && po.getTenantId().equals(expectPo.getTenantId())));

        AppTypeDto result = this.appTypeService.add(new AppTypeDto(expectPo.getId(), expectPo.getName()), tenantId);

        Mockito.verify(this.appBuilderAppTypeMapper, Mockito.times(1)).insert(Mockito.any(AppBuilderAppTypePo.class));
        Assertions.assertEquals(expectPo.getId(), result.getId());
        Assertions.assertEquals(expectPo.getName(), result.getName());
    }

    @Test
    void shouldReturnNewIdWhenAddGivenTypeWithoutId() {
        String tenantId = "tenant";
        AppBuilderAppTypePo expectPo = new AppBuilderAppTypePo("1", "type1", tenantId, LocalDateTime.now(),
                LocalDateTime.now());
        Mockito.doNothing()
                .when(this.appBuilderAppTypeMapper)
                .insert(Mockito.argThat(
                        po -> !po.getId().isEmpty() && po.getName().equals(expectPo.getName())
                                && po.getTenantId().equals(expectPo.getTenantId())));

        AppTypeDto result = this.appTypeService.add(new AppTypeDto("", expectPo.getName()), tenantId);

        Mockito.verify(this.appBuilderAppTypeMapper, Mockito.times(1)).insert(Mockito.any(AppBuilderAppTypePo.class));
        Assertions.assertFalse(result.getId().isEmpty());
        Assertions.assertEquals(expectPo.getName(), result.getName());
    }

    @Test
    void shouldCallMapperWhenDeleteGivenTypeId() {
        String tenantId = "tenant";
        AppBuilderAppTypePo expectPo = new AppBuilderAppTypePo("1", "type1", tenantId, LocalDateTime.now(),
                LocalDateTime.now());
        Mockito.doNothing().when(this.appBuilderAppTypeMapper).delete(expectPo.getId(), tenantId);

        this.appTypeService.delete(expectPo.getId(), tenantId);

        Mockito.verify(this.appBuilderAppTypeMapper, Mockito.times(1)).delete(expectPo.getId(), tenantId);
    }

    @Test
    void shouldCallMapperWhenUpdateGivenTypeInfo() {
        String tenantId = "tenant";
        AppBuilderAppTypePo expectPo = new AppBuilderAppTypePo("1", "type1", tenantId, LocalDateTime.now(),
                LocalDateTime.now());
        Mockito.doNothing()
                .when(this.appBuilderAppTypeMapper)
                .update(Mockito.argThat(
                        po -> po.getId().equals(expectPo.getId()) && po.getName().equals(expectPo.getName())
                                && po.getTenantId().equals(expectPo.getTenantId())));

        this.appTypeService.update(new AppTypeDto(expectPo.getId(), expectPo.getName()), tenantId);

        Mockito.verify(this.appBuilderAppTypeMapper, Mockito.times(1)).update(Mockito.any(AppBuilderAppTypePo.class));
    }
}