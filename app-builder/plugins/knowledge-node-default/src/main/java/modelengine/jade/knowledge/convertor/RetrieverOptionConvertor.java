/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.convertor;

import modelengine.jade.knowledge.entity.RetrieverOption;
import modelengine.jade.knowledge.entity.RetrieverServiceOption;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 检索配置实体转换器。
 *
 * @author 刘信宏
 * @since 2024-10-08
 */
@Mapper
public interface RetrieverOptionConvertor {
    /**
     * 获取 {@link RetrieverOptionConvertor} 的实例。
     */
    RetrieverOptionConvertor INSTANCE = Mappers.getMapper(RetrieverOptionConvertor.class);

    /**
     * 将检索节点服务的配置实体转换为 {@link RetrieverOption}。
     *
     * @param option 表示检索节点服务的配置实体的 {@link RetrieverServiceOption}。
     * @param apiKey 表示用户api key的 {@link String}。
     * @return 表示检索配置实体的 {@link RetrieverOption}。
     */
    RetrieverOption fromRetrieverServiceOption(RetrieverServiceOption option, String apiKey);
}
