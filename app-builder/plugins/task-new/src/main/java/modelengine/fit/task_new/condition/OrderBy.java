/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.condition;

import lombok.Builder;
import lombok.Data;

/**
 * 排序类型实体类
 *
 * @author 孙怡菲
 * @since 2025-03-31
 */
@Data
@Builder
public class OrderBy {
    private String field;

    private String direction;
}
