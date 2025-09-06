/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;

/**
 * 为 {@link AppInfo} 提供默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Alias("Default-Impl")
@Component
public class BaseAppInfo implements AppInfo {
    private final String id;

    private final String key;

    private final String token;

    public BaseAppInfo() {
        this.id = "jade";
        this.key = "jade";
        this.token = "jade";
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
