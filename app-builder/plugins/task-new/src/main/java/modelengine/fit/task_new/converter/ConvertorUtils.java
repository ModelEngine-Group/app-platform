/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.converter;

import static modelengine.fit.jober.aipp.constants.AippConst.INST_AGENT_RESULT_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CHILD_INSTANCE_ID;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CREATE_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CREATOR_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_DATA_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_FORM_VERSION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_CURR_NODE_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_FINISH_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_FLOW_INST_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_MODIFY_BY_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_MODIFY_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_NAME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_PROGRESS_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_RESUME_DURATION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_SMART_FORM_TIME_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.INST_STATUS_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.TASK_ID_KEY;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.task_new.entity.MetaInstance;
import modelengine.fitframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 转换器工具类
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
public class ConvertorUtils {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
            Locale.ROOT);

    public static MetaInstance toMetaInstance(InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        Map<String, Object> infoMappings = instanceDeclarationInfo.getInfo().getValue();
        return MetaInstance.builder()
                .taskName(ObjectUtils.cast(infoMappings.getOrDefault(INST_NAME_KEY, null)))
                .creator(context.getOperator())
                .createTime(ObjectUtils.cast(infoMappings.getOrDefault(INST_CREATE_TIME_KEY, LocalDateTime.now())))
                .modifyBy(ObjectUtils.cast(infoMappings.getOrDefault(INST_MODIFY_BY_KEY, null)))
                .modifyTime(ObjectUtils.cast(infoMappings.getOrDefault(INST_MODIFY_TIME_KEY, LocalDateTime.now())))
                .finishTime(ObjectUtils.cast(infoMappings.getOrDefault(INST_FINISH_TIME_KEY, LocalDateTime.now())))
                .flowInstanceId(ObjectUtils.cast(infoMappings.getOrDefault(INST_FLOW_INST_ID_KEY, null)))
                .currFormId(ObjectUtils.cast(infoMappings.getOrDefault(INST_CURR_FORM_ID_KEY, null)))
                .currFormVersion(ObjectUtils.cast(infoMappings.getOrDefault(INST_CURR_FORM_VERSION_KEY, null)))
                .currFormData(ObjectUtils.cast(infoMappings.getOrDefault(INST_CURR_FORM_DATA_KEY, null)))
                .smartFormTime(ObjectUtils.cast(infoMappings.getOrDefault(INST_SMART_FORM_TIME_KEY, null)))
                .resumeDuration(ObjectUtils.cast(infoMappings.getOrDefault(INST_RESUME_DURATION_KEY, null)))
                .instanceStatus(ObjectUtils.cast(infoMappings.getOrDefault(INST_STATUS_KEY, null)))
                .instanceProgress(ObjectUtils.cast(infoMappings.getOrDefault(INST_PROGRESS_KEY, null)))
                .instanceAgentResult(ObjectUtils.cast(infoMappings.getOrDefault(INST_AGENT_RESULT_KEY, null)))
                .instanceChildInstanceId(ObjectUtils.cast(infoMappings.getOrDefault(INST_CHILD_INSTANCE_ID, null)))
                .instanceCurrNodeId(ObjectUtils.cast(infoMappings.getOrDefault(INST_CURR_NODE_ID_KEY, null)))
                .build();
    }

    public static Instance toInstance(String instanceId, InstanceDeclarationInfo instanceDeclarationInfo) {
        Map<String, Object> infoMappings = instanceDeclarationInfo.getInfo().getValue();
        Map<String, String> converted = infoMappings.entrySet()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
        return new Instance(instanceId, converted, Collections.emptyList());
    }

    public static Instance toInstance(MetaInstance metaInstance) {
        Instance instance = new Instance();
        instance.setId(metaInstance.getId());
        Map<String, String> info = new HashMap<>();
        info.put(TASK_ID_KEY, ObjectUtils.cast(metaInstance.getTaskId()));
        info.put(INST_NAME_KEY, ObjectUtils.cast(metaInstance.getTaskName()));
        info.put(INST_CREATOR_KEY, ObjectUtils.cast(metaInstance.getCreator()));
        info.put(INST_CREATE_TIME_KEY, getFormatTime(metaInstance.getCreateTime()));
        info.put(INST_MODIFY_BY_KEY, ObjectUtils.cast(metaInstance.getModifyBy()));
        info.put(INST_MODIFY_TIME_KEY, getFormatTime(metaInstance.getModifyTime()));
        info.put(INST_FINISH_TIME_KEY, getFormatTime(metaInstance.getFinishTime()));
        info.put(INST_FLOW_INST_ID_KEY, ObjectUtils.cast(metaInstance.getFlowInstanceId()));
        info.put(INST_CURR_FORM_ID_KEY, ObjectUtils.cast(metaInstance.getCurrFormId()));
        info.put(INST_CURR_FORM_VERSION_KEY, ObjectUtils.cast(metaInstance.getCurrFormVersion()));
        info.put(INST_CURR_FORM_DATA_KEY, ObjectUtils.cast(metaInstance.getCurrFormData()));
        info.put(INST_SMART_FORM_TIME_KEY, getFormatTime(metaInstance.getSmartFormTime()));
        info.put(INST_RESUME_DURATION_KEY, ObjectUtils.cast(metaInstance.getResumeDuration()));
        info.put(INST_STATUS_KEY, ObjectUtils.cast(metaInstance.getInstanceStatus()));
        info.put(INST_PROGRESS_KEY, ObjectUtils.cast(metaInstance.getInstanceProgress()));
        info.put(INST_AGENT_RESULT_KEY, ObjectUtils.cast(metaInstance.getInstanceAgentResult()));
        info.put(INST_CHILD_INSTANCE_ID, ObjectUtils.cast(metaInstance.getInstanceChildInstanceId()));
        info.put(INST_CURR_NODE_ID_KEY, ObjectUtils.cast(metaInstance.getInstanceCurrNodeId()));
        instance.setInfo(info);
        instance.setTags(Collections.emptyList());
        return instance;
    }

    private static String getFormatTime(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return DATETIME_FORMATTER.format(time);
    }
}
