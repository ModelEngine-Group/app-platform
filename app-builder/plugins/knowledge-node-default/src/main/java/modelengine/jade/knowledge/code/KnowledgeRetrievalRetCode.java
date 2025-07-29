/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.code;

import modelengine.jade.common.code.RetCode;

/**
 * 知识检索节点返回码枚举。
 *
 * @author 马朝阳
 * @since 2024-11-28
 */
public enum KnowledgeRetrievalRetCode implements RetCode {
    /**
     * 表示知识检索节点参数类型不符合入参规范。
     */
    INVALID_PARAMETER_TYPE_ERROR(130701001, "Invalid knowledge retrieval parameter type: {0}.");

    private final int code;
    private final String msg;

    KnowledgeRetrievalRetCode(int code, String msg) {
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