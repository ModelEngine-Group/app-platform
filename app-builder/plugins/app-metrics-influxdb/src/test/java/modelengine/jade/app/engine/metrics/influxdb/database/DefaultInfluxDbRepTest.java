/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import modelengine.jade.app.engine.metrics.influxdb.bo.BasicMetricsBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.RequestSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserNumBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.database.support.DefaultInfluxDbRep;
import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.utils.SampleLevel;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.InfluxDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@link DefaultInfluxDbRep} 的测试。
 *
 * @author 高嘉乐
 * @since 2025-01-02
 */
@FitTestWithJunit(includeClasses = DefaultInfluxDbRep.class)
@DisplayName("测试 InfluxDbRepImpl")
@Disabled("暂时本地使用 testcontainer 进行测试，流水线支持后启用")
class DefaultInfluxDbRepTest {
    private static final String DATABASE = "test_database";
    private static final String USER = "test_user";
    private static final String PASSWORD = "test_password";
    private static InfluxDB mockInfluxDB;

    @Fit
    private InfluxDbRep influxDbRep;

    @Mock
    private InfluxDB influxDB;

    @BeforeAll
    static void setUp() {
        final InfluxDBContainer<?> container =
                new InfluxDBContainer<>(DockerImageName.parse("influxdb:1.8")).withDatabase(DATABASE)
                        .withUsername(USER)
                        .withPassword(PASSWORD);
        container.start();

        mockInfluxDB = InfluxDBFactory.connect(container.getUrl(), container.getUsername(), container.getPassword())
                .setDatabase(container.getDatabase());
        initInfluxdb();
    }

    @AfterAll
    static void tearDown() {
        mockInfluxDB.close();
    }

    @BeforeEach
    void init() {
        doAnswer(invocation -> (mockInfluxDB.query(invocation.getArgument(0)))).when(this.influxDB)
                .query(any(Query.class));
    }

    @Test
    @DisplayName("应用不存在时，查询指标数据应返回默认值")
    void shouldReturnDefaultWhenGetBasicMetricWithAppNotExist() {
        BasicMetricsBo metric = this.influxDbRep.getBasicMetricBo("app_not_exist", 0, 200);

        assertThat(metric).isNotNull()
                .extracting(BasicMetricsBo::getSum, BasicMetricsBo::getCount, BasicMetricsBo::getBucket0)
                .containsExactly(0L, 0L, 0L);
    }

    @Test
    @DisplayName("获取基本指标数据正确")
    void getBasicMetricCorrect() {
        BasicMetricsBo metric = this.influxDbRep.getBasicMetricBo("test_app", 0, 200);

        assertThat(metric).isNotNull()
                .extracting(BasicMetricsBo::getSum, BasicMetricsBo::getCount, BasicMetricsBo::getBucket0)
                .containsExactly(100L, 10L, 1L);
    }

    @Test
    @DisplayName("获取请求数量最多的用户数据正确")
    void getTopUsersCorrect() {
        List<UserBo> topUsers = this.influxDbRep.getTopUsers("test_app", 0, 200, 5);

        assertThat(topUsers).isNotNull().hasSize(1).extracting(UserBo::getUserName).containsExactly("test_user");
    }

    @Test
    @DisplayName("获取请求数量最多的部门数据正确")
    void getTopDepartmentsCorrect() {
        List<DepartmentBo> topDepartments = this.influxDbRep.getTopDepartments("test_app", 0, 200, 5);

        assertThat(topDepartments).isNotNull()
                .hasSize(1)
                .extracting(DepartmentBo::getDepartmentName)
                .containsExactly("L6");
    }

    @Test
    @DisplayName("获取用户来源正确")
    void getUserSourceCorrect() {
        List<UserSourceBo> userSourceList =
                this.influxDbRep.getUserSource("test_app", 0, 200, DepartmentLevel.DEP_LEVEL_3);

        assertThat(userSourceList).isNotNull()
                .hasSize(1)
                .extracting(UserSourceBo::getDepartmentName)
                .containsExactly("L3");
    }

    @Test
    @DisplayName("获取请求来源正确")
    void getRequestSourceCorrect() {
        List<RequestSourceBo> requestSourceList =
                this.influxDbRep.getRequestSource("test_app", 0, 200, DepartmentLevel.DEP_LEVEL_4);

        assertThat(requestSourceList).isNotNull()
                .hasSize(1)
                .extracting(RequestSourceBo::getDepartmentName)
                .containsExactly("L4");
    }

    @Test
    @DisplayName("获取用户访问趋势正确")
    void getUserAccessTrendsCorrect() {
        List<UserAccessTrendBo> userAccessTrends =
                this.influxDbRep.getUserAccessTrends("test_app", 0, 200, SampleLevel.LEVEL_1);

        assertThat(userAccessTrends).isNotNull()
                .hasSize(1)
                .extracting(UserAccessTrendBo::getCount)
                .containsExactly(10L);
    }

    @Test
    @DisplayName("获取用户数量正确")
    void getUserNumCorrect() {
        UserNumBo userNum = this.influxDbRep.getUserNumBo("test_app", 0, 200);

        assertThat(userNum.getCount()).isEqualTo(1);
    }

    private static void initInfluxdb() {
        Point point = Point.measurement("request")
                .tag("app_id", "test_app")
                .tag("user_name", "test_user")
                .addField("bucket0", 1)
                .addField("bucket1", 2)
                .addField("bucket2", 3)
                .addField("bucket3", 4)
                .addField("count", 10)
                .addField("sum", 100)
                .tag("l1_name", "L1")
                .tag("l2_name", "L2")
                .tag("l3_name", "L3")
                .tag("l4_name", "L4")
                .tag("l5_name", "L5")
                .tag("l6_name", "L6")
                .time(100, TimeUnit.MICROSECONDS)
                .build();
        mockInfluxDB.write(point);
    }
}