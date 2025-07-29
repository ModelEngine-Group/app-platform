/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 表单上传dto
 *
 * @author 陈潇文
 * @since 2024/11/18
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormFileDto {
    @Property(name = "imgUrl")
    private String imgUrl;

    @Property(name = "iframeUrl")
    private String iframeUrl;

    @Property(name = "fileUuid")
    private String fileUuid;

    @Property(name = "fileName")
    private String fileName;

    @Property(name = "schema")
    private Map<String, Object> schema;
}
