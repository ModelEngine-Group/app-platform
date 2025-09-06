/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fitframework.annotation.Component;

import java.util.Collections;
import java.util.List;

/**
 * {@link PersonService} 的默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Component
public class BasePersonService implements PersonService {
    @Override
    public List<EmployeeVO> searchEmployeeInfo(String keyword, String uid) {
        return Collections.emptyList();
    }

    @Override
    public EmployeeDetailVO getPersonInfo(String employeeNumber, String uid, String uuid, String globalUserId) {
        return new EmployeeDetailVO();
    }
}
