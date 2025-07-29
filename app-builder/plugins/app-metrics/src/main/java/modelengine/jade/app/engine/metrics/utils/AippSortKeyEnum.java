/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.utils;

import lombok.Getter;

import java.util.Arrays;

/**
 * 枚举类，用于表示排序的key
 *
 * @author 孙怡菲
 * @since 2024-10-16
 */
@Getter
public enum AippSortKeyEnum {
    CREATE_AT("created_at"),
    UPDATE_AT("updated_at");

    private final String key;

    AippSortKeyEnum(String key) {
        this.key = key;
    }

    /**
     * 根据输入的key获取对应的枚举值
     *
     * @param key 需要查找的key
     * @return 返回对应的枚举值
     */
    public static AippSortKeyEnum getSortKey(String key) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
