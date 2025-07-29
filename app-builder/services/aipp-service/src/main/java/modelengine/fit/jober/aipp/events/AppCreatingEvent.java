/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.events;


import modelengine.fitframework.event.Event;

/**
 * 应用创建事件。
 *
 * @author 李金绪
 * @since 2024-12-05
 */
public class AppCreatingEvent implements Event {
    private final Object publisher;

    public AppCreatingEvent(Object publisher) {
        this.publisher = publisher;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }
}
