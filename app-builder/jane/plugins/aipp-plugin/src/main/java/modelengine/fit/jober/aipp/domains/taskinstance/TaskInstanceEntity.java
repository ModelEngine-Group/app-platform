/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.UsefulUtils;
import modelengine.fit.jober.entity.task.TaskProperty;

import lombok.Getter;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用实例的数据类.
 *
 * @author 张越
 * @since 2025-01-08
 */
public abstract class TaskInstanceEntity<T extends TaskInstanceEntity<T>> {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, Object> info;
    private final List<String> tags;

    @Getter
    private String taskId;

    @Getter
    private String instanceId;

    TaskInstanceEntity() {
        this.info = new HashMap<>();
        this.tags = new ArrayList<>();
    }

    /**
     * 设置任务id.
     *
     * @param taskId 任务id.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setTaskId(String taskId) {
        this.taskId = taskId;
        return this.self();
    }

    /**
     * 设置任务实例id.
     *
     * @param instanceId 任务实例id.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this.self();
    }

    /**
     * 设置任务实例名称.
     *
     * @param name 任务实例名称.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setName(String name) {
        this.info.put(AippConst.INST_NAME_KEY, name);
        return this.self();
    }

    /**
     * 设置任务实例创建人.
     *
     * @param creator 任务实例创建人.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setCreator(String creator) {
        this.info.put(AippConst.INST_CREATOR_KEY, creator);
        return this.self();
    }

    /**
     * 设置任务实例状态.
     *
     * @param status 任务实例状态.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setStatus(String status) {
        this.info.put(AippConst.INST_STATUS_KEY, status);
        return this.self();
    }

    /**
     * 设置任务实例进度.
     *
     * @param progress 任务实例进度.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setProgress(String progress) {
        this.info.put(AippConst.INST_PROGRESS_KEY, progress);
        return this.self();
    }

    /**
     * 设置任务实例表单id.
     *
     * @param formId 任务实例表单.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setFormId(String formId) {
        this.info.put(AippConst.INST_CURR_FORM_ID_KEY, formId);
        return this.self();
    }

    /**
     * 设置任务实例表单版本.
     *
     * @param formVersion 任务实例表单版本.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setFormVersion(String formVersion) {
        this.info.put(AippConst.INST_CURR_FORM_VERSION_KEY, formVersion);
        return this.self();
    }

    /**
     * 设置当前节点id.
     *
     * @param nodeId 任务当前节点id.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setCurrentNodeId(String nodeId) {
        this.info.put(AippConst.INST_CURR_NODE_ID_KEY, nodeId);
        return this.self();
    }

    /**
     * 设置创建时间.
     *
     * @param createTime 任务创建时间.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setCreateTime(LocalDateTime createTime) {
        this.info.put(AippConst.INST_CREATE_TIME_KEY, createTime);
        return this.self();
    }

    /**
     * 设置任务实例完成时间.
     *
     * @param finishTime 任务实例完成时间.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setFinishTime(LocalDateTime finishTime) {
        this.info.put(AippConst.INST_FINISH_TIME_KEY, finishTime);
        return this.self();
    }

    /**
     * 设置任务实例动态表单时间.
     *
     * @param smartFormTime 动态表单时间.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setSmartFormTime(LocalDateTime smartFormTime) {
        this.info.put(AippConst.INST_SMART_FORM_TIME_KEY, smartFormTime);
        return this.self();
    }

    /**
     * 设置恢复时间.
     *
     * @param resumeDuration 恢复时间.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setResumeDuration(String resumeDuration) {
        this.info.put(AippConst.INST_RESUME_DURATION_KEY, resumeDuration);
        return this.self();
    }

    /**
     * 设置任务实例参数.
     *
     * @param key 参数.
     * @param value 参数值.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T putInfo(String key, Object value) {
        Optional.ofNullable(key).ifPresent(k -> this.info.put(k, value));
        return this.self();
    }

    /**
     * 批量设置任务实例参数.
     *
     * @param infos 任务实例参数的映射集合.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T putInfos(Map<String, String> infos) {
        UsefulUtils.doIfNotNull(infos, this.info::putAll);
        return this.self();
    }

    /**
     * 从业务数据中提取数据.
     *
     * @param businessData 业务参数.
     * @param props 数据库中的参数列表.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T fetch(Map<String, Object> businessData, List<TaskProperty> props) {
        businessData.forEach((key, value) -> {
            if (props.stream().anyMatch(item -> item.getName().equals(key))) {
                this.info.put(key, value);
            }
        });
        return this.self();
    }

    /**
     * 批量添加标签.
     *
     * @param tags 标签列表.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T putTags(List<String> tags) {
        UsefulUtils.doIfNotNull(tags, this.tags::addAll);
        return this.self();
    }

    /**
     * 设置任务实例子实例id.
     *
     * @param childInstanceId 子实例id.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setChildInstanceId(String childInstanceId) {
        this.info.put(AippConst.INST_CHILD_INSTANCE_ID, childInstanceId);
        return this.self();
    }

    /**
     * 设置任务实例运行追踪id.
     *
     * @param flowInstanceId 任务实例追踪id.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setFlowTraceId(String flowInstanceId) {
        this.info.put(AippConst.INST_FLOW_INST_ID_KEY, flowInstanceId);
        return this.self();
    }

    /**
     * 设置任务实例大模型输出结果.
     *
     * @param llmOutput 任务实例大模型输出结果.
     * @return {@link TaskInstanceEntity} 对象.
     */
    public T setLlmOutput(String llmOutput) {
        this.info.put("llmOutput", llmOutput);
        return this.self();
    }

