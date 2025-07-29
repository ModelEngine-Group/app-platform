/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.runtime.entity;

/**
 * 参数.
 *
 * @author 张越
 * @since 2024-05-23
 */
public class Parameter {
    // 输入, json格式的字符串.
    private String input;

    // 输出, json格式的字符串.
    private String output;

    /**
     * 获取input字符串.
     *
     * @return {@link String} 对象.
     */
    public String getInput() {
        return input;
    }

    /**
     * 设置input字符串.
     *
     * @param input {@link String} 对象.
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * 获取output字符串.
     *
     * @return 输出字符串.
     */
    public String getOutput() {
        return output;
    }

    /**
     * 设置output字符串.
     *
     * @param output 输出字符串.
     */
    public void setOutput(String output) {
        this.output = output;
    }
}
