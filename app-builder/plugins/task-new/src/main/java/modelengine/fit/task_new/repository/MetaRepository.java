/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.repository;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;

import java.util.List;
import java.util.Optional;

/**
 * Meta 数据库 Repo 层接口
 *
 * @author 孙怡菲
 * @since 2025-03-31
 */
public interface MetaRepository {
    Meta insertOne(MetaDeclarationInfo metaDeclarationInfo, OperationContext context);

    void deleteOne(String id);

    void updateOne(String id, MetaDeclarationInfo metaDeclarationInfo, OperationContext context);

    Optional<Meta> retrieveByName(String name);

    Meta retrieve(String id);

    List<Meta> list(MetaFilter metaFilter, long offset, int limit);

    int getCount(MetaFilter metaFilter);

    List<Meta> listLatest(MetaFilter metaFilter, long offset, int limit);

    int getLatestCount(MetaFilter metaFilter);
}
