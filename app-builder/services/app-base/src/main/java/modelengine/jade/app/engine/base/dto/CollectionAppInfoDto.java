/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.dto;

import modelengine.fitframework.annotation.Property;
import modelengine.jade.app.engine.base.po.UsrAppInfoAndCollectionPo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 收藏应用消息体
 *
 * @since 2024-5-29
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionAppInfoDto {
    @Property(description = "收藏记录列表")
    List<UsrAppInfoAndCollectionPo> collectionPoList;

    @Property(description = "默认应用")
    UsrAppInfoAndCollectionPo defaultApp;
}
