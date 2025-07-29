/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.mapper.AppBuilderRuntimeInfoMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.DatabaseBaseTest;
import modelengine.fit.runtime.entity.Parameter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * {@link AppBuilderRuntimeInfoRepositoryImpl} 对应测试类。
 *
 * @author 杨祥宇
 * @since 2025-04-10
 */
class AppBuilderRuntimeInfoRepositoryImplTest extends DatabaseBaseTest {
    private final AppBuilderRuntimeInfoMapper mapper =
            sqlSessionManager.openSession(true).getMapper(AppBuilderRuntimeInfoMapper.class);
    private AppBuilderRuntimeInfoRepository runtimeInfoRepository;

    @BeforeEach
    void setUp() {
        this.runtimeInfoRepository = new AppBuilderRuntimeInfoRepositoryImpl(mapper);
    }

    @Test
    @DisplayName("测试成功获取超期的运行时信息")
    void testGetExpiredRuntimeInfosSuccess() {
        AppBuilderRuntimeInfo info = genRuntimeInfo();
        this.runtimeInfoRepository.insertOne(info);
        List<AppBuilderRuntimeInfo> expiredRuntimeInfos = this.runtimeInfoRepository.selectByTraceId("0");
        Assertions.assertEquals(0, expiredRuntimeInfos.size());
    }

    private AppBuilderRuntimeInfo genRuntimeInfo() {
        Parameter parameter = new Parameter();
        return AppBuilderRuntimeInfo.builder()
                .traceId("1")
                .flowDefinitionId("1")
                .instanceId("1")
                .nodeId("1")
                .nodeType("PREVIEW")
                .startTime(1)
                .endTime(2)
                .status("ERROR")
                .parameters(List.of(parameter))
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("测试成功根据 id 列表删除运行信息")
    void testDeleteRuntimeInfosSuccess() {
        this.runtimeInfoRepository.insertOne(genRuntimeInfo());
        this.mapper.deleteRuntimeInfos(Arrays.asList(1L, 2L));
        List<AppBuilderRuntimeInfo> expiredRuntimeInfos = this.runtimeInfoRepository.selectByTraceId("1");
        Assertions.assertEquals(0, expiredRuntimeInfos.size());
    }
}