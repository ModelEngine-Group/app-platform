/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph;

import modelengine.fit.security.Decryptor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.conf.ConfigDecryptor;

import java.util.Locale;
import java.util.Optional;

/**
 * 配置文件解密配置器。
 *
 * @author 陈镕希
 * @since 2023-09-01
 */
@Component
public class FlowGraphSccConfigDecryptor implements ConfigDecryptor {
    private final Decryptor decryptor;

    public FlowGraphSccConfigDecryptor(Decryptor decryptor) {
        this.decryptor = decryptor;
    }

    private static boolean isEncrypted(String value) {
        return value.toLowerCase(Locale.ROOT).startsWith("enc(") && value.endsWith(")");
    }

    @Override
    public Optional<String> decrypt(String key, String originValue) {
        if (!isEncrypted(originValue)) {
            return Optional.empty();
        }
        return Optional.of(this.decryptor.decrypt(originValue.substring(4, originValue.length() - 1)));
    }
}