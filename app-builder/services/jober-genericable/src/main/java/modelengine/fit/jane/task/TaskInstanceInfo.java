/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示任务实例的信息。
 *
 * @author 梁济时
 * @since 2023-11-20
 */
public class TaskInstanceInfo {
    private String id;

    private String typeId;

    private String sourceId;

    private Map<String, String> info;

    private List<String> tags;

    private List<String> categories;

    public TaskInstanceInfo() {
        this(null, null, null, null, null, null);
    }

    public TaskInstanceInfo(String id, String typeId, String sourceId, Map<String, String> info, List<String> tags,
            List<String> categories) {
        this.id = id;
        this.typeId = typeId;
        this.sourceId = sourceId;
        this.info = info;
        this.tags = tags;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && obj.getClass() == getClass()) {
            TaskInstanceInfo another = cast(obj);
            return Objects.equals(this.getId(), another.getId()) && Objects.equals(this.getTypeId(),
                    another.getTypeId()) && Objects.equals(this.getSourceId(), another.getSourceId()) && equals(
                    this.getInfo(), another.getInfo()) && equals(this.getTags(), another.getTags()) && equals(
                    this.getCategories(), another.getCategories());
        } else {
            return false;
        }
    }

    private static boolean equals(Map<String, String> map1, Map<String, String> map2) {
        if (map1 == null) {
            return map2 == null;
        }
        if (map2 == null || map2.size() != map1.size()) {
            return false;
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            Object value1 = entry.getValue();
            Object value2 = map2.get(entry.getKey());
            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean equals(List<String> list1, List<String> list2) {
        if (list1 == null) {
            return list2 == null;
        }
        if (list2 == null || list2.size() != list1.size()) {
            return false;
        }
        return CollectionUtils.difference(list1, list2).isEmpty();
    }

    @Override
    public int hashCode() {
        Map<String, String> actualInfo = nullIf(this.getInfo(), Collections.emptyMap());
        List<String> actualTags = nullIf(this.getTags(), Collections.emptyList());
        List<String> actualCategories = nullIf(this.getCategories(), Collections.emptyList());
        int size = 3 + (actualInfo.size() << 2) + actualTags.size() + actualCategories.size();
        List<Object> values = new ArrayList<>(size);
        values.add(this.getId());
        values.add(this.getTypeId());
        values.add(this.getSourceId());
        actualInfo.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            values.add(entry.getKey());
            values.add(entry.getValue());
        });
        values.addAll(actualTags.stream().sorted().collect(Collectors.toList()));
        values.addAll(actualCategories.stream().sorted().collect(Collectors.toList()));
        return Arrays.hashCode(values.toArray());
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, typeId={1}, sourceId={2}, info={3}, tags={4}, categories={5}]",
                this.getId(), this.getTypeId(), this.getSourceId(), Optional.ofNullable(this.getInfo())
                        .orElseGet(Collections::emptyMap)
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> StringUtils.format("{0}={1}", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(", ", "[", "]")), Optional.ofNullable(this.getTags())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .sorted()
                        .collect(Collectors.joining(", ", "[", "]")), Optional.ofNullable(this.getCategories())
                        .orElseGet(Collections::emptyList)
                        .stream()
                        .sorted()
                        .collect(Collectors.joining(", ", "[", "]")));
    }
}
