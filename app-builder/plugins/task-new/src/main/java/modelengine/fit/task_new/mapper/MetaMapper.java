/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.mapper;

import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.task_new.condition.OrderBy;
import modelengine.fit.task_new.po.MetaPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Meta 数据库 Mapper 类
 *
 * @author 孙怡菲
 * @since 2025-03-31
 */
public interface MetaMapper {
    void insertOne(MetaPo metaPo);

    void updateOne(MetaPo metaPo);

    void deleteOne(String id);

    Optional<MetaPo> retrieveByName(String name);

    Optional<MetaPo> retrieve(String id);

    List<MetaPo> list(@Param("metaFilter") MetaFilter metaFilter, @Param("attributes") Map<String, String> attributes,
            @Param("orderBy") OrderBy orderBy, @Param("offset") long offset, @Param("limit") int limit);

    int getCount(@Param("metaFilter") MetaFilter metaFilter, @Param("attributes") Map<String, String> attributes,
            @Param("orderBy") OrderBy orderBy);

    List<MetaPo> listLatest(@Param("metaFilter") MetaFilter metaFilter,
            @Param("attributes") Map<String, String> attributes, @Param("orderBy") OrderBy orderBy,
            @Param("offset") long offset, @Param("limit") int limit);

    int getLatestCount(@Param("metaFilter") MetaFilter metaFilter, @Param("attributes") Map<String, String> attributes,
            @Param("orderBy") OrderBy orderBy);
}
