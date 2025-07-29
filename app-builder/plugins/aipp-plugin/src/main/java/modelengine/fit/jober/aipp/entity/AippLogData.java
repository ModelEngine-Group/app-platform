/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;

import java.util.Map;

/**
 * aipp实例历史记录json数据
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class AippLogData {
    @Property(description = "表单id，log_type为FORM时填充", name = "form_id")
    @JsonProperty("form_id")
    private String formId;

    @Property(description = "表单id，log_type为FORM时填充", name = "form_version")
    @JsonProperty("form_version")
    private String formVersion;

    @Property(description = "表单参数，log_type为FORM时填充", name = "form_args")
    @JsonProperty("form_args")
    private String formArgs;

    @Property(description = "提示信息，log_type为MSG时填充")
    private String msg;

    @Property(description = "表单渲染数据")
    private String formAppearance;

    @Property(description = "表单填充数据")
    private String formData;

    @Property(description = "日志额外信息")
    private Map<String, Object> infos;
}
