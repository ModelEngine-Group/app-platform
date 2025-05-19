/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * i18n国际化资源的结构体
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class I18nPo {
    private String id;
    private String key;
    private String language;
    private String value;
}
