/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 表示 Json 的数据对象。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
@Data
@AllArgsConstructor
public class JsonEntity {
    /**
     * Json 文件的具体内容。
     */
    private List<String> contents;

    /**
     * Json 文件的数据约束。
     */
    private String schema;
}
