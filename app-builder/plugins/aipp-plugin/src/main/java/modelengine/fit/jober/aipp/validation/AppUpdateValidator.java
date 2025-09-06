/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.validation;

/**
 * App 更新校验器
 *
 * @author 邬涨财
 * @since 2024-06-20
 */
public interface AppUpdateValidator {
    /**
     * 校验一个app是否可以更新
     *
     * @param id 待验证的appId
     */
    void validate(String id);
}
