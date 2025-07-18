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
     * 从数据库中读出iconValue后去除/v1/api前面的前缀。
     *
     * @param storedValue 数据库中存的值的 {@link String}。
     * @return 修改后的值的 {@link String}。
     */
    String toFrontend(String storedValue);

    /**
     * 从前端传入的iconValue中去除/v1/api前面的前缀后落库。
     *
     * @param frontendValue 前端传入的值的 {@link String}。
     * @return 去除前缀后的值的 {@link String}。
     */
    String toStorage(String frontendValue);
}
