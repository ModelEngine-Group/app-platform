/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

/**
 * Task 的枚举类。
 *
 * @author 李金绪
 * @since 2024/6/12
 */
public enum ToolCategoryEnum {
    HUGGINGFACE("HuggingFace"),
    LANGCHAIN("LangChain"),
    LLAMAINDEX("LlamaIndex"),
    BUILTIN("Builtin");

    private final String name;

    ToolCategoryEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}