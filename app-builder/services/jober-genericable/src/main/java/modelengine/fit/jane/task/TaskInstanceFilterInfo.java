/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为任务实例提供查询条件。
 *
 * @author 梁济时
 * @since 2023-11-20
 */
public class TaskInstanceFilterInfo {
    private Map<String, List<String>> infos;

    private List<String> categories;

    private List<String> typeIds;

    public TaskInstanceFilterInfo() {
        this(null, null, null);
    }

    public TaskInstanceFilterInfo(Map<String, List<String>> infos, List<String> categories, List<String> typeIds) {
        this.infos = infos;
        this.categories = categories;
        this.typeIds = typeIds;
    }

    public Map<String, List<String>> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, List<String>> infos) {
        this.infos = infos;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<String> typeIds) {
        this.typeIds = typeIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            TaskInstanceFilterInfo that = (TaskInstanceFilterInfo) obj;
            Map<String, List<String>> thisInfos = nullIf(this.getInfos(), Collections.emptyMap());
            Map<String, List<String>> thatInfos = nullIf(that.getInfos(), Collections.emptyMap());
            if (thisInfos.size() != thatInfos.size()) {
                return false;
            }
            for (Map.Entry<String, List<String>> entry : thisInfos.entrySet()) {
                String key = entry.getKey();
                List<String> thisValues = entry.getValue();
                List<String> thatValues = thatInfos.get(key);
                if (!equalsIgnoreSort(thisValues, thatValues)) {
                    return false;
                }
            }
            return equalsIgnoreSort(this.getCategories(), that.getCategories());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        Map<String, List<String>> sortedInfos = this.getSortedInfos();
        List<String> sortedCategories = this.getSortedCategories();
        List<String> sortedTypeIds = this.getSortedTypeIds();
        List<Object> values = new LinkedList<>();
        values.add(this.getClass());
        sortedInfos.forEach((key, value) -> {
            values.add(key);
            values.addAll(value);
        });
        values.addAll(sortedCategories);
        values.addAll(sortedTypeIds);
        return Arrays.hashCode(values.toArray());
    }

    @Override
    public String toString() {
        Map<String, List<String>> sortedInfos = this.getSortedInfos();
        List<String> sortedCategories = this.getSortedCategories();
        List<String> sortedTypeIds = this.getSortedTypeIds();
        return StringUtils.format("[infos={0}, categories={1}, typeIds={2}]", toString(sortedInfos),
                toString(sortedCategories), toString(sortedTypeIds));
    }

    private static List<String> sort(List<String> origin) {
        List<String> actual = new ArrayList<>(origin);
        Collections.sort(actual);
        return actual;
    }

    private static boolean equalsIgnoreSort(List<String> thisValues, List<String> thatValues) {
        List<String> actualThisValues = nullIf(thisValues, Collections.emptyList());
        List<String> actualThatValues = nullIf(thatValues, Collections.emptyList());
        if (actualThisValues.size() != actualThatValues.size()) {
            return false;
        }
        List<String> sortedThisValues = sort(actualThisValues);
        List<String> sortedThatValues = sort(actualThatValues);
        for (int i = 0; i < sortedThisValues.size(); i++) {
            String thisValue = sortedThisValues.get(i);
            String thatValue = sortedThatValues.get(i);
            if (!Objects.equals(thisValue, thatValue)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, List<String>> getSortedInfos() {
        Map<String, List<String>> actualInfos = nullIf(this.getInfos(), Collections.emptyMap());
        Map<String, List<String>> sortedInfos = new LinkedHashMap<>(actualInfos.size());
        actualInfos.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sortedInfos.put(entry.getKey(), sort(entry.getValue())));
        return sortedInfos;
    }

    private List<String> getSortedCategories() {
        return sort(nullIf(this.getCategories(), Collections.emptyList()));
    }

    private List<String> getSortedTypeIds() {
        return sort(nullIf(this.getTypeIds(), Collections.emptyList()));
    }

    private static String toString(List<String> values) {
        return values.stream().collect(Collectors.joining(", ", "[", "]"));
    }

    private static String toString(Map<String, List<String>> values) {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + '=' + toString(entry.getValue()))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
