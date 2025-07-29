/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fitframework.conf.ConfigDecryptor;

import java.util.Optional;

/**
 * 为 {@link ConfigDecryptor} 提供默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
public class DefaultConfigDecryptor implements ConfigDecryptor {
    @Override
    public Optional<String> decrypt(String key, String originValue) {
        return Optional.of(originValue);
    }
}
