/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnore;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.util.AppImExportUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 应用导出配置类。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportDto {
    @Property(description = "应用配置版本号")
    String version;

    @Property(description = "应用基础信息")
    AppExportApp app;

    @Property(description = "应用 configUI 配置信息")
    AppExportConfig config;

    @Property(description = "应用流程图配置信息")
    AppExportFlowGraph flowGraph;

    /**
     * 获取头像文件的路径。
     *
     * @param contextRoot 表示请求上下文根的 {@link String}。
     * @param context 表示操作人上下文信息的 {@link String}。
     * @param resourcePath 表示资源目录的 {@link String}。
     * @return 表示获取到的头像文件的路径的 {@link String}。
     */
    @JsonIgnore
    public String getIconPath(String contextRoot, String resourcePath, OperationContext context) {
        Object iconAttr = this.app.getAttributes().get("icon");
        String iconContent = iconAttr instanceof Map ? ObjectUtils.cast(
                ObjectUtils.<Map<String, Object>>cast(iconAttr).get("content")) : StringUtils.EMPTY;
        if (StringUtils.isBlank(iconContent)) {
            return iconContent;
        }
        String iconExtension = ObjectUtils.cast(ObjectUtils.<Map<String, Object>>cast(iconAttr).get("type"));
        return AppImExportUtil.saveIconFile(iconContent,
                iconExtension,
                context.getTenantId(),
                contextRoot,
                resourcePath);
    }

    /**
     * 获取类型.
     *
     * @return 类型.
     */
    @JsonIgnore
    public String getType() {
        return ObjectUtils.cast(this.app.getAttributes().getOrDefault("appType", AppTypeEnum.APP.code()));
    }
}
