/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.code;

import modelengine.jade.common.code.RetCode;

/**
 * 插件 edm 知识库的调用错误码。
 *
 * @author 何嘉斌
 * @since 2024-10-31
 */
public enum EdmManagerRetCode implements RetCode {
    /**
     * 获取 edm 知识库响应的数据内容失败。
     */
    EDM_EXCHANGE_ERROR(130703001, "The feature of linking to external knowledge base will be launched on 2025-04-30, so stay tuned.");

    private final int code;
    private final String msg;

    EdmManagerRetCode(int code, String msg) {
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