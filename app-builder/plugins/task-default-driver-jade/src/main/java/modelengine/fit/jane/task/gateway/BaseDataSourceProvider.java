/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import modelengine.fitframework.annotation.Component;

import javax.sql.DataSource;

/**
 * {@link DataSourceProvider} 的默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Component
public class BaseDataSourceProvider implements DataSourceProvider {
    private final DataSource dataSource;

    public BaseDataSourceProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
