/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotBlank;
import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotNull;

import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippNodeForms;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.jober.entity.task.TaskProperty;

import lombok.Getter;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * 应用任务的数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public abstract class TaskEntity<T extends TaskEntity<T>> {
    private static final String DEFAULT_DESCRIPTION = "aipp 编排应用";

    // 对应数据库表中的templateId，对应Meta中的id.
    @Getter
    private String appSuiteId;

    // 对应数据库表中的id，对应Meta中的versionId.
    @Getter
    private String taskId;

    @Getter
    private String name;

    @Getter
    private String category;

    @Getter
    private String creator;

    @Getter
    private String lastModifier;

    @Getter
    private String tenant;

    @Getter
    private String version;

    @Getter
    private LocalDateTime creationTime;

    @Getter
    private LocalDateTime lastModificationTime;

    private List<TaskProperty> properties;

    private Map<String, Object> attributes;

    TaskEntity() {
        this.properties = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    /**
     * 设置名称.
     *
     * @param name 名称.
     * @return {@link TaskEntity} 对象.
     */
    public T setName(String name) {
        doIfNotBlank(name, v -> this.name = v);
        return this.self();
    }

    /**
     * 设置版本.
     *
     * @param version 版本.
     * @return {@link TaskEntity} 对象.
     */
    public T setVersion(String version) {
        doIfNotBlank(version, v -> this.version = v);
        return this.self();
    }

    /**
     * 设置app的唯一标识.
     *
     * @param appSuiteId app的唯一标识.
     * @return {@link TaskEntity} 对象.
     */
    public T setAppSuiteId(String appSuiteId) {
        this.appSuiteId = appSuiteId;
        return this.self();
    }

    /**
     * 设置分类.
     *
     * @param category 分类.
     * @return {@link TaskEntity} 对象.
     */
    public T setCategory(String category) {
        this.category = category;
        return this.self();
    }

    /**
     * 设置aipp类型.
     *
     * @param aippType aipp类型.
     * @return {@link TaskEntity} 对象.
     */
    public T setAippType(String aippType) {
        this.attributes.put(AippConst.ATTR_AIPP_TYPE_KEY, aippType);
        return this.self();
    }

    /**
     * 设置状态.
     *
     * @param status 状态码.
     * @return {@link TaskEntity} 对象.
     */
    public T setStatus(String status) {
        this.attributes.put(AippConst.ATTR_META_STATUS_KEY, status);
        return this.self();
    }

    /**
     * 设置任务id.
     *
     * @param taskId 任务id.
     * @return {@link TaskEntity} 对象.
     */
    public T setTaskId(String taskId) {
        doIfNotBlank(taskId, v -> this.taskId = v);
        return this.self();
    }

    /**
     * 设置应用版本id.
     *
     * @param appId 应用版本id.
     * @return {@link TaskEntity} 对象.
     */
    public T setAppId(String appId) {
        doIfNotBlank(appId, v -> this.attributes.put(AippConst.ATTR_APP_ID_KEY, v));
        return self();
    }

    /**
     * 设置任务创建时间.
     *
     * @param creationTime 创建时间.
     * @return {@link TaskEntity} 对象.
     */
    public T setCreationTime(LocalDateTime creationTime) {
        doIfNotNull(creationTime, v -> this.creationTime = v);
        return this.self();
    }

    /**
     * 设置任务最后一次修改时间.
     *
     * @param lastModificationTime 最近一次修改时间.
     * @return {@link TaskEntity} 对象.
     */
    public T setLastModificationTime(LocalDateTime lastModificationTime) {
        doIfNotNull(lastModificationTime, v -> this.lastModificationTime = v);
        return this.self();
    }

    /**
     * 设置创建人.
     *
     * @param creator 创建人.
     * @return {@link TaskEntity} 对象.
     */
    public T setCreator(String creator) {
        doIfNotBlank(creator, v -> this.creator = v);
        return this.self();
    }

    /**
     * 设置描述.
     *
     * @param description 描述.
     * @return {@link TaskEntity} 对象.
     */
    public T setDescription(String description) {
        if (StringUtils.isNotBlank(description)) {
            this.attributes.put(AippConst.ATTR_DESCRIPTION_KEY,
                    Optional.ofNullable(description).orElse(DEFAULT_DESCRIPTION));
        }
        return this.self();
    }

    /**
     * 设置图标.
     *
     * @param icon 图标.
     * @return {@link TaskEntity} 对象.
     */
    public T setIcon(String icon) {
        if (StringUtils.isNotBlank(icon)) {
            this.attributes.put(AippConst.ATTR_META_ICON_KEY, icon);
        }
        return this.self();
    }

    /**
     * 设置流程id.
     *
     * @param flowId 流程id.
     * @return {@link TaskEntity} 对象.
     */
    public T setFlowConfigId(String flowId) {
        doIfNotNull(flowId, v -> this.attributes.put(AippConst.ATTR_FLOW_CONFIG_ID_KEY, v));
        return this.self();
    }

    /**
     * 设置发布描述.
     *
     * @param publishDescription 发布描述.
     * @return {@link TaskEntity} 对象.
     */
    public T setPublishDescription(String publishDescription) {
        doIfNotNull(publishDescription, v -> this.attributes.put(AippConst.ATTR_PUBLISH_DESCRIPTION, v));
        return this.self();
    }

    /**
     * 设置发布日志.
     *
     * @param publishLog 发布日志.
     * @return {@link TaskEntity} 对象.
     */
    public T setPublishLog(String publishLog) {
        doIfNotNull(publishLog, v -> this.attributes.put(AippConst.ATTR_PUBLISH_UPDATE_LOG, v));
        return this.self();
    }

    /**
     * 设置工具store中的唯一标志.
     *
     * @param uniqueName 在工具中的唯一标识.
     * @return {@link TaskEntity} 对象.
     */
    public T setUniqueName(String uniqueName) {
        doIfNotBlank(uniqueName, v -> this.attributes.put(AippConst.ATTR_UNIQUE_NAME, v));
        return this.self();
    }

    /**
     * 设置属性中的版本号.
     *
     * @param version 版本号.
     * @return {@link TaskEntity} 对象.
     */
    public T setAttributeVersion(String version) {
        doIfNotNull(version, v -> this.attributes.put(AippConst.ATTR_VERSION_KEY, v));
        return this.self();
    }

    /**
     * 设置流程定义id.
     *
     * @param definitionId 流程定义id.
     * @return {@link TaskEntity} 对象.
     */
    public T setFlowDefinitionId(String definitionId) {
        this.attributes.put(AippConst.ATTR_FLOW_DEF_ID_KEY, definitionId);
        return this.self();
    }

    /**
     * 设置发布时间.
     *
     * @param publishTime 发布时间.
     * @return {@link TaskEntity} 对象.
     */
    public T setPublishTime(String publishTime) {
        this.attributes.put(AippConst.ATTR_PUBLISH_TIME_KEY, publishTime);
        return this.self();
    }

    /**
     * 设置基线版本.
     *
     * @param baseLineVersion 基线版本.
     * @return {@link TaskEntity} 对象.
     */
    public T setBaseLineVersion(String baseLineVersion) {
        this.attributes.put(AippConst.ATTR_BASELINE_VERSION_KEY, baseLineVersion);
        return this.self();
    }

    /**
     * 从 {@link AippDto} 中提取数据.
     *
     * @param args {@link AippDto} 对象.
     * @return {@link TaskEntity} 对象.
     */
    public T fetch(AippDto args) {
        Optional.ofNullable(args).ifPresent(a -> {
            this.name = a.getName();
            this.version = a.getVersion();
            this.setDescription(a.getDescription());
            this.attributes.put(AippConst.ATTR_META_ICON_KEY, a.getIcon());
            this.attributes.put(AippConst.ATTR_APP_ID_KEY, a.getAppId());
        });
        return this.self();
    }

    /**
     * 从 {@link AippCreateDto} 中提取数据.
     *
     * @param baseline {@link AippCreateDto} 对象.
     * @return {@link TaskEntity} 对象.
     */
    public T fetch(AippCreateDto baseline) {
        Optional.ofNullable(baseline)
                .ifPresent(b -> this.setBaseLineVersion(b.getVersion()).setAppSuiteId(b.getAippId()));
        return this.self();
    }

    /**
     * 从流程视图中提取数据.
     *
     * @param flowView 流程视图.
     * @return {@link TaskEntity} 对象.
     */
    public T fetch(Map<String, Object> flowView) {
        Optional.ofNullable(flowView).ifPresent(fv -> {
            this.setFlowConfigId(ObjectUtils.cast(fv.get(AippConst.FLOW_CONFIG_ID_KEY)));
            this.setAttributeVersion(ObjectUtils.cast(fv.get(AippConst.FLOW_CONFIG_VERSION_KEY)));
        });
        return this.self();
    }

    /**
     * 从 nodeForms 中提取数据.
     *
     * @param nodeForms 节点表单数据.
     * @return {@link TaskEntity} 对象.
     */
    public T fetch(List<AippNodeForms> nodeForms) {
        for (AippNodeForms node : nodeForms) {
            if (node.getMetaInfo().isEmpty()) {
                continue;
            }
            if (NodeTypes.START.getType().equalsIgnoreCase(node.getType())) {
                this.attributes.put(AippConst.ATTR_START_FORM_ID_KEY, node.getMetaInfo().get(0).getFormId());
                this.attributes.put(AippConst.ATTR_START_FORM_VERSION_KEY, node.getMetaInfo().get(0).getVersion());
            }
            if (NodeTypes.END.getType().equalsIgnoreCase(node.getType())) {
                this.attributes.put(AippConst.ATTR_END_FORM_ID_KEY, node.getMetaInfo().get(0).getFormId());
                this.attributes.put(AippConst.ATTR_END_FORM_VERSION_KEY, node.getMetaInfo().get(0).getVersion());
            }
        }
        return this.self();
    }

    /**
     * 访问所有的attributes数据.
     *
     * @param consumer 消费者.
     */
    public void visitAttributes(BiConsumer<String, Object> consumer) {
        this.attributes.forEach(consumer);
    }

    /**
     * 设置属性列表.
     *
     * @param properties 属性列表.
     * @return {@link TaskEntity} 对象.
     */
    public T setProperties(List<TaskProperty> properties) {
        this.properties = properties;
        return this.self();
    }

    /**
     * 从 {@link Meta} 中加载数据.
     *
     * @param meta {@link Meta} 对象.
     */
    public void loadFrom(Meta meta) {
        this.appSuiteId = meta.getId();
        this.taskId = meta.getVersionId();
        this.name = meta.getName();
        this.category = meta.getCategory();
        this.creator = meta.getCreator();
        this.lastModifier = meta.getLastModifier();
        this.tenant = meta.getTenant();
        this.version = meta.getVersion();
        this.creationTime = meta.getCreationTime();
        this.lastModificationTime = meta.getLastModificationTime();
        this.properties = meta.getProperties();
        this.attributes = meta.getAttributes();
    }

    @Override
    public TaskEntity clone() throws CloneNotSupportedException {
        return ObjectUtils.cast(super.clone());
    }

    /**
     * 构建 {@link AppTask} 对象.
     *
     * @return {@link AppTask} 对象.
     */
    public AppTask build() {
        return new AppTask(this);
    }

    /**
     * 获取发布日志.
     *
     * @return {@link String} 发布日志.
     */
    public String getPublishLog() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_PUBLISH_UPDATE_LOG));
    }

    /**
     * 获取发布描述信息.
     *
     * @return {@link String} 发布描述.
     */
    public String getPublishDescription() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_PUBLISH_DESCRIPTION));
    }

    /**
     * 获取图标.
     *
     * @return 图标.
     */
    public String getIcon() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_META_ICON_KEY));
    }

    /**
     * 获取描述.
     *
     * @return 描述.
     */
    public String getDescription() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_DESCRIPTION_KEY));
    }

    /**
     * 获取状态.
     *
     * @return 状态.
     */
    public String getStatus() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_META_STATUS_KEY));
    }

    /**
     * 获取发布时间.
     *
     * @return 发布时间.
     */
    public String getPublishTime() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_PUBLISH_TIME_KEY));
    }

    /**
     * 获取流程定义id.
     *
     * @return 流程定义id.
     */
    public String getFlowDefinitionId() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_FLOW_DEF_ID_KEY));
    }

    /**
     * 获取流程配置id.
     *
     * @return 流程配置id.
     */
    public String getFlowConfigId() {
        // flow_graph的id.
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_FLOW_CONFIG_ID_KEY));
    }

    /**
     * 获取属性中的版本.
     *
     * @return 版本.
     */
    public String getAttributeVersion() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_VERSION_KEY));
    }

    /**
     * 获取store中对应的唯一标识.
     *
     * @return store中的唯一标识.
     */
    public String getUniqueName() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_UNIQUE_NAME));
    }

    /**
     * 获取应用id.
     *
     * @return 应用id.
     */
    public String getAppId() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_APP_ID_KEY));
    }

    /**
     * 获取应用类型.
     *
     * @return 应用类型.
     */
    public String getAippType() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_AIPP_TYPE_KEY));
    }

    /**
     * 获取基线版本.
     *
     * @return 基线版本.
     */
    public String getBaseLineVersion() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_BASELINE_VERSION_KEY));
    }

    /**
     * 获取开始表单唯一标识.
     *
     * @return {@link String} 对象.
     */
    public String getStartFormId() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_START_FORM_ID_KEY));
    }

    /**
     * 获取开始表单版本.
     *
     * @return {@link String} 对象.
     */
    public String getStartFormVersion() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_START_FORM_VERSION_KEY));
    }

    /**
     * 获取结束表单唯一标识.
     *
     * @return {@link String} 对象.
     */
    public String getEndFormId() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_END_FORM_ID_KEY));
    }

    /**
     * 获取结束表单版本.
     *
     * @return {@link String} 对象.
     */
    public String getEndFormVersion() {
        return ObjectUtils.cast(this.attributes.get(AippConst.ATTR_END_FORM_VERSION_KEY));
    }

    /**
     * 获取属性.
     *
     * @return {@link List}{@code <}{@link TaskProperty}{@code >} 列表.
     */
    public List<TaskProperty> getProperties() {
        return Collections.unmodifiableList(this.properties);
    }

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this.self());
    }

    /**
     * 返回自身.
     *
     * @return <T> 当前的对象.
     */
    public abstract T self();
}
