/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import javax.sql.DataSource;

/**
 * data source提供方
 *
 * @author 梁济时
 * @since 2023/11/28
 */
public interface DataSourceProvider {
    /**
     * 获取data source
     *
     * @return data source
     */
    DataSource getDataSource();
}
