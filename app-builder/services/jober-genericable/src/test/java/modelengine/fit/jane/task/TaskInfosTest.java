/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TaskInfosTest
 *
 * @author 梁济时
 * @since 2023/11/28
 */
@DisplayName("测试 TaskInfos 工具类")
class TaskInfosTest {
    private TaskInfo task;

    @BeforeEach
    void setup() {
        task = new TaskInfo();
        task.setId("39390d96282f41a492b9e4c3697daf1d");
        task.setName("demo-task");
        task.setProperties(Collections.emptyList());

        TaskTypeInfo type1 = new TaskTypeInfo();
        type1.setId("39390d96282f41a492b9e4c3697daf1d");
        type1.setName("type-1");

        TaskTypeInfo type11 = new TaskTypeInfo();
        type11.setId("105e8caa1e254e98a5b276b7d2a44e30");
        type11.setName("type-11");

        TaskTypeInfo type12 = new TaskTypeInfo();
        type12.setId("f1ad131fbf314ef0adeac899526113ec");
        type12.setName("type-12");

        TaskTypeInfo type121 = new TaskTypeInfo();
        type121.setId("f1ad131fbf314ef0adeac899526113ec");
        type121.setName("type-121");

        TaskTypeInfo type2 = new TaskTypeInfo();
        type2.setId("b7569c2f7a7c4fd888842c2c14bcdaff");
        type2.setName("type-2");

        type1.setChildren(Arrays.asList(type11, type12));
        type11.setChildren(null);
        type12.setChildren(Collections.singletonList(type121));
        type121.setChildren(null);
        type2.setChildren(Collections.emptyList());
        task.setTypes(Arrays.asList(type1, type2));
    }

    @Test
    @DisplayName("当存在待查找的类型时，返回从根类型的链")
    void should_return_chain_when_found() {
        List<TaskTypeInfo> chain = TaskInfos.lookupTaskType(task, "f1ad131fbf314ef0adeac899526113ec");
        assertEquals(2, chain.size());
        assertEquals("type-1", chain.get(0).getName());
        assertEquals("type-12", chain.get(1).getName());
    }

    @Test
    @DisplayName("当不存在待查找的类型时，返回空的列表")
    void should_return_empty_when_not_found() {
        List<TaskTypeInfo> chain = TaskInfos.lookupTaskType(task, "7c45e58ecd8d48aaba1cc57701478cd9");
        assertTrue(chain.isEmpty());
    }
}