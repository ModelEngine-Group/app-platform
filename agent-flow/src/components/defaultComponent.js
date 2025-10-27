/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
 * 默认component.
 *
 * @param jadeConfig 配置.
 */
export const defaultComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
    };

    /**
     * 获取React组件.
     *
     * @param shapeStatus 图形状态集合.
     * @param data 数据.
     */
    self.getReactComponents = (shapeStatus, data) => {
    };

    /**
     * 处理器
     */
    self.reducers = (config, action) => {
        const newConfig = {...config};
        if (action.type === "system_update") {
            action.changes.forEach(c => {
                newConfig[c.key] = c.value;
            });
            return newConfig;
        }
        throw Error('Unknown action: ' + action.type);
    };

    return self;
}
