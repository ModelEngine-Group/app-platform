/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.repository.impl;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.TASK_NOT_FOUND;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.task_new.condition.OrderBy;
import modelengine.fit.task_new.mapper.MetaMapper;
import modelengine.fit.task_new.po.MetaPo;
import modelengine.fit.task_new.repository.MetaRepository;
import modelengine.fit.task_new.util.UUIDUtil;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Meta 数据库 Repo 层实现
 *
 * @author 孙怡菲
 * @since 2025-03-31
 */
@Component
public class MetaRepositoryImpl implements MetaRepository {
    private final MetaMapper metaMapper;

    public MetaRepositoryImpl(MetaMapper metaMapper) {
        this.metaMapper = metaMapper;
    }

    @Override
    public Meta insertOne(MetaDeclarationInfo metaDeclarationInfo, OperationContext context) {
        MetaPo metaPO = MetaPo.convertToMetaPO(metaDeclarationInfo, context);
        String templateId = StringUtils.isNotEmpty(metaPO.getTemplateId()) ? metaPO.getTemplateId() : UUIDUtil.uuid();
        metaPO.setTemplateId(templateId);
        metaPO.setId(UUIDUtil.uuid());
        metaPO.setCreatedBy(context.getOperator());
        LocalDateTime now = LocalDateTime.now();
        metaPO.setUpdatedAt(now);
        metaPO.setCreatedAt(now);
        this.metaMapper.insertOne(metaPO);
        return MetaPo.convertToMeta(metaPO);
    }

    @Override
    public void deleteOne(String id) {
        this.metaMapper.deleteOne(id);
    }

    @Override
    public void updateOne(String id, MetaDeclarationInfo metaDeclarationInfo, OperationContext context) {
        MetaPo metaPo = MetaPo.convertToMetaPO(metaDeclarationInfo, context);
        metaPo.setId(id);
        metaPo.setUpdatedAt(LocalDateTime.now());
        this.metaMapper.updateOne(metaPo);
    }

    @Override
    public Optional<Meta> retrieveByName(String name) {
        return this.metaMapper.retrieveByName(name).map(MetaPo::convertToMeta);
    }

    @Override
    public Meta retrieve(String id) {
        return this.metaMapper.retrieve(id).map(MetaPo::convertToMeta).orElseThrow(() -> new AippException(TASK_NOT_FOUND));
    }

    @Override
    public List<Meta> list(MetaFilter metaFilter, long offset, int limit) {
        List<MetaPo> metas = this.metaMapper.list(metaFilter, this.getAttributes(metaFilter), getOrderBy(metaFilter), offset, limit);
        return metas.stream().map(MetaPo::convertToMeta).toList();
    }

    @Override
    public int getCount(MetaFilter metaFilter) {
        return this.metaMapper.getCount(metaFilter, this.getAttributes(metaFilter), getOrderBy(metaFilter));
    }

    @Override
    public List<Meta> listLatest(MetaFilter metaFilter, long offset, int limit) {
        List<MetaPo> metas = this.metaMapper.listLatest(metaFilter, this.getAttributes(metaFilter), getOrderBy(metaFilter), offset, limit);
        return metas.stream().map(MetaPo::convertToMeta).toList();
    }

    @Override
    public int getLatestCount(MetaFilter metaFilter) {
        return this.metaMapper.getLatestCount(metaFilter, this.getAttributes(metaFilter), getOrderBy(metaFilter));
    }

    private Map<String, String> getAttributes(MetaFilter metaFilter) {
        Map<String, List<String>> attributes = metaFilter.getAttributes();
        return attributes == null ? Map.of() : attributes.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));
    }

    private OrderBy getOrderBy(MetaFilter metaFilter) {
        if (metaFilter.getOrderBys() == null || metaFilter.getOrderBys().isEmpty()) {
            return null;
        }
        // value格式为"asc(created_at)" "desc(updated_at)"
        String value = metaFilter.getOrderBys().get(0);
        int parenIndex = value.indexOf("(");
        String direction = value.substring(0, parenIndex);
        String field = value.substring(parenIndex + 1, value.length() - 1);
        return OrderBy.builder().field(field).direction(direction).build();
    }
}
