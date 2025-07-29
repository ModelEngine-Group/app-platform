/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

/**
 * 应用实例的数据类，当实例作为领域对象使用时的数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public class TaskInstanceDomainEntity extends TaskInstanceEntity<TaskInstanceDomainEntity> {
    TaskInstanceDomainEntity() {
        super();
    }

    @Override
    public TaskInstanceDomainEntity self() {
        return this;
    }
}
