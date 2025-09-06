/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.service;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jane.common.entity.OperationContext;

/**
 * 对接模型使能和服务接入模型源，并提供模型网关映射服务的扩展接口，用于增加一层可配置选择实现的能力。
 *
 * @author songyongtan
 * @since 2025-03-17
 */
public interface AippModelCenterExtension {
    /**
     * 查询接入的模型服务的列表。
     *
     * @param type 表示模型类型的 {@link String}。
     * @param scene 标识模型适用场景的 {@link String}。
     * @param context 调用者的上下文数据的 {@link OperationContext}。
     * @return 表示模型服务列表的 {@link ModelListDto}，如果发生异常，返回空列表。
     */
    ModelListDto fetchModelList(String type, String scene, OperationContext context);

    /**
     * 根据 tag 等信息查询模型的访问信息。
     *
     * @param tag 表示模型服务来源的标签的 {@link String}。
     * @param modelName 标识模型名称的 {@link String}。
     * @param context 调用者的上下文数据的 {@link OperationContext}。
     * @return 表示模型网关地址的 {@link ModelAccessInfo}，配合 {@link ChatOptions} 使用。
     */
    ModelAccessInfo getModelAccessInfo(String tag, String modelName, OperationContext context);

    /**
     * 获取默认使用的模型服务信息。
     *
     * @param type 表示模型类型的 {@link String}。
     * @param context 调用者的上下文数据的 {@link OperationContext}。
     * @return 表示模型服务信息的 {@link ModelAccessInfo}。
     */
    ModelAccessInfo getDefaultModel(String type, OperationContext context);
}
