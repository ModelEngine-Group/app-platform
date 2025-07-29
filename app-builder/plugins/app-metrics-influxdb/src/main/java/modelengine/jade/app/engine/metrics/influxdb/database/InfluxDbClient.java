/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.database;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.config.InfluxDbConfig;

import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.log.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.util.concurrent.TimeUnit;

/**
 * InfluxDb 客户端。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Component
public class InfluxDbClient {
    private static final Logger log = Logger.get(InfluxDbClient.class);
    private static final long CONNECT_TIMEOUT = 10L;
    private static final long READ_TIMEOUT = 10L;
    private static final long WRITE_TIMEOUT = 10L;

    private final InfluxDbConfig properties;

    public InfluxDbClient(@Fit InfluxDbConfig properties) {
        this.properties = properties;
    }

    /**
     * 创建 influxDB 的工厂方法。
     *
     * @return 表示 InfluxDB 实例的 {@link InfluxDB}。
     */
    @Bean
    public InfluxDB influxDb() {
        OkHttpClient.Builder client = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder().header("Connection", "close").build();
                    return chain.proceed(newRequest);
                });

        InfluxDB influxDb =
                InfluxDBFactory.connect(properties.getUrl(), properties.getUsername(), properties.getPassword(), client)
                        .setDatabase(properties.getDatabase())
                        .enableGzip();

        notNull(influxDb.ping(), "Connect influxdb failed.");
        return influxDb;
    }
}