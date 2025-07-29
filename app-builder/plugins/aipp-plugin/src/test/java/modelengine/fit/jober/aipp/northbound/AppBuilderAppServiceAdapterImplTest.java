/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.chat.AppMetadata;
import modelengine.fit.jober.aipp.dto.chat.AppQueryParams;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link AippChatServiceAdapterImpl} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 AppBuilderAppServiceAdapterImpl")
public class AppBuilderAppServiceAdapterImplTest {
    private final AppBuilderAppService appBuilderAppService = mock(AppBuilderAppService.class);
    private final AppBuilderAppServiceAdapterImpl appBuilderAppServiceAdapterImpl =
            new AppBuilderAppServiceAdapterImpl(appBuilderAppService);

    @Test
    @DisplayName("测试应用元数据类转换为适配器类。")
    void testDtoConvertToAdapter() {
        AppBuilderAppMetadataDto appBuilderAppMetadataDto1 = mock(AppBuilderAppMetadataDto.class);
        AppBuilderAppMetadataDto appBuilderAppMetadataDto2 = mock(AppBuilderAppMetadataDto.class);
        AppMetadata appMetadata1 = mock(AppMetadata.class);
        AppMetadata appMetadata2 = mock(AppMetadata.class);
        when(appBuilderAppMetadataDto1.getType()).thenReturn("testType1");
        when(appBuilderAppMetadataDto1.getName()).thenReturn("testName1");
        when(appBuilderAppMetadataDto2.getType()).thenReturn("testType2");
        when(appBuilderAppMetadataDto2.getName()).thenReturn("testName2");
        when(appMetadata1.getType()).thenReturn("testType1");
        when(appMetadata1.getName()).thenReturn("testName1");
        when(appMetadata2.getType()).thenReturn("testType2");
        when(appMetadata2.getName()).thenReturn("testName2");

        Rsp<RangedResultSet<AppBuilderAppMetadataDto>> rsp =
                Rsp.ok(RangedResultSet.create(Arrays.asList(appBuilderAppMetadataDto1, appBuilderAppMetadataDto2),
                        new RangeResult(1, 2, 3)));
        RangedResultSet<AppMetadata> result =
                appBuilderAppServiceAdapterImpl.appMetadataDtoConvertToAdapter(rsp.getData());
        assertThat(result.getResults()).hasSize(2)
                .extracting(AppMetadata::getName)
                .containsExactly("testName1", "testName2");
        assertThat(result.getResults()).extracting(AppMetadata::getType).containsExactly("testType1", "testType2");
    }

    @Test
    @DisplayName("测试查询应用列表")
    void shouldOkWhenTestQueryAppList() {
        AppQueryParams params = AppQueryParams.builder()
                .ids(Arrays.asList("id1", "id2"))
                .excludeNames(Arrays.asList("name1", "name2"))
                .name("name")
                .state("active")
                .offset(100)
                .limit(20)
                .type("app")
                .build();
        OperationContext operationContext = new OperationContext();
        operationContext.setTenantId("tenantId");
        List<AppBuilderAppMetadataDto> metaDtoList = new ArrayList<>();
        metaDtoList.add(AppBuilderAppMetadataDto.builder().name("name1").build());
        Rsp<RangedResultSet<AppBuilderAppMetadataDto>> rsp =
                Rsp.ok(RangedResultSet.create(metaDtoList, params.getOffset(), params.getLimit(), metaDtoList.size()));
        when(this.appBuilderAppService.list(any(AppQueryCondition.class),
                any(OperationContext.class),
                anyLong(),
                anyInt())).thenReturn(rsp);
        RangedResultSet<AppMetadata> resultSet = this.appBuilderAppServiceAdapterImpl.list(params, operationContext);
        assertThat(resultSet.getResults().size()).isEqualTo(1);
        assertThat(resultSet.getResults().get(0).getName()).isEqualTo("name1");
    }
}