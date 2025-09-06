/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.uid.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 表示版本生成器持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@Mapper
public interface IdGeneratorMapper {
    /**
     * 分配序列号。
     *
     * @return 表示生成序列号的 {@link Long}。
     */
    Long getNextId();
}
