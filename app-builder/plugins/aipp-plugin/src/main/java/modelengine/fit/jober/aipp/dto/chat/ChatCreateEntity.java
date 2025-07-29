/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * chat 创建参数类.
 *
 * @author 张越
 * @since 2025-01-14
 */
@Data
@Builder
public class ChatCreateEntity {
    private String appId;
    private String appVersion;
    private Map<String, String> attributes;
    private String chatName;
    private String chatId;
    private String taskInstanceId;
}
