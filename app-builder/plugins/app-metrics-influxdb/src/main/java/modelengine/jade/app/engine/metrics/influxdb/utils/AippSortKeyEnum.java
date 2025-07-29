/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.utils;

import lombok.Getter;

import java.util.Arrays;

/**
 * 枚举类，用于表示排序的键。
 *
 * @author 高嘉乐
 * @since 2025/01/17
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
     * 根据输入的键获取对应的枚举值。
     *
     * @param key 表示需要查找的键的 {@link String}。
     * @return 返回对应枚举值的 {@link AippSortKeyEnum}。
     */
    public static AippSortKeyEnum getSortKey(String key) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
