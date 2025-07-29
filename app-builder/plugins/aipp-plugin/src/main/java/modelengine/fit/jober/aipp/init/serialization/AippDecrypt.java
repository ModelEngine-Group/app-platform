/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.init.serialization;

import modelengine.fit.security.Decryptor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.conf.ConfigDecryptor;
import modelengine.fitframework.inspection.Validation;

import java.util.Locale;
import java.util.Optional;

/**
 * AippDecrypt
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Component
public class AippDecrypt implements ConfigDecryptor {
    private final Decryptor decryptor;

    public AippDecrypt(Decryptor decryptor) {
        this.decryptor = Validation.notNull(decryptor, "The decryptor cannot be null.");
    }

    private static boolean isEncrypted(String value) {
        return value.toLowerCase(Locale.ROOT).startsWith("enc(") && value.endsWith(")");
    }

    @Override
    public Optional<String> decrypt(String key, String originValue) {
        if (!isEncrypted(originValue)) {
            return Optional.empty();
        }
        String encrypted = originValue.substring(4, originValue.length() - 1);
        String decrypted = this.decryptor.decrypt(encrypted);
        return Optional.of(decrypted);
    }
}
