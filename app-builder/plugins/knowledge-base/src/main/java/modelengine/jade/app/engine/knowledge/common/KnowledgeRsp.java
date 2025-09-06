/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.common;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

/**
 * 知识库异常场景统一返回结构体。
 *
 * @since 2024-06-18
 */
@Data
public class KnowledgeRsp {
    @Property(description = "状态码", example = "0")
    private int code;
    @Property(description = "状态信息", example = "success")
    private String msg;

    /**
     * 静态构造函数
     *
     * @param code 返回数据错误码
     * @param msg 返回错误信息
     * @return KnowledgeRsp构造的对象
     */
    public static KnowledgeRsp err(int code, String msg) {
        KnowledgeRsp rsp = new KnowledgeRsp();
        rsp.setCode(code);
        rsp.setMsg(msg);
        return rsp;
    }
}
