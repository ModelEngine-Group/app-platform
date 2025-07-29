/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.code;

import modelengine.jade.common.code.RetCode;

/**
 * 应用评估模块返回码枚举。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public enum AppEvalDatasetRetCode implements RetCode {
    /**
     * 评估数据 schema 校验失败。
     */
    DATA_INVALID_ERROR(130801001, "Invalid data: {0}."),

    /**
     * 评估数据已被删除。
     */
    DATA_DELETED_ERROR(130801002, "Unable to delete data id {0}."),

    /**
     * 评估数据集删除时有数据插入。
     */
    DATASET_DELETION_ERROR(130801003, "Unable to delete dataset id {0}."),

    /**
     * 用户信息不存在。
     */
    USER_CONTEXT_NOT_FOUND(130801004, "Unable to find user context."),

    /**
     * 评估数据文件无效。
     */
    FILE_INVALID_ERROR(130801005, "Invalid file error.");

    private final int code;

    private final String msg;

    AppEvalDatasetRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}