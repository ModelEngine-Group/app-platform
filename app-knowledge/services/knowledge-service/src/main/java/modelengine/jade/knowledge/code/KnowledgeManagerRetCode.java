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
    QUERY_CONFIG_LENGTH_MORE_THAN_ONE(130703002, "在{0}知识库平台有超过1个配置为默认使用，请检查知识库配置。"),

    /**
     * 查询知识库列表接口失败。
     */
    QUERY_KNOWLEDGE_LIST_ERROR(130703003, "获取知识库列表失败，原因：{0}。"),

    /**
     * 请求知识库平台鉴权失败
     */
    AUTHENTICATION_ERROR(130703004, "知识库平台鉴权失败，原因：{0}。"),

    /**
     * 请求知识库平台内部失败
     */
    INTERNAL_SERVICE_ERROR(130703005, "知识库平台接口内部错误，原因：{0}。"),

    /**
     * 客户端请求错误
     */
    CLIENT_REQUEST_ERROR(130703006, "知识库平台请求错误，原因：{0}。"),

    /**
     * 资源不存在
     */
    NOT_FOUND(130703007, "知识库平台接口不存在，原因：{0}。"),

    /**
     * 知识库检索失败
     */
    QUERY_KNOWLEDGE_ERROR(130703008, "知识库检索失败，原因：{0}。"),

    /**
     * 相同的知识库配置已存在，请修改配置重试。
     */
    CONFIG_IS_EXISTED(130703009, "相同的知识库配置已存在，请修改配置重试。");

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
