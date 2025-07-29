/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.config;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * 智能表单创建配置参数。
 *
 * @author 陈潇文
 * @since 2024-11-25
 */
@Component
@AcceptConfigValues("app-engine.form.create")
public class AippFormCreateConfig {
    /**
     * 智能表单最大数量，单位：个
     */
    private long maximumNum;

    /**
     * 获取智能表单最大数量。
     *
     * @return 表示智能表单最大数量的 {@link Long}。
     */
    public long getMaximumNum() {
        return this.maximumNum;
    }

    /**
     * 设置智能表单最大数量。
     *
     * @param maximumNum 表示智能表单最大数量的 {@link Long}。
     */
    public void setMaximumNum(long maximumNum) {
        this.maximumNum = maximumNum;
    }
}
