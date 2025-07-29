/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.enums.LlmModelNameEnum;

import java.io.IOException;

/**
 * 大模型多模态交互接口
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
public interface LLMService {
    /**
     * 使用文本文件与大模型交互
     *
     * @param prompt 大模型提示词
     * @param model 大模型
     * @return 大模型返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askModelWithText(String prompt, LlmModelNameEnum model) throws IOException;

    /**
     * 使用文本与大模型交互
     *
     * @param prompt 大模型提示词
     * @param maxTokens 大模型最大生成的 token 数量
     * @param temperature 温度
     * @param model 大模型
     * @return 大模型返回值
     * @throws IOException 读取数据过程发生异常
     */
    String askModelWithText(String prompt, int maxTokens, double temperature, LlmModelNameEnum model)
            throws IOException;
}
