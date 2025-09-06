/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fit.jane.common.validation.Size;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Aipp创建/更新参数
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippDto {
    @Property(description = "aipp 名称", example = "aipp")
    @Size(min = 1, max = 255, message = "名称长度范围[1, 255]")
    private String name;

    @Property(description = "aipp 描述", example = "aipp 编排应用")
    @Size(min = 0, max = 1024, message = "描述长度范围[0, 1024]")
    private String description;

    @Property(description = "流程视图定义", name = "flow_view_data")
    private Map<String, Object> flowViewData;

    @Property(description = "aipp 发布链接", name = "publish_url")
    private String publishUrl;

    @Property(description = "aipp 头像")
    private String icon;

    @Property(description = "aipp 版本号")
    private String version;

    @Property(description = "aipp 发布到store的唯一标识")
    private String uniqueName;

    @Property(description = "aipp 唯一标识")
    private String id;

    @Property(description = "app 唯一标识")
    private String appId;

    @Property(description = "app 类型")
    private String type;

    @Property(description = "app 业务类型分类")
    private String appType;

    @Property(description = "aipp 发布描述", example = "该发布的作用是生成稳定版本")
    private String publishedDescription;

    @Property(description = "aipp 发布更新日志", example = "该发布更新了流程")
    private String publishedUpdateLog;

    @Property(description = "应用分类")
    private String appCategory;

    /**
     * 获取元数据id.
     *
     * @return 元数据id.
     */
    public String getMetaId() {
        return String.valueOf(this.flowViewData.getOrDefault(AippConst.FLOW_CONFIG_ID_KEY, StringUtils.EMPTY));
    }

    /**
     * 获取版本.
     *
     * @return 版本.
     */
    public String getVersion() {
        return Optional.ofNullable(this.version)
                .orElseGet(() -> String.valueOf(
                        this.flowViewData.getOrDefault(AippConst.FLOW_CONFIG_VERSION_KEY, StringUtils.EMPTY)));
    }

    /**
     * 获取流程id.
     *
     * @return 流程id.
     */
    public String getFlowId() {
        return ObjectUtils.cast(this.flowViewData.get(AippConst.FLOW_CONFIG_ID_KEY));
    }

    /**
     * 获取preview版本.
     *
     * @return preview版本.
     */
    public String getPreviewVersion() {
        return ObjectUtils.cast(this.flowViewData.get(AippConst.FLOW_CONFIG_VERSION_KEY));
    }

    /**
     * 设置预览版本.
     *
     * @param previewVersion 预览版本.
     */
    public void setPreviewVersion(String previewVersion) {
        this.flowViewData.put(AippConst.FLOW_CONFIG_VERSION_KEY, previewVersion);
    }
}
