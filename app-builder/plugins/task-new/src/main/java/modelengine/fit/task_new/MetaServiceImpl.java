/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.entity.task.TaskProperty;
import modelengine.fit.task_new.repository.MetaRepository;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * Meta 服务层实现
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
@Component
public class MetaServiceImpl implements MetaService {
    private final MetaRepository metaRepository;

    public MetaServiceImpl(MetaRepository metaRepository) {
        this.metaRepository = metaRepository;
    }

    @Override
    public Meta create(MetaDeclarationInfo declaration, OperationContext context) {
       return this.metaRepository.insertOne(declaration, context);
    }

    @Override
    public void patch(String versionId, MetaDeclarationInfo declaration, OperationContext context) {
        this.metaRepository.updateOne(versionId, declaration, context);
    }

    @Override
    @Deprecated
    public void publish(String versionId, OperationContext context) {
        throw new IllegalStateException("Unsupported function");
    }

    @Override
    public void delete(String versionId, OperationContext context) {
        this.metaRepository.deleteOne(versionId);
    }

    @Override
    public RangedResultSet<Meta> list(MetaFilter filter, boolean isLatestOnly, long offset, int limit, OperationContext context) {
        if (isLatestOnly) {
            List<Meta> metas = this.metaRepository.listLatest(filter, offset, limit);
            int total = this.metaRepository.getLatestCount(filter);
            return RangedResultSet.create(metas, offset, limit, total);
        }
        List<Meta> metas = this.metaRepository.list(filter, offset, limit);
        int total = this.metaRepository.getCount(filter);
        return RangedResultSet.create(metas, offset, limit, total);
    }

    @Override
    public Meta retrieve(String versionId, OperationContext context) {
        return this.metaRepository.retrieve(versionId);
    }

    @Override
    @Deprecated
    public TaskProperty createProperty(String versionId, MetaPropertyDeclarationInfo declaration, OperationContext context) {
        throw new IllegalStateException("Unsupported function");
    }

    @Override
    @Deprecated
    public void patchProperty(String versionId, String propertyId, MetaPropertyDeclarationInfo declaration, OperationContext context) {
        throw new IllegalStateException("Unsupported function");
    }

    @Override
    @Deprecated
    public void deleteProperty(String versionId, String propertyId, OperationContext context) {
        throw new IllegalStateException("Unsupported function");
    }
}
