/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import lombok.Getter;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * 应用类型枚举类
 *
 * @author 李鑫
 * @since 2024/4/23
 */
@Getter
public enum AppCategory {
    APP("应用", "app", "APP", "APP", "system"),
    WATER_FLOW("工具流", "waterFlow", "TOOL", "WATERFLOW", "system"),
    FIT("工具", "fit", "TOOL", "FIT", "system");

    private final String description;
    private final String type;
    private final String category;
    private final String tag;
    private final String source;

    AppCategory(String description, String type, String category, String tag, String source) {
        this.description = description;
        this.type = type;
        this.category = category;
        this.tag = tag;
        this.source = source;
    }

    /**
     * 根据 {@code type} 找到枚举值
     *
     * @param type 表示类型。
     * @return {@link Optional} 包的枚举值。
     */
    public static Optional<AppCategory> findByType(String type) {
        for (AppCategory appCategory : AppCategory.values()) {
            if (StringUtils.equalsIgnoreCase(appCategory.getType(), type)) {
                return Optional.of(appCategory);
            }
        }
        return Optional.empty();
    }
}
