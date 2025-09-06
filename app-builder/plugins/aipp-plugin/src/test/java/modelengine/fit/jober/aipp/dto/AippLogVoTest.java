/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fit.jober.aipp.controller.AippLogController;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.vo.AippLogVO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 为 {@link AippLogController} 提供测试
 *
 * @author 吴宇伦
 * @since 2024-08-22
 */
@ExtendWith(MockitoExtension.class)
public class AippLogVoTest {
    @Test
    void testAippLogVO() {
        AippLogCreateDto aippLogCreateDto = AippLogCreateDto.builder().aippId("123")
                .logId("123").version("1.0.0").aippType("PREVIEW").instanceId("123")
                .logData("123").logType("MSG").path("/123").chatId("123").atChatId("123").build();
        AippLogVO aippLogVO = AippLogVO.fromCreateDto(aippLogCreateDto);
        Assertions.assertEquals(aippLogVO.getLogId(), "123");
    }
}
