/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common;

import java.io.IOException;

/**
 * 音频提取文本接口
 *
 * @author 刘信宏
 * @since 2024/1/19
 */
@FunctionalInterface
public interface AudioTextFunction<T1, R> {
    /**
     * apply
     *
     * @param t1 t1
     * @return R
     * @throws IOException IOException
     */
    R apply(T1 t1) throws IOException;
}
