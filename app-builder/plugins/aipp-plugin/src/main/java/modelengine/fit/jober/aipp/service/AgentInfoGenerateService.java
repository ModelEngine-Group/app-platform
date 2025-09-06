/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;

import java.util.List;

/**
 * 根据用户描述生成应用信息的服务
 *
 * @author 兰宇晨
 * @since 2024-12-2
 */
public interface AgentInfoGenerateService {
    /**
     * 根据描述生成智能体名字。
     *
     * @param desc 表示对智能体描述的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 根据智能体描述所生成名字的 {@link String}。
     */
    String generateName(String desc, OperationContext context);

    /**
     * 根据描述生成智能体开场白。
     *
     * @param desc 表示对智能体描述的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 根据智能体描述所生成开场白的 {@link String}。
     */
    String generateGreeting(String desc, OperationContext context);

    /**
     * 根据描述生成智能体提示词。
     *
     * @param desc 表示对智能体描述的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 根据智能体描述所生成提示词的 {@link String}。
     */
    String generatePrompt(String desc, OperationContext context);

    /**
     * 根据描述选择智能体所需工具。
     *
     * @param desc 表示对智能体描述的 {@link String}。
     * @param creator 表示智能体创建者表示的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 根据描述所匹配工具唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> selectTools(String desc, String creator, OperationContext context);
}