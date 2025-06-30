/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow;

import static com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource;

import modelengine.fit.waterflow.exceptions.ServerInternalException;
import modelengine.fitframework.log.Logger;

import org.apache.ibatis.datasource.DataSourceFactory;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * 为测试构造的Druid数据源及配置初始化类
 *
 * @author 陈镕希
 * @since 2023-06-19
 */
public class DruidDataSourceFactory implements DataSourceFactory {
    private static final Logger log = Logger.get(DruidDataSourceFactory.class);

    private DataSource dataSource;

    @Override
    public void setProperties(Properties properties) {
        try {
            this.dataSource = createDataSource(properties);
            // druid原生api抛出的是基类异常，因此此处必须捕获受检异常基类
        } catch (final Exception ex) {
            log.error("Failed to init druid data source: {}", ex.getMessage());
            log.debug("details: ", ex);
            throw new ServerInternalException("DruidDataSourceFactory set properties error.");
        }
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
