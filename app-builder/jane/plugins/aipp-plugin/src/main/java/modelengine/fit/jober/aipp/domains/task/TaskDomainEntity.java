/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

/**
 * 普通数据类，当Task作为领域对象时使用的entity类.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class TaskDomainEntity extends TaskEntity<TaskDomainEntity> {
    TaskDomainEntity() {}

    @Override
    public TaskDomainEntity self() {
        return this;
    }
}
