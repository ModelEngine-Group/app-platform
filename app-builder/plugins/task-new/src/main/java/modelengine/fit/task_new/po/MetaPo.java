/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.po;

import static modelengine.fit.task_new.consts.MetaConst.PROPERTY_NAME_LIST;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.Builder;
import lombok.Data;
import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jober.entity.task.TaskProperty;

import java.time.LocalDateTime;

/**
 * Meta 数据库 PO 类
 *
 * @author 孙怡菲
 * @since 2025-03-31
 */
@Data
@Builder
public class MetaPo {
    private String id;

    private String name;

    private String version;

    private String tenantId;

    private String templateId;

    private String attributes;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    public static Meta convertToMeta(MetaPo metaPO) {
        Meta meta = new Meta();
        meta.setVersionId(metaPO.getId());
        meta.setName(metaPO.getName());
        meta.setVersion(metaPO.getVersion());
        meta.setTenant(metaPO.getTenantId());
        meta.setId(metaPO.getTemplateId());
        meta.setAttributes(JSONObject.parseObject(metaPO.getAttributes(), new TypeReference<>() {}));
        meta.setCreator(metaPO.getCreatedBy());
        meta.setCreationTime(metaPO.getCreatedAt());
        meta.setLastModifier(metaPO.getUpdatedBy());
        meta.setLastModificationTime(metaPO.getUpdatedAt());
        meta.setProperties(PROPERTY_NAME_LIST.stream().map(MetaPo::buildProperty).toList());
        return meta;
    }

    public static MetaPo convertToMetaPO(MetaDeclarationInfo metaDeclarationInfo, OperationContext context) {
        return MetaPo.builder()
                .name(require(metaDeclarationInfo.getName()))
                .version(require(metaDeclarationInfo.getVersion()))
                .tenantId(context.getTenantId())
                .templateId(require(metaDeclarationInfo.getBasicMetaTemplateId()))
                .attributes(JSONObject.toJSONString(require(metaDeclarationInfo.getAttributes())))
                .updatedBy(context.getOperator())
                .build();
    }

    private static <T> T require(Undefinable<T> value) {
        if (Boolean.TRUE.equals(value.getDefined())) {
            return value.getValue();
        }
        return null;
    }

    private static TaskProperty buildProperty(String name) {
        TaskProperty property = new TaskProperty();
        property.setName(name);
        return property;
    }
}
