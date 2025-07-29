/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import lombok.Data;

/**
 * 知识库界面选项国际化信息。
 *
 * @author 马朝阳
 * @since 2024-10-12
 */
@Data
public class KnowledgeI18nInfo {
    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数描述。
     */
    private String description;

    public KnowledgeI18nInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
