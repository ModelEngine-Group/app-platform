/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.instance.Instance;

import java.util.Map;

/**
 * 开始对话入参数据类
 *
 * @author 邬涨财
 * @since 2025-01-26
 */
public record StartChatParam(Map<String, Object> businessData, Meta meta, OperationContext context, Instance metaInst,
                             String flowDefinitionId, ChatSession<Object> chatSession) {}