/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.dto;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户反馈信息传输类
 *
 * @since 2024-5-24
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedbackDto {
    @Property(description = "反馈记录 id")
    private Long id;

    @Property(description = "实例id")
    private String instanceId;

    @Property(description = "用户反馈 -1 未反馈 0 点赞 1 点踩")
    private Integer userFeedback;

    @Property(description = "用户反馈文本")
    private String userFeedbackText;
}
