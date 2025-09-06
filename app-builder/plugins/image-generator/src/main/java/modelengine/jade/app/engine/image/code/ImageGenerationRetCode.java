/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.image.code;

import modelengine.jade.common.code.RetCode;
import modelengine.jade.common.model.ModelInfo;

/**
 * 文生图模块返回码枚举，返回码最大值为 256。
 *
 * @author 何嘉斌
 * @since 2025-01-21
 */
public enum ImageGenerationRetCode implements RetCode, ModelInfo {
    /**
     * 解析 URL 失败。
     */
    MALFORMED_URL(131103001, "Malformed URL has occurred: {0}.");

    private final int code;
    private final String msg;

    ImageGenerationRetCode(int code, String msg) {
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