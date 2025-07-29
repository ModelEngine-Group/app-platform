/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * 为 {@link TaskInfo} 提供工具类。
 *
 * @author 梁济时
 * @since 2023-11-14
 */
public final class TaskInfos {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private TaskInfos() {
    }

    /**
     * 在任务中查找指定唯一标识的类型。
     * <p>其返回的结果是任务类型的路径。例如，任务类型树为：</p>
     * <ul>
     *     <li>Type-1
     *     <ul>
     *         <li>Type-11</li>
     *         <li>Type-12</li>
     *     </ul>
     *     </li>
     *     <li>Type-2</li>
     * </ul>
     * 若查找 {@code Type-12}，则返回的结果为：{@code [Type-1, Type-12]}。
     *
     * @param task 表示待查找类型的任务的 {@link TaskInfo}。
     * @param typeId 表示待查找的类型的唯一标识的 {@link String}。
     * @return 如果未找到类型，则是一个空列表，否则为从根类型到待查找类型的任务类型列表的
     * {@link List}{@code <}{@link TaskTypeInfo}{@code >}。
     */
    public static List<TaskTypeInfo> lookupTaskType(TaskInfo task, String typeId) {
        notNull(task, "The task to lookup type cannot be null.");
        notNull(typeId, "The id of task type to lookup cannot be null.");
        return lookupTaskType(task.getTypes(), type -> StringUtils.equalsIgnoreCase(type.getId(), typeId));
    }

    /**
     * 在任务中查找指定名称的类型。
     * <p>其返回的结果是任务类型的路径。例如，任务类型树为：</p>
     * <ul>
     *     <li>Type-1
     *     <ul>
     *         <li>Type-11</li>
     *         <li>Type-12</li>
     *     </ul>
     *     </li>
     *     <li>Type-2</li>
     * </ul>
     * 若查找 {@code Type-12}，则返回的结果为：{@code [Type-1, Type-12]}。
     *
     * @param task 表示待查找类型的任务的 {@link TaskInfo}。
     * @param name 表示待查找的类型的名称的 {@link String}。
     * @return 如果未找到类型，则是一个空列表，否则为从根类型到待查找类型的任务类型列表的
     * {@link List}{@code <}{@link TaskTypeInfo}{@code >}。
     */
    public static List<TaskTypeInfo> lookupTaskTypeByName(TaskInfo task, String name) {
        notNull(task, "The task to lookup type cannot be null.");
        notNull(name, "The name of task type to lookup cannot be null.");
        return lookupTaskType(task.getTypes(), type -> StringUtils.equalsIgnoreCase(type.getName(), name));
    }

    private static List<TaskTypeInfo> lookupTaskType(List<TaskTypeInfo> types, Predicate<TaskTypeInfo> predicate) {
        if (types == null) {
            return Collections.emptyList();
        }
        for (TaskTypeInfo type : types) {
            if (predicate.test(type)) {
                return Collections.singletonList(type);
            }
            List<TaskTypeInfo> found = lookupTaskType(type.getChildren(), predicate);
            if (!found.isEmpty()) {
                List<TaskTypeInfo> results = new ArrayList<>(found.size() + 1);
                results.add(type);
                results.addAll(found);
                return results;
            }
        }
        return Collections.emptyList();
    }
}