/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.export;

import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.AppImExportUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

/**
 * 应用导出配置中的应用信息配置。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportApp {
    private String name;
    private String tenantId;
    private String type;
    private String appBuiltType;
    private String version;
    private Map<String, Object> attributes;
    private String appCategory;
    private String appType;

    /**
     * 设置图标.
     *
     * @param icon 图标.
     */
    public void setIcon(String icon) {
        this.attributes.put("icon", this.getIconAttributes(AippFileUtils.getFileNameFromIcon(icon)));
    }

    private Map<String, String> getIconAttributes(String iconPath) {
        try {
            File iconFile = FileUtils.canonicalize(iconPath);
            Map<String, String> iconAttr = MapBuilder.<String, String>get().build();
            byte[] iconBytes = AppImExportUtil.readAllBytes(Files.newInputStream(iconFile.toPath()));
            iconAttr.put("content", Base64.getEncoder().encodeToString(iconBytes));
            iconAttr.put("type", AppImExportUtil.extractIconExtension(iconFile.getName()));
            return iconAttr;
        } catch (IllegalStateException | IOException e) {
            return MapBuilder.<String, String>get().put("content", StringUtils.EMPTY).build();
        }
    }
}
