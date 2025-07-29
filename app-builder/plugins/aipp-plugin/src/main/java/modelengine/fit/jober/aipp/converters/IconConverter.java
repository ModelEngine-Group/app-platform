/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters;

/**
 * 头像转换器接口。
 *
 * @author 陈镕希
 * @since 2025-07-16
 */
public interface IconConverter {
    /**
     * 从数据库中读出 {@code iconValue} 后添加前端直接调用前缀。
     *
     * @param storedValue 数据库中存的值的 {@link String}。
     * @return 补充前端直接调用前缀后的值的 {@link String}。
     */
    String toFrontend(String storedValue);

    /**
     * 从前端传入的 {@code iconValue} 中截取文件名部分后落库。
     *
     * @param frontendValue 前端传入的值的 {@link String}。
     * @return 截取文件名部分后的值的 {@link String}。
     */
    String toStorage(String frontendValue);
}
