/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.uid;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.uid.mapper.IdGeneratorMapper;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 简单 UID 生成实现。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@Component
public class SimpleUidGenerator implements UidGenerator {
    private final IdGeneratorMapper idGeneratorMapper;

    /**
     * 表示简单 UID 生成器的构建器。
     *
     * @param idGeneratorMapper 表示 id 生成持久层接口。
     */
    public SimpleUidGenerator(IdGeneratorMapper idGeneratorMapper) {
        this.idGeneratorMapper = notNull(idGeneratorMapper, "The mapper cannot be null.");
    }

    @Override
    @Fitable("simple")
    public long getUid() {
        return this.idGeneratorMapper.getNextId();
    }
}