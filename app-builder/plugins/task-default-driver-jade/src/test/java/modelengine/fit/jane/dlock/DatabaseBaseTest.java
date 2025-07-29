/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.IoUtils;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * 所有测试的父类，执行h2数据库初始化等功能
 *
 * @author 李哲峰
 * @since 2023-12-08
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
    }

    private static void initDB() {
        sqlSessionManager = initialize();
        executeSqlInFolder("create");
        executeSqlInFolder("update");
    }

    private static SqlSessionManager initialize() {
        try (InputStream in = IoUtils.resource(DatabaseBaseTest.class.getClassLoader(), MYBATIS_CONF_KEY)) {
            return SqlSessionManager.newInstance(new SqlSessionFactoryBuilder().build(in, DatabaseBaseTest.JDBC));
        } catch (IOException ex) {
            log.error("initialize catch IOException {}", ex.getMessage(), ex);
            throw new IllegalStateException(
                    String.format(Locale.ROOT, "Failed to load Mybatis configurations from resource. [key=%s]",
                            MYBATIS_CONF_KEY));
        }
    }

    /**
     * 执行SQL语句的方法
     *
     * @param sqlKey sql语句文件地址
     */
    protected static void executeSqlInFile(String sqlKey) {
        try (InputStream in = IoUtils.resource(DatabaseBaseTest.class.getClassLoader(), sqlKey);
                Connection connection = DriverManager.getConnection(JDBC.getProperty("jdbc.url"),
                        JDBC.getProperty("jdbc.username"), JDBC.getProperty("jdbc.password"));
                Statement statement = connection.createStatement()) {
            statement.execute(new String(IoUtils.read(in), StandardCharsets.UTF_8));
            connection.commit();
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /**
     * 执行SQL语句的方法
     *
     * @param sqlFile sql语句
     */
    protected static void executeSqlFile(File sqlFile) {
        try (Connection connection = DriverManager.getConnection(JDBC.getProperty("jdbc.url"),
                JDBC.getProperty("jdbc.username"), JDBC.getProperty("jdbc.password"));
                Statement statement = connection.createStatement()) {
            String sql = new String(Files.readAllBytes(sqlFile.toPath()), StandardCharsets.UTF_8);
            statement.execute(sql);
            connection.commit();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
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

    private static void executeSqlInFolder(String folderName) {
        File rootFolder = new File("." + File.separator + "sql" + File.separator + folderName);
        executeSqlInSubfolders(rootFolder);
    }

    private static void executeSqlInSubfolders(File folder) {
        if (!folder.isDirectory()) {
            return;
        }
        File[] files = folder.listFiles();
        Optional.ofNullable(files).ifPresent(sqlFiles -> Arrays.stream(sqlFiles).forEach(sqlFile -> {
            if (sqlFile.isDirectory()) {
                executeSqlInSubfolders(sqlFile); // 递归调用，处理子文件夹
            }
            if (sqlFile.getName().contains("compatible_with_h2")) {
                return;
            }
            if (sqlFile.getName().endsWith(".sql")) {
                executeSqlFile(sqlFile);
            }
        }));
    }

    @AfterEach
    void initData() {
        insertTable();
    }

    @AfterEach
    void cleanData() {
        cleanTable();
    }

    /**
     * 构造数据库表
     */
    protected void insertTable() {
    }

    /**
     * 清理数据库表
     */
    protected void cleanTable() {
    }
}
