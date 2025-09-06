/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.condition;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * MetaInstance 条件实体类
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
@Data
@Builder
public class MetaInstanceCondition {
    private List<String> ids;
    private List<String> metaIds;
    private long offset;
    private int limit;
}
