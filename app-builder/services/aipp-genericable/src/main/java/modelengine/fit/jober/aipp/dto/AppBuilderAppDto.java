/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 表示 app 应用的 dto 对象。
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppDto {
    @Property(description = "应用名称")
    private String name;
    @Property(description = "应用类型")
    private String type;
    @Property(description = "创建者")
    private String createBy;
    @Property(description = "更新者")
    private String updateBy;
    @Property(description = "版本")
    private String version;
    @Property(description = "应用类别")
    private String appCategory;
    @Property(description = "创建时间")
    private LocalDateTime createAt;
    @Property(description = "更新时间")
    private LocalDateTime updateAt;
    @Property(description = "配置元数据")
    private AppBuilderConfigDto config;
    @Property(description = "流程")
    private AppBuilderFlowGraphDto flowGraph;
    @Property(description = "应用的唯一标识符")
    private String id;
    @Property(description = "应用属性")
    private Map<String, Object> attributes;
    @Property(description = "应用配置项")
    private List<AppBuilderConfigFormPropertyDto> configFormProperties;
    @Property(description = "应用状态")
    private String state;
    @Property(description = "创建时间")
    private LocalDateTime baselineCreateAt;
    @Property(description = "聊天短链地址")
    private String chatUrl;
    private String appType;
    private String appBuiltType;

    // 这块property转化能力暂时没有，等框架合入该能力，再加上value值
    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "aipp 发布链接")
    private String publishUrl;

    @Property(description = "aipp 发布描述")
    private String publishedDescription;

    @Property(description = "aipp 发布更新日志")
    private String publishedUpdateLog;


    /**
     * 获取描述.
     *
     * @return {@link String} 描述信息.
     */
    public String getDescription() {
        return String.valueOf(this.attributes.getOrDefault("description", StringUtils.EMPTY));
    }

    /**
     * 获取图标.
     *
     * @return {@link String} 图标信息.
     */
    public String getIcon() {
        return String.valueOf(this.attributes.getOrDefault("icon", StringUtils.EMPTY));
    }

    /**
     * 设置图标.
     *
     * @param icon 图标.
     */
    public void setIcon(String icon) {
        this.attributes.put("icon", icon);
    }

    /**
     * 设置描述.
     *
     * @param description 描述信息.
     */
    public void setDescription(String description) {
        this.attributes.put("description", description);
    }
}
