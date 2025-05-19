/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.Getter;
import lombok.Setter;
import modelengine.fitframework.flowable.Emitter;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * 表示建立应用会话的类。
 *
 * @param <T> 表示所发送的数据的类型的 {@link T}。
 * @author 陈潇文
 * @since 2024-09-26
 */
@Getter
@Setter
public class ChatSession<T> {
    Emitter<Object> emitter;
    String appId;
    boolean isDebug;
    Locale locale;
    LocalDateTime expireTime;
    boolean isOccupied;

    public ChatSession(Emitter<Object> emitter, String appId, boolean isDebug, Locale locale) {
        this.appId = appId;
        this.emitter = emitter;
        this.isDebug = isDebug;
        this.locale = locale;
        this.expireTime = LocalDateTime.now().plusMinutes(30);
        this.isOccupied = false;
    }
}
