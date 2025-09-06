/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 对象信息查询对象
 *
 * @author 邬涨财
 * @since 2024-10-15
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryChatInfoRequest {
    @Property(description = "condition", name = "condition")
    private Map<String, String> condition;

    @Property(description = "offset", name = "offset", defaultValue = "0")
    private Integer offset;

    @Property(description = "limit", name = "limit", defaultValue = "1")
    private Integer limit;

    @Property(description = "create by", name = "create_by")
    @JsonProperty("create_by")
    private String createBy;
}
