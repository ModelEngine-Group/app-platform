/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.code;

import modelengine.jade.common.code.RetCode;

/**
 * 知识库错误码。
 *
 * @author 陈潇文
 * @since 205-04-24
 */
public enum KnowledgeManagerRetCode implements RetCode {
    /**
     * 查询到默认使用的config数量超过一个。
     */
    QUERY_CONFIG_LENGTH_MORE_THAN_ONE(130703002, "在{0}知识库平台有超过1个配置为默认使用，请检查知识库配置。");
    private final int code;
    private final String msg;

    KnowledgeManagerRetCode(int code, String msg) {
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
