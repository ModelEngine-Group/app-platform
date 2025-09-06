/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import static modelengine.fit.jober.aipp.constant.AippConstant.DOWNLOAD_FILE_ORIGIN;
import static modelengine.fit.jober.aipp.constant.AippConstant.NAS_SHARE_DIR;

import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.util.TenantUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link IconConverter} 的默认实现类。
 *
 * @author 陈镕希
 * @since 2025-07-16
 */
@Component
public class IconConverterImpl implements IconConverter {
    private final String contextRoot;

    private static final Pattern FILE_PATH_PATTERN;
    private static final String FILE_PATH_PARAM = "filePath=";
    private static final String FILE_NAME_PARAM = "&fileName=";

    static {
        String regex = FILE_PATH_PARAM + Pattern.quote(NAS_SHARE_DIR) + "/([^&]+)";
        FILE_PATH_PATTERN = Pattern.compile(regex);
    }

    public IconConverterImpl(@Value("${app-engine.contextRoot}") String contextRoot) {
        this.contextRoot = contextRoot;
    }

    @Override
    public String toFrontend(String storedValue) {
        if (StringUtils.isBlank(storedValue)) {
            return storedValue;
        }

        Matcher matcher = FILE_PATH_PATTERN.matcher(storedValue);
        String fileName = matcher.find() ? matcher.group(1) : storedValue;
        return buildFileUrl(fileName);
    }

    @Override
    public String toStorage(String frontendValue) {
        if (StringUtils.isBlank(frontendValue)) {
            return frontendValue;
        }

        Matcher matcher = FILE_PATH_PATTERN.matcher(frontendValue);
        return matcher.find() ? matcher.group(1) : frontendValue;
    }

    private String buildFileUrl(String fileName) {
        String originWithTenant = String.format(DOWNLOAD_FILE_ORIGIN, TenantUtils.getDefaultTenantId());
        return this.contextRoot + originWithTenant + FILE_PATH_PARAM + NAS_SHARE_DIR + "/" + fileName
                + FILE_NAME_PARAM + fileName;
    }
}
