/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.jade.common.query.PageQueryParam;

import lombok.Data;
import lombok.EqualsAndHashCode;
import modelengine.fitframework.annotation.Property;

/**
 * 知识库列表检索参数实体。
 *
 * @author 何嘉斌
 * @since 2024-09-26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListRepoQueryParam extends PageQueryParam {
    @Property(description = "知识库名称")
    private String repoName;
}