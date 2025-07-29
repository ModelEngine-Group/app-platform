/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc.persist.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 流程实例锁持久化类
 *
 * @author 李哲峰
 * @since 2023/11/29
 */
@Builder
@Getter
@Setter
public class FlowLockPO {
    private String lockKey;

    private LocalDateTime expiredAt;

    private String lockedClient;
}

