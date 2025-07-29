/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * PPT页面的数据传输对象
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PptPageDto {
    private String title;

    private String content;
}
