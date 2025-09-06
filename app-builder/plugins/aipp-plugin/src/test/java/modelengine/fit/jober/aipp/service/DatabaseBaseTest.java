/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.IoUtils;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Properties;

/**
 * 数据库初始化类
 *
 * @author 邬涨财
 * @since 2024-08-07
 */
@ExtendWith(MockitoExtension.class)
public class DatabaseBaseTest {
    /**
     * 用于在各单元测试中提供session工厂
     */
    protected static SqlSessionManager sqlSessionManager;

    private static final Logger log = Logger.get(DatabaseBaseTest.class);

    private static final String MYBATIS_CONF_KEY = "mybatis.xml";

    private static final Properties JDBC = load();

    @BeforeAll
    static void init() {
        initDB();
        executeSqlInFile("sql/init.sql");
    }

    @BeforeEach
    void initData() {
        executeSqlInFile("sql/insert.sql");
    }

    @AfterEach
    void clearTable() {
        executeSqlInFile("sql/clear_table.sql");
    }

    private static void initDB() {
        sqlSessionManager = initialize();
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = IoUtils.resource(DatabaseBaseTest.class, "/jdbc.properties")) {
            properties.load(in);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load jdbc.properties for test.");
        }
        return properties;
    }

    private static SqlSessionManager initialize() {
        try (InputStream in = IoUtils.resource(DatabaseBaseTest.class.getClassLoader(), MYBATIS_CONF_KEY)) {
            return SqlSessionManager.newInstance(new SqlSessionFactoryBuilder().build(in, DatabaseBaseTest.JDBC));
        } catch (IOException ex) {
            log.error("initialize catch IOException {}", ex.getMessage(), ex);
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to load Mybatis configurations from resource. [key=%s]",
                    MYBATIS_CONF_KEY));
        }
    }

    private static void executeSqlInFile(String sqlKey) {
        String url = getProperty("jdbc.url");
        String userName = getProperty("jdbc.username");
        String password = getProperty("jdbc.password");
        try (InputStream in = IoUtils.resource(DatabaseBaseTest.class.getClassLoader(), sqlKey);
             Connection connection = DriverManager.getConnection(url, userName, password);
             Statement statement = connection.createStatement()) {
            statement.execute(new String(IoUtils.read(in), StandardCharsets.UTF_8));
            connection.commit();
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private static String getProperty(String key) {
        return JDBC.getProperty(key);
    }
}
