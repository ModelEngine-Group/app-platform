/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 用来向前端展示aippId和version
 *
 * @author 姚江
 * @since 2024-08-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaVo {
    @Property(description = "meta的Id，即aippId", name = "aipp_id")
    private String id;

    @Property(description = "aipp的版本", name = "version")
    private String version;
}
