/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import modelengine.fit.jane.RangeInfo;
import modelengine.fit.jane.RangeResultInfo;
import modelengine.fit.jane.RangedResultSetInfo;
import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.task.TaskInfo;
import modelengine.fit.jane.task.TaskInstanceFilterInfo;
import modelengine.fit.jane.task.TaskInstanceInfo;
import modelengine.fit.jane.task.TaskPropertyInfo;
import modelengine.fit.jane.task.TaskSourceInfo;
import modelengine.fit.jane.task.TaskTypeInfo;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DataStructTest
 *
 * @author 梁济时
 * @since 2023/11/28
 */
class DataStructTest {
    @Test
    @DisplayName("测试 RangeInfo 数据结构")
    void testRangeInfo() {
        beanTest(new RangeInfo(100L, 200));
    }

    @Test
    @DisplayName("测试 RangeResultInfo 数据结构")
    void testRangeResultInfo() {
        beanTest(new RangeResultInfo(200L, 300, 400L));
    }

    @Test
    @DisplayName("测试 RangedResultSetInfo 数据结构")
    void testRangedResultSet() {
        beanTest(new RangedResultSetInfo<>(Arrays.asList(1, 2), new RangeResultInfo(100L, 200, 300L)));
    }

    @Test
    @DisplayName("测试 TaskPropertyInfo 数据结构")
    void testTaskPropertyInfo() {
        beanTest(new TaskPropertyInfo("id", "name", "dataType", "description", true, false, "scope"));
    }

    @Test
    @DisplayName("测试 UndefinableValue 数据结构")
    void testUndefinableValue() {
        beanTest(new Undefinable<>(true, "hello"));
    }

    @Test
    @DisplayName("测试 TaskInstanceInfo 数据结构")
    void testTaskInstanceInfo() {
        Map<String, String> info = MapBuilder.<String, String>get()
                .put("integer", "1")
                .put("text", "hello")
                .put("datetime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .put("boolean", "true")
                .build();
        beanTest(new TaskInstanceInfo("b949f64a89314586a3e0425f8cc000e5", "7520aa42c8a14624bbbbf4eb0f10cd55",
                "ec1d45198f7345fe8c50862eeeabc3e9", info, Arrays.asList("t1", "t2"),
                Collections.singletonList("未完成")));
    }

    @Test
    @DisplayName("测试 TaskInfo 数据结构")
    void testTaskInfo() {
        beanTest(new TaskInfo("taskId", "taskName", Collections.singletonList(
                new TaskPropertyInfo("propertyId", "propertyName", "propertyDataType", "propertyDescription", true,
                        false, "propertyScope")),
                Collections.singletonList(new TaskTypeInfo("typeId", "typeName", null))));
    }

    @Test
    @DisplayName("测试 TaskTypeInfo 数据结构")
    void testTaskTypeInfo() {
        beanTest(new TaskTypeInfo("id", "name",
                Collections.singletonList(new TaskTypeInfo("id2", "name2", Collections.emptyList()))));
    }

    @Test
    @DisplayName("测试 TaskInstanceFilterInfo 数据结构")
    void testTaskInstanceFilterInfo() {
        beanTest(new TaskInstanceFilterInfo(MapBuilder.<String, List<String>>get()
                .put("c", Arrays.asList("c2", "c1", "c3"))
                .put("a", Arrays.asList("a1", "a2"))
                .put("b", Arrays.asList("b1", "b3", "b2"))
                .build(), Arrays.asList("category2", "category1", "category3"),
                Arrays.asList("typeId2", "typeId1", "typeId3")));
    }

    @Test
    @DisplayName("测试 TaskSourceInfo 数据结构")
    void testTaskSourceInfo() {
        beanTest(new TaskSourceInfo(new TaskInfo(), "world",
                MapBuilder.<String, Object>get().put("hello", 100).build()));
    }

    /**
     * 验证指定对象需要是一个 bean。
     * <ol>
     *     <li>Bean 的类型必须存在默认构造方法。</li>
     *     <li>通过 {@link BeanInfo} 描述的全属性拷贝，与被拷贝的实例具备相同的内容。</li>
     * </ol>
     *
     * @param object 表示待验证的对象的 {@link Object}。
     */
    private static void beanTest(Object object) {
        try {
            Class<?> clazz = object.getClass();
            Constructor<?> constructor = clazz.getConstructor();
            assertNotNull(constructor,
                    StringUtils.format("No default constructor declared in class {0}.", clazz.getName()));
            Object another = constructor.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            Map<String, PropertyDescriptor> properties = Stream.of(beanInfo.getPropertyDescriptors())
                    .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));
            Set<String> propertyNames = collectPropertyNames(clazz);
            for (String propertyName : propertyNames) {
                PropertyDescriptor property = properties.get(propertyName);
                assertNotNull(property,
                        StringUtils.format("Property {0} not found in class {1}.", propertyName, clazz.getName()));
                Method getter = property.getReadMethod();
                assertNotNull(getter,
                        StringUtils.format("No getter found in class {0} for property {1}.", clazz.getName(),
                                propertyName));
                Method setter = property.getWriteMethod();
                assertNotNull(setter,
                        StringUtils.format("No setter found in class {0} for property {1}.", clazz.getName(),
                                propertyName));
                AtomicReference<Object> value = new AtomicReference<>();
                assertDoesNotThrow(() -> value.set(getter.invoke(object)));
                assertDoesNotThrow(() -> setter.invoke(another, value.get()));
            }
            assertEquals(object, another);
            assertEquals(object.hashCode(), another.hashCode());
            assertEquals(object.toString(), another.toString());
        } catch (IntrospectionException | ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static Set<String> collectPropertyNames(Class<?> clazz) {
        Class<?> current = clazz;
        Set<String> propertyNames = new HashSet<>();
        while (current != null) {
            Field[] fields = current.getDeclaredFields();
            Stream.of(fields).filter(DataStructTest::isDataField).map(Field::getName).forEach(propertyNames::add);
            current = current.getSuperclass();
        }
        return propertyNames;
    }

    private static boolean isDataField(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isTransient(modifiers);
    }
}
