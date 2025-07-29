/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dummy;

import modelengine.fit.jane.common.entity.OperationContext;

public class OperationContextDummy {
    public static final String DUMMY_TENANT_ID = "a1b2c3d4e5f6";
    public static final String DUMMY_OPERATOR = "Taylor 00000001";
    public static final String DUMMY_GLOBAL_USER_ID = "167777777509824";
    public static final String DUMMY_ACCOUNT = "t00000001";
    public static final String DUMMY_EMPLOYEE_NUMBER = "00000001";
    public static final String DUMMY_NAME = "Taylor";
    public static final String DUMMY_OPERATOR_IP = "127.0.0.1";
    public static final String DUMMY_SOURCE_PLATFORM = "some Platform";
    public static final String DUMMY_LANGUAGE = "zh";

    public static OperationContext getDummy() {
        return new OperationContext(DUMMY_TENANT_ID,
                DUMMY_OPERATOR,
                DUMMY_GLOBAL_USER_ID, DUMMY_ACCOUNT,
                DUMMY_EMPLOYEE_NUMBER,
                DUMMY_NAME,
                DUMMY_OPERATOR_IP,
                DUMMY_SOURCE_PLATFORM,
                DUMMY_LANGUAGE);
    }

    public static boolean operationContextDummyMatcher(OperationContext context) {
        return DUMMY_TENANT_ID.equals(context.getTenantId()) && DUMMY_OPERATOR.equals(context.getOperator())
                && DUMMY_GLOBAL_USER_ID.equals(context.getGlobalUserId())
                && DUMMY_ACCOUNT.equals(context.getAccount())
                && DUMMY_EMPLOYEE_NUMBER.equals(context.getEmployeeNumber()) && DUMMY_NAME.equals(context.getName())
                && DUMMY_OPERATOR_IP.equals(context.getOperatorIp())
                && DUMMY_SOURCE_PLATFORM.equals(context.getSourcePlatform())
                && DUMMY_LANGUAGE.equals(context.getLanguage());
    }
}
