/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用于构建MindJsonElement对象的类
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Getter
@AllArgsConstructor
public class MindJsonElement {
    String name;
    String children;

    /**
     * 将name和children格式化为JSON字符串
     *
     * @param name name
     * @param children children
     * @return JSON格式字符串
     */
    public static String packToElementJson(String name, String children) {
        return String.format("{\"name\":\"%s\",\"children\":[%s]}", name, children);
    }
}
