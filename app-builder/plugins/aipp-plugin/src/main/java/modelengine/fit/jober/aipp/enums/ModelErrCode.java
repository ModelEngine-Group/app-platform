/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import java.util.Arrays;

/**
 * 模型报错枚举
 *
 * @author 陈潇文
 * @since 2024-10-8
 */
public enum ModelErrCode {
    MODEL_SERVICE_ERROR(500),
    MODEL_ROUTER_ERROR(404),
    REQUEST_PARAM_ERROR(400),
    INVALID_REQUEST_PARAM(422),
    GENERAL_ERROR(000);

    private final int errorCode;

    ModelErrCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 通过code获取枚举
     *
     * @param code code
     * @return 枚举对象
     */
    public static ModelErrCode getErrorCodes(int code) {
        return Arrays.stream(ModelErrCode.values())
                .filter(o -> code == o.errorCode)
                .findFirst()
                .orElse(ModelErrCode.GENERAL_ERROR);
    }
}
