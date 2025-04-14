/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用对话repo层。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
public interface AippChatRepository {
    List<String> getExpiredChatIds(int expiredDays, int limit);

    void forceDeleteChat(List<String> chatIds);
}
