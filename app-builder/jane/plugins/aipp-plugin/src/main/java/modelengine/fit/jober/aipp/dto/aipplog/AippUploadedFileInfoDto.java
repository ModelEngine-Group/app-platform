/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.aipplog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AIPP上传文件信息DTO
 *
 * @author 孙怡菲
 * @since 2024-04-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AippUploadedFileInfoDto {
    private String aippId;
    private String createUserAccount;
    private String filename;
    private String fileUuid;
}