    /**
     * 获取当前节点id.
     *
     * @return {@link TaskInstanceEntity} 对象.
     */
    public String getCurrentNodeId() {
        return this.makeString(this.info.get(AippConst.INST_CURR_NODE_ID_KEY));
    }

    /**
     * 获取任务实例名称.
     *
     * @return {@link String} 对象
     */
    public String getName() {
        return this.makeString(this.info.get(AippConst.INST_NAME_KEY));
    }

    /**
     * 获取大模型输出.
     *
     * @return {@link String} 对象
     */
    public String getLlmOutput() {
        return this.makeString(this.info.get("llmOutput"));
    }

    /**
     * 获取标签列表.
     *
     * @return {@link List}{@code <}{@link String}{@code >} 列表.
     */
    public List<String> getTags() {
        return new ArrayList<>(this.tags);
    }

    /**
     * 获取任务实例子实例id.
     *
     * @return {@link String} 对象
     */
    public String getChildInstanceId() {
        return this.makeString(this.info.get(AippConst.INST_CHILD_INSTANCE_ID));
    }

    /**
     * 获取任务实例进度.
     *
     * @return {@link String} 进度.
     */
    public String getProgress() {
        return this.makeString(this.info.get(AippConst.INST_PROGRESS_KEY));
    }

    /**
     * 获取状态.
     *
     * @return {@link Optional}{@code <}{@link String}{@code >} 状态值.
     */
    public Optional<String> getStatus() {
        return Optional.ofNullable(this.makeString(this.info.get(AippConst.INST_STATUS_KEY)));
    }

    /**
     * 获取创建人.
     *
     * @return {@link String} 对象
     */
    public String getCreator() {
        return this.makeString(this.info.get(AippConst.INST_CREATOR_KEY));
    }

    /**
     * 获取任务追溯id.
     *
     * @return {@link String} 对象
     */
    public String getFlowTranceId() {
        return this.makeString(this.info.get(AippConst.INST_FLOW_INST_ID_KEY));
    }

    /**
     * 获取表单id.
     *
     * @return {@link String} 对象
     */
    public String getFormId() {
        return this.makeString(this.info.get(AippConst.INST_CURR_FORM_ID_KEY));
    }

    /**
     * 获取表单版本.
     *
     * @return {@link String} 对象
     */
    public String getFormVersion() {
        return this.makeString(this.info.get(AippConst.INST_CURR_FORM_VERSION_KEY));
    }

    /**
     * 获取{@link String} 类型的参数映射集合.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >} 对象.
     */
    public Map<String, String> getStringInfos() {
        return this.info.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> this.makeString(e.getValue())));
    }

    /**
     * 获取{@link Object} 类型的参数映射集合.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 对象.
     */
    public Map<String, Object> getInfos() {
         return this.info;
    }

    /**
     * 获取动态表单时间.
     *
     * @return {@link String} 的 {@link Optional} 对象.
     */
    public Optional<String> getSmartFormTime() {
        Object smartFormTime = this.info.get(AippConst.INST_SMART_FORM_TIME_KEY);
        if (smartFormTime == null) {
            return Optional.empty();
        }
        if (smartFormTime instanceof LocalDateTime) {
            return Optional.of(DF.format(ObjectUtils.cast(smartFormTime)));
        }
        return Optional.of(this.makeString(smartFormTime));
    }

    /**
     * 获取duration.
     *
     * @return {@link Duration} 对象.
     */
    public Duration getDuration() {
        String smartFormTimeStr = this.getSmartFormTime().orElse(StringUtils.EMPTY);
        LocalDateTime smartFormTime = StringUtils.isBlank(smartFormTimeStr)
                ? LocalDateTime.now()
                : LocalDateTime.parse(smartFormTimeStr, DF);
        return Duration.between(smartFormTime, LocalDateTime.now());
    }

    /**
     * 获取创建时间.
     *
     * @return {@link String} 对象
     */
    public String getCreateTime() {
        Object createTime = this.info.get(AippConst.INST_CREATE_TIME_KEY);
        if (createTime == null) {
            return StringUtils.EMPTY;
        }
        if (createTime instanceof LocalDateTime) {
            return DF.format(ObjectUtils.cast(createTime));
        }
        return this.makeString(createTime);
    }

    /**
     * 获取完成时间.
     *
     * @param defaultValue 默认值.
     * @return {@link String} 对象
     */
    public String getFinishTime(String defaultValue) {
        Object finishTime = this.info.get(AippConst.INST_FINISH_TIME_KEY);
        if (finishTime == null) {
            return defaultValue;
        }
        if (finishTime instanceof LocalDateTime) {
            return DF.format(ObjectUtils.cast(finishTime));
        }
        return this.makeString(finishTime);
    }

    /**
     * 获取恢复时间.
     *
     * @return 恢复时间数值.
     */
    public long getResumeDuration() {
        String resumeDuration =
                ObjectUtils.cast(Optional.ofNullable(this.info.get(AippConst.INST_RESUME_DURATION_KEY)).orElse("0"));
        return Long.parseLong(resumeDuration);
    }

    /**
     * 通过实例数据构建任务实例对象.
     *
     * @return {@link AppTaskInstance} 对象.
     */
    public AppTaskInstance build() {
        AppTaskInstance taskInstance = new AppTaskInstance(this);
        taskInstance.setTaskId(this.getTaskId());
        taskInstance.setId(this.getInstanceId());
        return taskInstance;
    }

    private String makeString(Object value) {
        return this.makeString(value, null);
    }

    private String makeString(Object value, String defaultValue) {
        return Optional.ofNullable(value).map(ObjectUtils::<String>cast).orElse(defaultValue);
    }

    /**
     * 返回自身引用.
     *
     * @return <T> 自身的引用类型.
     */
    public abstract T self();
}
