/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober;

import modelengine.fit.jober.entity.user.EmployeeDetail;
import modelengine.fitframework.annotation.Genericable;

/**
 * Jober人员服务Genericable
 *
 * @author 陈镕希
 * @since 2023-11-10
 */
public interface UserService {
    /**
     * 通过指定信息查询对应用户详情信息。
     *
     * @param employeeNumber 表示用户纯工号的 {@link String}。
     * @param uid 表示用户短工号的 {@link String}。
     * @param uuid 表示用户uuid的 {@link String}。
     * @param globalUserId 表示用户全局id的 {@link String}。
     * @return 表示用户详情信息的 {@link EmployeeDetail}。
     */
    @Genericable(id = "c923f4b7e8b6475a97a7e6cc91e60c1f")
    EmployeeDetail getEmployeeDetail(String employeeNumber, String uid, String uuid, String globalUserId);
}
