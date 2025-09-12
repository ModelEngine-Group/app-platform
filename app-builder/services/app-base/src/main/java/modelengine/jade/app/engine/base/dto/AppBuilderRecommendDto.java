/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.dto;

import lombok.Data;

/**
 * 获取猜你想问数据的消息体。
 *
 * @author 杨海波
 * @since 2024-05-25
 */
@Data
public class AppBuilderRecommendDto {
    // 上一条问答对
    private String question;
    private String answer;
    private String model;
    private String modelTag;
    private String appOwner;
}
